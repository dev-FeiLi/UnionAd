package io.union.admin.service;

import io.union.admin.common.redis.CacheAdsAd;
import io.union.admin.common.redis.CacheAdsPlan;
import io.union.admin.dao.AdsAdRepository;
import io.union.admin.dao.AdsPlanRepository;
import io.union.admin.entity.AdsAd;
import io.union.admin.entity.AdsPlan;
import io.union.admin.entity.AdsUser;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Shell Li on 2017/8/8.
 */
@Service
public class AdsPlanService {
    final Logger LOG = LoggerFactory.getLogger(AdsPlanService.class);

    @Autowired
    private AdsPlanRepository adsPlanRepository;

    @Autowired
    private AdsUserService adsUserService;

    @Autowired
    private AdsAdRepository adsAdRepository;

    @Autowired
    private CacheAdsPlan cacheAdsPlan;

    @Autowired
    private CacheAdsAd cacheAdsAd;

    final static int DELETE_STATUS = 8;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Map<String, List<AdsPlan>> getAllAdsPlan() {
        Map<String, List<AdsPlan>> planTypeMap = new HashMap<String, List<AdsPlan>>();

        Map<Long, AdsPlan> planMap = cacheAdsPlan.map();
        List<AdsPlan> planList = new ArrayList<>();
        if (planMap != null) {
            planList = new ArrayList<>(planMap.values());
        }
        if (planMap == null || planMap.size() == 0) {
            planList = adsPlanRepository.findAll();
        }
        List<AdsPlan> cpmList = new ArrayList<>();
        List<AdsPlan> cpvList = new ArrayList<>();
        List<AdsPlan> cpcList = new ArrayList<>();
        List<AdsPlan> cpaList = new ArrayList<>();
        List<AdsPlan> cpsList = new ArrayList<>();
        planList.forEach(adsPlan -> {
                    int payType = adsPlan.getPayType();
                    switch (payType) {
                        case 1:
                            cpmList.add(adsPlan);
                            break;
                        case 2:
                            cpvList.add(adsPlan);
                            break;
                        case 3:
                            cpcList.add(adsPlan);
                            break;
                        case 4:
                            cpaList.add(adsPlan);
                            break;
                        case 5:
                            cpsList.add(adsPlan);
                            break;
                        default:
                            break;
                    }
                }
        );
        planTypeMap.put("cpmList", cpmList);
        planTypeMap.put("cpvList", cpvList);
        planTypeMap.put("cpcList", cpcList);
        planTypeMap.put("cpaList", cpaList);
        planTypeMap.put("cpsList", cpsList);

        return planTypeMap;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<AdsPlan> findAll(int page, int size) {
        Page<AdsPlan> list = null;
        try {
            if (size == 0) size = 20;
            Sort sort = new Sort(Sort.Direction.DESC, "planId");
            Pageable pageable = new PageRequest(page, size, sort);
            list = adsPlanRepository.findAll(pageable);
        } catch (Exception e) {
            LOG.error("findAll with page error: ", e);
        } finally {
            return list;
        }
    }

    /**
     * 计划列表  计划列表
     *
     * @param type
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<AdsPlan> findAllByPayType(String type, int page, int size) {
        Page<AdsPlan> list = null;
        try {
            if (size == 0) size = 20;
            Sort sort = new Sort(Sort.Direction.DESC, "planId");
            Pageable pageable = new PageRequest(page, size, sort);
            int payType = getPayTypeByParams(type);
            list = adsPlanRepository.findAllPlanByPayType(payType, pageable);
        } catch (Exception e) {
            LOG.error("findAll by paytype error", e);
        } finally {
            return list;
        }
    }

    /**
     * 计划根据 ‘计划名称’-‘计划ID’- ‘广告商ID’查询
     *
     * @param type             计划类型
     * @param value            输入值
     * @param searchCondition
     * @param pstatusCondition 状态条件
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<AdsPlan> findPlansByCondition(String type, String value, String searchCondition, String pstatusCondition, int page, int size) {
        Page<AdsPlan> list = null;
        try {
            if (size == 0) size = 20;
            int payType = getPayTypeByParams(type);
            Sort sort = new Sort(Sort.Direction.DESC, "planId");
            Pageable pageable = new PageRequest(page, size, sort);
            switch (searchCondition) {
                case "planName":
                    if (StringUtils.hasText(pstatusCondition)) {
                        list = payType == 0 ? adsPlanRepository.findAllByTitleContainingAndPstatus(value, Integer.valueOf(pstatusCondition), pageable) :
                                adsPlanRepository.findAllByPayTypeAndTitleContainingAndPstatus(payType, value, Integer.valueOf(pstatusCondition), pageable);
                    } else {
                        list = payType == 0 ? adsPlanRepository.findAllByTitleContaining(value, pageable) :
                                adsPlanRepository.findAllByPayTypeAndTitleContaining(payType, value, pageable);
                    }
                    break;
                case "planId":
                    int planId = Integer.valueOf(value);
                    if (StringUtils.hasText(pstatusCondition)) {
                        list = payType == 0 ? adsPlanRepository.findAllByPlanIdAndPstatus(planId, Integer.valueOf(pstatusCondition), pageable) :
                                adsPlanRepository.findAllByPayTypeAndPlanIdAndPstatus(payType, planId, Integer.valueOf(pstatusCondition), pageable);
                    } else {
                        list = payType == 0 ? adsPlanRepository.findAllByPlanId(planId, pageable) :
                                adsPlanRepository.findAllByPayTypeAndPlanId(payType, planId, pageable);
                    }
                    break;
                case "uid":
                    int uid = Integer.valueOf(value);
                    if (StringUtils.hasText(pstatusCondition)) {
                        list = payType == 0 ? adsPlanRepository.findAllByUidAndPstatus(uid, Integer.valueOf(pstatusCondition), pageable) :
                                adsPlanRepository.findAllByPayTypeAndUidAndPstatus(payType, uid, Integer.valueOf(pstatusCondition), pageable);
                    } else {
                        list = payType == 0 ? adsPlanRepository.findAllByUid(uid, pageable) : adsPlanRepository.findAllByPayTypeAndUid(payType, uid, pageable);
                    }
                    break;
                case "pstatus":
                    int pstatus = Integer.valueOf(value);
                    list = payType == 0 ? adsPlanRepository.findAllByPstatus(pstatus, pageable) :
                            adsPlanRepository.findAllByPayTypeAndPstatus(payType, pstatus, pageable);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            LOG.error("AdsPlanService findPlansByCondition error", e);
        } finally {
            return list;
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsPlan findOne(long planId) {
        AdsPlan plan = null;
        try {
            plan = cacheAdsPlan.singe(planId);
            if (plan == null) {
                plan = adsPlanRepository.findOne(planId);
                if (plan != null && plan.getPlanId() > 0) {
                    cacheAdsPlan.save(plan);
                }
            }
        } catch (Exception e) {
            LOG.error("findOne AdsPlan error: ", e);
        }
        return plan;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public long save(AdsPlan adsPlan) {
        long planId = 0;
        try {
            if (adsPlanRepository.save(adsPlan) != null) {
                cacheAdsPlan.save(adsPlan);
            }
            planId = adsPlan.getPlanId();
        } catch (Exception e) {
            LOG.error("save AdsPlan error: ", e);
        }
        return planId;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<AdsPlan> save(List<AdsPlan> list) {
        try {
            return adsPlanRepository.save(list);
        } catch (Exception e) {
            LOG.error("save error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Map<String, Object> save(JSONObject adsPlanJson) {
        Map<String, Object> map = new HashMap<>();
        List<String> errors = new ArrayList<>();

        if (!StringUtils.hasText(adsPlanJson.getString("title"))) {
            errors.add("计划名称");
        }
        if (!StringUtils.hasText(adsPlanJson.getString("uid"))) {
            errors.add("投放广告商");
        }
        if (!StringUtils.hasText(adsPlanJson.getString("payType"))) {
            errors.add("计费模式");
        }
        if (adsPlanJson.getString("price").equals("{}")) {
            errors.add("单价");
        }
        if (errors != null && errors.size() > 0) {
            map.put("notEmpty", errors);
            return map;
        }

        if (adsPlanJson.getDouble("clickRate") < 0.0d || adsPlanJson.getDouble("clickRate") >= 100d) {
            map.put("error", "点击率不能小于0，或者大于等于100");
            return map;
        }
        if (StringUtils.hasText(adsPlanJson.getString("adsUrl")) && !adsPlanJson.getString("adsUrl").startsWith("http")) {
            map.put("error", "广告地址必须是以http开头");
            return map;
        }

        AdsPlan adsPlan = getAdsPlanFromJosn(adsPlanJson);
        if (adsPlanRepository.save(adsPlan) != null) {
            cacheAdsPlan.save(adsPlan);
        }

        // 更改计划下面的所有广告的ads_url
        if (StringUtils.hasText(adsPlan.getAdsUrl())) {
            List<AdsAd> adsList = adsAdRepository.findAllByPlanidAndAstatusNot(adsPlan.getPlanId(), DELETE_STATUS);
            if (adsList != null && adsList.size() > 0) {
                adsList.forEach(adsAd -> {
                    adsAd.setAdurl(adsPlan.getAdsUrl());
                });
                adsAdRepository.save(adsList);
                for (AdsAd ad : adsList) {
                    cacheAdsAd.save(ad);
                }
            }
        }
        map.put("msg", "success");
        return map;
    }

    /**
     * AdsPlan里表示X个已经单独设置，price中单独设置站长的单价的话，这里加1
     *
     * @param jsonPrice
     * @return
     */
    private int userTotalFromPrice(JSONObject jsonPrice) {
        int userTotal = 0;
        Iterator keys = jsonPrice.keys();
        while (keys.hasNext()) {
            Long key = Long.valueOf(keys.next().toString());
            if (key > 4) {
                userTotal++;
            }
        }
        return userTotal;
    }

    /**
     * "cpm" --> 1
     * "cpv" --> 2
     * "cpc" --> 3
     * "cpa" --> 4
     * "cps" --> 5
     *
     * @param type
     * @return
     */
    private int getPayTypeByParams(String type) {
        String[] types = {"all", "cpm", "cpv", "cpc", "cpa", "cps"};
        List<String> typelist = new ArrayList<>(Arrays.asList(types));
        return typelist.indexOf(type);
    }

    private AdsPlan getAdsPlanFromJosn(JSONObject adsPlanJson) {
        AdsPlan adsPlan = new AdsPlan();

        JSONObject price = adsPlanJson.getJSONObject("price");
        int userTotal = userTotalFromPrice(price);
        if (StringUtils.hasText(adsPlanJson.getString("planId"))) {
            adsPlan.setPlanId(adsPlanJson.getLong("planId"));
            AdsPlan planCache = cacheAdsPlan.singe(adsPlanJson.getLong("planId"));
            adsPlan.setAddTime(planCache.getAddTime());
        } else {
            adsPlan.setAddTime(new Date());
        }
        adsPlan.setTitle(adsPlanJson.getString("title"));
        adsPlan.setPayType(adsPlanJson.getInt("payType"));
        adsPlan.setIsAudit(adsPlanJson.getString("isAudit"));
        adsPlan.setUid(adsPlanJson.getLong("uid"));
        AdsUser user = adsUserService.findOne(adsPlanJson.getLong("uid"));
        adsPlan.setUserName(user.getUsername());

        if (adsPlanJson.getDouble("clickRate") != 0.0d) {
            adsPlan.setClickRate(adsPlanJson.getDouble("clickRate"));
        } else {
            adsPlan.setClickRate(0d);
        }

        adsPlan.setPrice(adsPlanJson.getString("price"));
        adsPlan.setUserTotal(userTotal);
        if (StringUtils.hasText(adsPlanJson.getString("limitMoney"))) {
            adsPlan.setLimitMoney(adsPlanJson.getDouble("limitMoney"));
        } else {
            adsPlan.setLimitMoney(0);
        }
        if (StringUtils.hasText(adsPlanJson.getString("deduction"))) {
            adsPlan.setDeduction(adsPlanJson.getInt("deduction"));
        } else {
            adsPlan.setDeduction(0);
        }
        if (!adsPlanJson.getString("limitTime").equals("{}")) {
            adsPlan.setLimitTime(adsPlanJson.getString("limitTime"));
        }
        if (StringUtils.hasText(adsPlanJson.getString("limitDevice"))) {
            adsPlan.setLimitDevice(adsPlanJson.getString("limitDevice"));
        }
        if (StringUtils.hasText(adsPlanJson.getString("limitUid"))) {
            adsPlan.setLimitUid(adsPlanJson.getString("limitUid"));
        }
        if (StringUtils.hasText(adsPlanJson.getString("limitSite"))) {
            adsPlan.setLimitSite(adsPlanJson.getString("limitSite"));
        }
        adsPlan.setLimitDiv(adsPlanJson.getInt("limitDiv"));
        adsPlan.setLimitDivHeight(adsPlanJson.getInt("limitDivHeight"));
        adsPlan.setLimitPop(adsPlanJson.getInt("limitPop"));

        if (StringUtils.hasText(adsPlanJson.getString("limitType"))) {
            adsPlan.setLimitType(adsPlanJson.getString("limitType"));
        }
        if (StringUtils.hasText(adsPlanJson.getString("startTime"))) {
            String startTime = adsPlanJson.getString("startTime");
            Date startTimeDate = strToDate(DateTimeFormatter.ISO_DATE, startTime);
            if (null != startTimeDate) {
                adsPlan.setStartTime(startTimeDate);
            }
        }
        if (StringUtils.hasText(adsPlanJson.getString("stopTime"))) {
            String stopTime = adsPlanJson.getString("stopTime");
            Date stopTimeDate = strToDate(DateTimeFormatter.ISO_DATE, stopTime);
            if (null != stopTimeDate) {
                adsPlan.setStopTime(stopTimeDate);
            }
        }
        // limitArea 限制地区
        if (adsPlanJson.containsKey("limitArea")) {
            JSONObject limitArea = adsPlanJson.getJSONObject("limitArea");
            Iterator<String> it = limitArea.keys();
            StringBuffer sb = new StringBuffer();
            while (it.hasNext()) {
                String key = it.next();
                sb.append(limitArea.getString(key)).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            adsPlan.setLimitArea(sb.toString());
        }
        // 计划属性增加一个ads_url， by 林的需求。
        adsPlan.setAdsUrl(adsPlanJson.getString("adsUrl"));
        adsPlan.setPriority(adsPlanJson.getInt("priority"));

        if (adsPlan.getPlanId() == null) {
            // 新建计划:状态直接是9
            adsPlan.setPstatus(9);
        } else {
            // 编辑计划, 若该计划广告主是锁定状态，则计划也锁定
            if (user.getUstatus() != 1) {
                adsPlan.setPstatus(9);
            } else {
                adsPlan.setPstatus(adsPlanJson.getInt("pstatus"));
            }
        }
        // 计划增加是否跟踪效果
        if (adsPlanJson.get("isnotify") != null) {
            adsPlan.setIsnotify(adsPlanJson.getString("isnotify"));
        } else {
            adsPlan.setIsnotify("N");
        }
        return adsPlan;
    }

    private Date strToDate(DateTimeFormatter formatter, String datetime) {
        ZoneId zoneId = ZoneId.systemDefault();
        if (DateTimeFormatter.ISO_DATE.equals(formatter)) {
            LocalDate localDate = LocalDate.parse(datetime, formatter);
            ZonedDateTime zonedDateTime = localDate.atStartOfDay(zoneId);
            return Date.from(zonedDateTime.toInstant());
        } else if (DateTimeFormatter.ISO_DATE_TIME.equals(formatter)) {
            LocalDateTime localDateTime = LocalDateTime.parse(datetime, formatter);
            ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
            return Date.from(zonedDateTime.toInstant());
        }
        return null;
    }

    public Map<Long, AdsPlan> findMap() {
        try {
            return cacheAdsPlan.map();
        } catch (Exception e) {
            LOG.error("find map error: ", e);
        }
        return null;
    }
}
