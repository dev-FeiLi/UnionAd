package io.union.admin.controller;

import io.union.admin.entity.AdsAdvpay;
import io.union.admin.entity.AdsUser;
import io.union.admin.entity.SysManage;
import io.union.admin.service.AdsAdvpayService;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shell Li on 2017/8/22.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
@RequestMapping(value = "/manage")
public class AdvPayController {

    final static Logger LOG = LoggerFactory.getLogger(AdvPayController.class);

    @Autowired
    private AdsAdvpayService adsAdvpayService;

    @Autowired
    private AdsUserService adsUserService;


    @RequestMapping(value = "/advPayInit", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView adsList() {
        ModelAndView mv = new ModelAndView("advpay");
        // 广告主类型
        int advType = 2;
        List<AdsUser> userList = adsUserService.findAllByUtype(advType);
        mv.addObject("chargeTotal", adsAdvpayService.getTotolCharge());
        mv.addObject("advUser", userList);
        return mv;
    }

    @RequestMapping(value = "/getAdvPayList", method = {RequestMethod.GET, RequestMethod.POST})
    public JSONObject getAdvPayList(@RequestParam(defaultValue = "0", required = false) int page,
                                    @RequestParam(defaultValue = "20", required = false) int size) {
        JSONObject jsonResult = new JSONObject();
        try {
            Page<AdsAdvpay> pageList = adsAdvpayService.findAll(page, size);
            // 准备前台显示数据
            jsonResult.put("total", pageList.getTotalElements());
            jsonResult.put("pageNumber", pageList.getSize());
            jsonResult.put("rows", JSONArray.fromObject(pageList.getContent()));
        } catch (Exception e) {
            jsonResult.put("error", e.getMessage());
        }

        return jsonResult;
    }

    @RequestMapping(value = "/searchAdvpay", method = {RequestMethod.POST, RequestMethod.GET})
    public JSONObject searchAdvpay(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonResult = new JSONObject();

        List<AdsAdvpay> list = adsAdvpayService.getAdspayListBySearch(request);
        double moneyTotol = 0;
        if (list != null && list.size() != 0) {
            for (AdsAdvpay advpay : list) {
                if (1 == advpay.getPaytype()) {
                    moneyTotol = add(moneyTotol, advpay.getMoney());
                } else {
                    moneyTotol = sub(moneyTotol, advpay.getMoney());
                }
            }
        }
        jsonResult.put("totalMoney", moneyTotol);
        jsonResult.put("total", list.size());
        jsonResult.put("pageNumber", 1);
        jsonResult.put("rows", JSONArray.fromObject(list));

        return jsonResult;
    }

    /**
     * 广告主充值页面方法
     *
     * @param request
     * @param session
     * @return
     */
    @RequestMapping(value = "/adcRecharge", method = {RequestMethod.POST})
    public Map<String, Object> saveData(HttpServletRequest request, HttpSession session, @RequestBody Object model) {
        Map<String, Object> result = new HashMap<>();
        SysManage manager = (SysManage) session.getAttribute("userContext");
        return adsAdvpayService.rechargeAdvpay(model, manager);
    }

    // 导出execl文档
    @RequestMapping(value = "/advpay/downloadExcel", method = {RequestMethod.POST, RequestMethod.GET})
    public void download(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        // 下载文件的默认名称
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStamp = sdf.format(date);
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("广告主充值", "UTF-8")
                + timeStamp + ".xls");
        //编码
        response.setCharacterEncoding("UTF-8");
        List<AdsAdvpay> list = adsAdvpayService.getAdspayListBySearch(request);
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), AdsAdvpay.class, list);
        workbook.write(response.getOutputStream());
    }

    private double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    private double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }
}
