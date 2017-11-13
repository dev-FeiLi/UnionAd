package io.union.admin.service;


import io.union.admin.common.redis.CacheAdsUser;
import io.union.admin.common.redis.CacheLimitMoney;
import io.union.admin.common.zoo.ZooUtils;
import io.union.admin.dao.*;
import io.union.admin.entity.AdsImport;
import io.union.admin.entity.AdsPlan;
import io.union.admin.entity.AdsUser;
import io.union.admin.entity.UnionStats;
import net.sf.json.JSONObject;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Shell Li on 2017/8/16.
 */
@Service
public class AdsImportService {

    final static Logger LOG = LoggerFactory.getLogger(AdsImportService.class);

    @Autowired
    private AdsImportRepository adsImportRepository;
    @Autowired
    private AdsImportRepositoryCustomImpl adsImportRepositoryCustom;
    @Autowired
    private AdsUserRepository adsUserRepository;
    @Autowired
    private AdsPlanRepository adsPlanRepository;
    @Autowired
    private StatsRepository statsRepository;
    @Autowired
    private AffpayRepositoryCustomImpl affpayRepository;
    @Autowired
    private UnionSettingRepository unionSettingRepository;
    @Autowired
    private CacheAdsUser cacheAdsUser;
    @Autowired
    private CacheLimitMoney cacheLimitMoney;

    @Value("${spring.zookeeper.lock.stats}")
    private String statsPath;
    @Value("${spring.zookeeper.lock.user}")
    private String userPath;
    @Autowired
    private ZooUtils zooutils;

