package io.union.wap.controller;

import io.union.wap.common.redis.*;
import io.union.wap.common.utils.ApplicationConstant;
import io.union.wap.common.utils.Tool;
import io.union.wap.common.zoo.ZooUtils;
import io.union.wap.entity.*;
import io.union.wap.model.AffDateTypeData;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@EnableAutoConfiguration
@Scope("prototype")
public class AffController {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CacheAdsUser cacheAdsUser;
    @Autowired
    private CacheAdsZone cacheAdsZone;
    @Autowired
    private CacheAdsSite cacheAdsSite;
    @Autowired
    private CacheUnionSetting cacheUnionSetting;

    @Autowired
    private AdsAdService adsAdService;
    @Autowired
    private AdsPlanService adsPlanService;
    @Autowired
    private AdsUserService adsUserService;
    @Autowired
    private AdsClassService adsClassService;
    @Autowired
    private AdsSiteService adsSiteService;
    @Autowired
    private AdsZoneService adsZoneService;
    @Autowired
    private AdsAffpayService adsAffpayService;
    @Autowired
    private UnionStatsService unionStatsService;
    @Autowired
    private UserOperateLogService operateLogService;
    @Autowired
    private ZooUtils zooutils;
    @Value("${spring.zookeeper.lock.user}")
    String userPath;

