package io.union.admin.controller;

import io.union.admin.common.redis.CacheAdsArticle;
import io.union.admin.entity.AdsArticle;
import io.union.admin.service.AdsArticleService;
import io.union.admin.util.ToolUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by Shell Li on 2017/8/28.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
@RequestMapping(value = "/manage")
public class AdsArticleController {

    @Autowired
    private AdsArticleService adsArticleService;

    @Autowired
    private CacheAdsArticle cacheAdsArticle;
    private static final Logger log = getLogger(AdsArticleController.class);

    @RequestMapping(value = "/adsArticleInit", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView init(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("adsarticle");
        return mv;
    }

    @RequestMapping(value = "/adsArticleInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView initInfo(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("articleinfo");
        AdsArticle article;
        String id = request.getParameter("id");
        if (StringUtils.hasText(id)) {
            article = adsArticleService.findOne(Long.valueOf(id));
            mv.addObject("initType", "编辑文章");
            mv.addObject("article", article);
        } else {
            article = new AdsArticle();
            mv.addObject("initType", "新建文章");
            mv.addObject("article", article);
        }
        return mv;
    }

    @RequestMapping(value = "/articleList", method = {RequestMethod.POST, RequestMethod.GET})
    public String initData(@RequestParam(defaultValue = "0", required = false) int page,
                           @RequestParam(defaultValue = "20", required = false) int size, String title) {
        String jsonResult = null;
        try {
            Page<AdsArticle> list = null;
            if (!StringUtils.hasText(title)) {
                list = adsArticleService.findAll(page, size);
            } else {
                list = adsArticleService.findAllByTitle(page, size, title);
            }

            if (list != null) {
                jsonResult = ToolUtil.tableFormat(list);
            }
        } catch (Exception e) {
            log.error("articleList error: ", e);
            JSONObject err = new JSONObject();
            err.put("result", e.getMessage());
            jsonResult = err.toString();
        }

        return jsonResult;
    }

    @RequestMapping(value = "/saveArticle", method = {RequestMethod.POST})
    public Map<String, Object> saveData(HttpServletRequest request, @RequestBody Object model) {
        Map<String, Object> result = new HashMap<>();
        try {
            JSONObject modelJson = JSONObject.fromObject(model);
            if (!StringUtils.hasText(modelJson.getString("title"))) {
                result.put("errorMsg", "标题不能为空");
                return result;
            }
            if (!StringUtils.hasText(modelJson.getString("summary"))) {
                result.put("errorMsg", "摘要不能为空");
                return result;
            }
            if (!StringUtils.hasText(modelJson.getString("content"))) {
                result.put("errorMsg", "内容不能为空");
                return result;
            }
            if (!modelJson.containsKey("atop") || !StringUtils.hasText(modelJson.getString("atop"))) {
                result.put("errorMsg", "请选择是否置顶");
                return result;
            }
            if (!StringUtils.hasText(modelJson.getString("asort"))) {
                result.put("errorMsg", "排序不能为空");
                return result;
            }
            if (!modelJson.containsKey("astatus") || !StringUtils.hasText(modelJson.getString("astatus"))) {
                result.put("errorMsg", "请选择状态");
                return result;
            }

            AdsArticle article = (AdsArticle) JSONObject.toBean(modelJson, AdsArticle.class);
            if (0 == article.getId()) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date currentTime = formatter.parse(formatter.format(new Date()));
                article.setAddTime(currentTime);
            } else {
                article.setAddTime(new SimpleDateFormat("yyyy-MM-dd").parse(modelJson.getString("addTime")));
            }
            if (StringUtils.hasText(modelJson.getString("endTime"))) {
                article.setEndTime(new SimpleDateFormat("yyyy-MM-dd").parse(modelJson.getString("endTime")));
            } else {
                article.setEndTime(null);
            }
            if (StringUtils.hasText(modelJson.getString("beginTime"))) {
                article.setBeginTime(new SimpleDateFormat("yyyy-MM-dd").parse(modelJson.getString("beginTime")));
            } else {
                article.setBeginTime(null);
            }
            adsArticleService.saveOne(article);
            cacheAdsArticle.save(article);
        } catch (Exception e) {
            result.put("errorMsg", e.getMessage());
        }
        return result;
    }

}
