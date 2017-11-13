package io.union.wap.controller;

import io.union.wap.common.redis.CacheAdsArticle;
import io.union.wap.common.redis.CacheAdsUser;
import io.union.wap.common.redis.CacheUnionSetting;
import io.union.wap.common.redis.RedisKeys;
import io.union.wap.entity.AdsAritcle;
import io.union.wap.entity.AdsUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/8/1.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
public class WapController {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CacheAdsUser cacheAdsUser;
    @Autowired
    private CacheAdsArticle cacheAdsArticle;
    @Autowired
    private CacheUnionSetting cacheUnionSetting;

    @RequestMapping(value = {"/", "/{path}"}, method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView wapindex(@PathVariable(required = false) String path) {
        if (null == path || "".equals(path)) {
            path = "nine_index";
        }
        ModelAndView mav = new ModelAndView(path);
        try {
            String wapTitle = cacheUnionSetting.single(String.valueOf(RedisKeys.ADS_SETTING_WAP_TITLE));
            wapTitle = (null == wapTitle || "".equals(wapTitle)) ? "点优移动传媒" : wapTitle;
            mav.addObject("wapTitle", wapTitle);

            String wapUrl = cacheUnionSetting.single(String.valueOf(RedisKeys.ADS_SETTING_WAP_URL));
            wapUrl = (null == wapUrl || "".equals(wapUrl)) ? "http://www.dianyoo.cn/" : wapUrl;
            mav.addObject("wapUrl", wapUrl);

            List<AdsUser> list = cacheAdsUser.service();
            mav.addObject("services", list);
            mav.addObject("menu", path);
        } catch (Exception e) {
            mav.setViewName("error");
            logger.error("wap " + path + " error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/nine_notice"}, method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView wapnotice() {
        ModelAndView mav = new ModelAndView("nine_notice");
        try {
            String wapTitle = cacheUnionSetting.single(String.valueOf(RedisKeys.ADS_SETTING_WAP_TITLE));
            wapTitle = (null == wapTitle || "".equals(wapTitle)) ? "点优移动传媒" : wapTitle;
            mav.addObject("wapTitle", wapTitle);

            String wapUrl = cacheUnionSetting.single(String.valueOf(RedisKeys.ADS_SETTING_WAP_URL));
            wapUrl = (null == wapUrl || "".equals(wapUrl)) ? "http://www.dianyoo.cn/" : wapUrl;
            mav.addObject("wapUrl", wapUrl);

            List<AdsAritcle> list = cacheAdsArticle.list();
            if (null != list && list.size() > 0) {
                Collections.sort(list, (AdsAritcle org, AdsAritcle tag) -> {
                    if (tag.getAtop().compareTo(org.getAtop()) > 0) {
                        return 1;
                    } else if (tag.getAtop().equals(org.getAtop())) {
                        if (tag.getAsort() > org.getAsort()) {
                            return 1;
                        } else if (tag.getAsort().equals(org.getAsort())) {
                            return tag.getId() >= org.getId() ? 0 : -1;
                        } else {
                            return -1;
                        }
                    } else {
                        return -1;
                    }
                });
                List<AdsAritcle> sublist = new ArrayList<>();
                Calendar now = Calendar.getInstance(), begin = Calendar.getInstance(), end = Calendar.getInstance();
                for (AdsAritcle aritcle : list) {
                    if (null != aritcle.getBeginTime()) {
                        begin.setTimeInMillis(aritcle.getBeginTime().getTime());
                    }
                    if (null != aritcle.getEndTime()) {
                        end.setTimeInMillis(aritcle.getEndTime().getTime());
                    }
                    if (now.compareTo(begin) >= 0 && now.compareTo(end) <= 0) {
                        sublist.add(aritcle);
                    }
                }
                if (list.size() > 10) {
                    mav.addObject("alist", sublist.subList(0, 10));
                } else {
                    mav.addObject("alist", sublist);
                }
            }
            mav.addObject("menu", "notice");
        } catch (Exception e) {
            logger.error("wap notice error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/detail/{id}"}, method = {RequestMethod.GET})
    public ModelAndView noticepage(@PathVariable String id) {
        ModelAndView mav = new ModelAndView("page");
        try {
            Long articeid = Long.valueOf(id);
            AdsAritcle aritcle = cacheAdsArticle.single(articeid);
            if (null != aritcle && aritcle.getId() > 0) {
                mav.addObject("article", aritcle);
            } else {
                mav.addObject("errmsg", "没有相关的文章，请返回列表页或者首页继续操作");
                mav.setViewName("error");
            }
            mav.addObject("menu", "notice");
        } catch (Exception e) {
            mav.addObject("errmsg", "输入的链接有错误，请确保输入正确");
            mav.setViewName("error");
            logger.error("page error: ", e);
        }
        return mav;
    }
}
