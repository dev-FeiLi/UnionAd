package io.union.admin.schedule;

import io.union.admin.common.redis.CacheAdsUser;
import io.union.admin.common.redis.CacheLockPach;
import io.union.admin.common.redis.CacheUnionSetting;
import io.union.admin.common.redis.RedisKeys;
import io.union.admin.common.zoo.ZooUtils;
import io.union.admin.entity.AdsAd;
import io.union.admin.entity.AdsAffpay;
import io.union.admin.entity.AdsPlan;
import io.union.admin.entity.AdsUser;
import io.union.admin.service.AdsAdService;
import io.union.admin.service.AdsAffpayService;
import io.union.admin.service.AdsPlanService;
import io.union.admin.service.AdsUserService;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class SystemSchedule {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ExecutorService exec = Executors.newCachedThreadPool();

    private String scheduleEnable;
    private String userPath;
    private ZooUtils zooutils;
    private AdsUserService adsUserService;
    private AdsAdService adsAdService;
    private AdsAffpayService adsAffpayService;
    private AdsPlanService adsPlanService;
    private CacheAdsUser cacheAdsUser;
    private CacheLockPach cacheLockPach;
    private CacheUnionSetting cacheUnionSetting;
    private int defaultMoney;
    private int timeout;

    public SystemSchedule(@Value("${spring.schedule.status}") String scheduleEnable, @Autowired ZooUtils zooutils, @Value("${spring.zookeeper.lock.user}") String userPath,
                          @Value("${spring.zookeeper.timeout}") int timeout, @Value("${clear.money.default}") int defaultMoney, @Autowired CacheUnionSetting cacheUnionSetting,
                          @Autowired CacheAdsUser cacheAdsUser, @Autowired AdsUserService adsUserService, @Autowired CacheLockPach cacheLockPach,
                          @Autowired AdsAffpayService adsAffpayService, @Autowired AdsPlanService adsPlanService, @Autowired AdsAdService adsAdService) {
        this.scheduleEnable = scheduleEnable;
        this.zooutils = zooutils;
        this.userPath = userPath;
        this.timeout = timeout;
        this.defaultMoney = defaultMoney;
        this.adsUserService = adsUserService;
        this.adsAdService = adsAdService;
        this.adsPlanService = adsPlanService;
        this.adsAffpayService = adsAffpayService;
        this.cacheAdsUser = cacheAdsUser;
        this.cacheLockPach = cacheLockPach;
        this.cacheUnionSetting = cacheUnionSetting;
    }

    /**
     * A.对于站长会员，每天凌晨清算昨日的收入，今日收入重置为零，并根据会员的结算类型（日结，周结，月结）<br/>
     * 和结算类型的各自结算日期，还有系统最低结算金额，判断是否给会员结算，结算则生成结算记录<br/>
     * <br/>
     * B.对于广告主会员，每天凌晨就清算昨日的支出，今日支出重置为零<br/>
     * <br/>
     * C.对于计划，因为有限额的规定，限额期限是一天，所以在凌晨就把限额锁定的计划，重置为正常状态
     * <br/>
     * D.因为有导量的因素存在，所以执行时间从0点延后到3点，在结算日期当天，导量必须在3点之前完成
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void updateUserAndPlanEveryWhenZeroClock() {
        try {
            if (!"enable".equals(scheduleEnable)) {
                return;
            }
            Runnable affrunner = () -> {
                logger.info("update aff schedule start");
                try {
                    Page<AdsUser> list = adsUserService.findUustatus(1, 1, 0, Integer.MAX_VALUE);
                    if (null == list || list.getTotalElements() == 0) {
                        return;
                    }
                    LocalDateTime dateTime = LocalDateTime.now();
                    int date = dateTime.getDayOfMonth(); // 当前的日期
                    int week = dateTime.getDayOfWeek().getValue(); //当前周几
                    String dayOfweek = dateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                    // 系统的结算日期和结算金额
                    String weekClear = cacheUnionSetting.single(String.valueOf(RedisKeys.ADS_SETTING_WEEK_DATE)),
                            monthClear = cacheUnionSetting.single(String.valueOf(RedisKeys.ADS_SETTING_MONTH_DATE)),
                            minPay = cacheUnionSetting.single(String.valueOf(RedisKeys.ADS_SETTING_MIN_PAY));
                    int clearMinPay = null == minPay ? defaultMoney : Integer.valueOf(minPay);
                    list.forEach(user -> {
                        try {
                            InterProcessMutex lock = null;
                            Long uid = user.getUid();
                            int paytype = user.getPaytype();
                            double money = 0.0, xmoney = 0.0;
                            try {
                                lock = zooutils.buildLock(userPath + uid);
                                if (lock.acquire(timeout, TimeUnit.SECONDS)) {
                                    money = user.getMoney();
                                    xmoney = user.getXmoney();
                                    user.setDaymoney(money); // 当日的金额变成昨日的金额
                                    user.setMoney(0.0); // 零点则清零当日金额
                                    if (week == 1) { // 如果是每周星期一，清空当周金额
                                        user.setWeekmoney(0.0);
                                    }
                                    if (date == 1) { // 如果是每月1号的话，清空当月金额
                                        user.setMonthmoney(0.0);
                                    }
                                    adsUserService.saveOne(user);
                                    cacheAdsUser.save(user);
                                } else {
                                    logger.error("lock timeout, check the server log");
                                }
                            } catch (Exception e) {
                                logger.error("lock error: ", e);
                            } finally {
                                zooutils.release(lock);
                            }
                            AdsAffpay affpay = new AdsAffpay();
                            affpay.setAddTime(new Date());
                            affpay.setPstatus(0);
                            affpay.setPayinfo("自动结算");
                            affpay.setUid(uid);
                            affpay.setUsername(user.getUsername());
                            if (paytype == 1 && money >= clearMinPay) { // 日结会员
                                affpay.setMoney(money);
                                affpay.setRealmoney(money);
                                affpay.setPaytype(1);
                                adsAffpayService.save(affpay);
                            } else if (paytype == 2 && dayOfweek.equals(weekClear) && xmoney >= clearMinPay) { // 周结会员
                                affpay.setMoney(xmoney);
                                affpay.setRealmoney(xmoney);
                                affpay.setPaytype(2);
                                adsAffpayService.save(affpay);
                            } else if (paytype == 3 && String.valueOf(date).equals(monthClear) && xmoney >= clearMinPay) { // 月结会员
                                affpay.setMoney(xmoney);
                                affpay.setRealmoney(xmoney);
                                affpay.setPaytype(3);
                                adsAffpayService.save(affpay);
                            }
                        } catch (Exception e) {
                            logger.error("conpute error: ", e);
                        }
                    });
                } catch (Exception e) {
                    logger.error("update aff schedule error: ", e);
                }
                logger.info("update aff schedule stop");
            };
            Runnable planrunner = () -> {
                logger.info("update plan schedule start");
                try {
                    Page<AdsPlan> list = adsPlanService.findPlansByCondition("all", "1", "pstatus", null, 0, Integer.MAX_VALUE);
                    if (null == list || list.getSize() == 0) {
                        return;
                    }
                    list.forEach(plan -> {
                        try {
                            plan.setPstatus(0);
                            adsPlanService.save(plan);
                            List<AdsAd> adlist = adsAdService.findAllByPlanIdOutOfStatus(plan.getPlanId(), 1);
                            if (null != adlist && adlist.size() > 0) {
                                for (AdsAd item : adlist) {
                                    item.setAstatus(0);
                                    adsAdService.save(item);
                                }
                            }
                        } catch (Exception e) {
                            logger.error("plan error: ", e);
                        }
                    });
                } catch (Exception e) {
                    logger.error("update plan schedule error: ", e);
                }
                logger.info("update plan schedule stop");
            };
            exec.submit(affrunner);
            exec.submit(planrunner);
            runner(2);
            runner(3);
            runner(4);
        } catch (Exception e) {
            logger.error("schedule error: ", e);
        }
    }

    /**
     * 因为需要zookeeper做分布式锁，所以导致了zookeeper的路径数据积压严重<br/>
     * 这些路径有的只是临时或有效时间是一天而已，所以可以清除掉<br/>
     * 每天在量最小的时间（一般都是凌晨4点至5点之间）启动清除工作<br/>
     * 需要判断每个路径下是否有等待锁的线程，没有的话，就直接删除
     */
    @Scheduled(cron = "0 0 4 * * *")
    public void deleteZookeeperUselessPathWhenFiveClock() {
        logger.info("delete zookeeper data start");
        try {
            Set<String> fields = cacheLockPach.pop();
            if (null != fields) {
                for (String item : fields) {
                    if (!item.contains(userPath)) {
                        zooutils.deleteChild(item);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("delete zookeeper data error: ", e);
        }
        logger.info("delete zookeeper data stop");
    }

    private void runner(final Integer utype) {
        Runnable runner = () -> {
            logger.info("update " + utype + " schedule start");
            try {
                Page<AdsUser> list = adsUserService.findUustatus(1, utype, 0, Integer.MAX_VALUE);
                if (null == list || list.getSize() == 0) {
                    return;
                }
                LocalDateTime dateTime = LocalDateTime.now();
                int date = dateTime.getDayOfMonth(); // 当前的日期
                int week = dateTime.getDayOfWeek().getValue(); //当前周几
                list.forEach(user -> {
                    try {
                        InterProcessMutex lock = null;
                        Long uid = user.getUid();
                        try {
                            lock = zooutils.buildLock(userPath + uid);
                            if (lock.acquire(timeout, TimeUnit.SECONDS)) {
                                double money = user.getMoney();
                                user.setDaymoney(money);
                                user.setMoney(0.0);// 零点则清零当日金额
                                if (week == 1) { // 如果是每周星期一，清空当周金额
                                    user.setWeekmoney(0.0);
                                }
                                if (date == 1) { // 如果是每月1号的话，清空当月金额
                                    user.setMonthmoney(0.0);
                                }
                                adsUserService.saveOne(user);
                                cacheAdsUser.save(user);
                            } else {
                                logger.error("lock timeout, check the server log");
                            }
                        } catch (Exception e) {
                            logger.error("lock error: ", e);
                        } finally {
                            zooutils.release(lock);
                        }
                    } catch (Exception e) {
                        logger.error("conpute error: ", e);
                    }
                });
            } catch (Exception e) {
                logger.error("update " + utype + "  schedule error: ", e);
            }
            logger.info("update " + utype + "  schedule stop");
        };
        exec.submit(runner);
    }
}
