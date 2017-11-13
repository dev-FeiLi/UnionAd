package io.union.wap.controller;

import io.union.wap.common.redis.CacheAdsUser;
import io.union.wap.common.redis.CacheUnionSetting;
import io.union.wap.common.redis.RedisKeys;
import io.union.wap.common.utils.ApplicationConstant;
import io.union.wap.common.utils.Tool;
import io.union.wap.common.zoo.ZooUtils;
import io.union.wap.entity.AdsUser;
import io.union.wap.entity.UserOperateLog;
import io.union.wap.service.AdsUserService;
import io.union.wap.service.UserOperateLogService;
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
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RestController
@EnableAutoConfiguration
@Scope("prototype")
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CacheUnionSetting cacheUnionSetting;
    @Autowired
    private CacheAdsUser cacheAdsUser;

    @Autowired
    private AdsUserService adsUserService;
    @Autowired
    private UserOperateLogService operateLogService;
    @Autowired
    private ZooUtils zooutils;
    @Value("${spring.zookeeper.lock.user}")
    String userPath;

    @RequestMapping(value = {"/logcheck"}, method = {RequestMethod.POST})
    public ModelAndView logcheck(HttpServletRequest request, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        try {
            String username = request.getParameter("username"), password = request.getParameter("password");
            char[] symbols = {',', ';', '\'', '"', '(', ')', '!', '=', '%', '*'};
            if (null == username || "".equals(username) || null == password || "".equals(password)) {
                mav.setViewName("error");
                mav.addObject(ApplicationConstant.SESSION_ERR_MSG, "输入有误，请返回登录页继续操作");
                return mav;
            }
            if (StringUtils.containsAny(username, symbols)) {
                mav.setViewName("error");
                mav.addObject(ApplicationConstant.SESSION_ERR_MSG, "输入有特殊符号，请返回登录页继续操作");
                return mav;
            }

            AdsUser user = cacheAdsUser.single(username);
            if (null == user || user.getUid().equals(0l)) {
                mav.setViewName("error");
                mav.addObject(ApplicationConstant.SESSION_ERR_MSG, "该用户名不存在，请返回登录页继续操作");
                return mav;
            }
            int ustatus = user.getUstatus();
            if (ustatus != 1) {
                mav.setViewName("error");
                mav.addObject(ApplicationConstant.SESSION_ERR_MSG, "新用户需联系客服进行激活才能登录");
                return mav;
            }
            PasswordEncoder encoder = new StandardPasswordEncoder();
            String tagPassword = user.getPassword();
            if (!encoder.matches(password, tagPassword)) {
                mav.setViewName("error");
                mav.addObject(ApplicationConstant.SESSION_ERR_MSG, "用户名密码不匹配，请返回登录页继续操作");
                return mav;
            }
            int utype = user.getUtype();
            if (utype == 1) {
                mav.setViewName("redirect:/aff/index");
            } else if (utype == 2) {
                mav.setViewName("redirect:/adv/index");
            } else {
                mav.setViewName("error");
                mav.addObject(ApplicationConstant.SESSION_ERR_MSG, "用户名不能登录，请返回登录页继续操作");
                return mav;
            }
            Long uid = user.getUid();
            InterProcessMutex lock = zooutils.buildLock(userPath + uid);
            try {
                if (lock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                    user = cacheAdsUser.single(uid);
                    user.setLoginip(Tool.obtainRequestIp(request));
                    user.setLogintime(new Date());
                    user = adsUserService.saveOne(user);
                    cacheAdsUser.save(user);
                }
                session.removeAttribute(ApplicationConstant.SESSION_ERR_MSG);
                session.setAttribute(ApplicationConstant.SESSION_USER_CONTEXT, user);
            } catch (Exception e) {
                session.setAttribute(ApplicationConstant.SESSION_ERR_MSG, "登录超时，请重新登录");
                logger.error("lock error: ", e);
            } finally {
                zooutils.release(lock);
            }
        } catch (Exception e) {
            mav.setViewName("error");
            mav.addObject(ApplicationConstant.SESSION_ERR_MSG, "未知错误，请返回登录页继续操作");
            logger.error("logcheck error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/reg/{path}"}, method = {RequestMethod.GET})
    public ModelAndView registepath(@PathVariable String path) {
        ModelAndView mav = new ModelAndView();
        try {
            String wapTitle = cacheUnionSetting.single(String.valueOf(RedisKeys.ADS_SETTING_WAP_TITLE));
            wapTitle = (null == wapTitle || "".equals(wapTitle)) ? "点优移动传媒" : wapTitle;
            mav.addObject("wapTitle", wapTitle);

            if ("adv".equals(path)) {
                mav.setViewName("registe_adv");
            } else if ("aff".equals(path)) {
                mav.setViewName("registe_aff");
            } else {
                mav.setViewName("error");
                mav.addObject(ApplicationConstant.SESSION_ERR_MSG, "PAGE NOT FOUND，请返回登录页继续操作");
                return mav;
            }
        } catch (Exception e) {
            mav.setViewName("error");
            mav.addObject(ApplicationConstant.SESSION_ERR_MSG, "未知错误，请返回登录页继续操作");
            logger.error("registe error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/reg/{path}"}, method = {RequestMethod.POST})
    public ModelAndView register(HttpServletRequest request, HttpSession session, @PathVariable String path) {
        ModelAndView mav = new ModelAndView();
        try {
            int utype = 1;
            if ("adv".equals(path)) {
                utype = 2;
                mav.setViewName("redirect:/adv/index");
            } else if ("aff".equals(path)) {
                utype = 1;
                mav.setViewName("redirect:/aff/index");
            } else {
                mav.setViewName("error");
                mav.addObject(ApplicationConstant.SESSION_ERR_MSG, "PAGE NOT FOUND，请返回登录页继续操作");
                return mav;
            }
            String username = request.getParameter("username"), password = request.getParameter("password"),
                    affirmpw = request.getParameter("affirmpw"), qq = request.getParameter("qq"),
                    phone = request.getParameter("phone"), bankname = request.getParameter("bankname"),
                    branch = request.getParameter("bankbranch"), account = request.getParameter("bankaccount"),
                    banknum = request.getParameter("banknum");
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(affirmpw) || StringUtils.isEmpty(qq)) {
                mav.setViewName("error");
                mav.addObject(ApplicationConstant.SESSION_ERR_MSG, "注册信息不能空，请返回注册页继续操作");
                return mav;
            }
            AdsUser usertemp = cacheAdsUser.single(username);
            if (null != usertemp && usertemp.getUid() > 0) {
                mav.setViewName("error");
                mav.addObject(ApplicationConstant.SESSION_ERR_MSG, "用户名已存在，请返回注册页继续操作");
                return mav;
            }
            if (!StringUtils.equals(password, affirmpw)) {
                mav.setViewName("error");
                mav.addObject(ApplicationConstant.SESSION_ERR_MSG, "前后密码不一致，请返回注册页继续操作");
                return mav;
            }
            String ip = Tool.obtainRequestIp(request);
            PasswordEncoder encoder = new StandardPasswordEncoder();
            AdsUser user = new AdsUser();
            user.setBankbranch(branch);
            user.setBankaccount(account);
            user.setBankname(bankname);
            user.setBanknum(banknum);
            user.setChartype(1);
            user.setDaymoney(0.0);
            user.setDeduction("");
            user.setIdcard("");
            user.setLimitdiv(0);
            user.setLimitdivheight(0);
            user.setLimitplan("");
            user.setLimitpop(0);
            user.setLimiturl(0);
            user.setLoginip(null);
            user.setLogintime(null);
            user.setMemo("");
            user.setMobile(phone);
            user.setMoney(0.0);
            user.setMonthmoney(0.0);
            user.setPassword(encoder.encode(password));
            user.setPaytype(2);
            user.setQq(qq);
            user.setRealname("");
            user.setRegip(ip);
            user.setRegtime(new Date());
            user.setRuntype(0);
            user.setServiceid(0L);
            user.setUsername(username);
            user.setUstatus(0);
            user.setUtype(utype);
            user.setWeekmoney(0.0);
            user.setXmoney(0.0);
            user = adsUserService.saveOne(user);
            cacheAdsUser.save(user);
            session.setAttribute(ApplicationConstant.SESSION_USER_CONTEXT, user);
            session.removeAttribute(ApplicationConstant.SESSION_ERR_MSG);

            // 记录操作日志
            UserOperateLog log = new UserOperateLog();
            log.setAddTime(new Date());
            log.setIp(ip);
            log.setOstatus(0);
            log.setOtype(utype);
            log.setUtype(utype);
            log.setUid(user.getUid());
            log.setOvalue("wap registe");
            operateLogService.saveOne(log);
        } catch (Exception e) {
            mav.setViewName("error");
            mav.addObject(ApplicationConstant.SESSION_ERR_MSG, "未知错误，请返回登录页继续操作");
            logger.error("register error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/logout"}, method = {RequestMethod.GET})
    public ModelAndView logout(HttpSession session) {
        ModelAndView mav = new ModelAndView();
        try {
            session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            mav.setViewName("redirect:/index");
        } catch (Exception e) {
            logger.error("logout error: ", e);
        }
        return mav;
    }
}
