package io.union.admin.controller;

import io.union.admin.service.StatHourReqsService;
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
 * 请求趋势图
 * <p>
 * Created by win10 on 2017/8/28.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
@RequestMapping(value = "/manage")
public class StatHourReqsController {
    final static Logger LOG = LoggerFactory.getLogger(StatHourReqsController.class);
    @Autowired
    private StatHourReqsService statHourReqsService;

    @RequestMapping(value = "/requestAnalysis", method = RequestMethod.GET)
    public ModelAndView initPage() {
        ModelAndView mv = new ModelAndView("requestChart");

        return mv;
    }

    /**
     * 条件查询
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/searchRequestData", method = RequestMethod.POST)
    public Map<String, Object> searchRequestData(HttpServletRequest request, @RequestBody Object model) {
        Map<String, Object> result = new HashMap<>();
        try {
            JSONObject params = JSONObject.fromObject(model);
            return statHourReqsService.requestData(params);
        } catch (Exception e) {
            LOG.error("searchRequestData error", e);
            result.put("error", e);
        }
        return result;
    }
}
