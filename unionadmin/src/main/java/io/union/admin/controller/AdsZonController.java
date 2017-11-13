package io.union.admin.controller;

import io.union.admin.common.redis.CacheAdsAd;
import io.union.admin.common.redis.CacheAdsZone;
import io.union.admin.entity.AdsAd;
import io.union.admin.entity.AdsPlan;
import io.union.admin.entity.AdsUser;
import io.union.admin.entity.AdsZone;
import io.union.admin.service.AdsAdService;
import io.union.admin.service.AdsPlanService;
import io.union.admin.service.AdsUserService;
import io.union.admin.service.AdsZoneService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by chenmiaoyun on 2017/8/14.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
@RequestMapping(value = "/manage")
public class AdsZonController {
    final static Logger LOG = LoggerFactory.getLogger(AdsZonController.class);
    @Autowired
    private AdsZoneService adsZoneService;
    @Autowired
    private AdsUserService adsUserService;
    @Autowired
    private CacheAdsZone cacheAdsZone;
    @Autowired
    private AdsAdService adsAdService;
    @Autowired
    private AdsPlanService adsPlanService;
    @Autowired
    private CacheAdsAd cacheAdsAd;

    /**
     * 广告位页面
     *
     * @return
     */
    @RequestMapping(value = "/adsZone", method = {RequestMethod.GET})
    public ModelAndView AdsZoneList(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("affzone");
        Set<String> sizeset = new HashSet<>();
        try {
            String uid = request.getParameter("uid");
            if (org.springframework.util.StringUtils.hasText(uid)) {
                mav.addObject("uid", uid);
            }
            List<AdsZone> list = adsZoneService.findAll();
            for (AdsZone adsZone : list) {
                String size = adsZone.getWidth() + "x" + adsZone.getHeight();
                sizeset.add(size);
            }
            mav.addObject("AdsZoneList", list);
            mav.addObject("sizes", sizeset);
        } catch (Exception e) {
            LOG.error("AdsZone error: ", e);
        }
        return mav;
    }

    /**
     * 有条件就根据条件进行模糊查询，没条件就查询出全部的信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/adsZoneList", method = {RequestMethod.GET, RequestMethod.POST})
    public JSONObject planDataSearch(HttpServletRequest request) {
        JSONObject jsonShow = new JSONObject();
        try {
            String searchCondition = request.getParameter("searchCondition");
            int page = Integer.valueOf(request.getParameter("page"));
            int size = Integer.valueOf(request.getParameter("size"));
            String searchValue = request.getParameter("searchValue");
            String viewtype = request.getParameter("viewtype");
            if (org.springframework.util.StringUtils.hasText(searchCondition) && !"0".equals(searchValue)) {
                jsonShow = adsZoneService.findAdsZoneByCondition(viewtype, searchValue, 8, searchCondition, page, size);
            } else {
                jsonShow = adsZoneService.findAll(8, page, size);
            }
        } catch (Exception e) {
            LOG.error("AdsSiteList error", e);
        } finally {
            return jsonShow;
        }
    }

    /**
     * 根据状态查询出包含该状态的信息
     *
     * @return
     */
    @RequestMapping(value = "/findZstatus", method = {RequestMethod.GET, RequestMethod.POST})
    public JSONObject findSstatus(HttpServletRequest request) {
        JSONObject jsonShow = new JSONObject();
        try {
            String zstatus = request.getParameter("zstatus");
            int page = Integer.valueOf(request.getParameter("page"));
            int size = Integer.valueOf(request.getParameter("size"));
            jsonShow = adsZoneService.findByZstatus(Integer.valueOf(zstatus), page, size);
        } catch (Exception e) {
            LOG.error("findZstatus error", e);
        }
        return jsonShow;
    }

    /**
     * 根据计费模式去查询广告位数据
     *
     * @return
     */
    @RequestMapping(value = "/findByPaytype", method = {RequestMethod.GET, RequestMethod.POST})
    public JSONObject findByPaytype(HttpServletRequest request) {
        JSONObject jsonShow = new JSONObject();
        try {
            String paytype = request.getParameter("paytype");
            int page = Integer.valueOf(request.getParameter("page"));
            int size = Integer.valueOf(request.getParameter("size"));
            jsonShow = adsZoneService.findByPaytype(paytype, 8, page, size);
        } catch (Exception e) {
            LOG.error("findByPaytype error", e);
        }
        return jsonShow;
    }

