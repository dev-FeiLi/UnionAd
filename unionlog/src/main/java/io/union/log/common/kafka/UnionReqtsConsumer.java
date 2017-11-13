package io.union.log.common.kafka;

import io.union.log.common.redis.CacheLockPach;
import io.union.log.common.redis.CacheUnionReqts;
import io.union.log.common.utils.ApplicationConstant;
import io.union.log.common.zoo.ZooUtils;
import io.union.log.entity.UnionReqts;
import io.union.log.service.UnionReqtsService;
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
public class UnionReqtsConsumer {
    final Logger logger = LoggerFactory.getLogger(getClass());

    public UnionReqtsConsumer(@Value("${spring.kafka.bootstrap-servers}") String hosts, @Value("${spring.zookeeper.lock.reqts}") String reqtsPath, @Autowired UnionReqtsService unionReqtsService,
                              @Autowired CacheUnionReqts cacheUnionReqts, @Autowired ZooUtils zooutils, @Autowired CacheLockPach cacheLockPach) {
        AtomicBoolean closed = new AtomicBoolean(false);
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        ExecutorService exec = Executors.newCachedThreadPool();

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, ApplicationConstant.UNION_REQTS_COUNT_GROUP);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(ApplicationConstant.UNION_REQTS_COUNT_TOPIC));

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
                    Map<String, UnionReqts> map = new HashMap<>();
                    while (queue.size() != 0) {
                        try {
                            String message = queue.poll(1L, TimeUnit.SECONDS);
                            if (null == message || "".equals(message)) {
                                // Thread.sleep(1000);
                                continue;
                            }
                            //logger.info("queue size: " + queue.size());
                            // message = {"addTime":{"date":8,"hours":19,"seconds":25,"month":7,"timezoneOffset":-480,"year":117,"minutes":59,"time":1502193565619,"day":2},
                            // "zoneid":1,"uid":1,"id":0,"requests":1};
                            JSONObject request = JSONObject.fromObject(message);
                            JSONObject timeObj = request.getJSONObject("addTime");
                            Long zoneid = request.getLong("zoneid"), uid = request.getLong("uid"), time = timeObj.getLong("time");

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(time);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String today = sdf.format(calendar.getTime());

                            String lockPath = zoneid + today;
                            UnionReqts reqts = map.get(lockPath);
                            if (null == reqts) {
                                reqts = new UnionReqts();
                                reqts.setZoneid(zoneid);
                                reqts.setUid(uid);
                                reqts.setAddTime(calendar.getTime());
                                reqts.setReqeusts(0L);
                            }
                            long oldrequests = reqts.getReqeusts() == null ? 0 : reqts.getReqeusts();
                            reqts.setReqeusts(oldrequests + 1);
                            map.put(lockPath, reqts);
                        } catch (Exception e) {
                            logger.error("add error: ", e);
                        }
                    }
                    map.forEach((k, v) -> {
                        // 后期如果量大了就需要分布式处理
                        String lockPath = reqtsPath + k;
                        cacheLockPach.add(lockPath);
                        InterProcessMutex lock = zooutils.buildLock(lockPath);
                        try {
                            if (lock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                                Long zoneid = v.getZoneid(), invalidId = 0L;
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String today = sdf.format(v.getAddTime());

                                UnionReqts temp = cacheUnionReqts.single(zoneid, today);
                                if (null == temp || null == temp.getAddTime() || invalidId.equals(temp.getId())) {
                                    temp = v;
                                } else {
                                    long oldrequests = temp.getReqeusts() == null ? 0 : temp.getReqeusts();
                                    temp.setReqeusts(oldrequests + v.getReqeusts());
                                }
                                temp = unionReqtsService.saveOne(temp);
                                cacheUnionReqts.save(temp);
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
                    logger.error("kafka error: ", e);
                }
            }
        };
        exec.submit(runnable);
        exec.submit(runnable);
        exec.submit(runnable);
    }
}
