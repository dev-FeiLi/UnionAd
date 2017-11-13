package io.union.admin.controller;

import io.union.admin.common.redis.CacheAdsSite;
import io.union.admin.entity.AdsClass;
import io.union.admin.entity.AdsSite;
import io.union.admin.entity.AdsUser;
import io.union.admin.entity.UserOperateLog;
import io.union.admin.service.AdsClassService;
import io.union.admin.service.AdsSiteService;
import io.union.admin.service.AdsUserService;
import io.union.admin.service.UserOperateLogService;
import io.union.admin.util.ToolUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenmiaoyun on 2017/8/11.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
@RequestMapping(value = "/manage")
public class AdsSiteController {
    final static Logger LOG = LoggerFactory.getLogger(AdsUserController.class);
    @Autowired
    private AdsSiteService adsSiteService;
    @Autowired
    private AdsClassService adsClassService;
    @Autowired
    private AdsUserService adsUserService;
    @Autowired
    private UserOperateLogService userOperateLogService;
    @Autowired
    private CacheAdsSite cacheAdsSite;

    /**
     * 网站位页面
     *
     * @return
     */
    @RequestMapping(value = "/adsSite", method = {RequestMethod.GET})
    public ModelAndView AdsSiteList(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("AdsSiteList");
        try {
            String uid = request.getParameter("uid");
            if (org.springframework.util.StringUtils.hasText(uid)) {
                mav.addObject("uid", uid);
            }
            List<AdsSite> list = adsSiteService.findAll();
            //获取所有的网站类型
            List<AdsClass> adsClassesList = adsClassService.findAll();
            mav.addObject("pnode", list);
            mav.addObject("adsClassesList", adsClassesList);
        } catch (Exception e) {
            LOG.error("AdsSite error: ", e);
        }
        return mav;
    }

