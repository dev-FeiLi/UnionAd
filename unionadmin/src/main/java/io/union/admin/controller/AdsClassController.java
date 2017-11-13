package io.union.admin.controller;

import io.union.admin.entity.AdsClass;
import io.union.admin.service.AdsClassService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * Created by Shell Li on 2017/8/30.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
@RequestMapping(value = "/manage")
public class AdsClassController {

    @Autowired
    private AdsClassService adsClassService;

    @RequestMapping(value = "/adsClassInit", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView init() {
        ModelAndView mv = new ModelAndView("adsclass");
        return mv;
    }

    @RequestMapping(value = "/adsClassList", method = {RequestMethod.GET, RequestMethod.POST})
    public String dataList(@RequestParam(defaultValue = "0", required = false) int page,
                           @RequestParam(defaultValue = "20", required = false) int size) {
        JSONObject jsonResult = new JSONObject();
        try {
            Page<AdsClass> pageList = adsClassService.findAll(page, size);
            if (pageList != null) {
                // 准备前台显示数据
                jsonResult.put("total", pageList.getTotalElements());
                jsonResult.put("pageNumber", pageList.getSize());
                jsonResult.put("rows", JSONArray.fromObject(pageList.getContent()));
            }
        } catch (Exception e) {
            jsonResult.put("error:", e.getMessage());
        }

        return jsonResult.toString();
    }

    @RequestMapping(value = "/saveAdsClass", method = {RequestMethod.POST})
    public Map<String, Object> saveAdsClass(@RequestBody Object model) {
        JSONObject jsonObject = JSONObject.fromObject(model);
        return adsClassService.saveAdsClass(jsonObject);
    }

}
