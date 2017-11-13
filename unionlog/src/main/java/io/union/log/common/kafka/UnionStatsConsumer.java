package io.union.log.common.kafka;

import io.union.log.common.redis.CacheAdsUser;
import io.union.log.common.redis.CacheLockPach;
import io.union.log.common.redis.CacheUnionStats;
import io.union.log.common.utils.ApplicationConstant;
import io.union.log.common.zoo.ZooUtils;
import io.union.log.entity.AdsUser;
import io.union.log.entity.UnionStats;
import io.union.log.service.UnionStatsService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.math.NumberUtils;
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
public class UnionStatsConsumer {
    final Logger logger = LoggerFactory.getLogger(getClass());

    public UnionStatsConsumer(@Value("${spring.kafka.bootstrap-servers}") String hosts, @Autowired UnionStatsService unionStatsService, @Autowired CacheAdsUser cacheAdsUser,
                              @Autowired CacheUnionStats cacheUnionStats, @Value("${spring.zookeeper.lock.stats}") String statsPath,
                              @Autowired ZooUtils zooutils, @Autowired CacheLockPach cacheLockPach) {
        AtomicBoolean closed = new AtomicBoolean(false);
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        ExecutorService exec = Executors.newCachedThreadPool();

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, ApplicationConstant.UNION_STATS_COUNT_GROUP);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(ApplicationConstant.UNION_STATS_COUNT_TOPIC));

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
                    Map<String, UnionStats> tempmap = new HashMap<>();
                    while (queue.size() != 0) {
                        try {
                            // 这里不能用take，take会一直阻塞，poll可以设置超时
                            String message = queue.poll(1L, TimeUnit.SECONDS);
                            if (null == message || "".equals(message)) {
                                // Thread.sleep(1000);
                                continue;
                            }
                            // logger.info("queue size: " + queue.size());
                            // message = {"addTime":{"date":8,"hours":19,"seconds":25,"month":7,"timezoneOffset":-480,"year":117,"minutes":59,"time":1502193565619,"day":2},
                            // "clickip":0,"advid":2,"sumprofit":0,"sumadvpay":0,"paynum":0,"uid":1,"adid":1,"dedunum":0,"clicks":0,"siteid":1,"zoneid":1,"planid":1,
                            // "id":0,"sumpay":0,"paytype":1,"views":1};
                            JSONObject request = JSONObject.fromObject(message);
                            JSONObject timeObj = request.getJSONObject("addTime");
                            long zoneid = request.getLong("zoneid"), uid = request.getLong("uid"), advid = request.getLong("advid"),
                                    adid = request.getLong("adid"), planid = request.getLong("planid"), siteid = request.getLong("siteid"),
                                    paynum = request.getLong("paynum"), dedunum = request.getLong("dedunum"), time = timeObj.getLong("time"),
                                    views = request.getLong("views"), clicks = request.getLong("clicks"), clickip = request.getLong("clickip");
                            double sumprofit = request.getDouble("sumprofit"), sumadvpay = request.getDouble("sumadvpay"), sumpay = request.getDouble("sumpay");
                            int paytype = request.getInt("paytype");

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(time);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String today = sdf.format(calendar.getTime());

                            String lockPath = today + uid + zoneid + siteid + advid + planid + adid;
                            UnionStats unionStats = tempmap.get(lockPath);
                            if (null == unionStats) {
                                unionStats = new UnionStats();
                                unionStats.setSumprofit(0.0);
                                unionStats.setSumadvpay(0.0);
                                unionStats.setSumpay(0.0);
                                unionStats.setDedunum(0L);
                                unionStats.setPaynum(0L);
                                unionStats.setPaytype(paytype);
                                unionStats.setClickip(0L);
                                unionStats.setClicks(0L);
                                unionStats.setViews(0L);
                                unionStats.setAddTime(calendar.getTime());
                                unionStats.setPlanid(planid);
                                unionStats.setAdid(adid);
                                unionStats.setSiteid(siteid);
                                unionStats.setAdvid(advid);
                                unionStats.setZoneid(zoneid);
                                unionStats.setUid(uid);
                            }
                            if (paynum > 0) {
                                double oldsumpay = null == unionStats.getSumpay() ? 0.0 : unionStats.getSumpay();
                                unionStats.setSumpay(NumberUtils.createBigDecimal(Double.toString(oldsumpay)).add(NumberUtils.createBigDecimal(Double.toString(sumpay))).doubleValue());
                                long oldpaynum = null == unionStats.getPaynum() ? 0 : unionStats.getPaynum();
                                unionStats.setPaynum(oldpaynum + paynum);
                            } else if (dedunum > 0) {
                                long olddedunum = null == unionStats.getDedunum() ? 0 : unionStats.getDedunum();
                                unionStats.setDedunum(olddedunum + dedunum);
                            }
                            double oldsumadvpay = null == unionStats.getSumadvpay() ? 0.0 : unionStats.getSumadvpay();
                            unionStats.setSumadvpay(NumberUtils.createBigDecimal(Double.toString(oldsumadvpay)).add(NumberUtils.createBigDecimal(Double.toString(sumadvpay))).doubleValue());
                            double oldsumprofit = null == unionStats.getSumprofit() ? 0.0 : unionStats.getSumprofit();
                            unionStats.setSumprofit(NumberUtils.createBigDecimal(Double.toString(oldsumprofit)).add(NumberUtils.createBigDecimal(Double.toString(sumprofit))).doubleValue());
                            long oldviews = null == unionStats.getViews() ? 0 : unionStats.getViews();
                            unionStats.setViews(oldviews + views);
                            long oldclick = null == unionStats.getClicks() ? 0 : unionStats.getClicks();
                            unionStats.setClicks(oldclick + clicks);
                            long oldclickip = null == unionStats.getClickip() ? 0 : unionStats.getClickip();
                            unionStats.setClickip(oldclickip + clickip);
                            tempmap.put(lockPath, unionStats);
                        } catch (Exception e) {
                            logger.error("add settle error: ", e);
                        }
                    }
                    tempmap.forEach((k, v) -> {
                        // 后期如果量大了就需要分布式处理
                        String lockPath = statsPath + k;
                        cacheLockPach.add(lockPath);
                        InterProcessMutex lock = zooutils.buildLock(lockPath);
                        try {
                            if (lock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                                Long uid = v.getUid(), advid = v.getAdvid(), invalidId = 0L;
                                AdsUser affuser = cacheAdsUser.single(uid), advuser = cacheAdsUser.single(advid);
                                if (null == affuser || null == affuser.getUid() || null == advuser || null == advuser.getUid()) {
                                    logger.error("user or adv not found, uid=" + uid + ", adv=" + advid);
                                    return;
                                }
                                if (affuser.getUstatus() != 1 || advuser.getUstatus() != 1) {
                                    logger.error("user status invalid, it's not 1");
                                    return;
                                }
                                long zoneid = v.getZoneid(), siteid = v.getSiteid(), planid = v.getPlanid(), adid = v.getAdid();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String today = sdf.format(v.getAddTime());
                                UnionStats unionStats = cacheUnionStats.single(today, uid, zoneid, siteid, advid, planid, adid);
                                if (null == unionStats || null == unionStats.getAddTime() || invalidId.equals(unionStats.getId())) {
                                    unionStats = v;
                                } else {
                                    double oldsumadvpay = null == unionStats.getSumadvpay() ? 0.0 : unionStats.getSumadvpay();
                                    unionStats.setSumadvpay(NumberUtils.createBigDecimal(Double.toString(oldsumadvpay)).add(NumberUtils.createBigDecimal(Double.toString(v.getSumadvpay()))).doubleValue());
                                    double oldsumpay = null == unionStats.getSumpay() ? 0.0 : unionStats.getSumpay();
                                    unionStats.setSumpay(NumberUtils.createBigDecimal(Double.toString(oldsumpay)).add(NumberUtils.createBigDecimal(Double.toString(v.getSumpay()))).doubleValue());
                                    double oldsumprofit = null == unionStats.getSumprofit() ? 0.0 : unionStats.getSumprofit();
                                    unionStats.setSumprofit(NumberUtils.createBigDecimal(Double.toString(oldsumprofit)).add(NumberUtils.createBigDecimal(Double.toString(v.getSumprofit()))).doubleValue());
                                    long oldpaynum = null == unionStats.getPaynum() ? 0 : unionStats.getPaynum();
                                    unionStats.setPaynum(oldpaynum + v.getPaynum());
                                    long olddedunum = null == unionStats.getDedunum() ? 0 : unionStats.getDedunum();
                                    unionStats.setDedunum(olddedunum + v.getDedunum());
                                    long oldviews = null == unionStats.getViews() ? 0 : unionStats.getViews();
                                    unionStats.setViews(oldviews + v.getViews());
                                    long oldclick = null == unionStats.getClicks() ? 0 : unionStats.getClicks();
                                    unionStats.setClicks(oldclick + v.getClicks());
                                    long oldclickip = null == unionStats.getClickip() ? 0 : unionStats.getClickip();
                                    unionStats.setClickip(oldclickip + v.getClickip());
                                }
                                unionStats = unionStatsService.saveOne(unionStats);
                                cacheUnionStats.save(unionStats);
                            } else {
                                // 获取分布式锁超时，queue中的数据还在，可以继续计算
                                logger.error("lock timeout, must check zookeeper server or other web server status");
                                // 这里可以尝试发邮件，但可能会发生1秒一个邮件，所以需要累计异常次数后才发一次邮件
                            }
                        } catch (Exception e) {
                            logger.error("lock stats error: ", e);
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
