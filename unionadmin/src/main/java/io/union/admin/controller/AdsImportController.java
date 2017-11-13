package io.union.admin.controller;

import io.union.admin.common.redis.CacheUnionSetting;
import io.union.admin.entity.AdsImport;
import io.union.admin.entity.AdsPlan;
import io.union.admin.service.AdsImportService;
import io.union.admin.service.AdsPlanService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by win10 on 2017/9/5.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
@RequestMapping(value = "/manage")
public class AdsImportController {
    final static Logger LOG = LoggerFactory.getLogger(AdsAdController.class);
    final static Integer ADS_IMPORT_ISTATUS = 8;
    @Autowired
    private AdsImportService adsImportService;
    @Autowired
    private AdsPlanService adsPlanService;
    @Autowired
    private CacheUnionSetting cacheUnionSetting;

    @RequestMapping(value = "/adsImportList", method = RequestMethod.GET)
    public ModelAndView adsList() {
        ModelAndView mv = new ModelAndView("adsImportList");
        return mv;
    }

    /**
     * 列表显示数据导入信息（不包含状态为8的信息）
     *
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/adsImportData", method = {RequestMethod.GET, RequestMethod.POST})
    public String getAdvPayList(@RequestParam(defaultValue = "0", required = false) int page,
                                @RequestParam(defaultValue = "20", required = false) int size) {
        JSONObject jsonResult = new JSONObject();
        try {
            Page<AdsImport> pageList = adsImportService.findAllByIstatusNot(ADS_IMPORT_ISTATUS, page, size);
            // 准备前台显示数据
            jsonResult.put("total", pageList.getTotalElements());
            jsonResult.put("pageNumber", pageList.getSize());
            jsonResult.put("rows", JSONArray.fromObject(pageList.getContent()));
        } catch (Exception e) {
            jsonResult.put("error", e.getMessage());
        }

        return jsonResult.toString();
    }

    /**
     * 根据编号修改状态
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/updateIstatus", method = RequestMethod.POST)
    public Map<String, Object> updateIstatus(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", request.getParameter("id"));
            jsonObject.put("istatus", request.getParameter("istatus"));
            return adsImportService.updateStatus(jsonObject);
        } catch (Exception e) {
            LOG.error("save error:", e);
            map.put("error", "撤销失败，" + e.getMessage());
        }
        return map;
    }

    /**
     * 根据时间，网站主，广告主查询出记录信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/searchAdsImport", method = {RequestMethod.POST, RequestMethod.GET})
    public JSONArray searchAdsImport(HttpServletRequest request) {
        JSONObject jsonResult = new JSONObject();
        List<AdsImport> list = adsImportService.getAdspayListBySearch(request);
        return JSONArray.fromObject(list);
    }

    /**
     * 跳转到添加记录页面
     *
     * @return
     */
    @RequestMapping(value = "/addAdsImport", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView addAdsImport() {
        ModelAndView mv = new ModelAndView("adsImportInfo");
        Map<String, List<AdsPlan>> planOptions = adsPlanService.getAllAdsPlan();
        mv.addObject("planOptions", planOptions);

        String week = cacheUnionSetting.single("week_clear"), month = cacheUnionSetting.single("month_clear");
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(week.toUpperCase());
        mv.addObject("weekClear", dayOfWeek.getValue());
        mv.addObject("monthClear", month);
        return mv;
    }

    /**
     * 添加记录
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/saveAdsImport", method = {RequestMethod.POST})
    public Map<String, Object> saveAdsClass(@RequestBody Object model) {
        Map<String, Object> map = new HashMap<>();
        try {
            JSONObject jsonObject = JSONObject.fromObject(model);
            return adsImportService.saveAdsImport(jsonObject);
        } catch (Exception e) {
            LOG.error("save error:", e);
            map.put("error", "导入失败，" + e.getMessage());
        }
        return map;
    }
}