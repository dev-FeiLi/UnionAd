package io.union.admin.controller;


import io.union.admin.entity.AdsAd;
import io.union.admin.entity.AdsClass;
import io.union.admin.entity.AdsPlan;
import io.union.admin.entity.AdsUser;
import io.union.admin.service.AdsAdService;
import io.union.admin.service.AdsClassService;
import io.union.admin.service.AdsPlanService;
import io.union.admin.service.AdsUserService;
import io.union.admin.util.ToolUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Shell Li on 2017/8/8.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
@RequestMapping(value = "/manage")
public class AdPlanController {

    final static Logger LOG = LoggerFactory.getLogger(AdPlanController.class);

    /**
     * 广告商
     **/
    final static Integer AD_USER_TYPE = 2;
    @Autowired
    private AdsPlanService adsPlanService;

    @Autowired
    private AdsUserService adsUserService;

    @Autowired
    private AdsClassService adsClassService;

    @Autowired
    private AdsAdService adsAdService;

    @RequestMapping(value = "/planlist", method = {RequestMethod.GET})
    public ModelAndView planList(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("planlist");
        String planId = request.getParameter("planId");
        String uid = request.getParameter("uid");
        if (StringUtils.hasText(planId)) {
            mv.addObject("planId", planId);
        }
        if (StringUtils.hasText(uid)) {
            mv.addObject("uid", uid);
        }

        return mv;
    }

    @RequestMapping(value = "/initPlan")
    public ModelAndView initPlan(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new ModelAndView("planinfo");
        String planId = request.getParameter("planId");
        AdsPlan adsPlan;
        int[] payTypeArray = {1, 2, 3, 4};
        if (StringUtils.hasText(planId)) {
            adsPlan = adsPlanService.findOne(Integer.valueOf(planId));
            mv.addObject("initType", "编辑计划");
        } else {
            adsPlan = new AdsPlan();
            mv.addObject("initType", "新建计划");
        }
        mv.addObject("advUserList", adsUserService.findAllByUtype(AD_USER_TYPE));
        mv.addObject("planInfo", adsPlan);
        mv.addObject("payTypeArray", payTypeArray);
        mv.addObject("uid", request.getParameter("uid"));
        return mv;
    }

    @RequestMapping(value = "/getAdsPlanById", method = {RequestMethod.POST})
    public Map<String, Object> getAdsPlanById(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        String planId = request.getParameter("planId");
        AdsPlan adsPlan = adsPlanService.findOne(Integer.valueOf(planId));
        result.put("plan", adsPlan);
        if (StringUtils.hasText(adsPlan.getPrice())) {
            result.put("price", JSONObject.fromObject(adsPlan.getPrice()));
        }
        if (StringUtils.hasText(adsPlan.getLimitDevice())) {
            result.put("limitDevice", adsPlan.getLimitDevice().split(","));
        }
        if (StringUtils.hasText(adsPlan.getLimitSite())) {
            result.put("limitSite", adsPlan.getLimitSite().split(","));
        }
        if (StringUtils.hasText(adsPlan.getLimitType())) {
            result.put("limitType", adsPlan.getLimitType());
        }
        if (StringUtils.hasText(adsPlan.getLimitTime())) {
            result.put("limitTime", JSONObject.fromObject(adsPlan.getLimitTime()));
        }

        return result;
    }


    @RequestMapping(value = "/planData", method = {RequestMethod.GET})
    public String adsPlanData(@RequestParam(defaultValue = "0", required = false) int page,
                              @RequestParam(defaultValue = "20", required = false) int size) {
        String jsonResult = "";
        try {
            Page<AdsPlan> list = adsPlanService.findAll(page, size);
            if (list != null) {
                jsonResult = ToolUtil.tableFormat(list);
            }
        } catch (Exception e) {
            LOG.error("planData error: ", e);
            JSONObject err = new JSONObject();
            err.put("result", e.getMessage());
            jsonResult = err.toString();
        } finally {
            return jsonResult;
        }
    }

