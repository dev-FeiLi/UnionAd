package io.union.admin.controller;

import io.union.admin.common.redis.CacheLockPach;
import io.union.admin.common.redis.CacheUnionSetting;
import io.union.admin.common.zoo.ZooUtils;
import io.union.admin.entity.*;
import io.union.admin.schedule.OperateSaveJob;
import io.union.admin.service.*;
import io.union.admin.util.ApplicationConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/4/11.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
@RequestMapping("/manage")
public class ApplicationIndexController {
    final Logger LOG = LoggerFactory.getLogger(ApplicationIndexController.class);

    private UnionReqtsService unionReqtsService;
    private UnionStatsService unionStatsService;
    private UnionSettingService unionSettingService;
    private StatHourPvService statHourPvService;
    private UserOperateLogService userOperateLogService;
    private SysManageService sysManageService;
    private OperateSaveJob operateSaveJob;
    private CacheUnionSetting cacheUnionSetting;
    private CacheLockPach cacheLockPach;
    private ZooUtils zooUtils;
    private String rootPath;
    private String userPath;

    public ApplicationIndexController(@Autowired UnionReqtsService unionReqtsService, @Autowired UnionStatsService unionStatsService,
                                      @Autowired UnionSettingService unionSettingService, @Autowired StatHourPvService statHourPvService,
                                      @Autowired UserOperateLogService userOperateLogService, @Autowired CacheUnionSetting cacheUnionSetting,
                                      @Autowired ZooUtils zooUtils, @Value("${spring.zookeeper.lock.root}") String rootPath,
                                      @Autowired SysManageService sysManageService, @Autowired OperateSaveJob operateSaveJob,
                                      @Autowired CacheLockPach cacheLockPach, @Value("${spring.zookeeper.lock.user}") String userPath) {
        this.unionReqtsService = unionReqtsService;
        this.unionStatsService = unionStatsService;
        this.unionSettingService = unionSettingService;
        this.userOperateLogService = userOperateLogService;
        this.statHourPvService = statHourPvService;
        this.cacheUnionSetting = cacheUnionSetting;
        this.cacheLockPach = cacheLockPach;
        this.sysManageService = sysManageService;
        this.operateSaveJob = operateSaveJob;
        this.zooUtils = zooUtils;
        this.rootPath = rootPath;
        this.userPath = userPath;
    }

    //首页需要展现系统各种实时数据，以及各个服务器的实时状态信息
    //可以是各种图形报表
    @RequestMapping(value = "/index", method = {RequestMethod.GET})
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("index");
        try {
            // 第一行 6个方格
            long totalreqts = 0, totalpv = 0, totalsettle = 0;
            double totalpay = 0.0, totalincome = 0.0, totalprofit = 0.0;
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            UnionReqts reqts = unionReqtsService.findbyaddtime(today);
            if (null != reqts) {
                totalreqts = reqts.getReqeusts();
            }
            mav.addObject("totalreq", totalreqts);
            StatHourPv hourPv = statHourPvService.findbytime(today);
            if (null != hourPv) {
                totalpv = hourPv.total();
            }
            mav.addObject("totalpv", totalpv);
            UnionStats stats = unionStatsService.findByTime(today);
            if (null != stats && null != stats.getAddTime()) {
                totalsettle = stats.getPaynum();
                totalpay = stats.getSumpay();
                totalincome = stats.getSumadvpay();
                totalprofit = stats.getSumprofit();
            }
            mav.addObject("totalsettle", totalsettle);
            mav.addObject("totalpay", totalpay);
            mav.addObject("totalincome", totalincome);
            mav.addObject("totalprofit", totalprofit);

            // 第二行 4个方格
            long affnewcomer = 0, advnewcomer = 0, sitenewcomer = 0, adupdates = 0;
            List<UserOperateLog> userlogs = userOperateLogService.findAllTypeCount(today);
            if (null != userlogs && userlogs.size() > 0) {
                for (UserOperateLog item : userlogs) {
                    if (item.getOtype() == 1) affnewcomer = item.getId();
                    else if (item.getOtype() == 2) advnewcomer = item.getId();
                    else if (item.getOtype() == 5) sitenewcomer = item.getId();
                    else if (item.getOtype() == 6) adupdates = item.getId();
                }
            }
            mav.addObject("affnewcomer", affnewcomer);
            mav.addObject("advnewcomer", advnewcomer);
            mav.addObject("sitenewcomer", sitenewcomer);
            mav.addObject("adupdates", adupdates);

            // 第三行 3个form更新
            List<UnionSetting> settings = unionSettingService.findAll();
            if (null != settings && settings.size() > 0) {
                for (UnionSetting item : settings) {
                    mav.addObject(item.getSetkey(), item.getSetvalue());
                }
            }
        } catch (Exception e) {
            LOG.error("index error: ", e);
        }
        return mav;
    }

    @RequestMapping(value = "/updatesetting", method = {RequestMethod.POST})
    public void updateSetting(HttpServletRequest request, HttpServletResponse response) {
        String result = "";
        try {
            List<UnionSetting> settings = new ArrayList<>();
            Map<String, String[]> map = request.getParameterMap();
            if (null != map && map.size() > 0) {
                map.forEach((k, v) -> {
                    UnionSetting item = unionSettingService.findOne(k);
                    String value = "";
                    if (null != v && v.length > 0) {
                        value = v[0];
                    }
                    if (null != item && null != item.getSetkey()) {
                        item.setSetvalue(value);
                    }
                    settings.add(item);
                });
            }
            if (settings.size() > 0) {
                unionSettingService.save(settings);
                cacheUnionSetting.save(settings);
            }
            result = "{\"success\":\"success\"}";
        } catch (Exception e) {
            result = "{\"errmsg\":\"" + e.getMessage() + "\"}";
            LOG.error("update setting error: ", e);
        } finally {
            try {
                response.getWriter().write(result);
            } catch (Exception e) {
                LOG.error("response write error: " + e);
            }
        }
    }

    @RequestMapping(value = "/removelock", method = {RequestMethod.POST})
    public void deleteLockNode(HttpServletResponse response) {
        String result = "";
        try {
            Runnable run = () -> {
                Set<String> fields = cacheLockPach.pop();
                if (null != fields) {
                    for (String item : fields) {
                        if (!item.contains(userPath)) {
                            zooUtils.deleteChild(item);
                        }
                    }
                }
            };
            new Thread(run).start();
            result = "{\"success\":\"success\"}";
        } catch (Exception e) {
            result = "{\"errmsg\":\"" + e.getMessage() + "\"}";
            LOG.error("remove lock error: ", e);
        } finally {
            try {
                response.getWriter().write(result);
            } catch (Exception e) {
                LOG.error("response write error: " + e);
            }
        }
    }

    @RequestMapping(value = "/menuchange", method = {RequestMethod.GET})
    public ModelAndView menuchange(HttpServletRequest request, HttpSession session) {
        ModelAndView mav = new ModelAndView("redirect:/manage/index");
        try {
            SysManage manage = (SysManage) session.getAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            manage = sysManageService.findOne(manage.getManId());
            String position = request.getParameter("pos");
            String menuPos = "0".equals(position) ? "sidebar-mini sidebar-collapse" : "layout-top-nav";
            manage.setMenuPos(menuPos);
            operateSaveJob.addManage(manage);
            session.setAttribute(ApplicationConstant.SESSION_USER_CONTEXT, manage);
        } catch (Exception e) {
            LOG.error("change menu error: ", e);
        }
        return mav;
    }
}
