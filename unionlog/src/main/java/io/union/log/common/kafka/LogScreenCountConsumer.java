package io.union.log.common.kafka;

import io.union.log.common.redis.CacheLockPach;
import io.union.log.common.redis.CacheLogScreen;
import io.union.log.common.utils.ApplicationConstant;
import io.union.log.common.zoo.ZooUtils;
import io.union.log.entity.LogScreen;
import io.union.log.service.LogScreenService;
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
public class LogScreenCountConsumer {
    final Logger logger = LoggerFactory.getLogger(getClass());

    public LogScreenCountConsumer(@Value("${spring.kafka.bootstrap-servers}") String hosts, @Autowired LogScreenService logScreenService, @Autowired CacheLogScreen cacheLogScreen,
                                  @Autowired ZooUtils zooutils, @Value("${spring.zookeeper.lock.screen}") String rootPath, @Autowired CacheLockPach cacheLockPach) {
        AtomicBoolean closed = new AtomicBoolean(false);
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        ExecutorService exec = Executors.newCachedThreadPool();

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, ApplicationConstant.LOG_SCREEN_COUNT_GROUP);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(ApplicationConstant.LOG_SCREEN_COUNT_TOPIC));

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
                    Map<String, LogScreen> tempmap = new HashMap<>();
                    while (queue.size() != 0) {
                        try {
                            String message = queue.poll(1L, TimeUnit.SECONDS);
                            if (null == message || "".equals(message)) {
                                // Thread.sleep(1000);
                                continue;
                            }
                            //logger.info("queue size: " + queue.size());
                            // message = {"cusNum":0,"uid":1,"addTime":{"date":8,"hours":19,"seconds":26,"month":7,"timezoneOffset":-480,"year":117,"minutes":3,"time":1502190206483,"day":2},"cusScreen":"380x580","id":0};
                            JSONObject request = JSONObject.fromObject(message);
                            JSONObject timeObj = request.getJSONObject("addTime");
                            Long uid = request.getLong("uid"), time = timeObj.getLong("time"), cusNum = request.getLong("cusNum");
                            String cusScreen = request.getString("cusScreen");

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(time);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String today = sdf.format(calendar.getTime());

                            String key = cusScreen + uid + today;
                            LogScreen screen = tempmap.get(key);
                            if (null == screen) {
                                screen = new LogScreen();
                                screen.setUid(uid);
                                screen.setCusScreen(cusScreen);
                                screen.setCusNum(0L);
                                screen.setAddTime(calendar.getTime());
                            }
                            long screennum = null == screen.getCusNum() ? 0 : screen.getCusNum();
                            screen.setCusNum(screennum + cusNum);
                            tempmap.put(key, screen);
                        } catch (Exception e) {
                            logger.error("add error: ", e);
                        }
                    }
                    tempmap.forEach((k, v) -> {
                        // 这个涉及到后台定时任务的计算，如果缓存在本地的话，有可能造成数据错乱
                        // 这里加入分布式锁，同时后台和官网都需要使用该分布锁，防止用户数据错乱
                        String lockPath = rootPath + k;
                        cacheLockPach.add(lockPath);
                        InterProcessMutex lock = zooutils.buildLock(lockPath);
                        try {
                            if (lock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                                Long uid = v.getUid(), cusNum = v.getCusNum(), invalidId = 0L;
                                String cusScreen = v.getCusScreen();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String today = sdf.format(v.getAddTime());

                                LogScreen screen = cacheLogScreen.single(cusScreen, uid, today);
                                if (null == screen || null == screen.getAddTime() || invalidId.equals(screen.getId())) {
                                    screen = v;
                                } else {
                                    long screennum = null == screen.getCusNum() ? 0 : screen.getCusNum();
                                    screen.setCusNum(screennum + cusNum);
                                }
                                screen = logScreenService.saveOne(screen);
                                cacheLogScreen.save(screen);
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