    @RequestMapping(value = "/planDataSearch", method = {RequestMethod.GET, RequestMethod.POST})
    public String planDataSearch(HttpServletRequest request, HttpServletResponse response) {
        String jsonResult = "";
        try {
            String payType = request.getParameter("type");
            String searchCondition = request.getParameter("searchCondition");
            String pstatusCondition = request.getParameter("pstatusCondition");
            int page = Integer.valueOf(request.getParameter("page"));
            int size = Integer.valueOf(request.getParameter("size"));
            Page<AdsPlan> list = null;
            if (StringUtils.hasText(searchCondition)) {
                String searchBy = request.getParameter("searchValue");
                list = adsPlanService.findPlansByCondition(payType, searchBy, searchCondition, pstatusCondition, page, size);
            } else {
                list = adsPlanService.findAllByPayType(payType, page, size);
            }
            if (list != null) {
                jsonResult = ToolUtil.tableFormat(list);
            }
        } catch (Exception e) {
            LOG.error("planDataSearch error", e);
            JSONObject err = new JSONObject();
            err.put("result", e.getMessage());
            jsonResult = err.toString();
        } finally {
            return jsonResult;
        }
    }

    /**
     * 计划列表- “激活” “锁定”更改状态
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/adsPlanSaveStatus", method = {RequestMethod.POST})
    public String adsPlanSaveStatus(HttpServletRequest request, HttpServletResponse response) {
        String jsonResult = "success";
        Map<String, String> result = new HashMap<>();
        result.put("result", jsonResult);
        try {
            String planId = request.getParameter("planId"), pstatus = request.getParameter("pstatus");
            AdsPlan adsPlan = adsPlanService.findOne(Integer.valueOf(planId));
            if (adsPlan == null) {
                result.put("error", "未知计划");
                jsonResult = JSONObject.fromObject(result).toString();
                return jsonResult;
            }
            if (StringUtils.isEmpty(pstatus)) {
                result.put("error", "未知操作");
                jsonResult = JSONObject.fromObject(result).toString();
                return jsonResult;
            }
            Integer status = Integer.valueOf(pstatus);
            // 判断该计划的广告主是否是锁定
            AdsUser user = adsUserService.findOne(adsPlan.getUid());
            if (user.getUstatus() == 9 && status == 0) {
                result.put("error", "该计划的广告商已经被锁定。请查看");
                jsonResult = JSONObject.fromObject(result).toString();
                return jsonResult;
            }
            adsPlan.setPstatus(status);
            // 更改计划状态
            adsPlanService.save(adsPlan);
            // 更改该计划下面广告的状态
            int lockStatus = 9;
            List<AdsAd> list = adsAdService.findAllByPlanIdOutOfStatus(adsPlan.getPlanId(), lockStatus);
            if (list != null && list.size() != 0) {
                for (AdsAd ad : list) {
                    if ("9".equals(pstatus)) {
                        // 2:计划锁定
                        ad.setAstatus(2);
                        adsAdService.save(ad);
                    } else if ("0".equals(pstatus)) {
                        // 0 正常
                        ad.setAstatus(0);
                        adsAdService.save(ad);
                    }
                }
            }
            jsonResult = JSONObject.fromObject(result).toString();
        } catch (Exception e) {
            LOG.error("authssave error: ", e);
            result.put("result", e.getMessage());
            jsonResult = JSONObject.fromObject(result).toString();
        } finally {
            return jsonResult;
        }
    }

    /**
     * 新建（编辑）计划
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/dataSave", method = {RequestMethod.POST, RequestMethod.GET})
    public Map<String, Object> dataSave(HttpServletRequest request, @RequestBody Object model) {
        Map<String, Object> result = new HashMap<>();
        try {
            JSONObject palnInfo = JSONObject.fromObject(model);
            result = adsPlanService.save(palnInfo);
        } catch (Exception e) {
            LOG.error("dataSave error: ", e);
            result.put("error", e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/getSiteClass", method = {RequestMethod.POST})
    public Map<String, Object> siteClassData(HttpServletRequest request, HttpServletResponse response) {
        String jsonResult = "";
        Map<String, Object> result = new HashMap<>();
        List<AdsClass> list = adsClassService.findAll();
        result.put("class", list);
        return result;
    }

}
