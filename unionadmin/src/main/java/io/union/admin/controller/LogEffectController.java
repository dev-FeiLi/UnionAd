package io.union.admin.controller;

import io.union.admin.entity.LogEffect;
import io.union.admin.service.LogEffectService;
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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shell Li on 2017/11/1.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
@RequestMapping(value = "/manage")
public class LogEffectController {

    final static Logger logger = LoggerFactory.getLogger(LogEffectController.class);

    @Autowired
    private LogEffectService logEffectService;

    @RequestMapping(path = "/subSideInfoList", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView pageInit(HttpServletRequest request){
        ModelAndView mv = new ModelAndView("subsideinfolist");
        String affuid = request.getParameter("affuid"), advuid = request.getParameter("advuid"), planid = request.getParameter("planid"),
                regTime = request.getParameter("regTime");
        affuid = StringUtils.defaultIfEmpty(affuid, "");
        advuid = StringUtils.defaultIfEmpty(advuid, "");
        planid = StringUtils.defaultIfEmpty(planid, "");
        regTime = StringUtils.defaultIfEmpty(regTime, "");

        mv.addObject("affuid", affuid);
        mv.addObject("advuid", advuid);
        mv.addObject("planid", planid);
        mv.addObject("regTime", regTime);

        return mv;
    }

    @RequestMapping(path = "/subSideData", method = {RequestMethod.POST, RequestMethod.GET})
    public String pageData(HttpServletRequest request, @RequestParam(defaultValue = "0", required = false) int page,
                           @RequestParam(defaultValue = "50", required = false) int size) {
        String jsonResult = "";
        try {
            Map<String, String> parmasMap = new HashMap<>();
            Enumeration<String> enums = request.getParameterNames();
            while (enums.hasMoreElements()) {
                String paramName = enums.nextElement();
                String paramValue = request.getParameter(paramName);
                //形成键值对应的map
                parmasMap.put(paramName, paramValue);
            }

            // search by page and conditions
            Page<LogEffect> pagelist = logEffectService.findByPage(parmasMap, new PageRequest(page, size));
            if (null != pagelist && pagelist.getContent().size() > 0) {
                JSONArray resultArray = JSONArray.fromObject(pagelist.getContent());
                long total = pagelist.getTotalElements();
                int pageNumber = pagelist.getSize();
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