    /**
     * 初始化广告列表数据
     *
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<AdsImport> findAllByIstatusNot(Integer istatus, int page, int size) {
        Page<AdsImport> list;
        if (size == 0) size = 20;
        Sort sort = new Sort(Sort.Direction.DESC, "addTime");
        Pageable pageable = new PageRequest(page, size, sort);
        list = adsImportRepository.findAllByIstatusNot(istatus, pageable);
        return list;
    }

    /**
     * 根据id条件查询
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsImport findOne(Long id) {
        try {
            return adsImportRepository.findOne(id);
        } catch (Exception e) {
            LOG.error("find error: ", e);
        }
        return null;
    }

    /**
     * 添加
     *
     * @param adsImport
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Long saveOne(AdsImport adsImport) {
        Long id = 0L;
        try {
            AdsImport adsImport1 = adsImportRepository.save(adsImport);
            if (adsImport1 != null) {
                id = adsImport1.getId();
            }
        } catch (Exception e) {
            LOG.error("save error: ", e);
        }
        return id;
    }

    public List<AdsImport> getAdspayListBySearch(HttpServletRequest request) {
        List<AdsImport> list = new ArrayList<>();
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String searchId = request.getParameter("searchId");
        String searchType = request.getParameter("searchType");
        Map<String, Object> params = new HashMap<>();

        try {
            if (StringUtils.hasText(startDate)) {
                params.put("startDate", new SimpleDateFormat("yyyy-MM-dd").parse(startDate));
            }
            if (StringUtils.hasText(endDate)) {
                params.put("endDate", new SimpleDateFormat("yyyy-MM-dd").parse(endDate));
            }
            if (StringUtils.hasText(searchId)) {
                params.put("searchId", searchId);
            }
            params.put("searchType", searchType);
            list = adsImportRepositoryCustom.findAll(params);
        } catch (Exception e) {
            LOG.error("getAdspayListBySearch", e.getMessage());
        }

        return list;
    }

    /**
     * 刚开始的时候，是不允许跨结算周期导量的，但运营人员有在零点之后导量的习惯，所以必然存在跨周期的情况；<br/>
     * 当然最好的情况是在导量之后判断是否有支付记录，有则修改其“应付”金额；<br/>
     * 但运营人员觉得这个关系不大，可以手动结算，而且在一段时间内整个系统的支付能对得上就好。<br/>
     * 所以最后干脆就去掉跨周期的限制；
     *
     * @param model
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Map<String, Object> saveAdsImport(JSONObject model) {
        Map<String, Object> map = new HashMap<>();
        Object planObj = model.get("planid");
        if (null == planObj) {
            map.put("error", "请选择一个计划");
            return map;
        }
        Object dataObj = model.get("data");
        if (null == dataObj) {
            map.put("error", "导入数据不能为空");
            return map;
        }
        Long planid = model.getLong("planid");
        AdsPlan plan = adsPlanRepository.findOne(planid);
        if (null == plan || null == plan.getPlanId()) {
            map.put("error", "计划不存在");
            return map;
        }
        Long advuid = plan.getUid(), zoneid = 0L, adid = 0L, siteid = 0L, statsId;
        AdsUser advuser = cacheAdsUser.single(advuid);
        if (null == advuser || advuser.getUtype() != 2) {
            map.put("error", "该广告主不存在");
            return map;
        }
        JSONObject price = JSONObject.fromObject(plan.getPrice());
        String errMsg = "";

        String data = model.getString("data");
        List<String> list = new ArrayList<>(Arrays.asList(data.split("\\n")));//以换行符分隔得到数组
        for (String item : list) {
            // 2017-09-08|1001|1000|100000|2
            // 2017-09-08|1001|1000|100000
            String[] array = item.split("\\|");
            if (array.length != 5 && array.length != 4) {
                errMsg += item + ", 格式不对\n";
                continue;
            }
            String date = array[0].trim(), uid = array[1].trim(), affpay = array[2].trim(), advpay = array[3].trim();
            LocalDate targetday = LocalDate.parse(date, DateTimeFormatter.ISO_DATE), today = LocalDate.now(), yestoday = LocalDate.now().minusDays(1);
            Long affuid = Long.valueOf(uid), affpaynum = Long.valueOf(affpay), advpaynum = Long.valueOf(advpay) - affpaynum;
            if (advpaynum <= 0) {
                errMsg += item + ", 广告主结算数不能小于网站主结算数\n";
                continue;
            }
            AdsUser affuser = cacheAdsUser.single(affuid);
            if (null == affuser || affuser.getUtype() != 1) {
                errMsg += item + ", 网站主不存在\n";
                continue;
            }

            double affprice = 0.0, advprice = 0.0;
            if (array.length == 5) {
                String device = array[4].trim();
                Object devicePrice = price.get(device);
                if (null == devicePrice) {
                    errMsg += item + ", 计划不包含该设备单价或会员单价\n";
                    continue;
                }
                JSONObject itemPrice = JSONObject.fromObject(devicePrice);
                affprice = itemPrice.getDouble("aff");
                advprice = itemPrice.getDouble("adv");
            } else {
                Object memberPrice = price.get(uid);
                if (null != memberPrice) {
                    JSONObject itemPrice = price.getJSONObject(uid);
                    affprice = itemPrice.getDouble("aff");
                    advprice = itemPrice.getDouble("adv");
                } else {
                    Iterator<String> iterator = price.keys();
                    if (iterator.hasNext()) {
                        JSONObject itemPrice = price.getJSONObject(iterator.next());
                        affprice = itemPrice.getDouble("aff");
                        advprice = itemPrice.getDouble("adv");
                    }
                }
            }
            double sumpay = new BigDecimal(affprice).multiply(new BigDecimal(affpay)).doubleValue();
            double sumadvpay = new BigDecimal(advprice).multiply(new BigDecimal(advpay)).doubleValue();
            double sumprofit = new BigDecimal(sumadvpay).subtract(new BigDecimal(sumpay)).doubleValue();
            if (sumadvpay > advuser.getXmoney()) {
                errMsg += item + ", 广告主余额不足以扣除该次导量\n";
                continue;
            }
            String lockPath = date + uid + zoneid + siteid + advuid + planid + adid;
            InterProcessMutex statslock = null;
            try {
                statslock = zooutils.buildLock(statsPath + lockPath);
                if (statslock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                    UnionStats unionStats;
                    List<UnionStats> statslist = statsRepository.findAll(affuid, planid, adid, advuid, siteid, zoneid, date);
                    if (null == statslist || statslist.size() == 0) {
                        ZoneId zoneId = ZoneId.systemDefault();
                        ZonedDateTime zdt = targetday.atStartOfDay(zoneId);
                        Date addTime = Date.from(zdt.toInstant());
                        unionStats = new UnionStats();
                        unionStats.setAddTime(addTime);
                        unionStats.setViews(0L);
                        unionStats.setClicks(0L);
                        unionStats.setClickip(0L);
                        unionStats.setAdid(adid);
                        unionStats.setAdvid(advuid);
                        unionStats.setPaytype(2);
                        unionStats.setPlanid(planid);
                        unionStats.setUid(affuid);
                        unionStats.setZoneid(zoneid);
                        unionStats.setSiteid(siteid);
                        unionStats.setDedunum(0L);
                        unionStats.setPaynum(0L);
                        unionStats.setSumpay(0.0);
                        unionStats.setSumadvpay(0.0);
                        unionStats.setSumprofit(0.0);
                    } else {
                        unionStats = statslist.get(0);
                    }
                    unionStats.setViews(unionStats.getViews() + advpaynum);
                    unionStats.setPaynum(unionStats.getPaynum() + affpaynum);
                    unionStats.setDedunum(unionStats.getDedunum() + advpaynum);
                    unionStats.setSumpay(new BigDecimal(unionStats.getSumpay()).add(new BigDecimal(sumpay)).doubleValue());
                    unionStats.setSumadvpay(new BigDecimal(unionStats.getSumadvpay()).add(new BigDecimal(sumadvpay)).doubleValue());
                    unionStats.setSumprofit(new BigDecimal(unionStats.getSumprofit()).add(new BigDecimal(sumprofit)).doubleValue());
                    unionStats = statsRepository.save(unionStats);
                    statsId = unionStats.getId();

                    // 广告主扣款
                    InterProcessMutex advlock = null;
                    try {
                        advlock = zooutils.buildLock(userPath + advuid);
                        if (advlock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                            advuser = cacheAdsUser.single(advuid);
                            if (today.isEqual(targetday)) {
                                advuser.setMoney(new BigDecimal(advuser.getMoney()).add(new BigDecimal(sumadvpay)).doubleValue());
                            }
                            if (yestoday.isEqual(targetday)) {
                                advuser.setDaymoney(new BigDecimal(advuser.getDaymoney()).add(new BigDecimal(sumadvpay)).doubleValue());
                            }
                            LocalDate firstDayOfWeek = today.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
                            if (!firstDayOfWeek.isAfter(targetday)) {
                                advuser.setWeekmoney(new BigDecimal(advuser.getWeekmoney()).add(new BigDecimal(sumadvpay)).doubleValue());
                            }
                            LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
                            if (!firstDayOfMonth.isAfter(targetday)) {
                                advuser.setMonthmoney(new BigDecimal(advuser.getMonthmoney()).add(new BigDecimal(sumadvpay)).doubleValue());
                            }
                            advuser.setXmoney(new BigDecimal(advuser.getXmoney()).subtract(new BigDecimal(sumadvpay)).doubleValue());
                            adsUserRepository.save(advuser);

                            // 网站主收款
                            InterProcessMutex afflock = null;
                            try {
                                afflock = zooutils.buildLock(userPath + affuid);
                                if (afflock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                                    affuser = cacheAdsUser.single(affuid);
                                    if (today.isEqual(targetday)) {
                                        affuser.setMoney(new BigDecimal(affuser.getMoney()).add(new BigDecimal(sumpay)).doubleValue());
                                    }
                                    if (yestoday.isEqual(targetday)) {
                                        affuser.setDaymoney(new BigDecimal(affuser.getDaymoney()).add(new BigDecimal(sumpay)).doubleValue());
                                    }
                                    if (!firstDayOfWeek.isAfter(targetday)) {
                                        affuser.setWeekmoney(new BigDecimal(affuser.getWeekmoney()).add(new BigDecimal(sumpay)).doubleValue());
                                    }
                                    if (!firstDayOfMonth.isAfter(targetday)) {
                                        affuser.setMonthmoney(new BigDecimal(affuser.getMonthmoney()).add(new BigDecimal(sumpay)).doubleValue());
                                    }
                                    affuser.setXmoney(new BigDecimal(affuser.getXmoney()).add(new BigDecimal(sumpay)).doubleValue());
                                    adsUserRepository.save(affuser);

                                    ZoneId zoneId = ZoneId.systemDefault();
                                    ZonedDateTime zonedDateTime = targetday.atStartOfDay(zoneId);
                                    Instant instant = zonedDateTime.toInstant();

                                    AdsImport adsImport = new AdsImport();
                                    adsImport.setAddTime(new Date());
                                    adsImport.setAdvid(advuid);
                                    adsImport.setAdvname(advuser.getUsername());
                                    adsImport.setAdvnum(advpaynum);
                                    adsImport.setAdvprice(advprice);
                                    adsImport.setUid(affuid);
                                    adsImport.setAffname(affuser.getUsername());
                                    adsImport.setAffnum(affpaynum);
                                    adsImport.setAffprice(affprice);
                                    adsImport.setData(item);
                                    adsImport.setIstatus(0);
                                    adsImport.setPlanid(planid);
                                    adsImport.setTitle(plan.getTitle());
                                    adsImport.setStatDate(Date.from(instant));
                                    adsImport.setStatsid(statsId);
                                    adsImport.setSumpay(sumpay);
                                    adsImport.setSumadvpay(sumadvpay);
                                    adsImport.setSumprofit(sumprofit);
                                    adsImportRepository.save(adsImport);

                                    cacheAdsUser.save(advuser);
                                    cacheAdsUser.save(affuser);
                                    cacheLimitMoney.add(planid, date, sumadvpay);
                                } else {
                                    throw new RuntimeException();
                                }
                            } catch (Exception e) {
                                LOG.error("udate aff " + affuid + " error: ", e);
                                throw new RuntimeException();
                            } finally {
                                zooutils.release(afflock);
                            }
                        } else {
                            throw new RuntimeException();
                        }
                    } catch (Exception e) {
                        LOG.error("update adv " + advuid + " error: ", e);
                        throw new RuntimeException();
                    } finally {
                        zooutils.release(advlock);
                    }
                } else {
                    throw new RuntimeException();
                }
            } catch (Exception e) {
                LOG.error("udate stats error: ", e);
                throw new RuntimeException();
            } finally {
                zooutils.release(statslock);
            }
        }
        if (errMsg.length() > 0) {
            map.put("error", errMsg);
        }
        return map;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Map<String, Object> updateStatus(JSONObject model) {
        Map<String, Object> map = new HashMap<>();
        String id = model.getString("id");//importId
        String istatus = model.getString("istatus");//状态
        if (org.apache.commons.lang.StringUtils.isEmpty(id) || org.apache.commons.lang.StringUtils.isEmpty(istatus)) {
            map.put("error", "撤销或删除的记录ID或状态未知");
            return map;
        }
        Long importId = Long.valueOf(id);
        Integer status = Integer.valueOf(istatus);
        AdsImport adsImport = adsImportRepository.findOne(importId);
        if (null == adsImport) {
            map.put("error", "不存在该记录：" + id);
            return map;
        }
        if (status == 8 && adsImport.getIstatus() != 9) {
            map.put("error", "不能直接删除，需先撤销后删除");
            return map;
        }
        Long affuid = adsImport.getUid(), advuid = adsImport.getAdvid(), planid = adsImport.getPlanid(),
                affpaynum = adsImport.getAffnum(), advpaynum = adsImport.getAdvnum(),
                zoneid = 0L, adid = 0L, siteid = 0L, statsId = adsImport.getStatsid();
        double sumpay = adsImport.getSumpay(), sumadvpay = adsImport.getSumadvpay(), sumprofit = adsImport.getSumprofit();

        if (status == 8) {
            adsImport.setIstatus(8);
            adsImportRepository.save(adsImport);
            return map;
        }
        if (status == 9) {
            if (adsImport.getIstatus() == 9) {
                map.put("error", "该记录已经撤销了，只能删除");
                return map;
            }
            Date statDate = adsImport.getStatDate();
            Instant instant = statDate.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDate targetday = instant.atZone(zoneId).toLocalDate(), today = LocalDate.now(), yestoday = LocalDate.now().minusDays(1);
            String date = targetday.format(DateTimeFormatter.ISO_DATE);
            AdsUser affuser = cacheAdsUser.single(affuid);
            if (null == affuser) {
                map.put("error", "网站主不存在");
                return map;
            }

            String lockPath = date + affuid + zoneid + siteid + advuid + planid + adid;
            InterProcessMutex statslock = null;
            try {
                statslock = zooutils.buildLock(statsPath + lockPath);
                if (statslock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                    UnionStats unionStats = statsRepository.findOne(statsId);
                    if (null == unionStats) {
                        map.put("error", "不存在该汇总数据：" + statsId);
                        return map;
                    }
                    unionStats.setPaynum(unionStats.getPaynum() - affpaynum);
                    unionStats.setDedunum(unionStats.getDedunum() - advpaynum);
                    unionStats.setSumpay(new BigDecimal(unionStats.getSumpay()).subtract(new BigDecimal(sumpay)).doubleValue());
                    unionStats.setSumadvpay(new BigDecimal(unionStats.getSumadvpay()).subtract(new BigDecimal(sumadvpay)).doubleValue());
                    unionStats.setSumprofit(new BigDecimal(unionStats.getSumprofit()).subtract(new BigDecimal(sumprofit)).doubleValue());
                    statsRepository.save(unionStats);

                    InterProcessMutex afflock = null;
                    try {
                        afflock = zooutils.buildLock(userPath + affuid);
                        if (afflock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                            affuser = cacheAdsUser.single(affuid);
                            if (today.isEqual(targetday)) {
                                affuser.setMoney(new BigDecimal(affuser.getMoney()).subtract(new BigDecimal(sumpay)).doubleValue());
                            }
                            if (yestoday.isEqual(targetday)) {
                                affuser.setDaymoney(new BigDecimal(affuser.getDaymoney()).subtract(new BigDecimal(sumpay)).doubleValue());
                            }
                            LocalDate firstDayOfWeek = today.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
                            if (!firstDayOfWeek.isAfter(targetday)) {
                                affuser.setWeekmoney(new BigDecimal(affuser.getWeekmoney()).subtract(new BigDecimal(sumpay)).doubleValue());
                            }
                            LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
                            if (!firstDayOfMonth.isAfter(targetday)) {
                                affuser.setMonthmoney(new BigDecimal(affuser.getMonthmoney()).subtract(new BigDecimal(sumpay)).doubleValue());
                            }
                            affuser.setXmoney(new BigDecimal(affuser.getXmoney()).subtract(new BigDecimal(sumpay)).doubleValue());
                            adsUserRepository.save(affuser);

                            InterProcessMutex advlock = null;
                            try {
                                advlock = zooutils.buildLock(userPath + advuid);
                                if (advlock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                                    AdsUser advuser = cacheAdsUser.single(advuid);
                                    if (today.isEqual(targetday)) {
                                        advuser.setMoney(new BigDecimal(advuser.getMoney()).subtract(new BigDecimal(sumadvpay)).doubleValue());
                                    }
                                    if (yestoday.isEqual(targetday)) {
                                        advuser.setDaymoney(new BigDecimal(advuser.getDaymoney()).subtract(new BigDecimal(sumadvpay)).doubleValue());
                                    }
                                    if (!firstDayOfWeek.isAfter(targetday)) {
                                        advuser.setWeekmoney(new BigDecimal(advuser.getWeekmoney()).subtract(new BigDecimal(sumadvpay)).doubleValue());
                                    }
                                    if (!firstDayOfMonth.isAfter(targetday)) {
                                        advuser.setMonthmoney(new BigDecimal(advuser.getMonthmoney()).subtract(new BigDecimal(sumadvpay)).doubleValue());
                                    }
                                    advuser.setXmoney(new BigDecimal(advuser.getXmoney()).add(new BigDecimal(sumadvpay)).doubleValue());
                                    adsUserRepository.save(advuser);

                                    adsImport.setIstatus(9);
                                    adsImportRepository.save(adsImport);

                                    cacheAdsUser.save(advuser);
                                    cacheAdsUser.save(affuser);
                                    cacheLimitMoney.subtract(planid, date, sumadvpay);
                                } else {
                                    throw new RuntimeException();
                                }
                            } catch (Exception e) {
                                LOG.error("reset adv " + advuid + " error: ", e);
                                throw new RuntimeException();
                            } finally {
                                zooutils.release(advlock);
                            }
                        } else {
                            throw new RuntimeException();
                        }
                    } catch (Exception e) {
                        LOG.error("reset aff " + affuid + " error: ", e);
                        throw new RuntimeException();
                    } finally {
                        zooutils.release(afflock);
                    }
                } else {
                    throw new RuntimeException();
                }
            } catch (Exception e) {
                LOG.error("reset unionStats error: ", e);
                throw new RuntimeException();
            } finally {
                zooutils.release(statslock);
            }
        }
        return map;
    }

    // 判断导入的时间是否不会超过结算时间
    /*public boolean type(Integer paytype, LocalDate targetdate) {
        UnionSetting weekClearSetting = unionSettingRepository.findOne(String.valueOf(RedisKeys.ADS_SETTING_WEEK_DATE)),
                monthClearSetting = unionSettingRepository.findOne(String.valueOf(RedisKeys.ADS_SETTING_MONTH_DATE));
        String weekClear = weekClearSetting.getSetvalue(), monthClear = monthClearSetting.getSetvalue();
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(weekClear.toUpperCase());
        LocalDate weekday = today.with(TemporalAdjusters.previousOrSame(dayOfWeek));
        LocalDate monthday = today.withDayOfMonth(Integer.valueOf(monthClear));
        today = paytype == 2 ? weekday : (paytype == 3 ? monthday : today);
        return today.isAfter(targetdate);
    }*/
}