    @RequestMapping(value = {"/aff/index"}, method = {RequestMethod.GET})
    public ModelAndView affindex(HttpServletRequest request, HttpSession session) {
        ModelAndView mav = new ModelAndView("affindex");
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
                    mav.setViewName("redirect:/aff/index");
                    return mav;
                }
                session.setAttribute(ApplicationConstant.SESSION_USER_CONTEXT, curuser);
            } else {
                session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                mav.setViewName("redirect:/aff/index");
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

            // 一个礼拜的数据
            List<AffDateTypeData> weekData = new ArrayList<>();
            LocalDate localDate = LocalDate.now();
            String dayBeforeWeek = localDate.minusDays(6).format(DateTimeFormatter.ISO_DATE);
            List<UnionStats> list = unionStatsService.findAffListByTimeAndUid(dayBeforeWeek, uid);
            if (null != list && list.size() > 0) {
                Map<String, AffDateTypeData> map = new HashMap<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                // 计费模式：1cpm，2cpv，3cpc，4cpa，5cps
                for (UnionStats stats : list) {
                    String date = sdf.format(stats.getAddTime());
                    AffDateTypeData data = map.get(date);
                    if (null == data) {
                        data = new AffDateTypeData();
                        data.setDate(date);
                        data.setUid(uid);
                        data.setCps(0.0);
                        data.setCpc(0.0);
                        data.setCpa(0.0);
                        data.setCpm(0.0);
                        data.setCpv(0.0);
                    }
                    int paytype = stats.getPaytype();
                    switch (paytype) {
                        case 1:
                            data.setCpm(stats.getSumpay());
                            break;
                        case 2:
                            data.setCpv(stats.getSumpay());
                            break;
                        case 3:
                            data.setCpc(stats.getSumpay());
                            break;
                        case 4:
                            data.setCpa(stats.getSumpay());
                            break;
                        case 5:
                            data.setCps(stats.getSumpay());
                            break;
                        default:
                            break;
                    }
                    map.put(date, data);
                }
                map.forEach((k, v) -> weekData.add(v));
                weekData.sort((org, tag) -> tag.getDate().compareTo(org.getDate()));
                mav.addObject("data", weekData);
            }
        } catch (Exception e) {
            logger.error("aff index error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/aff/site"}, method = {RequestMethod.GET})
    public ModelAndView affsite(HttpSession session) {
        ModelAndView mav = new ModelAndView("affsite");
        try {
            mav.addObject("menu", "site");
            Object object = session.getAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            AdsUser user = (AdsUser) object;
            Long uid = user.getUid();
            AdsUser curuser = cacheAdsUser.single(uid);
            if (null != curuser) {
                int ustatus = curuser.getUstatus();
                if (ustatus != 1) {
                    session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                    mav.setViewName("redirect:/aff/index");
                    return mav;
                }
                session.setAttribute(ApplicationConstant.SESSION_USER_CONTEXT, curuser);
            } else {
                session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                mav.setViewName("redirect:/aff/index");
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

            List<AdsClass> classes = adsClassService.findAll();
            if (null != classes && classes.size() > 0) {
                Map<Long, AdsClass> map = new HashMap<>();
                for (AdsClass clazz : classes) {
                    Long id = clazz.getId();
                    map.put(id, clazz);
                }
                mav.addObject("classes", map);
            }
            List<AdsSite> sites = adsSiteService.findByUid(uid);
            mav.addObject("sites", sites);
        } catch (Exception e) {
            logger.error("site error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/aff/site"}, method = {RequestMethod.POST})
    public ModelAndView affsitesave(HttpServletRequest request, HttpSession session) {
        ModelAndView mav = new ModelAndView("redirect:/aff/site");
        try {
            Object object = session.getAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            AdsUser user = (AdsUser) object;
            Long uid = user.getUid();
            String sitename = request.getParameter("sitename"), siteurl = request.getParameter("siteurl"),
                    sitetype = request.getParameter("sitetype"), beian = request.getParameter("beian"),
                    dayip = request.getParameter("dayip"), daypv = request.getParameter("daypv"),
                    siteid = request.getParameter("siteid");
            if (StringUtils.isEmpty(sitename) || StringUtils.isEmpty(siteurl) || StringUtils.isEmpty(sitetype)) {
                session.setAttribute(ApplicationConstant.SESSION_ERR_MSG, "网站信息不能空");
                return mav;
            }
            siteurl = Tool.host(siteurl);
            AdsSite site;
            if (StringUtils.isEmpty(siteid)) {
                site = new AdsSite();
                site.setAddTime(new Date());
                site.setUid(uid);
            } else {
                site = adsSiteService.findOne(Long.valueOf(siteid));
            }
            site.setBeian(StringUtils.trimToEmpty(beian));
            site.setDayip(StringUtils.isEmpty(dayip) ? 0L : Long.valueOf(dayip));
            site.setDaypv(StringUtils.isEmpty(daypv) ? 0L : Long.valueOf(daypv));
            site.setSitename(StringUtils.trimToEmpty(sitename));
            site.setSiteurl(StringUtils.trimToEmpty(siteurl));
            site.setSitetype(StringUtils.isEmpty(sitetype) ? 1 : Integer.valueOf(sitetype));
            site.setSstatus(9);
            site = adsSiteService.saveOne(site); // 会员后台就不提供修改的操作了，只提供添加，修改的话，需要找媒介
            cacheAdsSite.save(site);
            session.setAttribute(ApplicationConstant.SESSION_SUC_MSG, "添加成功");

            // 记录操作日志
            String ovalue = "url: " + site.getSiteurl();
            UserOperateLog log = new UserOperateLog();
            log.setAddTime(new Date());
            log.setIp(Tool.obtainRequestIp(request));
            log.setOstatus(0);
            log.setOtype(6);
            log.setUtype(1);
            log.setUid(user.getUid());
            log.setOvalue(ovalue);
            operateLogService.saveOne(log);
        } catch (Exception e) {
            logger.error("site save error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/aff/zone"}, method = {RequestMethod.GET})
    public ModelAndView affzone(HttpSession session) {
        ModelAndView mav = new ModelAndView("affzone");
        try {
            mav.addObject("menu", "zone");
            Object object = session.getAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            AdsUser user = (AdsUser) object;
            Long uid = user.getUid();
            AdsUser curuser = cacheAdsUser.single(uid);
            if (null != curuser) {
                int ustatus = curuser.getUstatus();
                if (ustatus != 1) {
                    session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                    mav.setViewName("redirect:/aff/index");
                    return mav;
                }
                session.setAttribute(ApplicationConstant.SESSION_USER_CONTEXT, curuser);
            } else {
                session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                mav.setViewName("redirect:/aff/index");
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

            List<AdsZone> list = adsZoneService.findByUid(uid);
            if (null != list && list.size() > 0) {
                mav.addObject("zones", list);
            }
            String currentHost = cacheUnionSetting.single(String.valueOf(RedisKeys.ADS_SETTING_JS_URL));
            currentHost = StringUtils.isEmpty(currentHost) ? "http://e.dianyoo.cn/" : currentHost;
            mav.addObject("currentHost", currentHost);

            List<Integer> paylist = new ArrayList<>(), viewlist = new ArrayList<>();
            Set<Integer> viewset = new HashSet<>();
            Set<String> sizeset = new HashSet<>();
            List<AdsAd> adlist = adsAdService.findByStatus(0);
            List<AdsPlan> planlist = adsPlanService.findByStatus(0);
            if (null != adlist && adlist.size() > 0) {
                for (AdsAd ad : adlist) {
                    viewset.add(ad.getAdtype());
                    if (ad.getHeight() > 100) {
                        String size = ad.getWidth() + "x" + ad.getHeight();
                        sizeset.add(size);
                    }
                }
                viewlist.addAll(viewset);
                mav.addObject("views", viewlist);
                mav.addObject("sizes", sizeset);
            }
            if (null != planlist && planlist.size() > 0) {
                for (AdsPlan plan : planlist) {
                    paylist.add(plan.getPaytype());
                }
                mav.addObject("pays", paylist);
            }
        } catch (Exception e) {
            logger.error("zone error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/aff/zone"}, method = {RequestMethod.POST})
    public ModelAndView affzonesave(HttpServletRequest request, HttpSession session) {
        ModelAndView mav = new ModelAndView("redirect:/aff/zone");
        try {
            Object object = session.getAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            AdsUser user = (AdsUser) object;
            Long uid = user.getUid();
            String zoneid = request.getParameter("zoneid"), zonename = request.getParameter("zonename"),
                    paytype = request.getParameter("paytype"), viewtype = request.getParameter("viewtype"),
                    sizetype = request.getParameter("sizetype");
            AdsZone zone;
            if (StringUtils.isEmpty(zoneid)) {
                zone = new AdsZone();
                zone.setAddTime(new Date());
                zone.setUid(uid);
            } else {
                List<AdsSite> sites = adsSiteService.findByUid(uid);
                if (null == sites || sites.size() == 0) {
                    session.setAttribute(ApplicationConstant.SESSION_ERR_MSG, "还没有添加网站，添加新网站了才能添加广告位");
                    return mav;
                } else {
                    boolean valid = false;
                    for (AdsSite item : sites) {
                        if ("0".equals(item.getSstatus())) {
                            valid = true;
                            break;
                        }
                    }
                    if (!valid) {
                        session.setAttribute(ApplicationConstant.SESSION_ERR_MSG, "网站还没审核，审核通过了才能添加广告位");
                        return mav;
                    }
                }
                zone = adsZoneService.findOne(Long.valueOf(zoneid));
            }
            Integer pay = StringUtils.isEmpty(paytype) ? 2 : Integer.valueOf(paytype);
            Integer view = StringUtils.isEmpty(viewtype) ? 2 : Integer.valueOf(viewtype);
            Integer width = 640, height = 200;
            if (!StringUtils.isEmpty(sizetype)) {
                String[] sizes = sizetype.split("x");
                width = Integer.valueOf(sizes[0]);
                height = Integer.valueOf(sizes[1]);
            }
            zone.setZonename(StringUtils.isEmpty(zonename) ? "" : zonename);
            zone.setHcontrol("");
            zone.setHeight(height);
            zone.setWidth(width);
            zone.setPaytype(pay);
            zone.setViewtype(view);
            zone.setViewadids("");
            zone.setZstatus(9);
            zone = adsZoneService.saveOne(zone);
            cacheAdsZone.save(zone);

            session.setAttribute(ApplicationConstant.SESSION_SUC_MSG, "添加成功");
        } catch (Exception e) {
            logger.error("zone save error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/aff/settlement"}, method = {RequestMethod.GET})
    public ModelAndView affsettlement(HttpSession session) {
        ModelAndView mav = new ModelAndView("affsettlement");
        try {
            mav.addObject("menu", "settlement");
            Object object = session.getAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            AdsUser user = (AdsUser) object;
            Long uid = user.getUid();
            AdsUser curuser = cacheAdsUser.single(uid);
            if (null != curuser) {
                int ustatus = curuser.getUstatus();
                if (ustatus != 1) {
                    session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                    mav.setViewName("redirect:/aff/index");
                    return mav;
                }
                session.setAttribute(ApplicationConstant.SESSION_USER_CONTEXT, curuser);
            } else {
                session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                mav.setViewName("redirect:/aff/index");
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

            List<AdsAffpay> paylog = adsAffpayService.findByUid(uid);
            if (null != paylog && paylog.size() > 0) {
                mav.addObject("paylist", paylog);
            }
        } catch (Exception e) {
            logger.error("settlement error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/aff/bank"}, method = {RequestMethod.GET})
    public ModelAndView affbank(HttpSession session) {
        ModelAndView mav = new ModelAndView("affbank");
        try {
            mav.addObject("menu", "bank");
            Object object = session.getAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            AdsUser user = (AdsUser) object;
            Long uid = user.getUid();
            AdsUser curuser = cacheAdsUser.single(uid);
            if (null != curuser) {
                int ustatus = curuser.getUstatus();
                if (ustatus != 1) {
                    session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                    mav.setViewName("redirect:/aff/index");
                    return mav;
                }
                session.setAttribute(ApplicationConstant.SESSION_USER_CONTEXT, curuser);
            } else {
                session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                mav.setViewName("redirect:/aff/index");
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
            logger.error("bank error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/aff/bank"}, method = {RequestMethod.POST})
    public ModelAndView affbanksave(HttpServletRequest request, HttpSession session) {
        ModelAndView mav = new ModelAndView("redirect:/aff/bank");
        try {
            Object object = session.getAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            AdsUser user = (AdsUser) object;
            Long uid = user.getUid();
            InterProcessMutex lock = zooutils.buildLock(userPath + uid);
            try {
                if (lock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                    AdsUser curuser = cacheAdsUser.single(uid);
                    String bankname = request.getParameter("bankname"), bankbranch = request.getParameter("bankbranch"),
                            banknum = request.getParameter("banknum"), bankaccount = request.getParameter("bankaccount");
                    curuser.setBankname(StringUtils.isEmpty(bankname) ? "" : bankname);
                    curuser.setBankbranch(StringUtils.isEmpty(bankbranch) ? "" : bankbranch);
                    curuser.setBanknum(StringUtils.isEmpty(banknum) ? "" : banknum);
                    curuser.setBankaccount(StringUtils.isEmpty(bankaccount) ? "" : bankaccount);
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
            logger.error("bank save error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = {"/aff/basic"}, method = {RequestMethod.GET})
    public ModelAndView affbasic(HttpSession session) {
        ModelAndView mav = new ModelAndView("affbasic");
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
                    mav.setViewName("redirect:/aff/index");
                    return mav;
                }
                session.setAttribute(ApplicationConstant.SESSION_USER_CONTEXT, curuser);
            } else {
                session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                mav.setViewName("redirect:/aff/index");
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

    @RequestMapping(value = {"/aff/basic"}, method = {RequestMethod.POST})
    public ModelAndView affbasicsave(HttpServletRequest request, HttpSession session) {
        ModelAndView mav = new ModelAndView("redirect:/aff/basic");
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

    @RequestMapping(value = {"/aff/pass"}, method = {RequestMethod.GET})
    public ModelAndView affpass(HttpSession session) {
        ModelAndView mav = new ModelAndView("affpass");
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
                    mav.setViewName("redirect:/aff/index");
                    return mav;
                }
                session.setAttribute(ApplicationConstant.SESSION_USER_CONTEXT, curuser);
            } else {
                session.removeAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
                mav.setViewName("redirect:/aff/index");
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

    @RequestMapping(value = {"/aff/pass"}, method = {RequestMethod.POST})
    public ModelAndView affpasssave(HttpServletRequest request, HttpSession session) {
        ModelAndView mav = new ModelAndView("redirect:/aff/pass");
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
            log.setOtype(3);
            log.setUtype(1);
            log.setUid(user.getUid());
            log.setOvalue(ovalue);
            operateLogService.saveOne(log);
        } catch (Exception e) {
            logger.error("basic save error: ", e);
        }
        return mav;
    }
}
