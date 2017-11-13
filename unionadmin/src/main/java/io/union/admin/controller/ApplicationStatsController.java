package io.union.admin.controller;


import io.union.admin.common.redis.CacheAdsUser;
import io.union.admin.entity.*;
import io.union.admin.model.UnionStatsPlan;
import io.union.admin.service.*;
import io.union.admin.util.ToolUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@EnableAutoConfiguration
@Scope("prototype")
@RequestMapping("/manage")
public class ApplicationStatsController {
    final Logger logger = LoggerFactory.getLogger(getClass());

    private UnionStatsService unionStatsService;
    private AdsAdService adsAdService;
    private AdsUserService adsUserService;
    private AdsPlanService adsPlanService;
    private AdsZoneService adsZoneService;


    private CacheAdsUser cacheAdsUser;

    public ApplicationStatsController(@Autowired UnionStatsService unionStatsService, @Autowired AdsAdService adsAdService,
                                      @Autowired AdsUserService adsUserService, @Autowired AdsPlanService adsPlanService,
                                      @Autowired AdsZoneService adsZoneService, @Autowired CacheAdsUser cacheAdsUser) {
        this.unionStatsService = unionStatsService;
        this.adsAdService = adsAdService;
        this.adsUserService = adsUserService;
        this.adsPlanService = adsPlanService;
        this.adsZoneService = adsZoneService;
        this.cacheAdsUser = cacheAdsUser;
    }

