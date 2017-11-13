package io.union.admin.controller;

import io.union.admin.common.redis.CacheAdsUser;
import io.union.admin.common.zoo.ZooUtils;
import io.union.admin.entity.AdsUser;
import io.union.admin.entity.SysManage;
import io.union.admin.entity.UserOperateLog;
import io.union.admin.service.AdsUserService;
import io.union.admin.service.SysManageService;
import io.union.admin.service.UserOperateLogService;
import io.union.admin.util.ApplicationConstant;
import io.union.admin.util.ToolUtil;
import net.sf.json.JSONObject;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenmiaoyun on 2017/8/7.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
@RequestMapping(value = "/manage")
public class AdsUserController {
    final static Logger LOG = LoggerFactory.getLogger(AdsUserController.class);
    @Autowired
    private AdsUserService adsUserService;
    @Autowired
    private SysManageService sysManageService;

    @Autowired
    private UserOperateLogService userOperateLogService;

    @Autowired
    private CacheAdsUser cacheAdsUser;
    @Autowired
    private ZooUtils zooutils;
    @Value("${spring.zookeeper.lock.user}")
    String userPath;

    /**
     * 站长页面
     *
     * @return
     */
    @RequestMapping(value = "/userList", method = {RequestMethod.GET})
    public ModelAndView userList(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("userList");
        try {
            //列表查询出所有的客服用户名
            List<AdsUser> userNameList = adsUserService.getAllUserName(3);
            mav.addObject("AllUserName", userNameList);
            mav.addObject("uidHidden", request.getParameter("uid"));
        } catch (Exception e) {
            LOG.error("userList error: ", e);
        }
        return mav;
    }

    /**
     * 客服页面
     *
     * @return
     */
    @RequestMapping(value = "/CustomerPage", method = {RequestMethod.GET})
    public ModelAndView CustomerPage() {
        ModelAndView mav = new ModelAndView("Customer");
        try {
            List<AdsUser> list = adsUserService.findAll();
            mav.addObject("pnode", list);
        } catch (Exception e) {
            LOG.error("CustomerPage error: ", e);
        }
        return mav;
    }

    /**
     * 广告商页面
     *
     * @return
     */
    @RequestMapping(value = "/advPage", method = {RequestMethod.GET})
    public ModelAndView advPage(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("advList");
        try {
            // 商务列表
            List<AdsUser> businessList = adsUserService.getAllUserName(4);
            mav.addObject("businessList", businessList);
            mav.addObject("uid", request.getParameter("uid"));
        } catch (Exception e) {
            LOG.error("advPage error: ", e);
        }
        return mav;
    }

    /**
     * 商务页面
     *
     * @return
     */
    @RequestMapping(value = "/businessPage", method = {RequestMethod.GET})
    public ModelAndView businessPage() {
        ModelAndView mav = new ModelAndView("business");
        try {
            List<AdsUser> list = adsUserService.findAll();
            mav.addObject("pnode", list);
        } catch (Exception e) {
            LOG.error("businessPage error: ", e);
        }
        return mav;
    }

