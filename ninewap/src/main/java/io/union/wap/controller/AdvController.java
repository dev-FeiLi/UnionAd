package io.union.wap.controller;

import io.union.wap.common.redis.CacheAdsAd;
import io.union.wap.common.redis.CacheAdsPlan;
import io.union.wap.common.redis.CacheAdsUser;
import io.union.wap.common.utils.ApplicationConstant;
import io.union.wap.common.utils.Tool;
import io.union.wap.common.zoo.ZooUtils;
import io.union.wap.entity.*;
import io.union.wap.service.*;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@EnableAutoConfiguration
@Scope("prototype")
public class AdvController {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CacheAdsUser cacheAdsUser;
    @Autowired
    private CacheAdsAd cacheAdsAd;
    @Autowired
    private CacheAdsPlan cacheAdsPlan;

    @Autowired
    private AdsUserService adsUserService;
    @Autowired
    private AdsAdService adsAdService;
    @Autowired
    private AdsPlanService adsPlanService;
    @Autowired
    private AdsAdvpayService adsAdvpayService;
    @Autowired
    private UnionStatsService unionStatsService;
    @Autowired
    private UserOperateLogService operateLogService;
    @Autowired
    private ZooUtils zooutils;
    @Value("${spring.zookeeper.lock.user}")
    String userPath;