    @RequestMapping(value = {"/{prefix}statlist"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView statPage(HttpServletRequest request, @PathVariable String prefix) {
        ModelAndView mav = new ModelAndView("statlist");
        try {
            // 报表类型：planid -> 计划报表，uid -> 站长报表，adid -> 广告报表，zoneid -> 广告位报表
            // String type = prefix + "id";
            mav.addObject("stattype", prefix);

            // 接收查询条件
            String searchField = request.getParameter("searchField"), searchValue = request.getParameter("searchValue"),
                    sortField = request.getParameter("sortField"), limitType = request.getParameter("limitType"),
                    startTime = request.getParameter("startTime"), stopTime = request.getParameter("stopTime");
            searchField = StringUtils.defaultIfEmpty(searchField, "");
            searchValue = StringUtils.defaultIfEmpty(searchValue, "");
            sortField = StringUtils.defaultIfEmpty(sortField, "");
            limitType = StringUtils.defaultIfEmpty(limitType, "0");
            startTime = StringUtils.defaultIfEmpty(startTime, "");
            stopTime = StringUtils.defaultIfEmpty(stopTime, "");

            // “计划报表”当页进入的时候，默认查询当天. --by lin
            if ("today".equals(startTime)){
                LocalDate localDate = LocalDate.now();
                String today = localDate.format(DateTimeFormatter.ISO_DATE);
                startTime = today;
            }

            mav.addObject("searchField", searchField);
            mav.addObject("searchValue", searchValue);
            mav.addObject("sortField", sortField);
            mav.addObject("limitType", limitType);
            mav.addObject("startTime", startTime);
            mav.addObject("stopTime", stopTime);
        } catch (Exception e) {
            logger.error("stat page error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/{prefix}statdata"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String queryDate(HttpServletRequest request, @PathVariable String prefix,
                            @RequestParam(defaultValue = "0", required = false) int page,
                            @RequestParam(defaultValue = "50", required = false) int size) {
        String jsonResult = "";
        try {
            String type = prefix + "id";
            // 接收查询条件
            String searchField = request.getParameter("searchField"), searchValue = request.getParameter("searchValue"),
                    sortField = request.getParameter("sortField"), limitType = request.getParameter("limitType"),
                    startTime = request.getParameter("startTime"), stopTime = request.getParameter("stopTime");
            Pageable pageable = new PageRequest(page, size);
            Long idValue = StringUtils.isEmpty(searchValue) ? null : Long.valueOf(searchValue);
            Integer paytype = StringUtils.isEmpty(limitType) ? null : Integer.valueOf(limitType);
            Page<UnionStats> pagelist = unionStatsService.findByPage(type, searchField, idValue, sortField, paytype, startTime, stopTime, pageable);
            if (null != pagelist && pagelist.getContent().size() > 0) {
                JSONArray array = JSONArray.fromObject(pagelist.getContent()), resultArray = new JSONArray();
                long total = pagelist.getTotalElements();
                int pageNumber = pagelist.getSize();
                if ("uid".equals(type)) {
                    Map<Long, AdsUser> map = adsUserService.findMap();
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject item = array.getJSONObject(i);
                        Long id = item.getLong("id");
                        String name = "";
                        AdsUser temp = map.get(id);
                        if (null != temp) {
                            name = temp.getUsername();
                        }
                        item.put("name", name);
                        resultArray.add(item);
                    }
                } else if ("planid".equals(type)) {
                    Map<Long, AdsPlan> map = adsPlanService.findMap();
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject item = array.getJSONObject(i);
                        Long id = item.getLong("id");
                        String name = "";
                        AdsPlan temp = map.get(id);
                        if (null != temp) {
                            name = temp.getTitle();
                        }
                        item.put("name", name);
                        resultArray.add(item);
                    }
                } else if ("adid".equals(type)) {
                    Map<Long, AdsAd> map = adsAdService.findMap();
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject item = array.getJSONObject(i);
                        Long id = item.getLong("id");
                        String name = "";
                        AdsAd temp = map.get(id);
                        if (null != temp) {
                            name = temp.getAdname();
                        }
                        item.put("name", name);
                        resultArray.add(item);
                    }
                } else if ("zoneid".equals(type)) {
                    Map<Long, AdsZone> map = adsZoneService.findMap();
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject item = array.getJSONObject(i);
                        Long id = item.getLong("id");
                        String name = "";
                        AdsZone temp = map.get(id);
                        if (null != temp) {
                            name = temp.getZonename();
                        }
                        item.put("name", name);
                        resultArray.add(item);
                    }
                }
                // 汇总
                UnionStats item = unionStatsService.sumByPage(searchField, idValue, paytype, startTime, stopTime);
                JSONObject footer = JSONObject.fromObject(item);
                footer.put("addTime", "汇总");
                jsonResult = ToolUtil.tableFormat(total, pageNumber, resultArray, footer);
            }
        } catch (Exception e) {
            logger.error("query error: ", e);
            JSONObject err = new JSONObject();
            err.put("result", e.getMessage());
            jsonResult = err.toString();
        }
        return jsonResult;
    }

    @RequestMapping(value = "/{prefix}statdataExport", method = {RequestMethod.POST, RequestMethod.GET})
    public void exportData(@PathVariable String prefix,
                           @RequestParam(defaultValue = "0", required = false) int page,
                           @RequestParam(defaultValue = "50", required = false) int size,
                           HttpServletRequest request, HttpServletResponse response) throws Exception {
        String type = prefix + "id";
        String searchField = request.getParameter("searchField"), searchValue = request.getParameter("searchValue"),
                sortField = request.getParameter("sortField"), limitType = request.getParameter("limitType"),
                startTime = request.getParameter("startTime"), stopTime = request.getParameter("stopTime");

        Pageable pageable = new PageRequest(page, Integer.MAX_VALUE);
        Long idValue = StringUtils.isEmpty(searchValue) ? null : Long.valueOf(searchValue);
        Integer paytype = StringUtils.isEmpty(limitType) ? null : Integer.valueOf(limitType);
        Page<UnionStats> pagelist = unionStatsService.findByPage(type, searchField, idValue, sortField, paytype, startTime, stopTime, pageable);
        List<UnionStats> contentList = pagelist.getContent();
        List<UnionStats> exportList = new ArrayList<>();
        // 计划报表导出list
        List<UnionStatsPlan> exportPlanList = new ArrayList<>();

        if (contentList != null && contentList.size() > 0) {
            if ("uid".equals(type)) {
                Map<Long, AdsUser> map = adsUserService.findMap();
                for (UnionStats us : contentList) {
                    Long id = us.getId();
                    String name = "";
                    AdsUser temp = map.get(id);
                    if (null != temp) name = temp.getUsername();
                    us.setName("[" + us.getId() + "]" + name);
                    if (us.getPaynum() != 0L) {
                        float clickRate = new BigDecimal(us.getClickip()).divide(new BigDecimal(us.getPaynum()), 4, BigDecimal.ROUND_HALF_DOWN)
                                .multiply(new BigDecimal(100)).floatValue();
                        us.setClickRate(clickRate + "%");
                    }
                    if (us.getViews() != 0L) {
                        float payRate = new BigDecimal(us.getPaynum()).divide(new BigDecimal(us.getViews()), 4, BigDecimal.ROUND_HALF_DOWN)
                                .multiply(new BigDecimal(100)).floatValue();
                        us.setPayRate(payRate + "%");
                    }
                    exportList.add(us);
                }
            } else if ("planid".equals(type)) {
                Map<Long, AdsPlan> map = adsPlanService.findMap();
                Map<Long, AdsUser> userMap = cacheAdsUser.map();

                for (UnionStats us : contentList) {
                    UnionStatsPlan unionStatsPlan = new UnionStatsPlan();
                    // copy UnionStats's properties to UnionStatsPlan
                    BeanUtils.copyProperties(unionStatsPlan, us);
                    Long id = unionStatsPlan.getId();
                    String name = "";
                    AdsPlan adsPlan = map.get(id);
                    if (null != adsPlan) name = adsPlan.getTitle();
                    unionStatsPlan.setName("[" + unionStatsPlan.getId() + "]" + name);
                    if (unionStatsPlan.getPaynum() != 0L) {
                        float clickRate = new BigDecimal(unionStatsPlan.getClickip()).divide(new BigDecimal(unionStatsPlan.getPaynum()), 4, BigDecimal.ROUND_HALF_DOWN)
                                .multiply(new BigDecimal(100)).floatValue();
                        unionStatsPlan.setClickRate(clickRate + "%");
                    }
                    if (unionStatsPlan.getViews() != 0L) {
                        float payRate = new BigDecimal(unionStatsPlan.getPaynum()).divide(new BigDecimal(unionStatsPlan.getViews()), 4, BigDecimal.ROUND_HALF_DOWN)
                                .multiply(new BigDecimal(100)).floatValue();
                        unionStatsPlan.setPayRate(payRate + "%");
                    }
                    // 获得广告商
                    if (userMap != null && adsPlan != null) {
                        AdsUser adsUser = userMap.get(adsPlan.getUid());
                        if (adsUser != null && !StringUtils.isEmpty(adsUser.getUsername())) {
                            unionStatsPlan.setUsername(adsUser.getUsername());
                            unionStatsPlan.setUid(adsPlan.getUid());
                        }
                    }
                    exportPlanList.add(unionStatsPlan);
                }
            } else if ("adid".equals(type)) {
                Map<Long, AdsAd> map = adsAdService.findMap();
                for (UnionStats us : contentList) {
                    Long id = us.getId();
                    String name = "";
                    AdsAd temp = map.get(id);
                    if (null != temp) name = temp.getAdname();
                    us.setName("[" + us.getId() + "]" + name);
                    if (us.getPaynum() != 0L) {
                        float clickRate = new BigDecimal(us.getClickip()).divide(new BigDecimal(us.getPaynum()), 4, BigDecimal.ROUND_HALF_DOWN)
                                .multiply(new BigDecimal(100)).floatValue();
                        us.setClickRate(clickRate + "%");
                    }
                    if (us.getViews() != 0L) {
                        float payRate = new BigDecimal(us.getPaynum()).divide(new BigDecimal(us.getViews()), 4, BigDecimal.ROUND_HALF_DOWN)
                                .multiply(new BigDecimal(100)).floatValue();
                        us.setPayRate(payRate + "%");
                    }
                    exportList.add(us);
                }
            } else if ("zoneid".equals(type)) {
                Map<Long, AdsZone> map = adsZoneService.findMap();
                for (UnionStats us : contentList) {
                    Long id = us.getId();
                    String name = "";
                    AdsZone temp = map.get(id);
                    if (null != temp) name = temp.getZonename();
                    us.setName("[" + us.getId() + "]" + name);
                    if (us.getPaynum() != 0L) {
                        float clickRate = new BigDecimal(us.getClickip()).divide(new BigDecimal(us.getPaynum()), 4, BigDecimal.ROUND_HALF_DOWN)
                                .multiply(new BigDecimal(100)).floatValue();
                        us.setClickRate(clickRate + "%");
                    }
                    if (us.getViews() != 0L) {
                        float payRate = new BigDecimal(us.getPaynum()).divide(new BigDecimal(us.getViews()), 4, BigDecimal.ROUND_HALF_DOWN)
                                .multiply(new BigDecimal(100)).floatValue();
                        us.setPayRate(payRate + "%");
                    }
                    exportList.add(us);
                }
            }
        }

        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        // 下载文件的默认名称
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("_yyyy-MM-dd_HHmmss");
        String timeStamp = sdf.format(date);
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(prefix + "id", "UTF-8")
                + timeStamp + ".xls");
        // 编码
        response.setCharacterEncoding("UTF-8");
        Workbook workbook = null;
        if ("planid".equals(type)) {
            workbook = ExcelExportUtil.exportExcel(new ExportParams(), UnionStatsPlan.class, exportPlanList);
        } else {
            workbook = ExcelExportUtil.exportExcel(new ExportParams(), UnionStats.class, exportList);
        }

        workbook.write(response.getOutputStream());
    }

}