    /**
     * 分页查询站长信息
     *
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/adsUserList", method = {RequestMethod.GET})
    public String adsUserList(@RequestParam(defaultValue = "0", required = false) int page, @RequestParam(defaultValue = "50", required = false) int size) {
        String jsonResult = "";
        try {
            Page<AdsUser> list = adsUserService.findAll(1, 8, page, size);
            if (null != list) {
                jsonResult = ToolUtil.tableFormat(list);
            }
        } catch (Exception e) {
            LOG.error("userData error: ", e);
            JSONObject err = new JSONObject();
            err.put("result", e.getMessage());
            jsonResult = err.toString();
        } finally {
            return jsonResult;
        }
    }

    /**
     * 添加用户的信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/usersave"}, method = {RequestMethod.POST})
    public String userSave(HttpServletRequest request) {
        String jsonResult = "success";
        Map<String, String> result = new HashMap<>();
        result.put("result", jsonResult);
        try {
            String username = request.getParameter("username"), password = request.getParameter("password"),
                    utype = request.getParameter("utype"), qq = request.getParameter("qq"),
                    realname = request.getParameter("realname"), serviceid = request.getParameter("serviceid");
            AdsUser adsUser = new AdsUser();
            if (null != password && !"".equals(password)) {
                PasswordEncoder passwordEncoder = new StandardPasswordEncoder();
                adsUser.setPassword(passwordEncoder.encode(password));
            }
            adsUser.setUsername(username);
            adsUser.setUtype(Integer.valueOf(utype));
            adsUser.setQq(qq);
            adsUser.setRealname(realname);
            adsUser.setRegtime(new Date());
            adsUser.setRegip(request.getRemoteAddr());
            adsUser.setMoney(0.0000);
            adsUser.setDaymoney(0.0000);
            adsUser.setWeekmoney(0.0000);
            adsUser.setMonthmoney(0.0000);
            adsUser.setXmoney(0.0000);

            if (StringUtils.hasText(serviceid)) {
                adsUser.setServiceid(Long.valueOf(serviceid));
            }else {
                adsUser.setServiceid(0l);
            }
            adsUser.setLimiturl(Integer.valueOf(0));
            adsUser.setLimitdiv(Integer.valueOf(0));
            adsUser.setLimitdivheight(Integer.valueOf(0));
            adsUser.setLimitpop(Integer.valueOf(0));
            adsUser.setUstatus(Integer.valueOf(0));
            adsUser.setRuntype(Integer.valueOf(0));
            adsUser.setPaytype(Integer.valueOf(2));
            adsUser.setChartype(Integer.valueOf(2));
            adsUserService.saveOne(adsUser);
            cacheAdsUser.save(adsUser);
            // 客服，商务 需要放入缓存 ADS_KEFU_ID_MAP
            if (adsUser != null && (adsUser.getUtype() == 3 || adsUser.getUtype() == 4)) {
                cacheAdsUser.saveStaff(adsUser);
            }
            jsonResult = JSONObject.fromObject(result).toString();
        } catch (Exception e) {
            LOG.error("userSave error: ", e);
            result.put("result", e.getMessage());
            jsonResult = JSONObject.fromObject(result).toString();
        }
        return jsonResult;
    }

    /**
     * 网站主 新建
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/addAdsUser"}, method = {RequestMethod.POST})
    public String addAdsUser(HttpServletRequest request) {
        String jsonResult = "success";
        Map<String, String> result = new HashMap<>();
        result.put("result", jsonResult);
        try {
            String username = request.getParameter("username"),
                    utype = request.getParameter("utype");
            AdsUser adsUser = new AdsUser();
            Map map = new HashMap();
            String password = request.getParameter("password");//密码
            String memo = request.getParameter("memo");//站长说明
            String serviceid = request.getParameter("serviceid");//专属客服
            String bankname = request.getParameter("bankname");//收款银行名称
            String realname = request.getParameter("realname");//真实姓名
            String qq = request.getParameter("qq");//QQ
            String mobile = request.getParameter("mobile");//电话
            String ID = request.getParameter("idcard");//身份证号码
            String bankbranch = request.getParameter("bankbranch");//开户分行
            String banknum = request.getParameter("banknum");//收款账号
            String limitplan = request.getParameter("limitplan");//limitplan
            String cpc = request.getParameter("cpc");//cpc扣量
            String cpv = request.getParameter("cpv");//cpv扣量
            String cpa = request.getParameter("cpa");//cpa扣量
            String cps = request.getParameter("cps");//cps扣量
            String cpm = request.getParameter("cpm");//cpv扣量
            String limiturl = request.getParameter("limiturl");
            String limitdiv = request.getParameter("limitdiv");
            String limitdivheight = request.getParameter("limitdivheight");
            String limitpop = request.getParameter("limitpop");
            String runtype = request.getParameter("runtype");
            String paytype = request.getParameter("paytype");
            String chartype = request.getParameter("chartype");
            String bankaccount = request.getParameter("bankaccount");
            if (cpc != "" && cpc != null) {
                if (Long.valueOf(cpc) < 0 || Long.valueOf(cpc) > 100) {
                    result.put("result", "cpc不能小于0与大于100");
                    jsonResult = JSONObject.fromObject(result).toString();
                    return jsonResult;
                }
                map.put("cpc", cpc);
            }
            if (cpv != "" && cpv != null) {
                if (Long.valueOf(cpv) < 0 || Long.valueOf(cpv) > 100) {
                    result.put("result", "cpv不能小于0与大于100");
                    jsonResult = JSONObject.fromObject(result).toString();
                    return jsonResult;
                }
                map.put("cpv", cpv);
            }
            if (cpa != "" && cpa != null) {
                if (Long.valueOf(cpa) < 0 || Long.valueOf(cpa) > 100) {
                    result.put("result", "cpa不能小于0与大于100");
                    jsonResult = JSONObject.fromObject(result).toString();
                    return jsonResult;
                }
                map.put("cpa", cpa);
            }
            if (cps != "" && cps != null) {
                if (Long.valueOf(cps) < 0 || Long.valueOf(cps) > 100) {
                    result.put("result", "cps不能小于0与大于100");
                    jsonResult = JSONObject.fromObject(result).toString();
                    return jsonResult;
                }
                map.put("cps", cps);
            }
            if (cpm != "" && cpm != null) {
                if (Long.valueOf(cpm) < 0 || Long.valueOf(cpm) > 100) {
                    result.put("result", "cpm不能小于0与大于100");
                    jsonResult = JSONObject.fromObject(result).toString();
                    return jsonResult;
                }
                map.put("cpm", cpm);
            }
            JSONObject json = JSONObject.fromObject(map);
            if (null != password && !"".equals(password)) {
                PasswordEncoder passwordEncoder = new StandardPasswordEncoder();
                adsUser.setPassword(passwordEncoder.encode(password));
            }
            if (null != serviceid && !"".equals(serviceid)) {
                adsUser.setServiceid(Long.valueOf(serviceid));
            } else {
                adsUser.setServiceid(Long.valueOf(0));
            }
            if (null != limitdivheight && !"".equals(limitdivheight)) {
                adsUser.setLimitdivheight(Integer.valueOf(limitdivheight));
            } else {
                adsUser.setLimitdivheight(Integer.valueOf(0));
            }
            adsUser.setMobile(mobile);
            adsUser.setDeduction(json.toString());
            adsUser.setMemo(memo);
            adsUser.setBankname(bankname);
            adsUser.setRealname(realname);
            adsUser.setQq(qq);
            adsUser.setBankbranch(bankbranch);
            adsUser.setBanknum(banknum);
            adsUser.setLimitplan(limitplan);
            adsUser.setLimiturl(Integer.valueOf(limiturl));
            adsUser.setLimitdiv(Integer.valueOf(limitdiv));
            adsUser.setLimitpop(Integer.valueOf(limitpop));
            adsUser.setRuntype(Integer.valueOf(runtype));
            adsUser.setPaytype(Integer.valueOf(paytype));
            adsUser.setChartype(Integer.valueOf(chartype));
            adsUser.setIdcard(ID);
            adsUser.setRegtime(new Date());
            adsUser.setRegip(request.getRemoteAddr());
            adsUser.setMoney(0.0000);
            adsUser.setDaymoney(0.0000);
            adsUser.setWeekmoney(0.0000);
            adsUser.setMonthmoney(0.0000);
            adsUser.setXmoney(0.0000);
            adsUser.setUsername(username);
            adsUser.setUstatus(Integer.valueOf(0));
            adsUser.setUtype(Integer.valueOf(utype));
            adsUser.setBankaccount(bankaccount);
            adsUserService.saveOne(adsUser);
            cacheAdsUser.save(adsUser);
            jsonResult = JSONObject.fromObject(result).toString();
        } catch (Exception e) {
            LOG.error("addAdsUser error: ", e);
            result.put("result", e.getMessage());
            jsonResult = JSONObject.fromObject(result).toString();
        }
        return jsonResult;
    }

    /**
     * 客服- 广告商-商务 修改用户信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
    public String updateUser(HttpServletRequest request) {
        String jsonResult = "success";
        Map<String, String> result = new HashMap<>();
        result.put("result", jsonResult);
        InterProcessMutex lock = null;
        try {
            Map map = new HashMap();
            String uid = request.getParameter("uid");//uid
            String username = request.getParameter("username2");
            String password = request.getParameter("password");//密码
            String memo = request.getParameter("memo");//
            String serviceid = request.getParameter("serviceid");
            String realname = request.getParameter("realname");//真实姓名
            String qq = request.getParameter("qq");//QQ
            String mobile = request.getParameter("mobile");//电话
            String ID = request.getParameter("idcard");//身份证号码
            AdsUser adsUser;
            JSONObject json = JSONObject.fromObject(map);
            lock = zooutils.buildLock(userPath + uid);
            if (lock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                adsUser = cacheAdsUser.single(Long.valueOf(uid));
                if (adsUser == null) {
                    adsUser = adsUserService.findOne(Long.valueOf(uid));
                }
                if (null != password && !"".equals(password)) {
                    PasswordEncoder passwordEncoder = new StandardPasswordEncoder();
                    adsUser.setPassword(passwordEncoder.encode(password));
                }
                if (StringUtils.hasText(serviceid)) {
                    adsUser.setServiceid(Long.valueOf(serviceid));
                }
                adsUser.setMobile(mobile);
                adsUser.setMemo(memo);
                adsUser.setUsername(username);
                adsUser.setRealname(realname);
                adsUser.setQq(qq);
                adsUser.setIdcard(ID);
                adsUserService.saveOne(adsUser);
                cacheAdsUser.save(adsUser);
                jsonResult = JSONObject.fromObject(result).toString();
            }
        } catch (Exception e) {
            LOG.error("updateUser error: ", e);
            result.put("result", e.getMessage());
            jsonResult = JSONObject.fromObject(result).toString();
        } finally {
            zooutils.release(lock);
        }
        return jsonResult;
    }

    /**
     * 网站主  编辑
     * @param request
     * @return
     */
    @RequestMapping(value = "/updateAdsUser", method = RequestMethod.POST)
    public String updateAdsUser(HttpServletRequest request) {
        String jsonResult = "success";
        Map<String, String> result = new HashMap<>();
        result.put("result", jsonResult);
        InterProcessMutex lock = null;
        try {
            Map map = new HashMap();
            String uid = request.getParameter("uid");//uid
            String password = request.getParameter("password");//密码
            String memo = request.getParameter("memo");//站长说明
            String serviceid = request.getParameter("serviceid");//专属客服
            String bankname = request.getParameter("bankname");//收款银行名称
            String realname = request.getParameter("realname");//真实姓名
            String qq = request.getParameter("qq");//QQ
            String mobile = request.getParameter("mobile");//电话
            String ID = request.getParameter("idcard");//身份证号码
            String bankbranch = request.getParameter("bankbranch");//开户分行
            String banknum = request.getParameter("banknum");//收款账号
            String limitplan = request.getParameter("limitplan");//limitplan
            String cpc = request.getParameter("cpc");//cpc扣量
            String cpv = request.getParameter("cpv");//cpv扣量
            String cpa = request.getParameter("cpa");//cpa扣量
            String cps = request.getParameter("cps");//cps扣量
            String cpm = request.getParameter("cpm");//cpv扣量
            String limiturl = request.getParameter("limiturl");
            String limitdiv = request.getParameter("limitdiv");
            String limitdivheight = request.getParameter("limitdivheight");
            String limitpop = request.getParameter("limitpop");
            String runtype = request.getParameter("runtype");
            String paytype = request.getParameter("paytype");
            String chartype = request.getParameter("chartype");
            String bankaccount = request.getParameter("bankaccount");
            if (cpc != "" && cpc != null) {
                if (Long.valueOf(cpc) < 0 || Long.valueOf(cpc) > 100) {
                    result.put("result", "cpc不能小于0与大于100");
                    jsonResult = JSONObject.fromObject(result).toString();
                    return jsonResult;
                }
                map.put("cpc", cpc);
            }
            if (cpv != "" && cpv != null) {
                if (Long.valueOf(cpv) < 0 || Long.valueOf(cpv) > 100) {
                    result.put("result", "cpv不能小于0与大于100");
                    jsonResult = JSONObject.fromObject(result).toString();
                    return jsonResult;
                }
                map.put("cpv", cpv);
            }
            if (cpa != "" && cpa != null) {
                if (Long.valueOf(cpa) < 0 || Long.valueOf(cpa) > 100) {
                    result.put("result", "cpa不能小于0与大于100");
                    jsonResult = JSONObject.fromObject(result).toString();
                    return jsonResult;
                }
                map.put("cpa", cpa);
            }
            if (cps != "" && cps != null) {
                if (Long.valueOf(cps) < 0 || Long.valueOf(cps) > 100) {
                    result.put("result", "cps不能小于0与大于100");
                    jsonResult = JSONObject.fromObject(result).toString();
                    return jsonResult;
                }
                map.put("cps", cps);
            }
            if (cpm != "" && cpm != null) {
                if (Long.valueOf(cpm) < 0 || Long.valueOf(cpm) > 100) {
                    result.put("result", "cpm不能小于0与大于100");
                    jsonResult = JSONObject.fromObject(result).toString();
                    return jsonResult;
                }
                map.put("cpm", cpm);
            }
            AdsUser adsUser;
            JSONObject json = JSONObject.fromObject(map);
            lock = zooutils.buildLock(userPath + uid);
            if (lock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                adsUser = cacheAdsUser.single(Long.valueOf(uid));
                if (adsUser == null) {
                    adsUser = adsUserService.findOne(Long.valueOf(uid));
                }
                if (null != password && !"".equals(password)) {
                    PasswordEncoder passwordEncoder = new StandardPasswordEncoder();
                    adsUser.setPassword(passwordEncoder.encode(password));
                }
                if (null != serviceid && !"".equals(serviceid)) {
                    adsUser.setServiceid(Long.valueOf(serviceid));
                } else {
                    adsUser.setServiceid(Long.valueOf(0));
                }
                if (null != limitdivheight && !"".equals(limitdivheight)) {
                    adsUser.setLimitdivheight(Integer.valueOf(limitdivheight));
                } else {
                    adsUser.setLimitdivheight(Integer.valueOf(0));
                }
                adsUser.setMobile(mobile);
                adsUser.setDeduction(json.toString());
                adsUser.setMemo(memo);
                adsUser.setBankname(bankname);
                adsUser.setRealname(realname);
                adsUser.setQq(qq);
                adsUser.setBankbranch(bankbranch);
                adsUser.setBanknum(banknum);
                adsUser.setLimitplan(limitplan);
                adsUser.setLimiturl(Integer.valueOf(limiturl));
                adsUser.setLimitdiv(Integer.valueOf(limitdiv));//是否暗层
                adsUser.setLimitpop(Integer.valueOf(limitpop));//是否暗弹
                adsUser.setRuntype(Integer.valueOf(runtype));//广告播放形式
                adsUser.setPaytype(Integer.valueOf(paytype));//站长结算周期
                adsUser.setChartype(Integer.valueOf(chartype));//站长24小时内的计费方式
                adsUser.setIdcard(ID);
                adsUser.setBankaccount(bankaccount);
                adsUserService.saveOne(adsUser);
                cacheAdsUser.save(adsUser);
                jsonResult = JSONObject.fromObject(result).toString();
            }
        } catch (Exception e) {
            LOG.error("updateAdsUser error: ", e);
            result.put("result", e.getMessage());
            jsonResult = JSONObject.fromObject(result).toString();
        } finally {
            zooutils.release(lock);
        }
        return jsonResult;
    }

