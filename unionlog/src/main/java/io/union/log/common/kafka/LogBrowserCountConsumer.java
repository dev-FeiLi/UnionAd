package io.union.log.common.kafka;

import io.union.log.common.redis.CacheLockPach;
import io.union.log.common.redis.CacheLogBrowser;
import io.union.log.common.utils.ApplicationConstant;
import io.union.log.common.zoo.ZooUtils;
import io.union.log.entity.LogBrowser;
import io.union.log.service.LogBrowserService;
import net.sf.json.JSONObject;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2017/7/26.
 */
@Component
public class LogBrowserCountConsumer {
    final Logger logger = LoggerFactory.getLogger(getClass());

    public LogBrowserCountConsumer(@Value("${spring.kafka.bootstrap-servers}") String hosts, @Autowired CacheLogBrowser cacheLogBrowser, @Autowired LogBrowserService logBrowserService,
                                   @Autowired ZooUtils zooutils, @Value("${spring.zookeeper.lock.browser}") String rootPath, @Autowired CacheLockPach cacheLockPach) {
        AtomicBoolean closed = new AtomicBoolean(false);
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        ExecutorService exec = Executors.newCachedThreadPool();

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, ApplicationConstant.LOG_BROWSER_COUNT_GROUP);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(ApplicationConstant.LOG_BROWSER_COUNT_TOPIC));

        exec.submit(() -> {
            while (!closed.get()) {
                try {
                    ConsumerRecords<String, String> records = consumer.poll(1000);
                    if (records.isEmpty()) {
                        Thread.sleep(5000);
                    }
                    for (ConsumerRecord<String, String> record : records) {
                        queue.put(record.value());
                    }
                } catch (Exception e) {
                    logger.error("kafka poll error: ", e);
                }
            }
        });
        Runnable runnable = () -> {
            while (!closed.get()) {
                try {
                    if (queue.size() == 0) {
                        Thread.sleep(5000);
                        continue;
                    }
                    Map<String, LogBrowser> map = new HashMap<>();
                    while (queue.size() != 0) {
                        try {
                            String message = queue.poll(1L, TimeUnit.SECONDS);
                            if (null == message || "".equals(message)) {
                                // Thread.sleep(1000);
                                continue;
                            }
                            //logger.info("queue size: " + queue.size());
                            // message = {"brwVersion":"44.21.110.0","uid":1,"addTime":{"date":8,"hours":16,"seconds":51,"month":7,"timezoneOffset":-480,"year":117,"minutes":37,"time":1502181471327,"day":2},
                            // "brwNum":1,"brwPlat":"android","id":0,"brwName":"ucbrowser"};
                            JSONObject request = JSONObject.fromObject(message);
                            JSONObject timeObj = request.getJSONObject("addTime");
                            Long uid = request.getLong("uid"), brwNum = request.getLong("brwNum"), time = timeObj.getLong("time");
                            String brwVersion = request.getString("brwVersion"), brwPlat = request.getString("brwPlat"), brwName = request.getString("brwName");
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(time);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String today = sdf.format(calendar.getTime());

                            String lockPath = uid + brwName + brwPlat + brwPlat + brwVersion + today;
                            LogBrowser browser = map.get(lockPath);
                            if (null == browser) {
                                browser = new LogBrowser();
                                browser.setUid(uid);
                                browser.setAddTime(calendar.getTime());
                                browser.setBrwName(brwName);
                                browser.setBrwVersion(brwVersion);
                                browser.setBrwPlat(brwPlat);
                                browser.setBrwNum(0L);
                            }
                            long browsernum = null == browser.getBrwNum() ? 0 : browser.getBrwNum();
                            browser.setBrwNum(browsernum + brwNum);
                            map.put(lockPath, browser);
                        } catch (Exception e) {
                            logger.error("add error: ", e);
                        }
                    }
                    map.forEach((k, v) -> {
                        // 这个涉及到后台定时任务的计算，如果缓存在本地的话，有可能造成数据错乱
                        // 这里加入分布式锁，同时后台和官网都需要使用该分布锁，防止用户数据错乱
                        String lockPath = rootPath + k;
                        cacheLockPach.add(lockPath);
                        InterProcessMutex lock = zooutils.buildLock(lockPath);
                        try {
                            if (lock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                                Long uid = v.getUid(), invalidId = 0L, brwNum = v.getBrwNum();
                                String brwName = v.getBrwName(), brwPlat = v.getBrwPlat(), brwVersion = v.getBrwVersion();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String today = sdf.format(v.getAddTime());
                                LogBrowser browser = cacheLogBrowser.single(uid, brwName, brwPlat, brwVersion, today);
                                if (null == browser || null == browser.getAddTime() || invalidId.equals(browser.getId())) {
                                    browser = v;
                                } else {
                                    long browsernum = null == browser.getBrwNum() ? 0 : browser.getBrwNum();
                                    browser.setBrwNum(browsernum + brwNum);
                                }
                                browser = logBrowserService.saveOne(browser);
                                cacheLogBrowser.save(browser);
                            } else {
                                // 获取分布式锁超时，queue中的数据还在，可以继续计算
                                logger.error("lock timeout, must check zookeeper server or other web server status");
                                // 这里可以尝试发邮件，但可能会发生1秒一个邮件，所以需要累计异常次数后才发一次邮件
                            }
                        } catch (Exception e) {
                            logger.error("lock error: ", e);
                        } finally {
                            zooutils.release(lock);
                        }
                    });
                } catch (Exception e) {
                    logger.error("kafka consumer poll error: ", e);
                }
            }
        };

        exec.submit(runnable);
        exec.submit(runnable);
        exec.submit(runnable);
    }
}
