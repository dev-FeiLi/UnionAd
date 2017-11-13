package io.union.log.common.kafka;

import io.union.log.common.redis.CacheUnionSetting;
import io.union.log.common.redis.RedisKeys;
import io.union.log.common.utils.ApplicationConstant;
import io.union.log.common.utils.Tools;
import io.union.log.entity.LogClicks;
import io.union.log.service.LogClicksService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
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
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2017/7/26.
 */
@Component
public class LogClicksConsumer {
    final Logger logger = LoggerFactory.getLogger(getClass());

    public LogClicksConsumer(@Value("${spring.kafka.bootstrap-servers}") String hosts,
                             @Autowired LogClicksService logClicksService,
                             @Autowired CacheUnionSetting cacheUnionSetting) {
        AtomicBoolean closed = new AtomicBoolean(false);
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        ExecutorService exec = Executors.newCachedThreadPool();

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, ApplicationConstant.LOG_CLICKS_COUNT_GROUP);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(ApplicationConstant.LOG_CLICKS_COUNT_TOPIC));

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
                    List<LogClicks> list = new ArrayList<>();
                    while (queue.size() != 0) {
                        try {
                            String message = queue.poll(1L, TimeUnit.SECONDS);
                            if (null == message || "".equals(message)) {
                                // Thread.sleep(1000);
                                continue;
                            }
                            //logger.info("queue size: " + queue.size());
                            JSONObject object = JSONObject.fromObject(message);
                            JSONObject timeObj = object.getJSONObject("addTime");
                            Long zoneid = object.getLong("zoneid"), userid = object.getLong("uid"), planid = object.getLong("planid"), adid = object.getLong("adid"),
                                    siteid = object.getLong("siteid"), time = timeObj.getLong("time"), pvnum = object.getLong("pvnum"), uvnum = object.getLong("uvnum"),
                                    urlnum = object.getLong("urlnum");
                            String brwVersion = object.getString("brwVersion"), brwUa = object.getString("brwUa"), brwName = object.getString("brwName"), cusOs = object.getString("cusOs"),
                                    pageTitle = object.getString("pageTitle"), sitePage = object.getString("sitePage"), refererUrl = object.getString("refererUrl"),
                                    cusProvince = object.getString("cusProvince"), cusCity = object.getString("cusCity"), cusIsp = object.getString("cusIsp"), cusIp = object.getString("cusIp"),
                                    cusScreen = object.getString("cusScreen"), cusJava = object.getString("cusJava"), cusCookie = object.getString("cusCookie"), cusFlash = object.getString("cusFlash"),
                                    clickPos = object.getString("clickPos"), jsessionid = object.getString("jsessionid"), isdeduction = object.getString("deduction"), doubt = object.getString("doubt");
                            Integer paytype = object.getInt("paytype"), device = object.getInt("device");
                            Double affprice = object.getDouble("price"), advprice = object.getDouble("advprice");

                            doubt = "Y".equalsIgnoreCase(doubt) ? "Y" : "N";

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(time);

                            LogClicks clicks = new LogClicks();
                            clicks.setAddTime(calendar.getTime());
                            clicks.setAdid(adid);
                            clicks.setBrwName(Tools.cutdown(brwName));
                            clicks.setBrwUa(Tools.cutdown(brwUa));
                            clicks.setBrwVersion(Tools.cutdown(brwVersion));
                            clicks.setCusProvince(Tools.cutdown(cusProvince));
                            clicks.setCusCity(Tools.cutdown(cusCity));
                            clicks.setCusIsp(Tools.cutdown(cusIsp));
                            clicks.setCusCookie(cusCookie);
                            clicks.setCusFlash(cusFlash);
                            clicks.setCusIp(Tools.cutdown(cusIp));
                            clicks.setCusJava(cusJava);
                            clicks.setCusOs(cusOs);
                            clicks.setCusScreen(cusScreen);
                            clicks.setDeduction(isdeduction);
                            clicks.setJsessionid(jsessionid);
                            clicks.setRefererUrl(Tools.cutdown(refererUrl));
                            clicks.setPageTitle(Tools.cutdown(pageTitle));
                            clicks.setSitePage(Tools.cutdown(sitePage));
                            clicks.setPlanid(planid);
                            clicks.setPaytype(paytype);
                            clicks.setUid(userid);
                            clicks.setSiteid(siteid);
                            clicks.setZoneid(zoneid);
                            clicks.setPrice(new BigDecimal(String.valueOf(affprice)).doubleValue());
                            clicks.setAdvprice(new BigDecimal(String.valueOf(advprice)).doubleValue());
                            clicks.setDevice(device);
                            clicks.setClickPos(clickPos);
                            clicks.setPvnum(pvnum);
                            clicks.setUvnum(uvnum);
                            clicks.setUrlnum(urlnum);
                            clicks.setDoubt(doubt);
                            list.add(clicks);
                        } catch (Exception e) {
                            logger.error("add error: ", e);
                        }
                    }
                    // 由于云服务器的硬盘IO限制，当并发高到一定程度的时候，硬盘IO成为瓶颈，影响了数据库的同步
                    // 这个时候，就要把日志记录的保存给停掉，空出空间给其他计算
                    String value = cacheUnionSetting.single(String.valueOf(RedisKeys.ADS_SETTING_LOG_CLICK));
                    if (list.size() > 0 && !StringUtils.equalsIgnoreCase(value, "N")) {
                        logClicksService.save(list);
                    }
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