    /**
     * 列表查询出所有的广告商户
     *
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/advList", method = {RequestMethod.GET})
    public String advList(@RequestParam(defaultValue = "0", required = false) int page, @RequestParam(defaultValue = "50", required = false) int size) {
        String jsonResult = "";
        try {
            Page<AdsUser> list = adsUserService.findAll(2, 8, page, size);
            if (null != list) {
                jsonResult = ToolUtil.tableFormat(list);
            }
        } catch (Exception e) {
            LOG.error("advList error: ", e);
            JSONObject err = new JSONObject();
            err.put("result", e.getMessage());
            jsonResult = err.toString();
        } finally {
            return jsonResult;
        }
    }

    /**
     * 列表查询出所有的客服信息
     *
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/customerList", method = {RequestMethod.GET})
    public String CustomerList(@RequestParam(defaultValue = "0", required = false) int page, @RequestParam(defaultValue = "50", required = false) int size) {
        String jsonResult = "";
        try {
            Page<AdsUser> list = adsUserService.findAll(3, 8, page, size);
            if (null != list) {
                jsonResult = ToolUtil.tableFormat(list);
            }
        } catch (Exception e) {
            LOG.error("customerList error: ", e);
            JSONObject err = new JSONObject();
            err.put("result", e.getMessage());
            jsonResult = err.toString();
        } finally {
            return jsonResult;
        }
    }

    /**
     * 列表分页查询出所有的商务信息
     *
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/business", method = {RequestMethod.GET})
    public String business(@RequestParam(defaultValue = "0", required = false) int page, @RequestParam(defaultValue = "50", required = false) int size) {
        String jsonResult = "";
        try {
            Page<AdsUser> list = adsUserService.findAll(4, 8, page, size);
            if (null != list) {
                jsonResult = ToolUtil.tableFormat(list);
            }
        } catch (Exception e) {
            LOG.error("business error: ", e);
            JSONObject err = new JSONObject();
            err.put("result", e.getMessage());
            jsonResult = err.toString();
        } finally {
            return jsonResult;
        }
    }

    /**
     * 根据用户类型和状态条件去列表查询出站长信息
     *
     * @return
     */
    @RequestMapping(value = "/findUustatus", method = {RequestMethod.GET, RequestMethod.POST})
    public String findUustatus(HttpServletRequest request) {
        String jsonResult = "";
        try {
            String ustatus = request.getParameter("ustatus");
            String utype = request.getParameter("utype");
            int page = Integer.valueOf(request.getParameter("page"));
            int size = Integer.valueOf(request.getParameter("size"));
            Page<AdsUser> list = adsUserService.findUustatus(Integer.valueOf(ustatus), Integer.valueOf(utype), page, size);
            if (list != null) {
                jsonResult = ToolUtil.tableFormat(list);
            }
        } catch (Exception e) {
            LOG.error("findUustatus error", e);
            JSONObject err = new JSONObject();
            err.put("result", e.getMessage());
            jsonResult = err.toString();
        }
        return jsonResult;
    }

