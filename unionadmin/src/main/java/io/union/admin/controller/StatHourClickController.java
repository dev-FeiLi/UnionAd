package io.union.admin.controller;

import io.union.admin.service.StatHourClickService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shell Li on 2017/9/25.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
@RequestMapping(value = "/manage")
public class StatHourClickController {
    final static Logger LOG = LoggerFactory.getLogger(StatHourClickController.class);

    @Autowired
    private StatHourClickService statHourClickService;

    @RequestMapping(value = "/clickAnalysis", method = RequestMethod.GET)
    public ModelAndView initPage() {
        ModelAndView mv = new ModelAndView("clickChart");
        return mv;
    }

    /**
     * 条件查询
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/searchClickData", method = RequestMethod.POST)
    public Map<String, Object> searchRequestData(HttpServletRequest request, @RequestBody Object model) {
        Map<String, Object> result = new HashMap<>();
        try {
            JSONObject params = JSONObject.fromObject(model);
            return statHourClickService.clickData(params);
        } catch (Exception e) {
            LOG.error("searchRequestData error", e);
            result.put("error", e);
        }
        return result;
    }

}
