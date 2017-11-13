package io.union.admin.controller;

import io.union.admin.entity.AdsPlan;
import io.union.admin.entity.AdsUser;
import io.union.admin.entity.LogVisit;
import io.union.admin.service.AdsPlanService;
import io.union.admin.service.AdsUserService;
import io.union.admin.service.LogVisitService;
import io.union.admin.util.ToolUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shell Li on 2017/10/11.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
@RequestMapping(value = "/manage")
public class LogVisitAnalysisController {
    final static Logger logger = LoggerFactory.getLogger(LogVisitAnalysisController.class);

    @Autowired
    private LogVisitService logVisitService;
    @Autowired
    private AdsUserService adsUserService;
    @Autowired
    private AdsPlanService adsPlanService;

    @RequestMapping(value = "/logVisitAnalysis", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView initPage(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("logvisit");
        String cusIp = request.getParameter("cusIp"), uid = request.getParameter("uid"), planid = request.getParameter("planid"),
                adid = request.getParameter("adid"), date = request.getParameter("date");
        cusIp = StringUtils.defaultIfEmpty(cusIp, "");
        uid = StringUtils.defaultIfEmpty(uid, "");
        planid = StringUtils.defaultIfEmpty(planid, "");
        adid = StringUtils.defaultIfEmpty(adid, "");
        date = StringUtils.defaultIfEmpty(date, "");

        mv.addObject("cusIp", cusIp);
        mv.addObject("uid", uid);
        mv.addObject("planid", planid);
        mv.addObject("adid", adid);
        mv.addObject("date", date);

        return mv;
    }

    @RequestMapping(value = "/logVisitData", method = {RequestMethod.POST, RequestMethod.GET})
    public String searchData(HttpServletRequest request, @RequestParam(defaultValue = "0", required = false) int page,
                             @RequestParam(defaultValue = "50", required = false) int size) {
        // 查询条件
        String cusIp = request.getParameter("cusIp"), uid = request.getParameter("uid"), planid = request.getParameter("planid"),
                adid = request.getParameter("adid"), date = request.getParameter("date");
        Pageable pageable = new PageRequest(page, size);
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("cusIp", cusIp);
        paramsMap.put("uid", uid);
        paramsMap.put("planid", planid);
        paramsMap.put("adid", adid);
        paramsMap.put("date", date);

        String jsonResult = "";
        if (StringUtils.isEmpty(cusIp) && StringUtils.isEmpty(uid) && StringUtils.isEmpty(planid) && StringUtils.isEmpty(adid)) {
            return jsonResult;
        }

        try {
            Page<LogVisit> pagelist = logVisitService.findByPage(paramsMap, pageable);
            if (null != pagelist && pagelist.getContent().size() > 0) {
                JSONArray array = JSONArray.fromObject(pagelist.getContent()), resultArray = new JSONArray();
                long total = pagelist.getTotalElements();
                int pageNumber = pagelist.getSize();
                for (int i = 0; i < array.size(); i++) {
                    Map<Long, AdsUser> mapUser = adsUserService.findMap();
                    JSONObject item = array.getJSONObject(i);
                    String username = "";
                    Long userId = item.getLong("uid");
                    AdsUser user = mapUser.get(userId);
                    if (null != user) {
                        username = user.getUsername();
                    }
                    item.put("username", username);

                    Map<Long, AdsPlan> mapPlan = adsPlanService.findMap();
                    String planName = "";
                    Long planId = item.getLong("planid");
                    AdsPlan adsPlan = mapPlan.get(planId);
                    if (null != adsPlan) {
                        planName = adsPlan.getTitle();
                    }
                    item.put("planname", planName);
                    resultArray.add(item);
                }
                jsonResult = ToolUtil.tableFormat(total, pageNumber, resultArray);
            }
        } catch (Exception e) {
            logger.error("query error: ", e);
            JSONObject err = new JSONObject();
            err.put("result", e.getMessage());
            jsonResult = err.toString();
        }

        return jsonResult;
    }


}