    /**
     * 根据用户编号和状态修改用户信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    public String updateStatus(HttpServletRequest request) {
        String jsonResult = "success";
        Map<String, String> result = new HashMap<>();
        result.put("result", jsonResult);
        InterProcessMutex lock = null;
        try {
            String uid = request.getParameter("uid");//uid
            String ustatus = request.getParameter("ustatus");//状态
            AdsUser adsUser = null;
            lock = zooutils.buildLock(userPath + uid);
            if (lock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                adsUser = cacheAdsUser.single(Long.valueOf(uid));
                if (adsUser == null) {
                    adsUser = adsUserService.findOne(Long.valueOf(uid));
                }
                adsUser.setUstatus(Integer.valueOf(ustatus));
                adsUserService.saveOne(adsUser);
                cacheAdsUser.save(adsUser);
                jsonResult = JSONObject.fromObject(result).toString();
            }

            // 首页 点击立即处理 “新增网站主”/ “新增广告主”，更新完之后，  user_operate_log 更新, otype:1 站长注册
            int otype = 1;
            if (1 == adsUser.getUtype()) {
                otype = 1;
            } else if (2 == adsUser.getUtype()) {
                otype = 2;
            }
            List<UserOperateLog> updateList = userOperateLogService.findAll(Long.valueOf(uid), otype, adsUser.getRegtime());
            if (updateList != null && updateList.size() > 0) {
                updateList.forEach(userOperateLog -> {
                    userOperateLog.setOstatus(1);
                });
            }
            userOperateLogService.saveList(updateList);
        } catch (Exception e) {
            LOG.error("updateStatus error: ", e);
            result.put("result", e.getMessage());
            jsonResult = JSONObject.fromObject(result).toString();
        } finally {
            zooutils.release(lock);
        }
        return jsonResult;
    }

    /**
     * 有条件就根据条件进行模糊查询，没条件就查询出全部的信息
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/userData", method = {RequestMethod.GET, RequestMethod.POST})
    public String planDataSearch(HttpServletRequest request, HttpServletResponse response) {
        String jsonResult = "";
        try {
            String searchCondition = request.getParameter("searchCondition");
            int page = Integer.valueOf(request.getParameter("page"));
            int size = Integer.valueOf(request.getParameter("size"));
            String utype = request.getParameter("utype");
            String searchBy = request.getParameter("searchValue");
            Page<AdsUser> list = null;
            if (StringUtils.hasText(searchCondition) && StringUtils.hasText(searchBy)) {
                list = adsUserService.findAdsUserByCondition(Integer.valueOf(utype), searchBy, 8, searchCondition, page, size);
            } else {
                list = adsUserService.findAll(Integer.valueOf(utype), 8, page, size);
            }
            if (list != null) {
                jsonResult = ToolUtil.tableFormat(list);
            }
        } catch (Exception e) {
            LOG.error("userData error", e);
            JSONObject err = new JSONObject();
            err.put("result", e.getMessage());
            jsonResult = err.toString();
        } finally {
            return jsonResult;
        }
    }

    @RequestMapping(value = "/getUserByUid", method = {RequestMethod.POST})
    public String getUserByUid(HttpServletRequest request) {
        JSONObject jsonResult = new JSONObject();
        String uid = request.getParameter("uid");
        AdsUser user;
        try {
            user = adsUserService.findOne(Long.valueOf(uid));
            if (user == null || 0 == user.getUid()) {
                jsonResult.put("error", "该用户不存在");
                return jsonResult.toString();
            }

            if (1 != user.getUtype()) {
                jsonResult.put("error", "该用户不是站长");
                return jsonResult.toString();
            }
            jsonResult.put("user", JSONObject.fromObject(user).toString());
        } catch (Exception e) {
            LOG.error("getUserByUid error", e.getMessage());
            jsonResult.put("error", e.getMessage());
        }
        return jsonResult.toString();
    }

    /**
     * 跳转到修改密码页面
     *
     * @return
     */
    @RequestMapping(value = "/updatePWD", method = {RequestMethod.GET})
    public ModelAndView updatePWD() {
        ModelAndView mav = new ModelAndView("updatePwd");
        return mav;
    }