    /**
     * 添加广告位于修改广告位
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/addZone"}, method = {RequestMethod.POST})
    public String addZone(HttpServletRequest request) {
        String jsonResult = "success";
        Map<String, String> result = new HashMap<>();
        result.put("result", jsonResult);
        try {
            String zoneid = request.getParameter("zoneid"), zonename = request.getParameter("zonename"),
                    paytype = request.getParameter("paytype"), viewtype = request.getParameter("viewtype"),
                    sizetype = request.getParameter("sizetype"), uid = request.getParameter("uid"),
                    hcontrol = request.getParameter("hcontrol"), jdomain = request.getParameter("jdomain"),
                    idomain = request.getParameter("idomain"), description = request.getParameter("description"),
                    viewname = request.getParameter("viewname");
            String[] viewadids2 = request.getParameterValues("viewadsid");
            AdsUser adsUser = adsUserService.findOne(Long.valueOf(uid));
            Map map = new HashMap();
            AdsZone zone;
            if (adsUser != null) {
                if (StringUtils.isEmpty(zoneid)) {
                    zone = new AdsZone();
                    zone.setAddTime(new Date());
                    zone.setUid(Long.valueOf(uid));
                } else {
                    zone = cacheAdsZone.single(Long.valueOf(zoneid));
                    if (zone == null) {
                        zone = adsZoneService.findOne(Long.valueOf(zoneid));
                    }
                }
                if (!StringUtils.isEmpty(hcontrol)) {
                    map.put("position", hcontrol);
                    JSONObject json = JSONObject.fromObject(map);
                    zone.setHcontrol(json.toString());
                }
                Integer pay = StringUtils.isEmpty(paytype) ? 2 : Integer.valueOf(paytype);
                Integer view = StringUtils.isEmpty(viewtype) ? 2 : Integer.valueOf(viewtype);
                Integer width = 640, height = 200;
                if (!StringUtils.isEmpty(sizetype)) {
                    String[] sizes = sizetype.split("x");
                    width = Integer.valueOf(sizes[0]);
                    height = Integer.valueOf(sizes[1]);
                }
                if (null != viewadids2 && viewadids2.length > 0) {
                    zone.setViewadids(StringUtils.join(viewadids2, ","));
                } else {
                    zone.setViewadids("");
                }
                zone.setZonename(StringUtils.isEmpty(zonename) ? "" : zonename);
                zone.setHeight(height);
                zone.setWidth(width);
                zone.setPaytype(pay);
                zone.setViewtype(view);
                zone.setZstatus(9);
                zone.setJdomain(StringUtils.isEmpty(jdomain) ? null : jdomain);
                zone.setIdomain(StringUtils.isEmpty(idomain) ? null : idomain);
                zone.setDescription(StringUtils.isEmpty(description) ? null : description);
                zone.setViewname(StringUtils.isEmpty(viewname) ? null : viewname);
                adsZoneService.saveOne(zone);
                cacheAdsZone.save(zone);
                jsonResult = JSONObject.fromObject(result).toString();
            } else {
                result.put("result", "没有该用户UID");
                jsonResult = JSONObject.fromObject(result).toString();
            }
        } catch (Exception e) {
            LOG.error("addZone error: ", e);
            result.put("result", e.getMessage());
            jsonResult = JSONObject.fromObject(result).toString();
        }
        return jsonResult;
    }

    /**
     * 根据编号修改广告位的状态
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/updateZstatus", method = RequestMethod.POST)
    public String updateStatus(HttpServletRequest request) {
        String jsonResult = "success";
        Map<String, String> result = new HashMap<>();
        result.put("result", jsonResult);
        try {
            String zoneid = request.getParameter("zoneid");//uid
            String zstatus = request.getParameter("zstatus");//状态
            AdsZone adsZone;
            if (null != zoneid && !"".equals(zoneid)) {
                adsZone = cacheAdsZone.single(Long.valueOf(zoneid));
                adsZone.setZstatus(Integer.valueOf(zstatus));
                adsZoneService.saveOne(adsZone);
                cacheAdsZone.save(adsZone);
            }
            jsonResult = JSONObject.fromObject(result).toString();
        } catch (Exception e) {
            LOG.error("updateZstatus error: ", e);
            result.put("result", e.getMessage());
            jsonResult = JSONObject.fromObject(result).toString();
        }
        return jsonResult;
    }

    @RequestMapping(value = "/findAllAdsAdByUid", method = RequestMethod.POST)
    public String findAllAdsAdByUid(HttpServletRequest request) {
        JSONObject data = new JSONObject();
        try {
            String uid = request.getParameter("uid");
            String viewtype = request.getParameter("viewtype");//广告类型
            String sizetype = request.getParameter("sizetype");//尺寸
            Integer width = 0, height = 0;
            if (!StringUtils.isEmpty(sizetype)) {
                String[] sizes = sizetype.split("x");
                width = Integer.valueOf(sizes[0]);
                height = Integer.valueOf(sizes[1]);
            }
            AdsUser adsUser = adsUserService.findOne(Long.valueOf(uid));
            if (null == adsUser) {
                data.put("errMsg", "没有该会员:" + uid);
                return data.toString();
            }
            JSONArray array = new JSONArray();
            List<String> limitPlanList = new ArrayList<>();
            String limitPlan = adsUser.getLimitplan();
            if (!StringUtils.isEmpty(limitPlan)) {
                limitPlanList = new ArrayList<>(Arrays.asList(limitPlan.split(",")));
            }
            //得到符合符合提交的广告信息
            List<AdsAd> list = cacheAdsAd.listByTypeAndSize(Integer.valueOf(viewtype), width, height);
            if (list != null && list.size() > 0) {
                for (AdsAd adsAd : list) {
                    if (adsAd == null || adsAd.getAstatus() != 0) {
                        continue;
                    }
                    if (limitPlanList.contains(String.valueOf(adsAd.getPlanid()))) {
                        continue;
                    }
                    AdsPlan adsPlan = adsPlanService.findOne(adsAd.getPlanid());
                    if (null == adsPlan || adsPlan.getPstatus() != 0) {
                        continue;
                    }
                    String limitUid = adsPlan.getLimitUid();
                    if (!StringUtils.isEmpty(limitUid)) {
                        List<String> limitUidList = new ArrayList<>(Arrays.asList(limitUid.split(",")));
                        if (limitUidList.contains(uid)) {
                            continue;
                        }
                    }
                    JSONObject adJson = JSONObject.fromObject(adsAd);
                    adJson.put("title", adsPlan.getTitle());
                    array.add(adJson);
                }
                data.put("data", array);
            }
        } catch (Exception e) {
            LOG.error("errorMsg", e);
        }
        return data.toString();
    }
}
