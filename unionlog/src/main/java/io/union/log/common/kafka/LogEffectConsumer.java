package io.union.log.common.kafka;

import io.union.log.common.utils.ApplicationConstant;
import io.union.log.entity.LogEffect;
import io.union.log.service.LogEffectService;
import net.sf.json.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2017/7/26.
 */
@Component
public class LogEffectConsumer {
    final Logger logger = LoggerFactory.getLogger(getClass());

    public LogEffectConsumer(@Value("${spring.kafka.bootstrap-servers}") String hosts,
                             @Autowired LogEffectService logEffectService) {
        AtomicBoolean closed = new AtomicBoolean(false);
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        ExecutorService exec = Executors.newCachedThreadPool();

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, ApplicationConstant.EFFECT_COUNT_GROUP);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(ApplicationConstant.EFFECT_COUNT_TOPIC));

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
                    List<LogEffect> list = new ArrayList<>();
                    while (queue.size() != 0) {
                        try {
                            String message = queue.poll(1L, TimeUnit.SECONDS);
                            if (null == message || "".equals(message)) {
                                // Thread.sleep(1000);
                                continue;
                            }
                            //logger.info("queue size: " + queue.size());
                            JSONObject object = JSONObject.fromObject(message);
                            JSONObject addTimeObj = object.getJSONObject("addTime"), regTimeObj = object.getJSONObject("regTime");
                            Long zoneid = object.getLong("zoneid"), affuid = object.getLong("affuid"), planid = object.getLong("planid"), adid = object.getLong("adid"),
                                    siteid = object.getLong("siteid"), advuid = object.getLong("advuid"), addtime = addTimeObj.getLong("time");
                            String affname = object.getString("affname"), advname = object.getString("advname"), planname = object.getString("planname"),
                                    adname = object.getString("adname"), sessionid = object.getString("sessionid"), ip = object.getString("clickip"), regid = null;

                            Object regidObj = object.get("regid");
                            if (null != regidObj) {
                                regid = object.getString("regid");
                            }

                            Calendar addCalendar = Calendar.getInstance();
                            addCalendar.setTimeInMillis(addtime);

                            LogEffect effect = new LogEffect();
                            effect.setAddTime(addCalendar.getTime());
                            effect.setAdid(adid);
                            effect.setAdname(adname);
                            effect.setAdvuid(advuid);
                            effect.setAdvname(advname);
                            effect.setAffuid(affuid);
                            effect.setAffname(affname);
                            effect.setClickip(ip);
                            effect.setPlanid(planid);
                            effect.setPlanname(planname);
                            effect.setSiteid(siteid);
                            effect.setZoneid(zoneid);
                            effect.setSessionid(sessionid);
                            if (regTimeObj != null && !regTimeObj.isEmpty()) {
                                Long regtime = regTimeObj.getLong("time");
                                Calendar regCalendar = Calendar.getInstance();
                                regCalendar.setTimeInMillis(regtime);
                                effect.setRegid(regid);
                                effect.setRegTime(regCalendar.getTime());
                            }
                            list.add(effect);
                        } catch (Exception e) {
                            logger.error("add error: ", e);
                        }
                    }
                    logEffectService.save(list);
                } catch (Exception e) {
                    logger.error("kafka consumer poll error: ", e);
                }
            }
        };
        exec.submit(runnable);
    }
}
