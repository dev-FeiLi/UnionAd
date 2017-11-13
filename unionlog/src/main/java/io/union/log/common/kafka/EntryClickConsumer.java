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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2017/7/26.
 */
@Component
public class EntryClickConsumer {
    final Logger logger = LoggerFactory.getLogger(getClass());

    private final KafkaConsumer<String, String> consumer;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public EntryClickConsumer(@Value("${spring.kafka.bootstrap-servers}") String hosts, @Autowired UnionProducer producer, @Autowired CacheAdsPlan cacheAdsPlan,
                              @Autowired CacheAdsUser cacheAdsUser, @Autowired CacheAdsAd cacheAdsAd, @Autowired CacheAdsZone cacheAdsZone, @Autowired CacheLimitMoney cacheLimitMoney,
                              @Autowired CacheTempCip cacheTempCip, @Autowired CacheTempCuv cacheTempCuv, @Autowired AdsPlanService adsPlanService, @Autowired AdsAdService adsAdService) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hosts);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, ApplicationConstant.CLICK_COUNT_GROUP);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(ApplicationConstant.CLICK_COUNT_TOPIC));

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
                    logger.error("click kafka consumer poll error: ", e);
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
                    //logger.info("click queue size: " + queue.size());
                    // log_visit json
                    // {"brwVersion":"40.0.2214.89","addTime":{"date":31,"hours":15,"seconds":30,"month":6,"timezoneOffset":-480,"year":117,"minutes":43,"time":1501487010300,"day":1},"pageTitle":"思路客_思路客小说网_更新最快的小说站",
                    // "brwUa":"Mozilla/5.0 (Linux; U; Android 7.0; zh-CN; MI 5 Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/40.0.2214.89 UCBrowser/11.6.2.948 Mobile Safari/537.36",
                    // "advprice":0,"sitePage":"http://192.168.199.180:8080/test.html","refererUrl":"","brwName":"ucbrowser","cusIsp":"对方和您在同一内部网","uid":1,"adid":1,"price":0,"siteid":0,
                    // "cusScreen":"360x640","id":0,"cusIp":"192.168.199.120","cusJava":"Y","jsessionid":"691451D5CEAF81C3A013ADF122DF5E70","cusCity":"","clickPos":"116,476","cusOs":"android","cusProvince":"局域网",
                    // "deduction":"N","cusCookie":"Y","cusFlash":"0","zoneid":1,"planid":1,"device":3,"paytype":2}
                    JSONObject object = JSONObject.fromObject(message);
                    JSONObject timeObj = object.getJSONObject("addTime");
                    Long zoneid = object.getLong("zoneid"), userid = object.getLong("uid"), planid = object.getLong("planid"), adid = object.getLong("adid"),
                            siteid = object.getLong("siteid"), time = timeObj.getLong("time"), pvnum = object.getLong("pvnum"), uvnum = object.getLong("uvnum"),
                            urlnum = object.getLong("urlnum"), invalidId = 0L;
                    String brwVersion = object.getString("brwVersion"), brwUa = object.getString("brwUa"), brwName = object.getString("brwName"), cusOs = object.getString("cusOs"),
                            pageTitle = object.getString("pageTitle"), sitePage = object.getString("sitePage"), refererUrl = object.getString("refererUrl"),
                            cusProvince = object.getString("cusProvince"), cusCity = object.getString("cusCity"), cusIsp = object.getString("cusIsp"), cusIp = object.getString("cusIp"),
                            cusScreen = object.getString("cusScreen"), cusJava = object.getString("cusJava"), cusCookie = object.getString("cusCookie"), cusFlash = object.getString("cusFlash"),
                            clickPos = object.getString("clickPos"), jsessionid = object.getString("jsessionid"), doubt = object.getString("doubt");
                    Integer paytype = object.getInt("paytype"), device = object.getInt("device");
                    doubt = "Y".equalsIgnoreCase(doubt) ? "Y" : "N";
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(time);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String today = sdf.format(calendar.getTime());
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);

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
                        uvduplicate = cacheTempCuv.exists(jsessionid, planid, userid, today);
                        ischarthistime = !uvduplicate;
                        if (!uvduplicate) {
                            TempCuv uv = new TempCuv();
                            uv.setAddTime(calendar.getTime());
                            uv.setPlanid(planid);
                            uv.setTempuv(jsessionid);
                            uv.setUid(userid);
                            producer.send(ApplicationConstant.TEMP_CUV_COUNT_TOPIC, JSONObject.fromObject(uv).toString());
                        }
                    } else if (chartype == 2) { // IP计费
                        ipduplicate = cacheTempCip.exists(cusIp, planid, userid, today);
                        ischarthistime = !ipduplicate;
                        if (!ipduplicate) {
                            TempCip ip = new TempCip();
                            ip.setAddTime(calendar.getTime());
                            ip.setPlanid(planid);
                            ip.setTempip(cusIp);
                            ip.setUid(userid);
                            producer.send(ApplicationConstant.TEMP_CIP_COUNT_TOPIC, JSONObject.fromObject(ip).toString());
                        }
                    }
                    // 是否扣量
                    String isdeduction = "N";
                    Double affprice = 0.0, advprice = 0.0;
                    long paynum = 0, dedunum = 0;
                    if (ischarthistime && paytype == 3) {//如果计费方式是cpc的话
                        String userdeduction = user.getDeduction();
                        int plandeduction = plan.getDeduction(), addeduction = ad.getDeduction(), radio = 0;
                        if (null != userdeduction && !"".equals(userdeduction)) {
                            JSONObject deduobj = JSONObject.fromObject(userdeduction);
                            Object o = deduobj.get("cpc");
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
                        // 如果不扣量的话，广告主的余额是否满足扣款
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
                        // 如果不扣量的话，计划是否达到限额
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
                    // 每小时的click量
                    StatHourClick statHourClick = new StatHourClick();
                    statHourClick.setAddTime(calendar.getTime());
                    statHourClick.setUid(userid);
                    statHourClick.setSiteid(siteid);
                    statHourClick.setPlanid(planid);
                    producer.send(ApplicationConstant.STAT_HOUR_CLICK_COUNT_TOPIC, JSONObject.fromObject(statHourClick).toString());

                    // 每小时的clickip量
                    if (chartype == 2 && !ipduplicate) {
                        StatHourClickip statHourClickip = new StatHourClickip();
                        statHourClickip.setAddTime(calendar.getTime());
                        statHourClickip.setUid(userid);
                        statHourClickip.setSiteid(siteid);
                        statHourClickip.setPlanid(planid);
                        producer.send(ApplicationConstant.STAT_HOUR_CLICKIP_COUNT_TOPIC, JSONObject.fromObject(statHourClickip).toString());
                    }
                    // 每小时的clickuv量
                    if (chartype == 1 && !uvduplicate) {
                        StatHourClickuv statHourClickuv = new StatHourClickuv();
                        statHourClickuv.setAddTime(calendar.getTime());
                        statHourClickuv.setUid(userid);
                        statHourClickuv.setSiteid(siteid);
                        statHourClickuv.setPlanid(planid);
                        producer.send(ApplicationConstant.STAT_HOUR_CLICKUV_COUNT_TOPIC, JSONObject.fromObject(statHourClickuv).toString());
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
                    stats.setClicks(1L);
                    stats.setViews(0L);
                    stats.setAddTime(calendar.getTime());
                    stats.setPlanid(planid);
                    stats.setAdid(adid);
                    stats.setSiteid(siteid);
                    stats.setAdvid(plan.getUid());
                    stats.setZoneid(zoneid);
                    stats.setUid(userid);
                    if ((!ipduplicate && chartype == 2) || (!uvduplicate && chartype == 1)) {
                        stats.setClickip(1L);
                    }
                    producer.send(ApplicationConstant.UNION_STATS_COUNT_TOPIC, JSONObject.fromObject(stats).toString());

                    // 因为mysql的索引key长度有限制，log_city表因为有date加到索引中
                    // 所以造成字符串长度有限，在这里先截断
                    if (null != cusProvince && cusProvince.length() > 100) {
                        cusProvince = cusProvince.substring(0, 100);
                    }
                    if (null != cusIsp && cusIsp.length() > 100) {
                        cusIsp = cusIsp.substring(0, 100);
                    }
                    // 保存log_click日志
                    LogClicks clicks = new LogClicks();
                    clicks.setAddTime(calendar.getTime());
                    clicks.setAdid(adid);
                    clicks.setBrwName(brwName);
                    clicks.setBrwUa(brwUa);
                    clicks.setBrwVersion(brwVersion);
                    clicks.setCusCity(cusCity);
                    clicks.setCusCookie(cusCookie);
                    clicks.setCusFlash(cusFlash);
                    clicks.setCusIp(cusIp);
                    clicks.setCusIsp(cusIsp);
                    clicks.setCusJava(cusJava);
                    clicks.setCusOs(cusOs);
                    clicks.setCusProvince(cusProvince);
                    clicks.setCusScreen(cusScreen);
                    clicks.setDeduction(isdeduction);
                    clicks.setJsessionid(jsessionid);
                    clicks.setPageTitle(pageTitle);
                    clicks.setPlanid(planid);
                    clicks.setPaytype(paytype);
                    clicks.setRefererUrl(refererUrl);
                    clicks.setSiteid(siteid);
                    clicks.setSitePage(sitePage);
                    clicks.setUid(userid);
                    clicks.setZoneid(zoneid);
                    clicks.setPrice(affprice);
                    clicks.setAdvprice(advprice);
                    clicks.setDevice(device);
                    clicks.setClickPos(clickPos);
                    clicks.setPvnum(pvnum);
                    clicks.setUvnum(uvnum);
                    clicks.setUrlnum(urlnum);
                    clicks.setDoubt(doubt);
                    producer.send(ApplicationConstant.LOG_CLICKS_COUNT_TOPIC, JSONObject.fromObject(clicks).toString());
                } catch (Exception e) {
                    logger.error("pv consumer error: ", e);
                }
            }
        });
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
