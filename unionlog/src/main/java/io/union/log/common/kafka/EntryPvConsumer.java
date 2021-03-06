package io.union.log.common.kafka;

import io.union.log.common.redis.*;
import io.union.log.common.utils.ApplicationConstant;
import io.union.log.common.utils.StatHourTool;
import io.union.log.entity.*;
import io.union.log.model.UserSettleModel;
import io.union.log.service.AdsAdService;
import io.union.log.service.AdsPlanService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.math.RandomUtils;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2017/7/26.
 */
@Component
public class EntryPvConsumer {
    final Logger logger = LoggerFactory.getLogger(getClass());

    private final KafkaConsumer<String, String> consumer;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public EntryPvConsumer(@Value("${spring.kafka.bootstrap-servers}") String hosts, @Autowired UnionProducer producer, @Autowired CacheAdsPlan cacheAdsPlan,
                           @Autowired CacheAdsUser cacheAdsUser, @Autowired CacheAdsAd cacheAdsAd, @Autowired CacheAdsZone cacheAdsZone,
                           @Autowired CacheLimitMoney cacheLimitMoney, @Autowired CacheTempIp cacheTempIp, @Autowired CacheTempUv cacheTempUv,
                           @Autowired AdsPlanService adsPlanService, @Autowired AdsAdService adsAdService, @Value("${spring.kafka.consumer.threads}") int threads) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, ApplicationConstant.PV_COUNT_GROUP);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 2000);

        // kafka 分区
        //TopicPartition partition = new TopicPartition(ApplicationConstant.PV_COUNT_TOPIC,partitionId);

        consumer = new KafkaConsumer<>(props);
        //consumer.assign(Collections.singletonList(partition));
        consumer.subscribe(Collections.singletonList(ApplicationConstant.PV_COUNT_TOPIC));

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
                    logger.error("pv kafka consumer poll error: ", e);
                }
            }
        });
        Runnable runnable = () -> {
            while (!closed.get()) {
                try {
                    String message = queue.poll(1, TimeUnit.SECONDS);
                    if (null == message || "".equals(message)) {
                        Thread.sleep(5000);
                        continue;
                    }
                    //long starttime = System.currentTimeMillis();
                    //logger.info("pv queue size: " + queue.size());
                    // log_visit json
                    // {"brwVersion":"unknow","addTime":{"date":26,"hours":19,"seconds":37,"month":6,"timezoneOffset":-480,"year":117,"minutes":30,"time":1501068637293,"day":3},
                    // "pageTitle":"思路客_思路客小说网_更新最快的小说站","brwUa":"Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",
                    // "sitePage":"http://192.168.2.103:8080/test.html","refererUrl":"","brwName":"safari","cusIsp":"对方和您在同一内部网","uid":1,"adid":1,"siteid":0,"cusScreen":"320x568",
                    // "id":0,"cusIp":"192.168.2.103","cusJava":"N","jsessionid":"6ECDD231AB9C5A4BE4F092AC016908EF","cusCity":"","cusOs":"iphone","cusProvince":"局域网","deduction":"N",
                    // "cusCookie":"Y","cusFlash":"0","zoneid":1,"planid":1,"paytype":2}
                    JSONObject object = JSONObject.fromObject(message);
                    JSONObject timeObj = object.getJSONObject("addTime");
                    Long zoneid = object.getLong("zoneid"), userid = object.getLong("uid"), planid = object.getLong("planid"), adid = object.getLong("adid"),
                            siteid = object.getLong("siteid"), time = timeObj.getLong("time"), pvnum = object.getLong("pvnum"), uvnum = object.getLong("uvnum"),
                            urlnum = object.getLong("urlnum"), uvpvnum = object.getLong("uvpvnum"), uvipnum = object.getLong("uvipnum"),
                            uvurlnum = object.getLong("uvurlnum"), invalidId = 0L;
                    String brwVersion = object.getString("brwVersion"), brwUa = object.getString("brwUa"), brwName = object.getString("brwName"), cusOs = object.getString("cusOs"),
                            pageTitle = object.getString("pageTitle"), sitePage = object.getString("sitePage"), refererUrl = object.getString("refererUrl"),
                            cusProvince = object.getString("cusProvince"), cusCity = object.getString("cusCity"), cusIsp = object.getString("cusIsp"), cusIp = object.getString("cusIp"),
                            cusScreen = object.getString("cusScreen"), cusJava = object.getString("cusJava"), cusCookie = object.getString("cusCookie"), cusFlash = object.getString("cusFlash"),
                            jsessionid = object.getString("jsessionid"), doubt = object.getString("doubt");

                    // DONE上线前需要删掉这部分
                    // ----------- 这里模拟新的session 主要是为了产生费用-------------------------------------------
                    //jsessionid = RandomStringUtils.random(32, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
                    // ---------------------------------------------------------------------------------------------

                    Integer paytype = object.getInt("paytype"), device = object.getInt("device");
                    doubt = "Y".equalsIgnoreCase(doubt) ? "Y" : "N";
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(time);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String today = sdf.format(calendar.getTime());
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);

                    // ---------------------------- 保存log_browser ----------------------------------------
                    LogBrowser logBrowser = new LogBrowser();
                    logBrowser.setUid(userid);
                    logBrowser.setAddTime(calendar.getTime());
                    logBrowser.setBrwName(brwName);
                    logBrowser.setBrwVersion(brwVersion);
                    logBrowser.setBrwPlat(cusOs);
                    logBrowser.setBrwNum(1L);
                    producer.send(ApplicationConstant.LOG_BROWSER_COUNT_TOPIC, JSONObject.fromObject(logBrowser).toString());

                    // ------------------------------- 保存log_city -----------------------------------------
                    // 因为mysql的索引key长度有限制，log_city表因为有date加到索引中
                    // 所以造成字符串长度有限，在这里先截断
                    if (null != cusProvince && cusProvince.length() > 100) {
                        cusProvince = cusProvince.substring(0, 100);
                    }
                    if (null != cusIsp && cusIsp.length() > 100) {
                        cusIsp = cusIsp.substring(0, 100);
                    }
                    LogCity logCity = new LogCity();
                    logCity.setUid(userid);
                    logCity.setCusProvince(cusProvince);
                    logCity.setCusNum(1L);
                    logCity.setCusIsp(cusIsp);
                    logCity.setCusCity(cusCity);
                    logCity.setAddTime(calendar.getTime());
                    producer.send(ApplicationConstant.LOG_CITY_COUNT_TOPIC, JSONObject.fromObject(logCity).toString());

                    // ------------------------------- 保存log_os -------------------------------------------
                    LogOs logOs = new LogOs();
                    logOs.setUid(userid);
                    logOs.setCusOs(cusOs);
                    logOs.setCusNum(1L);
                    logOs.setAddTime(calendar.getTime());
                    producer.send(ApplicationConstant.LOG_OS_COUNT_TOPIC, JSONObject.fromObject(logOs).toString());

                    // ------------------------------- 保存log_screen ---------------------------------------
                    LogScreen logScreen = new LogScreen();
                    logScreen.setUid(userid);
                    logScreen.setCusScreen(cusScreen);
                    logScreen.setCusNum(1L);
                    logScreen.setAddTime(calendar.getTime());
                    producer.send(ApplicationConstant.LOG_SCREEN_COUNT_TOPIC, JSONObject.fromObject(logScreen).toString());

                    // -------------------------------------------------------------------------------------
                    AdsUser user = cacheAdsUser.single(userid); // 站长
                    if (null == user || !userid.equals(user.getUid())) {
                        logger.error("aff user not found, uid=" + userid);
                        continue;
                    }

                    AdsZone zone = cacheAdsZone.single(zoneid);
                    if (null == zone || !zoneid.equals(zone.getZoneid())) {
                        logger.error("zone not found, zoneid=" + zoneid);
                        continue;
                    }

                    AdsPlan plan = cacheAdsPlan.single(planid);
                    if (null == plan || !planid.equals(plan.getPlanid())) {
                        logger.error("plan not found, planid=" + planid);
                        continue;
                    }

                    AdsUser advuser = cacheAdsUser.single(plan.getUid()); // 广告主
                    if (null == advuser || invalidId.equals(advuser.getUid())) {
                        logger.error("adv user not found, planid=" + planid);
                        continue;
                    }

                    AdsAd ad = cacheAdsAd.single(adid);
                    if (null == ad || !adid.equals(ad.getAdid())) {
                        logger.error("ad not found, adid=" + adid);
                        continue;
                    }

                    // 是按照哪种方式结算，ip 或 uv，该次是否计费
                    int chartype = user.getChartype();
                    boolean ischarthistime = false, ipduplicate = false, uvduplicate = false;
                    if (chartype == 1) { // UV计费
                        uvduplicate = cacheTempUv.exists(jsessionid, planid, userid, today);
                        ischarthistime = !uvduplicate;
                        if (!uvduplicate) {
                            TempUv uv = new TempUv();
                            uv.setAddTime(calendar.getTime());
                            uv.setPlanid(planid);
                            uv.setTempuv(jsessionid);
                            uv.setUid(userid);
                            producer.send(ApplicationConstant.TEMP_UV_COUNT_TOPIC, JSONObject.fromObject(uv).toString());
                        }
                    } else if (chartype == 2) { // IP计费
                        ipduplicate = cacheTempIp.exists(cusIp, planid, userid, today);
                        ischarthistime = !ipduplicate;
                        if (!ipduplicate) {
                            TempIp ip = new TempIp();
                            ip.setAddTime(calendar.getTime());
                            ip.setPlanid(planid);
                            ip.setTempip(cusIp);
                            ip.setUid(userid);
                            producer.send(ApplicationConstant.TEMP_IP_COUNT_TOPIC, JSONObject.fromObject(ip).toString());
                        }
                    }

                    // 是否扣量
                    String isdeduction = "N";
                    Double affprice = 0.0, advprice = 0.0;
                    long paynum = 0, dedunum = 0;
                    if (ischarthistime && paytype == 2) {//如果计费方式是cpv的话
                        String userdeduction = user.getDeduction();
                        int plandeduction = plan.getDeduction(), addeduction = ad.getDeduction(), radio = 0;
                        if (null != userdeduction && !"".equals(userdeduction)) {
                            JSONObject deduobj = JSONObject.fromObject(userdeduction);
                            Object o = deduobj.get("cpv");
                            if (null != o) {
                                radio = Integer.valueOf(String.valueOf(o));
                            }
                        }
                        if (radio == 0) {
                            if (addeduction > 0) {
                                isdeduction = isdeduction(addeduction);
                            } else if (plandeduction > 0) {
                                isdeduction = isdeduction(plandeduction);
                            }
                        } else {
                            isdeduction = isdeduction(radio);
                        }
                        String planprice = plan.getPrice();
                        JSONObject priceObject = JSONObject.fromObject(planprice), userprice;
                        userprice = priceObject.getJSONObject(String.valueOf(userid));
                        if (null == userprice || userprice.size() == 0) {
                            userprice = priceObject.getJSONObject(String.valueOf(device));
                            if (null == userprice || userprice.size() == 0) {
                                logger.error("plan price not setting, planid=" + planid);
                                continue;
                            }
                        }

                        advprice = userprice.getDouble("adv");
                        // 广告主的余额是否满足扣款
                        Double advleftmoney = advuser.getXmoney();
                        double leftmoney = new BigDecimal(advleftmoney).subtract(new BigDecimal(advprice)).doubleValue();
                        if (leftmoney < advprice) { // 无法再扣一次，则改变计划的状态
                            logger.info("adv left money not enough, planid=" + planid);
                            plan.setPstatus(2);
                            plan = adsPlanService.saveOne(plan);
                            cacheAdsPlan.save(plan);
                            // 顺便把该计划名下的广告的状态也修改了
                            List<AdsAd> list = cacheAdsAd.listbypid(planid);
                            if (null != list && list.size() > 0) {
                                for (AdsAd item : list) {
                                    if (item.getAstatus() != 9) {
                                        item.setAstatus(2);
                                    }
                                }
                                list = adsAdService.save(list);
                                cacheAdsAd.save(list);
                            }
                        }
                        // 计划是否达到限额
                        Double limitmoney = plan.getLimitmoney(), sumadvpay = cacheLimitMoney.single(planid, today);
                        double paymoney = new BigDecimal(sumadvpay).add(new BigDecimal(advprice)).doubleValue();
                        if (paymoney >= limitmoney) {// 达到限额则把计划的状态改为锁定或限额
                            plan.setPstatus(1);
                            plan = adsPlanService.saveOne(plan);
                            cacheAdsPlan.save(plan);
                            // 顺便把该计划名下的广告的状态也修改了
                            List<AdsAd> list = cacheAdsAd.listbypid(planid);
                            if (null != list && list.size() > 0) {
                                for (AdsAd item : list) {
                                    if (item.getAstatus() != 9) {
                                        item.setAstatus(1);
                                    }
                                }
                                list = adsAdService.save(list);
                                cacheAdsAd.save(list);
                            }
                        }
                        // 站长实时结算
                        if ("N".equals(isdeduction)) {
                            paynum = 1;
                            dedunum = 0;
                            affprice = userprice.getDouble("aff");
                            UserSettleModel affmodel = new UserSettleModel();
                            affmodel.setAffid(user.getUid());
                            affmodel.setAffprice(affprice);
                            affmodel.setUtype(user.getUtype());
                            affmodel.setAddTime(calendar.getTime());
                            producer.send(ApplicationConstant.AD_SETTLEMENT_TOPIC, JSONObject.fromObject(affmodel).toString());
                        } else {
                            paynum = 0;
                            dedunum = 1;
                        }
                        //广告主实时结算
                        UserSettleModel advmodel = new UserSettleModel();
                        advmodel.setAdvid(advuser.getUid());
                        advmodel.setAdvprice(advprice);
                        advmodel.setUtype(advuser.getUtype());
                        advmodel.setAddTime(calendar.getTime());
                        producer.send(ApplicationConstant.AD_SETTLEMENT_TOPIC, JSONObject.fromObject(advmodel).toString());

                        // 媒介业绩
                        if (null != user.getServiceid() && user.getServiceid() > 0) {
                            AdsUser cususer = cacheAdsUser.single(user.getServiceid());
                            if (null != cususer && cususer.getUid() > 0) {
                                UserSettleModel cusmodel = new UserSettleModel();
                                cusmodel.setAffid(cususer.getUid());
                                cusmodel.setAffprice(affprice);
                                cusmodel.setUtype(cususer.getUtype());
                                cusmodel.setAddTime(calendar.getTime());
                                producer.send(ApplicationConstant.AD_SETTLEMENT_TOPIC, JSONObject.fromObject(cusmodel).toString());
                            }
                        }

                        // 销售业绩
                        if (null != advuser.getServiceid() && advuser.getServiceid() > 0) {
                            AdsUser bussuser = cacheAdsUser.single(advuser.getServiceid());
                            if (null != bussuser && bussuser.getUid() > 0) {
                                UserSettleModel bussmodel = new UserSettleModel();
                                bussmodel.setAdvid(bussuser.getUid());
                                bussmodel.setAdvprice(advprice);
                                bussmodel.setUtype(bussuser.getUtype());
                                bussmodel.setAddTime(calendar.getTime());
                                producer.send(ApplicationConstant.AD_SETTLEMENT_TOPIC, JSONObject.fromObject(bussmodel).toString());
                            }
                        }

                        // 每日限额实时更新
                        cacheLimitMoney.add(planid, today, advprice);

                        // 每小时的结算汇总
                        StatHourMoney hourMoney = new StatHourMoney();
                        hourMoney.setAddTime(calendar.getTime());
                        hourMoney.setUid(userid);
                        hourMoney.setSiteid(siteid);
                        hourMoney.setPlanid(planid);
                        StatHourTool<StatHourMoney> hourMoneyTool = new StatHourTool<>();
                        hourMoney = hourMoneyTool.hour(hourMoney, hour, affprice);
                        producer.send(ApplicationConstant.STAT_HOUR_MONEY_COUNT_TOPIC, JSONObject.fromObject(hourMoney).toString());
                    }
                    // 每小时PV量
                    StatHourPv hourPv = new StatHourPv();
                    hourPv.setAddTime(calendar.getTime());
                    hourPv.setPlanid(planid);
                    hourPv.setSiteid(siteid);
                    hourPv.setUid(userid);
                    producer.send(ApplicationConstant.STAT_HOUR_PV_COUNT_TOPIC, JSONObject.fromObject(hourPv).toString());

                    // 每小时IP量
                    if (chartype == 2 && !ipduplicate) {
                        StatHourIp hourIp = new StatHourIp();
                        hourIp.setAddTime(calendar.getTime());
                        hourIp.setPlanid(planid);
                        hourIp.setSiteid(siteid);
                        hourIp.setUid(userid);
                        producer.send(ApplicationConstant.STAT_HOUR_IP_COUNT_TOPIC, JSONObject.fromObject(hourIp).toString());
                    }
                    // 每小时uv量
                    if (chartype == 1 && !uvduplicate) {
                        StatHourUv hourUv = new StatHourUv();
                        hourUv.setAddTime(calendar.getTime());
                        hourUv.setPlanid(planid);
                        hourUv.setSiteid(siteid);
                        hourUv.setUid(userid);
                        producer.send(ApplicationConstant.STAT_HOUR_UV_COUNT_TOPIC, JSONObject.fromObject(hourUv).toString());
                    }
                    // 每日的结算汇总
                    UnionStats stats = new UnionStats();
                    stats.setSumprofit(new BigDecimal(advprice).subtract(new BigDecimal(affprice)).doubleValue());
                    stats.setSumadvpay(advprice);
                    stats.setSumpay(affprice);
                    stats.setDedunum(dedunum);
                    stats.setPaynum(paynum);
                    stats.setPaytype(paytype);
                    stats.setClickip(0L);
                    stats.setClicks(0L);
                    stats.setViews(1L);
                    stats.setAddTime(calendar.getTime());
                    stats.setPlanid(planid);
                    stats.setAdid(adid);
                    stats.setSiteid(siteid);
                    stats.setAdvid(plan.getUid());
                    stats.setZoneid(zoneid);
                    stats.setUid(userid);
                    producer.send(ApplicationConstant.UNION_STATS_COUNT_TOPIC, JSONObject.fromObject(stats).toString());

                    // 保存log_visit日志
                    LogVisit visit = new LogVisit();
                    visit.setAddTime(calendar.getTime());
                    visit.setAdid(adid);
                    visit.setBrwName(brwName);
                    visit.setBrwUa(brwUa);
                    visit.setBrwVersion(brwVersion);
                    visit.setCusCity(cusCity);
                    visit.setCusCookie(cusCookie);
                    visit.setCusFlash(cusFlash);
                    visit.setCusIp(cusIp);
                    visit.setCusIsp(cusIsp);
                    visit.setCusJava(cusJava);
                    visit.setCusOs(cusOs);
                    visit.setCusProvince(cusProvince);
                    visit.setCusScreen(cusScreen);
                    visit.setDeduction(isdeduction);
                    visit.setJsessionid(jsessionid);
                    visit.setPageTitle(pageTitle);
                    visit.setPlanid(planid);
                    visit.setPaytype(paytype);
                    visit.setRefererUrl(refererUrl);
                    visit.setSiteid(siteid);
                    visit.setSitePage(sitePage);
                    visit.setUid(userid);
                    visit.setZoneid(zoneid);
                    visit.setPrice(affprice);
                    visit.setAdvprice(advprice);
                    visit.setDevice(device);
                    visit.setPvnum(pvnum);
                    visit.setUvnum(uvnum);
                    visit.setUrlnum(urlnum);
                    visit.setUvpvnum(uvpvnum);
                    visit.setUvipnum(uvipnum);
                    visit.setUvurlnum(uvurlnum);
                    visit.setDoubt(doubt);
                    producer.send(ApplicationConstant.LOG_VISIT_COUNT_TOPIC, JSONObject.fromObject(visit).toString());
                } catch (Exception e) {
                    logger.error("pv consumer error: ", e);
                }
            }
        };
        for (int i = 0; i < threads; i++) {
            exec.submit(runnable);
        }
    }

    private String isdeduction(int radio) {
        radio = radio * 10;
        int random = RandomUtils.nextInt(1000);
        if (random < radio) {
            return "Y";
        }
        return "N";
    }
}