    @RequestMapping(value = {"/adv/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView advindex(HttpServletRequest request, HttpSession session) {
        ModelAndView mav = new ModelAndView("advindex");
        try {
            mav.addObject("menu", "index");
            Object object = session.getAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            AdsUser user = (AdsUser) object;
            Long uid = user.getUid();
            AdsUser curuser = cacheAdsUser.single(uid);
            if (null != curuser) {
                int ustatus = curuser.getUstatus();
                if (ustatus != 1) {
                    session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                    mav.setViewName("redirect:/adv/index");
                    return mav;
                }
                session.setAttribute(ApplicationConstant.SESSION_USER_CONTEXT, curuser);
            } else {
                session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                mav.setViewName("redirect:/adv/index");
                return mav;
            }
            Object sucobj = session.getAttribute(ApplicationConstant.SESSION_SUC_MSG), errobj = session.getAttribute(ApplicationConstant.SESSION_ERR_MSG);
            if (null != sucobj) {
                mav.addObject(ApplicationConstant.SESSION_SUC_MSG, sucobj);
                session.removeAttribute(ApplicationConstant.SESSION_SUC_MSG);
            } else if (null != errobj) {
                mav.addObject(ApplicationConstant.SESSION_ERR_MSG, errobj);
                session.removeAttribute(ApplicationConstant.SESSION_ERR_MSG);
            }
            Map<Long, AdsPlan> planmap = cacheAdsPlan.map();
            mav.addObject("planmap", planmap);
            // 默认查询
            String starttime = request.getParameter("starttime"), stoptime = request.getParameter("stoptime");
            if (null == starttime || "".equals(starttime)) {
                LocalDate date = LocalDate.now();
                starttime = date.format(DateTimeFormatter.ISO_DATE);
            }
            List<UnionStats> list = unionStatsService.findAdvByTime(uid, starttime, stoptime);
            if (null != list && list.size() > 0) {
                mav.addObject("statlist", list);
            }
            mav.addObject("starttime", starttime);
            mav.addObject("stoptime", stoptime);
        } catch (Exception e) {
            logger.error("index error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/adv/ad"}, method = {RequestMethod.GET})
    public ModelAndView advad(HttpServletRequest request, HttpSession session) {
        ModelAndView mav = new ModelAndView("advad");
        try {
            mav.addObject("menu", "ad");
            Object object = session.getAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            AdsUser user = (AdsUser) object;
            Long uid = user.getUid();
            AdsUser curuser = cacheAdsUser.single(uid);
            if (null != curuser) {
                int ustatus = curuser.getUstatus();
                if (ustatus != 1) {
                    session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                    mav.setViewName("redirect:/adv/index");
                    return mav;
                }
                session.setAttribute(ApplicationConstant.SESSION_USER_CONTEXT, curuser);
            } else {
                session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                mav.setViewName("redirect:/adv/index");
                return mav;
            }
            Object sucobj = session.getAttribute(ApplicationConstant.SESSION_SUC_MSG), errobj = session.getAttribute(ApplicationConstant.SESSION_ERR_MSG);
            if (null != sucobj) {
                mav.addObject(ApplicationConstant.SESSION_SUC_MSG, sucobj);
                session.removeAttribute(ApplicationConstant.SESSION_SUC_MSG);
            } else if (null != errobj) {
                mav.addObject(ApplicationConstant.SESSION_ERR_MSG, errobj);
                session.removeAttribute(ApplicationConstant.SESSION_ERR_MSG);
            }
            // 可更改素材和链接，无法更改计划
            Map<Long, AdsPlan> planmap = cacheAdsPlan.map(), resultplans = new HashMap<>();
            Map<Long, AdsAd> admap = cacheAdsAd.map(), resultads = new HashMap<>();
            if (null != planmap && planmap.size() > 0) {
                planmap.forEach((k, v) -> {
                    if (uid.equals(v.getUid())) {
                        resultplans.put(k, v);
                    }
                });
            }
            if (null != admap && admap.size() > 0) {
                admap.forEach((k, v) -> {
                    if (uid.equals(v.getUid())) {
                        resultads.put(k, v);
                    }
                });
            }
            mav.addObject("planmap", resultplans);
            mav.addObject("admap", resultads);
        } catch (Exception e) {
            logger.error("ad error: ", e);
        }
        return mav;
    }

    //@RequestMapping(value = {"/adv/editad/{id}"}, method = {RequestMethod.GET})
    public ModelAndView advadedit(HttpServletRequest request, HttpSession session, @PathVariable Long id) {
        ModelAndView mav = new ModelAndView("advadedit");
        try {
            mav.addObject("menu", "ad");
            Object object = session.getAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            AdsUser user = (AdsUser) object;
            Long uid = user.getUid();
            AdsUser curuser = cacheAdsUser.single(uid);
            if (null != curuser) {
                int ustatus = curuser.getUstatus();
                if (ustatus != 1) {
                    session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                    mav.setViewName("redirect:/adv/index");
                    return mav;
                }
                session.setAttribute(ApplicationConstant.SESSION_USER_CONTEXT, curuser);
            } else {
                session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                mav.setViewName("redirect:/adv/index");
                return mav;
            }
            Object sucobj = session.getAttribute(ApplicationConstant.SESSION_SUC_MSG), errobj = session.getAttribute(ApplicationConstant.SESSION_ERR_MSG);
            if (null != sucobj) {
                mav.addObject(ApplicationConstant.SESSION_SUC_MSG, sucobj);
                session.removeAttribute(ApplicationConstant.SESSION_SUC_MSG);
            } else if (null != errobj) {
                mav.addObject(ApplicationConstant.SESSION_ERR_MSG, errobj);
                session.removeAttribute(ApplicationConstant.SESSION_ERR_MSG);
            }
            AdsAd ad = adsAdService.findOne(id);
            if (null == ad || ad.getAdid().equals(0)) {
                mav.setViewName("adverror");
                mav.addObject(ApplicationConstant.SESSION_ERR_MSG, "不存在该广告，请检查链接是否正常或联系客服");
                return mav;
            }
            AdsPlan plan = adsPlanService.findOne(ad.getPlanid());
            mav.addObject("ad", ad);
            mav.addObject("plan", plan);
        } catch (Exception e) {
            logger.error("editad error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/adv/ad/save"}, method = {RequestMethod.POST})
    public ModelAndView advadsave(HttpServletRequest request, HttpSession session) {
        ModelAndView mav = new ModelAndView("redirect:/adv/ad");
        try {
            Object object = session.getAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            AdsUser user = (AdsUser) object;
            Long uid = user.getUid();
            String id = request.getParameter("adid"), imgurl = request.getParameter("imgurl"),
                    adurl = request.getParameter("adurl");
            if (StringUtils.isEmpty(id)) {
                mav.setViewName("adverror");
                mav.addObject(ApplicationConstant.SESSION_ERR_MSG, "不存在该广告，请检查链接是否正常或联系客服");
                return mav;
            }
            AdsAd ad = adsAdService.findOne(Long.valueOf(id));
            if (null == ad || ad.getAdid().equals(0)) {
                mav.setViewName("adverror");
                mav.addObject(ApplicationConstant.SESSION_ERR_MSG, "不存在该广告，请检查链接是否正常或联系客服");
                return mav;
            }
            // 0点-8点 这段时间广告主可以修改广告，其他时间需通过运营人员更改
            LocalTime localTime = LocalTime.now();
            int hour = localTime.getHour();
            if (!(hour >= 0 && hour <= 8)) {
                session.setAttribute(ApplicationConstant.SESSION_ERR_MSG, "当前商务在线，请直接联系商务进行修改");
                return mav;
            }
            String changeText = "";
            boolean isChange = false;
            if (!StringUtils.isEmpty(imgurl) && !imgurl.equals(ad.getImageurl())) {
                isChange = true;
                changeText += "imgurl {old: " + ad.getImageurl() + ", new: " + imgurl + "}";
                ad.setImageurl(imgurl);
            }
            if (!StringUtils.isEmpty(adurl) && !adurl.equals(ad.getAdurl())) {
                isChange = true;
                changeText += ", adurl {old: " + ad.getAdurl() + ", new: " + adurl + "}";
                ad.setAdurl(adurl);
            }
            // 有了修改才更新数据库和缓存
            if (isChange) {
                ad = adsAdService.save(ad);
                cacheAdsAd.save(ad);
                // 记录广告主的操作，然后在后台提醒运营人员，以备检查修改是否合法
                // 记录操作日志
                UserOperateLog log = new UserOperateLog();
                log.setAddTime(new Date());
                log.setIp(Tool.obtainRequestIp(request));
                log.setOstatus(0);
                log.setOtype(5);
                log.setUtype(user.getUtype());
                log.setUid(uid);
                log.setOvalue(changeText);
                operateLogService.saveOne(log);
                session.setAttribute(ApplicationConstant.SESSION_SUC_MSG, "更新广告成功");
            }
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/adv/finance"}, method = {RequestMethod.GET})
    public ModelAndView advfinance(HttpSession session) {
        ModelAndView mav = new ModelAndView("advfinance");
        try {
            mav.addObject("menu", "finance");
            Object object = session.getAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            AdsUser user = (AdsUser) object;
            Long uid = user.getUid();
            AdsUser curuser = cacheAdsUser.single(uid);
            if (null != curuser) {
                int ustatus = curuser.getUstatus();
                if (ustatus != 1) {
                    session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                    mav.setViewName("redirect:/adv/index");
                    return mav;
                }
                session.setAttribute(ApplicationConstant.SESSION_USER_CONTEXT, curuser);
            } else {
                session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                mav.setViewName("redirect:/adv/index");
                return mav;
            }
            Object sucobj = session.getAttribute(ApplicationConstant.SESSION_SUC_MSG), errobj = session.getAttribute(ApplicationConstant.SESSION_ERR_MSG);
            if (null != sucobj) {
                mav.addObject(ApplicationConstant.SESSION_SUC_MSG, sucobj);
                session.removeAttribute(ApplicationConstant.SESSION_SUC_MSG);
            } else if (null != errobj) {
                mav.addObject(ApplicationConstant.SESSION_ERR_MSG, errobj);
                session.removeAttribute(ApplicationConstant.SESSION_ERR_MSG);
            }
            List<AdsAdvpay> list = adsAdvpayService.findByUid(uid);
            if (null != list && list.size() > 0) {
                mav.addObject("paylist", list);
            }
        } catch (Exception e) {
            logger.error("finance error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/adv/basic"}, method = {RequestMethod.GET})
    public ModelAndView affbasic(HttpSession session) {
        ModelAndView mav = new ModelAndView("advbasic");
        try {
            mav.addObject("menu", "basic");
            Object object = session.getAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            AdsUser user = (AdsUser) object;
            Long uid = user.getUid();
            AdsUser curuser = cacheAdsUser.single(uid);
            if (null != curuser) {
                int ustatus = curuser.getUstatus();
                if (ustatus != 1) {
                    session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                    mav.setViewName("redirect:/adv/index");
                    return mav;
                }
                session.setAttribute(ApplicationConstant.SESSION_USER_CONTEXT, curuser);
            } else {
                session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                mav.setViewName("redirect:/adv/index");
                return mav;
            }
            Object sucobj = session.getAttribute(ApplicationConstant.SESSION_SUC_MSG), errobj = session.getAttribute(ApplicationConstant.SESSION_ERR_MSG);
            if (null != sucobj) {
                mav.addObject(ApplicationConstant.SESSION_SUC_MSG, sucobj);
                session.removeAttribute(ApplicationConstant.SESSION_SUC_MSG);
            } else if (null != errobj) {
                mav.addObject(ApplicationConstant.SESSION_ERR_MSG, errobj);
                session.removeAttribute(ApplicationConstant.SESSION_ERR_MSG);
            }
        } catch (Exception e) {
            logger.error("basic error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/adv/basic"}, method = {RequestMethod.POST})
    public ModelAndView affbasicsave(HttpServletRequest request, HttpSession session) {
        ModelAndView mav = new ModelAndView("redirect:/adv/basic");
        try {
            Object object = session.getAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            AdsUser user = (AdsUser) object;
            Long uid = user.getUid();
            InterProcessMutex lock = zooutils.buildLock(userPath + uid);
            try {
                if (lock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                    AdsUser curuser = cacheAdsUser.single(uid);
                    String realname = request.getParameter("realname"), mobile = request.getParameter("mobile"),
                            qq = request.getParameter("qq"), idcard = request.getParameter("idcard");
                    curuser.setRealname(StringUtils.isEmpty(realname) ? "" : realname);
                    curuser.setMobile(StringUtils.isEmpty(mobile) ? "" : mobile);
                    curuser.setQq(StringUtils.isEmpty(qq) ? "" : qq);
                    curuser.setIdcard(StringUtils.isEmpty(idcard) ? "" : idcard);
                    adsUserService.saveOne(curuser);
                    cacheAdsUser.save(curuser);
                }
                session.setAttribute(ApplicationConstant.SESSION_SUC_MSG, "修改成功");
            } catch (Exception e) {
                session.setAttribute(ApplicationConstant.SESSION_ERR_MSG, "修改失败");
                logger.error("lock error: ", e);
            } finally {
                zooutils.release(lock);
            }
        } catch (Exception e) {
            logger.error("basic save error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/adv/pass"}, method = {RequestMethod.GET})
    public ModelAndView affpass(HttpSession session) {
        ModelAndView mav = new ModelAndView("advpass");
        try {
            mav.addObject("menu", "pass");
            Object object = session.getAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            AdsUser user = (AdsUser) object;
            Long uid = user.getUid();
            AdsUser curuser = cacheAdsUser.single(uid);
            if (null != curuser) {
                int ustatus = curuser.getUstatus();
                if (ustatus != 1) {
                    session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                    mav.setViewName("redirect:/adv/index");
                    return mav;
                }
                session.setAttribute(ApplicationConstant.SESSION_USER_CONTEXT, curuser);
            } else {
                session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                mav.setViewName("redirect:/adv/index");
                return mav;
            }
            Object sucobj = session.getAttribute(ApplicationConstant.SESSION_SUC_MSG), errobj = session.getAttribute(ApplicationConstant.SESSION_ERR_MSG);
            if (null != sucobj) {
                mav.addObject(ApplicationConstant.SESSION_SUC_MSG, sucobj);
                session.removeAttribute(ApplicationConstant.SESSION_SUC_MSG);
            } else if (null != errobj) {
                mav.addObject(ApplicationConstant.SESSION_ERR_MSG, errobj);
                session.removeAttribute(ApplicationConstant.SESSION_ERR_MSG);
            }
        } catch (Exception e) {
            logger.error("pass error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/adv/pass"}, method = {RequestMethod.POST})
    public ModelAndView affpasssave(HttpServletRequest request, HttpSession session) {
        ModelAndView mav = new ModelAndView("redirect:/adv/pass");
        try {
            Object object = session.getAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            AdsUser user = (AdsUser) object;
            Long uid = user.getUid();
            String oldpass = "";
            InterProcessMutex lock = zooutils.buildLock(userPath + uid);
            try {

                if (lock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                    AdsUser curuser = cacheAdsUser.single(uid);
                    String loginpw = request.getParameter("loginpw"), newspw = request.getParameter("newspw"),
                            affirmpw = request.getParameter("affirmpw");
                    if (StringUtils.isEmpty(loginpw) || StringUtils.isEmpty(newspw) || StringUtils.isEmpty(affirmpw)) {
                        session.setAttribute(ApplicationConstant.SESSION_ERR_MSG, "密码不能空");
                        return mav;
                    }
                    PasswordEncoder passwordEncoder = new StandardPasswordEncoder();
                    oldpass = curuser.getPassword();
                    if (!passwordEncoder.matches(loginpw, oldpass)) {
                        session.setAttribute(ApplicationConstant.SESSION_ERR_MSG, "旧密码错误");
                        return mav;
                    }
                    if (!newspw.equals(affirmpw)) {
                        session.setAttribute(ApplicationConstant.SESSION_ERR_MSG, "新密码前后输入不一致");
                        return mav;
                    }
                    curuser.setPassword(passwordEncoder.encode(newspw));
                    adsUserService.saveOne(curuser);
                    cacheAdsUser.save(curuser);
                }
                session.setAttribute(ApplicationConstant.SESSION_SUC_MSG, "修改成功");
            } catch (Exception e) {
                session.setAttribute(ApplicationConstant.SESSION_ERR_MSG, "修改失败");
                logger.error("lock error: ", e);
            } finally {
                zooutils.release(lock);
            }
            // 记录操作日志
            String ovalue = "old: " + oldpass + ", new: " + user.getPassword();
            UserOperateLog log = new UserOperateLog();
            log.setAddTime(new Date());
            log.setIp(Tool.obtainRequestIp(request));
            log.setOstatus(0);
            log.setOtype(4);
            log.setUtype(2);
            log.setUid(user.getUid());
            log.setOvalue(ovalue);
            operateLogService.saveOne(log);
        } catch (Exception e) {
            logger.error("basic save error: ", e);
        }
        return mav;
    }
}
