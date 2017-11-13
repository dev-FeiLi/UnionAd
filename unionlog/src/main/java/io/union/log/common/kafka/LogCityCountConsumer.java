package io.union.log.common.kafka;

import io.union.log.common.redis.CacheLockPach;
import io.union.log.common.redis.CacheLogCity;
import io.union.log.common.utils.ApplicationConstant;
import io.union.log.common.utils.HanyuPinyinHelper;
import io.union.log.common.utils.Tools;
import io.union.log.common.zoo.ZooUtils;
import io.union.log.entity.LogCity;
import io.union.log.service.LogCityService;
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
public class LogCityCountConsumer {
    final Logger logger = LoggerFactory.getLogger(getClass());

    public LogCityCountConsumer(@Value("${spring.kafka.bootstrap-servers}") String hosts, @Autowired LogCityService logCityService, @Autowired CacheLogCity cacheLogCity,
                                @Autowired ZooUtils zooutils, @Value("${spring.zookeeper.lock.city}") String rootPath, @Autowired CacheLockPach cacheLockPach) {
        AtomicBoolean closed = new AtomicBoolean(false);
        HanyuPinyinHelper hanyuPinyinHelper = new HanyuPinyinHelper();
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        ExecutorService exec = Executors.newCachedThreadPool();

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, ApplicationConstant.LOG_CITY_COUNT_GROUP);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(ApplicationConstant.LOG_CITY_COUNT_TOPIC));

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
                    Map<String, LogCity> map = new HashMap<>();
                    while (queue.size() != 0) {
                        try {
                            String message = queue.poll(1L, TimeUnit.SECONDS);
                            if (null == message || "".equals(message)) {
                                // Thread.sleep(1000);
                                continue;
                            }
                            //logger.info("queue size: " + queue.size());
                            // message = {"cusIsp":"电信","cusNum":0,"uid":1,"addTime":{"date":8,"hours":18,"seconds":48,"month":7,"timezoneOffset":-480,"year":117,"minutes":31,"time":1502188308661,"day":2},
                            // "cusCity":"","id":0,"cusProvince":"广东省深圳市"};
                            JSONObject request = JSONObject.fromObject(message);
                            JSONObject timeObj = request.getJSONObject("addTime");
                            Long uid = request.getLong("uid"), time = timeObj.getLong("time"), cusNum = request.getLong("cusNum");
                            String cusIsp = request.getString("cusIsp"), cusProvince = request.getString("cusProvince"), cusCity = request.getString("cusCity");

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(time);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String today = sdf.format(calendar.getTime());

                            String province = hanyuPinyinHelper.toHanyuPinyin(cusProvince);
                            String cityPinyin = hanyuPinyinHelper.toHanyuPinyin(cusCity);
                            String isp = hanyuPinyinHelper.toHanyuPinyin(cusIsp);
                            try {
                                province = province.replaceAll("[`~!@#$%^&*()\\-+=,/<>?|;:'\"_\\\\]", "");
                                province = province.replaceAll("[·~！￥…（），。、《》？；：‘’“”—]", "");
                            } catch (Exception e) {
                                province = "provinceinvalidletters";
                            }
                            try {
                                isp = isp.replaceAll("[`~!@#$%^&*()\\-+=,/<>?|;:'\"_\\\\]", "");
                                isp = isp.replaceAll("[·~！￥…（），。、《》？；：‘’“”—]", "");
                            } catch (Exception e) {
                                isp = "ispinvalidletters";
                            }

                            String lockPath = province + cityPinyin + isp + uid + today;
                            LogCity city = map.get(lockPath);
                            if (null == city) {
                                city = new LogCity();
                                city.setUid(uid);
                                city.setCusProvince(Tools.cutdown(cusProvince));
                                city.setCusNum(0L);
                                city.setCusIsp(Tools.cutdown(cusIsp));
                                city.setCusCity(cusCity);
                                city.setAddTime(calendar.getTime());
                            }
                            long citynum = null == city.getCusNum() ? 0 : city.getCusNum();
                            city.setCusNum(citynum + cusNum);
                            map.put(lockPath, city);
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
                                Long uid = v.getUid(), cusNum = v.getCusNum(), invalidId = 0L;
                                String cusProvince = v.getCusProvince(), cusIsp = v.getCusIsp();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String today = sdf.format(v.getAddTime());

                                LogCity city = cacheLogCity.single(cusProvince, cusIsp, uid, today);
                                if (null == city || null == city.getAddTime() || invalidId.equals(city.getId())) {
                                    city = v;
                                } else {
                                    long citynum = null == city.getCusNum() ? 0 : city.getCusNum();
                                    city.setCusNum(citynum + cusNum);
                                }
                                city = logCityService.saveOne(city);
                                cacheLogCity.save(city);
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