    @RequestMapping(value = "/updatePwdDate", method = RequestMethod.POST)
    public Map<String, Object> updatePwdDate(HttpServletRequest request, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            String passwordOne = request.getParameter("passwordOne");
            String passwordTwo = request.getParameter("passwordTwo");
            String passwordThree = request.getParameter("passwordThree");
            Object object = session.getAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            SysManage sysManage = (SysManage) object;
            PasswordEncoder passwordEncoder = new StandardPasswordEncoder();
            if (!passwordEncoder.matches(passwordOne, sysManage.getManPasswd())) {
                result.put("errorMsg", "输入的密码错误！");
                return result;
            }
            if (!passwordTwo.equals(passwordThree)) {
                result.put("errorMsg", "新密码与确认密码不一致！");
                return result;
            }
            sysManage.setManVersion(sysManage.getManVersion() + 1);
            sysManage.setManPasswd(passwordEncoder.encode(passwordTwo));
            sysManageService.save(sysManage);
        } catch (Exception e) {
            result.put("errorMsg", e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/existUid", method = RequestMethod.POST)
    public String existUid(HttpServletRequest request) {
        JSONObject jsonResult = new JSONObject();
        String affid = request.getParameter("affid");
        String affutype = request.getParameter("affutype");
        String advid = request.getParameter("advid");
        String advutype = request.getParameter("advutype");
        AdsUser affuser;
        AdsUser advuser;
        try {
            affuser = adsUserService.findOne(Long.valueOf(affid));
            advuser = adsUserService.findOne(Long.valueOf(advid));
            if ((affuser == null || 0 == affuser.getUid()) && (advuser == null || 0 == advuser.getUid())) {
                jsonResult.put("error", "该站长,广告主都不存在");
                return jsonResult.toString();
            }
            if (affuser == null || 0 == affuser.getUid()) {
                jsonResult.put("error", "该站长不存在");
                return jsonResult.toString();
            }
            if (advuser == null || 0 == advuser.getUid()) {
                jsonResult.put("error", "该广告主不存在");
                return jsonResult.toString();
            }
            if (Integer.valueOf(affutype) != affuser.getUtype() && Integer.valueOf(advutype) != advuser.getUtype()) {
                jsonResult.put("error", "没有这个网站主ID:" + Integer.valueOf(affid) + "和没有这个广告主ID:" + Integer.valueOf(advid));
                return jsonResult.toString();
            }
            if (Integer.valueOf(affutype) != affuser.getUtype()) {
                jsonResult.put("error", "没有这个网站主ID:" + Integer.valueOf(affid));
                return jsonResult.toString();
            }
            if (Integer.valueOf(advutype) != advuser.getUtype()) {
                jsonResult.put("error", "没有这个广告主ID:" + Integer.valueOf(advid));
                return jsonResult.toString();
            }
            jsonResult.put("affuser", JSONObject.fromObject(affuser).toString());
            jsonResult.put("advuser", JSONObject.fromObject(advuser).toString());
        } catch (Exception e) {
            LOG.error("existUid error", e.getMessage());
            jsonResult.put("error", e.getMessage());
        }
        return jsonResult.toString();
    }
}
