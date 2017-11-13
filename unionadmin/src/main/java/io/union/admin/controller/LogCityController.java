package io.union.admin.controller;

import io.union.admin.service.LogCityService;
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
 * Created by Shell Li on 2017/9/28.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
@RequestMapping(value = "/manage")
public class LogCityController {

    final static Logger LOG = LoggerFactory.getLogger(LogCityController.class);

    @Autowired
    private LogCityService logCityService;

    @RequestMapping(value = "/logCityAnalysis", method = RequestMethod.GET)
    public ModelAndView initPage() {
        ModelAndView mv = new ModelAndView("logcity");
        return mv;
    }

    @RequestMapping(value = "/logCitySearch", method = RequestMethod.POST)
    public Map<String, Object> searchData(HttpServletRequest request, @RequestBody Object model) {
        Map<String, Object> result = new HashMap<>();
        try {
            JSONObject param = JSONObject.fromObject(model);
            return logCityService.searchData(param);
        } catch (Exception e) {
            LOG.error("logCitySearch error", e);
            result.put("error", e);
        }
        return result;
    }

}
