package io.union.admin.controller;


import io.union.admin.entity.AdsAffpay;
import io.union.admin.entity.AdsAffpayInfo;
import io.union.admin.entity.AdsUser;
import io.union.admin.entity.SysManage;
import io.union.admin.service.AdsAffpayService;
import io.union.admin.service.AdsUserService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Shell Li on 2017/8/22.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
@RequestMapping(value = "/manage")
public class AffPayController {

    final static Logger LOG = LoggerFactory.getLogger(AffPayController.class);

    @Autowired
    private AdsAffpayService adsAffpayService;

    @Autowired
    private AdsUserService adsUserService;

    @RequestMapping(value = "/affPayInit", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView adsList() {
        ModelAndView mv = new ModelAndView("affpay");
        List<AdsAffpay> adsAffpays = adsAffpayService.getTotalMoney();
        if (adsAffpays != null) {
            mv.addObject("moneySum", adsAffpays.get(0).getMoney());
            mv.addObject("realMoneySum", adsAffpays.get(0).getRealmoney());
        } else {
            mv.addObject("moneySum", 0);
            mv.addObject("realMoneySum", 0);
        }
        return mv;
    }

    @RequestMapping(value = "/getAffPayList", method = {RequestMethod.GET, RequestMethod.POST})
    public String getAdvPayList(@RequestParam(defaultValue = "0", required = false) int page,
                                @RequestParam(defaultValue = "20", required = false) int size) {
        JSONObject jsonResult = new JSONObject();
        try {
            Page<AdsAffpay> pageList = adsAffpayService.findAll(page, size);
            // 准备前台显示数据
            jsonResult.put("total", pageList.getTotalElements());
            jsonResult.put("pageNumber", pageList.getSize());
            jsonResult.put("rows", JSONArray.fromObject(pageList.getContent()));
        } catch (Exception e) {
            jsonResult.put("error", e.getMessage());
        }

        return jsonResult.toString();
    }

    @RequestMapping(value = "/searchAffpay", method = {RequestMethod.POST, RequestMethod.GET})
    public String searchAffpay(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonResult = new JSONObject();
        List<AdsAffpay> list = adsAffpayService.getAffpayListBySearch(request);
        double moneyTotol = 0, realMoneyTotal = 0;
        if (list != null && list.size() != 0) {
            for (AdsAffpay affpay : list) {
                moneyTotol = add(moneyTotol, affpay.getMoney());
                realMoneyTotal = add(realMoneyTotal, affpay.getRealmoney());
            }
        }
        jsonResult.put("totalMoney", moneyTotol);
        jsonResult.put("realMoneyTotal", realMoneyTotal);
        jsonResult.put("total", list.size());
        jsonResult.put("rows", JSONArray.fromObject(list));

        return jsonResult.toString();
    }

    @RequestMapping(value = "/affPayMoney", method = {RequestMethod.POST})
    public Map<String, Object> saveData(HttpServletRequest request, HttpSession session, @RequestBody Object model) {
        Map<String, Object> result = new HashMap<>();
        SysManage manager = (SysManage) session.getAttribute("userContext");
        return adsAffpayService.affPayMoney(model, manager);
    }

    @RequestMapping(value = "/affPayMoneyEdit", method = {RequestMethod.POST})
    public Map<String, Object> editData(HttpServletRequest request, HttpSession session, @RequestBody Object model) {
        Map<String, Object> result = new HashMap<>();
        SysManage manager = (SysManage) session.getAttribute("userContext");
        return adsAffpayService.affPayMoneyEdit(model, manager);
    }

    @RequestMapping(value = "/batchUpdateStatus", method = {RequestMethod.POST})
    public Map<String, Object> batchUpdateStatus(HttpServletRequest request, HttpSession session, @RequestBody Object model) {
        JSONObject json = JSONObject.fromObject(model);
        SysManage manager = (SysManage) session.getAttribute("userContext");
        return adsAffpayService.batchUpdate(json, manager);
    }

    // 导出execl文档
    @RequestMapping(value = "/affpay/downloadExcel", method = {RequestMethod.POST, RequestMethod.GET})
    public void download(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        // 下载文件的默认名称
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("_yyyy-MM-dd_HHmmss");
        String timeStamp = sdf.format(date);
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("网站主支付", "UTF-8")
                + timeStamp + ".xls");
        // 编码
        response.setCharacterEncoding("UTF-8");
        List<AdsAffpay> list = adsAffpayService.getAffpayListBySearch(request);
        List<AdsAffpayInfo> excelList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (AdsAffpay affpay : list) {
                AdsUser user = adsUserService.findOne(affpay.getUid());
                AdsAffpayInfo affpayInfo = new AdsAffpayInfo();
                affpayInfo.setUid(affpay.getUid());
                affpayInfo.setUsername(affpay.getUsername());
                affpayInfo.setBankname(user.getBankname());
                affpayInfo.setBankbranch(user.getBankbranch());
                affpayInfo.setBanknum(user.getBanknum());
                affpayInfo.setBankaccount(user.getBankaccount());
                affpayInfo.setMoney(affpay.getMoney());

                if (affpay.getRealmoney() != null) {
                    affpayInfo.setRealmoney(Double.valueOf(new DecimalFormat("#.00").format(affpay.getRealmoney())));
                } else {
                    affpayInfo.setRealmoney(0d);
                }

                affpayInfo.setManAccount(affpay.getManAccount());
                if (affpay.getPaytime() != null) {
                    affpayInfo.setPaytime(new SimpleDateFormat("yyyy-MM-dd").format(affpay.getPaytime()));
                }
                affpayInfo.setAddTime(affpay.getAddTime());
                affpayInfo.setPstatus(affpay.getPstatus());
                affpayInfo.setPaytype(affpay.getPaytype());
                affpayInfo.setPayinfo(affpay.getPayinfo());
                excelList.add(affpayInfo);
            }
        }
        excelList.sort(((o1, o2) -> o1.getBankname().compareTo(o2.getBankname())));
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("网站主支付", "sheet1"), AdsAffpayInfo.class, excelList);
        workbook.write(response.getOutputStream());
    }

    private double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }
}
