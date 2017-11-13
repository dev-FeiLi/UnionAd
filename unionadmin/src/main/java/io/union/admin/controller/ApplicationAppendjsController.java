package io.union.admin.controller;

import io.union.admin.entity.AdsAppendjs;
import io.union.admin.service.AdsAppendjsService;
import io.union.admin.util.ToolUtil;
import net.sf.json.JSONArray;
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
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Administrator on 2017/4/11.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
@RequestMapping("/manage")
public class ApplicationAppendjsController {
    final Logger LOG = LoggerFactory.getLogger(ApplicationAppendjsController.class);

    private AdsAppendjsService adsAppendjsService;

    public ApplicationAppendjsController(@Autowired AdsAppendjsService adsAppendjsService) {
        this.adsAppendjsService = adsAppendjsService;
    }

    @RequestMapping(value = "/jslist", method = {RequestMethod.GET})
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("appendjs");
        return mav;
    }

    @RequestMapping(value = "/jsdata", method = {RequestMethod.GET, RequestMethod.POST})
    public String data() {
        String jsonResult = "";
        try {
            List<AdsAppendjs> list = adsAppendjsService.findAll();
            jsonResult = ToolUtil.tableFormat(1, 20, JSONArray.fromObject(list));
        } catch (Exception e) {
            LOG.error("jslist error: ", e);
        }
        return jsonResult;
    }

    @RequestMapping(value = "/jssave", method = {RequestMethod.POST})
    public void save(HttpServletRequest request, HttpServletResponse response) {
        String result = "";
        try {
            String idparam = request.getParameter("id"), title = request.getParameter("title"),
                    device = "", link = request.getParameter("link"),
                    deny = request.getParameter("deny"), allow = request.getParameter("allow"),
                    status = request.getParameter("state");
            String[] devices = request.getParameterMap().get("device");
            if (null != devices && devices.length > 0) {
                device = StringUtils.join(devices, ",");
            }
            AdsAppendjs appendjs;
            if (!StringUtils.isEmpty(idparam)) {
                appendjs = adsAppendjsService.find(Long.valueOf(idparam));
            } else {
                appendjs = new AdsAppendjs();
            }
            appendjs.setAllow(allow);
            appendjs.setDeny(deny);
            appendjs.setDevice(device);
            appendjs.setLink(link);
            appendjs.setTitle(title);
            if (!StringUtils.isEmpty(status)) {
                appendjs.setState(Integer.valueOf(status));
            }
            adsAppendjsService.save(appendjs);
            result = "{\"success\":\"success\"}";
        } catch (Exception e) {
            result = "{\"errmsg\":\"" + e.getMessage() + "\"}";
            LOG.error("save error: ", e);
        } finally {
            try {
                response.getWriter().write(result);
            } catch (Exception e) {
                LOG.error("response write error: " + e);
            }
        }
    }

    @RequestMapping(value = "/jsdelete", method = {RequestMethod.POST, RequestMethod.GET})
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        String result = "";
        try {
            String idparam = request.getParameter("id");
            if (!StringUtils.isEmpty(idparam)) {
                adsAppendjsService.delete(Long.valueOf(idparam));
            }
            result = "{\"success\":\"success\"}";
        } catch (Exception e) {
            result = "{\"errmsg\":\"" + e.getMessage() + "\"}";
            LOG.error("delete error: ", e);
        } finally {
            try {
                response.getWriter().write(result);
            } catch (Exception e) {
                LOG.error("response write error: " + e);
            }
        }
    }
}