    /**
     * 添加网站与修改网站
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/addsite", method = {RequestMethod.POST})
    public String affsitesave(HttpServletRequest request) {
        String jsonResult = "success";
        Map<String, String> result = new HashMap<>();
        result.put("result", jsonResult);
        try {
            String sitename = request.getParameter("sitename"), siteurl = request.getParameter("siteurl"),
                    sitetype = request.getParameter("sitetype"), beian = request.getParameter("beian"),
                    dayip = request.getParameter("dayip"), daypv = request.getParameter("daypv"),
                    siteid = request.getParameter("siteid"), uid = request.getParameter("uid");
            siteurl = ToolUtil.host(siteurl);
            AdsSite site;
            AdsUser adsUser = adsUserService.findByUtypeAndUid(1, Long.valueOf(uid), 8);
            AdsSite site2 = adsSiteService.findBySiteurl(siteurl);
            if (dayip != "" && dayip != null) {
                if (Long.valueOf(dayip) < 0) {
                    result.put("result", "dayip不能小于0");
                    jsonResult = JSONObject.fromObject(result).toString();
                    return jsonResult;
                }
            }
            if (daypv != "" && daypv != null) {
                if (Long.valueOf(daypv) < 0) {
                    result.put("result", "daypv不能小于0");
                    jsonResult = JSONObject.fromObject(result).toString();
                    return jsonResult;
                }
            }
            if (adsUser != null) {
                if (StringUtils.isEmpty(siteid)) {
                    site = new AdsSite();
                    site.setAddTime(new Date());
                } else {
                    site = cacheAdsSite.single(Long.valueOf(siteid));
                    if (site == null) {
                        site = adsSiteService.findOne(Long.valueOf(siteid));
                    }
                }
                if (site2 != null) {
                    if (site2.getSiteurl() != siteurl) {
                        site.setSiteurl(StringUtils.trimToEmpty(siteurl));
                    }
                } else {
                    site.setSiteurl(StringUtils.trimToEmpty(siteurl));
                }
                site.setUid(Long.valueOf(uid));
                site.setBeian(StringUtils.trimToEmpty(beian));
                site.setDayip(StringUtils.isEmpty(dayip) ? 0L : Long.valueOf(dayip));
                site.setDaypv(StringUtils.isEmpty(daypv) ? 0L : Long.valueOf(daypv));
                site.setSitename(StringUtils.trimToEmpty(sitename));
                site.setSitetype(StringUtils.isEmpty(sitetype) ? 1 : Integer.valueOf(sitetype));
                site.setSstatus(9);
                adsSiteService.saveOne(site);
                jsonResult = JSONObject.fromObject(result).toString();
            } else {
                result.put("result", "没有该用户UID");
                jsonResult = JSONObject.fromObject(result).toString();
            }
        } catch (Exception e) {
            LOG.error("Addsite error: ", e);
            result.put("result", e.getMessage());
            jsonResult = JSONObject.fromObject(result).toString();
        }
        return jsonResult;
    }

    /**
     * 根据网站类型查询网站信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/selectByType", method = {RequestMethod.GET})
    public String selectByType(HttpServletRequest request) {
        String jsonResult = "";
        try {
            String siteid = request.getParameter("searchValue");
            int page = Integer.valueOf(request.getParameter("page"));
            int size = Integer.valueOf(request.getParameter("size"));
            AdsClass adsClass = adsClassService.findOne(Integer.valueOf(siteid));
            if (adsClass != null) {
                Page<AdsSite> list = adsSiteService.findAll(adsClass.getId(), 8, page, size);
                if (null != list) {
                    jsonResult = ToolUtil.tableFormat(list);
                }
            }
        } catch (Exception e) {
            LOG.error("selectByType error: ", e);
            JSONObject err = new JSONObject();
            err.put("result", e.getMessage());
            jsonResult = err.toString();
        } finally {
            return jsonResult;
        }
    }

    /**
     * 有条件就根据条件进行模糊查询，没条件就查询出全部的信息(查询全部的站长域名信息)
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/adsSiteData", method = {RequestMethod.GET, RequestMethod.POST})
    public JSONObject siteDataSearch(HttpServletRequest request) {
        JSONObject jsonShow = new JSONObject();
        try {
            String searchCondition = request.getParameter("searchCondition");
            int page = Integer.valueOf(request.getParameter("page"));
            int size = Integer.valueOf(request.getParameter("size"));
            String searchValue = request.getParameter("searchValue");
            //得到网站类型的值
            String searchType = request.getParameter("searchType");
            Page<AdsSite> list = null;
            if (org.springframework.util.StringUtils.hasText(searchCondition) && !"0".equals(searchValue)) {
                jsonShow = adsSiteService.findAdsSiteByCondition(searchType, searchValue, 8, searchCondition, page, size);
            } else {
                jsonShow = adsSiteService.getAllAdsSiteByPageSize(page, size, 8);
            }
        } catch (Exception e) {
            LOG.error("siteDataSearch error", e);
        } finally {
            return jsonShow;
        }
    }

    /**
     * 根据用户名称模糊查询出网站信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/selectByType2", method = {RequestMethod.GET})
    public String findByUidContaining(HttpServletRequest request) {
        String jsonResult = "";
        try {
            String username = request.getParameter("searchValue");
            int page = Integer.valueOf(request.getParameter("page"));
            int size = Integer.valueOf(request.getParameter("size"));
            List<AdsUser> adsUserList = adsUserService.findByUsernameContaining(username);
            if (adsUserList.size() > 0) {
                for (AdsUser adsUser : adsUserList) {
                    Page<AdsSite> list = adsSiteService.findByUidContaining(adsUser.getUid(), page, size);
                    if (null != list) {
                        jsonResult = ToolUtil.tableFormat(list);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("selectByType2 error: ", e);
            JSONObject err = new JSONObject();
            err.put("result", e.getMessage());
            jsonResult = err.toString();
        } finally {
            return jsonResult;
        }
    }

    /**
     * 根据编号和状态修改站长域名表信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/updateSstatus", method = RequestMethod.POST)
    public String updateSstatus(HttpServletRequest request) {
        String jsonResult = "success";
        Map<String, String> result = new HashMap<>();
        result.put("result", jsonResult);
        try {
            String siteid = request.getParameter("siteid");//siteid
            String sstatus = request.getParameter("sstatus");//状态
            AdsSite adsSite = adsSiteService.findOne(Long.valueOf(siteid));
            adsSite.setSstatus(Integer.valueOf(sstatus));
            adsSiteService.saveOne(adsSite);
            jsonResult = JSONObject.fromObject(result).toString();

            // 首页 点击立即处理 “待审核网站”，更新完之后，  user_operate_log 更新, otype:6 新增网站
            int otype = 6;
            List<UserOperateLog> updateList = userOperateLogService.findAll(adsSite.getUid(), otype, adsSite.getAddTime());
            if (updateList != null && updateList.size() > 0) {
                updateList.forEach(userOperateLog -> {
                    userOperateLog.setOstatus(1);
                });
            }
            userOperateLogService.saveList(updateList);
        } catch (Exception e) {
            LOG.error("updateSstatus error: ", e);
            result.put("result", e.getMessage());
            jsonResult = JSONObject.fromObject(result).toString();
        }
        return jsonResult;
    }

    /**
     * 根据状态查询出包含该状态的信息
     *
     * @return
     */
    @RequestMapping(value = "/findSstatus", method = {RequestMethod.GET, RequestMethod.POST})
    public JSONObject findSstatus(HttpServletRequest request) {
        JSONObject jsonShow = new JSONObject();
        try {
            String sstatus = request.getParameter("sstatus");
            int page = Integer.valueOf(request.getParameter("page"));
            int size = Integer.valueOf(request.getParameter("size"));
            jsonShow = adsSiteService.findSstatus(Integer.valueOf(sstatus), page, size);
        } catch (Exception e) {
            LOG.error("findSstatus error", e);
        }
        return jsonShow;
    }
}
