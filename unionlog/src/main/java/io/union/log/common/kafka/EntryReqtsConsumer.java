package io.union.log.common.kafka;

import io.union.log.common.redis.CacheAdsZone;
import io.union.log.common.utils.ApplicationConstant;
import io.union.log.entity.AdsZone;
import io.union.log.entity.StatHourReqs;
import io.union.log.entity.UnionReqts;
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

import java.util.Calendar;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2017/7/26.
 */
@Component
public class EntryReqtsConsumer {
    final Logger logger = LoggerFactory.getLogger(getClass());

    private final KafkaConsumer<String, String> consumer;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public EntryReqtsConsumer(@Value("${spring.kafka.bootstrap-servers}") String hosts, @Autowired UnionProducer producer, @Autowired CacheAdsZone cacheAdsZone) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, ApplicationConstant.REQUEST_COUNT_GROUP);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(ApplicationConstant.REQUEST_COUNT_TOPIC));

        ExecutorService exec = Executors.newCachedThreadPool();
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
                    logger.error("request kafka consumer poll error: ", e);
                }
            }
        });
        exec.submit(() -> {
            while (!closed.get()) {
                try {
                    String message = queue.take();
                    if (null == message || "".equals(message)) {
                        Thread.sleep(5000);
                        continue;
                    }
                    //logger.info("request queue size: " + queue.size());
                    // message = "{"zoneid":1,"time":1654614554564}";
                    JSONObject request = JSONObject.fromObject(message);
                    Long zoneid = request.getLong("zoneid"), time = request.getLong("time"), userid = 0L;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(time);

                    AdsZone zone = cacheAdsZone.single(zoneid);
                    if (null != zone && zoneid.equals(zone.getZoneid())) {
                        userid = zone.getUid();
                    }
                    // 每小时总量
                    StatHourReqs reqs = new StatHourReqs();
                    reqs.setUid(userid);
                    reqs.setZoneid(zoneid);
                    reqs.setAddTime(calendar.getTime());
                    producer.send(ApplicationConstant.STAT_HOUR_REQS_COUNT_TOPIC, JSONObject.fromObject(reqs).toString());

                    // 每天总量
                    UnionReqts reqts = new UnionReqts();
                    reqts.setZoneid(zoneid);
                    reqts.setUid(userid);
                    reqts.setAddTime(calendar.getTime());
                    reqts.setReqeusts(0L);
                    producer.send(ApplicationConstant.UNION_REQTS_COUNT_TOPIC, JSONObject.fromObject(reqts).toString());
                } catch (Exception e) {
                    logger.error("request kafka process error: ", e);
                }
            }
        });
    }
}
