package io.union.log.common.kafka;

import io.union.log.common.redis.CacheLockPach;
import io.union.log.common.redis.CacheStatHourMoney;
import io.union.log.common.utils.ApplicationConstant;
import io.union.log.common.utils.StatHourTool;
import io.union.log.common.zoo.ZooUtils;
import io.union.log.entity.StatHourMoney;
import io.union.log.service.StatHourMoneyService;
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
public class StatHourMoneyConsumer {
    final Logger logger = LoggerFactory.getLogger(getClass());

    public StatHourMoneyConsumer(@Value("${spring.kafka.bootstrap-servers}") String hosts, @Autowired StatHourMoneyService statHourMoneyService, @Autowired CacheStatHourMoney cacheStatHourMoney,
                                 @Autowired ZooUtils zooutils, @Value("${spring.zookeeper.lock.hour.money}") String rootPath, @Autowired CacheLockPach cacheLockPach) {
        AtomicBoolean closed = new AtomicBoolean(false);
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        ExecutorService exec = Executors.newCachedThreadPool();

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, ApplicationConstant.STAT_HOUR_MONEY_COUNT_GROUP);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(ApplicationConstant.STAT_HOUR_MONEY_COUNT_TOPIC));

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
                    Map<String, StatHourMoney> tempmap = new HashMap<>();
                    while (queue.size() != 0) {
                        try {
                            String message = queue.poll(1L, TimeUnit.SECONDS);
                            if (null == message || "".equals(message)) {
                                //Thread.sleep(1000);
                                continue;
                            }
                            //logger.info("queue size: " + queue.size());
                            // message = {"hour18":0,"hour17":0,"hour16":0,"hour15":0,"addTime":null,"hour19":0,"hour10":0,"hour14":0,
                            // "hour13":0,"hour12":0,"hour11":0,"uid":0,"siteid":0,"id":0,"hour1":0,"hour0":0,"hour3":0,"hour2":0,
                            // "hour21":0,"hour20":0,"hour23":0,"hour22":0,"hour9":0,"hour8":0,"planid":0,"hour5":0,"hour4":0,"hour7":0,"hour6":0};
                            JSONObject request = JSONObject.fromObject(message);
                            JSONObject timeObj = request.getJSONObject("addTime");
                            Long uid = request.getLong("uid"), time = timeObj.getLong("time"), siteid = request.getLong("siteid"),
                                    planid = request.getLong("planid");

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(time);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String today = sdf.format(calendar.getTime());
                            int hour = calendar.get(Calendar.HOUR_OF_DAY);
                            Double affprice = request.getDouble("hour" + hour);

                            String key = "" + uid + siteid + planid + today;
                            StatHourMoney statHourMoney = tempmap.get(key);
                            if (null == statHourMoney) {
                                statHourMoney = new StatHourMoney();
                                statHourMoney.setAddTime(calendar.getTime());
                                statHourMoney.setUid(uid);
                                statHourMoney.setSiteid(siteid);
                                statHourMoney.setPlanid(planid);
                            }
                            StatHourTool<StatHourMoney> hourMoneyTool = new StatHourTool<>();
                            statHourMoney = hourMoneyTool.hour(statHourMoney, hour, affprice);
                            tempmap.put(key, statHourMoney);
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
                                Long uid = v.getUid(), siteid = v.getSiteid(), planid = v.getPlanid(), invalidId = 0L;
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(v.getAddTime().getTime());
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String today = sdf.format(calendar.getTime());
                                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                                Double money = 0.0;
                                StatHourTool<StatHourMoney> hourIpTool = new StatHourTool<>();
                                money = hourIpTool.poll(v, hour, Double.class, money);
                                money = null == money ? 0.0 : money;

                                StatHourMoney statHourMoney = cacheStatHourMoney.single(uid, siteid, planid, today);
                                if (null == statHourMoney || null == statHourMoney.getAddTime() || invalidId.equals(statHourMoney.getId())) {
                                    statHourMoney = v;
                                } else {
                                    StatHourTool<StatHourMoney> hourMoneyTool = new StatHourTool<>();
                                    statHourMoney = hourMoneyTool.hour(statHourMoney, hour, money);
                                }
                                statHourMoney = statHourMoneyService.saveOne(statHourMoney);
                                cacheStatHourMoney.save(statHourMoney);
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
