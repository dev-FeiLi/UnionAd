package io.union.log.common.kafka;

import io.union.log.common.redis.CacheAdsUser;
import io.union.log.common.utils.ApplicationConstant;
import io.union.log.common.zoo.ZooUtils;
import io.union.log.entity.AdsUser;
import io.union.log.model.UserProfitModel;
import io.union.log.service.AdsUserService;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2017/7/26.
 */
@Component
public class UnionSettleConsumer {
    final Logger logger = LoggerFactory.getLogger(getClass());

    public UnionSettleConsumer(@Value("${spring.kafka.bootstrap-servers}") String hosts, @Autowired AdsUserService adsUserService, @Autowired CacheAdsUser cacheAdsUser,
                               @Autowired ZooUtils zooutils, @Value("${spring.zookeeper.lock.user}") String userPath) {
        AtomicBoolean closed = new AtomicBoolean(false);
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        ExecutorService exec = Executors.newCachedThreadPool();

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, ApplicationConstant.AD_SETTLEMENT_GROUP);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);


        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(ApplicationConstant.AD_SETTLEMENT_TOPIC));

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
                    Map<Long, UserProfitModel> userMap = new HashMap<>();
                    while (queue.size() != 0) {
                        try {
                            // 这里不能用take，take会一直阻塞，poll可以设置超时
                            String message = queue.poll(1L, TimeUnit.SECONDS);
                            if (null == message || "".equals(message)) {
                                // Thread.sleep(1000);
                                continue;
                            }
                            //logger.info("queue size: " + queue.size());
                            // message = {"affprice":0,"affid":0,"advprice":0,"advid":0, "addTime":null, "utype": 1};
                            JSONObject request = JSONObject.fromObject(message);
                            Long affid = request.getLong("affid"), advid = request.getLong("advid");
                            double affprice = request.getDouble("affprice"), advprice = request.getDouble("advprice");
                            int utype = request.getInt("utype");
                            if (utype == 1 || utype == 3) { // 站长结算
                                UserProfitModel user = userMap.get(affid);
                                if (null == user) {
                                    user = new UserProfitModel();
                                    user.setUid(affid);
                                    user.setUtype(utype);
                                }
                                double affmoney = new BigDecimal(Double.toString(user.getMoney())).add(new BigDecimal(Double.toString(affprice))).doubleValue();
                                double affweekmoney = new BigDecimal(Double.toString(user.getWeekmoney())).add(new BigDecimal(Double.toString(affprice))).doubleValue();
                                double affmonthmoney = new BigDecimal(Double.toString(user.getMonthmoney())).add(new BigDecimal(Double.toString(affprice))).doubleValue();
                                double affxmoney = new BigDecimal(Double.toString(user.getXmoney())).add(new BigDecimal(Double.toString(affprice))).doubleValue();
                                user.setMoney(affmoney);
                                user.setWeekmoney(affweekmoney);
                                user.setMonthmoney(affmonthmoney);
                                user.setXmoney(affxmoney);
                                userMap.put(affid, user);
                            } else if (utype == 2 || utype == 4) { // 广告主结算
                                UserProfitModel user = userMap.get(advid);
                                if (null == user) {
                                    user = new UserProfitModel();
                                    user.setUid(advid);
                                    user.setUtype(utype);
                                }
                                double advmooney = new BigDecimal(Double.toString(user.getMoney())).add(new BigDecimal(Double.toString(advprice))).doubleValue();
                                double advweekmonty = new BigDecimal(Double.toString(user.getWeekmoney())).add(new BigDecimal(Double.toString(advprice))).doubleValue();
                                double advmonthmoney = new BigDecimal(Double.toString(user.getMonthmoney())).add(new BigDecimal(Double.toString(advprice))).doubleValue();
                                double advxmoney = new BigDecimal(Double.toString(user.getXmoney())).add(new BigDecimal(Double.toString(advprice))).doubleValue();
                                user.setMoney(advmooney);
                                user.setWeekmoney(advweekmonty);
                                user.setMonthmoney(advmonthmoney);
                                user.setXmoney(advxmoney);
                                userMap.put(advid, user);
                            }
                        } catch (Exception e) {
                            logger.error("add price error: ", e);
                        }
                    }
                    userMap.forEach((k, v) -> {
                        // 这个涉及到后台定时任务的计算，如果缓存在本地的话，有可能造成数据错乱
                        // 这里加入分布式锁，同时后台和官网都需要使用该分布锁，防止用户数据错乱
                        InterProcessMutex lock = zooutils.buildLock(userPath + k);
                        try {
                            if (lock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                                AdsUser user = cacheAdsUser.single(k);
                                if (null == user || null == user.getUid() || user.getUid() == 0) {
                                    logger.error("user not found, uid=" + k);
                                    return;
                                }
                                if (user.getUstatus() != 1) {
                                    logger.error("user status error, uid=" + k);
                                    return;
                                }
                                if (user.getUtype() == 1 || user.getUtype() == 3) { // 站长累计
                                    Double money = user.getMoney(), weekmoney = user.getWeekmoney(), monthmoney = user.getMonthmoney(), xmoney = user.getXmoney();
                                    money = null == money ? 0.0 : new BigDecimal(Double.toString(money)).add(new BigDecimal(Double.toString(v.getMoney()))).doubleValue();
                                    weekmoney = null == weekmoney ? 0.0 : new BigDecimal(Double.toString(weekmoney)).add(new BigDecimal(Double.toString(v.getWeekmoney()))).doubleValue();
                                    monthmoney = null == monthmoney ? 0.0 : new BigDecimal(Double.toString(monthmoney)).add(new BigDecimal(Double.toString(v.getMonthmoney()))).doubleValue();
                                    xmoney = (null == xmoney || user.getUtype() == 3) ? 0.0 : new BigDecimal(Double.toString(xmoney)).add(new BigDecimal(Double.toString(v.getXmoney()))).doubleValue();
                                    user.setMoney(money);
                                    user.setWeekmoney(weekmoney);
                                    user.setMonthmoney(monthmoney);
                                    user.setXmoney(xmoney);
                                    adsUserService.saveOne(user);
                                    cacheAdsUser.save(user);
                                } else if (user.getUtype() == 2 || user.getUtype() == 4) { // 广告主累计
                                    Double money = user.getMoney(), weekmoney = user.getWeekmoney(), monthmoney = user.getMonthmoney(), xmoney = user.getXmoney(), leftmoney;
                                    money = null == money ? 0.0 : new BigDecimal(Double.toString(money)).add(new BigDecimal(Double.toString(v.getMoney()))).doubleValue();
                                    weekmoney = null == weekmoney ? 0.0 : new BigDecimal(Double.toString(weekmoney)).add(new BigDecimal(Double.toString(v.getWeekmoney()))).doubleValue();
                                    monthmoney = null == monthmoney ? 0.0 : new BigDecimal(Double.toString(monthmoney)).add(new BigDecimal(Double.toString(v.getMonthmoney()))).doubleValue();
                                    xmoney = (null == xmoney || user.getUtype() == 4) ? 0.0 : new BigDecimal(Double.toString(xmoney)).subtract(new BigDecimal(Double.toString(v.getXmoney()))).doubleValue();
                                    leftmoney = Math.max(xmoney, 0);
                                    user.setMoney(money);
                                    user.setWeekmoney(weekmoney);
                                    user.setMonthmoney(monthmoney);
                                    user.setXmoney(leftmoney);
                                    adsUserService.saveOne(user);
                                    cacheAdsUser.save(user);
                                }
                            } else {
                                // 获取分布式锁超时，queue中的数据还在，可以继续计算
                                logger.error("lock timeout, must check zookeeper server or other web server status");
                                // 这里可以尝试发邮件，但可能会发生1秒一个邮件，所以需要累计异常次数后才发一次邮件
                            }
                        } catch (Exception e) {
                            logger.error("lock user error: ", e);
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
