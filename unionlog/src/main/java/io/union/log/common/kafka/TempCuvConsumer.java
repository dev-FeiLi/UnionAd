package io.union.log.common.kafka;

import io.union.log.common.utils.ApplicationConstant;
import io.union.log.entity.TempCuv;
import io.union.log.service.TempCuvService;
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

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2017/7/26.
 */
@Component
public class TempCuvConsumer {
    final Logger logger = LoggerFactory.getLogger(getClass());

    public TempCuvConsumer(@Value("${spring.kafka.bootstrap-servers}") String hosts, @Autowired TempCuvService tempCuvService) {
        AtomicBoolean closed = new AtomicBoolean(false);
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        ExecutorService exec = Executors.newCachedThreadPool();

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, ApplicationConstant.TEMP_CUV_COUNT_GROUP);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(ApplicationConstant.TEMP_CUV_COUNT_TOPIC));

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
                    Map<String, TempCuv> map = new HashMap<>();
                    while (queue.size() != 0) {
                        try {
                            String message = queue.poll(1L, TimeUnit.SECONDS);
                            if (null == message || "".equals(message)) {
                                // Thread.sleep(1000);
                                continue;
                            }
                            //logger.info("queue size: " + queue.size());
                            // message = {"uid":0,"addTime":null,"planid":0,"id":0,"tempuv":""}
                            JSONObject object = JSONObject.fromObject(message);
                            JSONObject timeObj = object.getJSONObject("addTime");
                            Long uid = object.getLong("uid"), time = timeObj.getLong("time"), planid = object.getLong("planid");
                            String tempuv = object.getString("tempuv");

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(time);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String today = sdf.format(calendar.getTime());

                            String key = today + tempuv + uid + planid;

                            TempCuv temp = new TempCuv();
                            temp.setAddTime(calendar.getTime());
                            temp.setPlanid(planid);
                            temp.setTempuv(tempuv);
                            temp.setUid(uid);
                            map.put(key, temp);
                        } catch (Exception e) {
                            logger.error("add error: ", e);
                        }
                    }
                    List<TempCuv> list = new ArrayList<>(map.values());
                    if (list.size() > 0) {
                        tempCuvService.save(list);
                        // cacheTempCuv.save(list);
                    }
                } catch (Exception e) {
                    logger.error("kafka consumer error: ", e);
                }
            }
        };
        exec.submit(runnable);
        exec.submit(runnable);
        exec.submit(runnable);
    }
}
