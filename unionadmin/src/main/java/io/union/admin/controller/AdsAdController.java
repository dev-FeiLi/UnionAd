package io.union.admin.controller;

import io.union.admin.entity.AdsAd;
import io.union.admin.entity.AdsPlan;
import io.union.admin.entity.UnionSetting;
import io.union.admin.entity.UserOperateLog;
import io.union.admin.service.AdsAdService;
import io.union.admin.service.AdsPlanService;
import io.union.admin.service.UnionSettingService;
import io.union.admin.service.UserOperateLogService;
import io.union.admin.util.ApplicationConstant;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

/**
 * Created by Shell Li on 2017/8/16.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
@RequestMapping(value = "/manage")
public class AdsAdController {

    final static Logger LOG = LoggerFactory.getLogger(AdsAdController.class);


    @Autowired
    private AdsAdService adsAdService;

    @Autowired
    private AdsPlanService adsPlanService;

    @Autowired
    private UnionSettingService unionSettingService;

    @Autowired
    private UserOperateLogService userOperateLogService;

    @RequestMapping(value = "/adslist", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView adsList(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("adslist");
        String planId = request.getParameter("planId");
        String adsid = request.getParameter("adsid");
        if (StringUtils.hasText(planId)) {
            mv.addObject("searchCondition", "planid");
            mv.addObject("searchContent", planId);
        }
        if (StringUtils.hasText(adsid)) {
            mv.addObject("searchCondition", "adsid");
            mv.addObject("searchContent", adsid);
        }

        return mv;
    }

    @RequestMapping(value = "/adData", method = {RequestMethod.POST, RequestMethod.GET})
    public JSONObject dataList(@RequestParam(defaultValue = "0", required = false) int page,
                               @RequestParam(defaultValue = "20", required = false) int size) {
        JSONObject jsonShow = new JSONObject();
        try {
            jsonShow = adsAdService.getAllAdByPageSize(page, size);
        } catch (Exception e) {
            LOG.error("dataList error :", e.getMessage());
        }
        return jsonShow;
    }

    @RequestMapping(value = "/adDataSearch", method = {RequestMethod.GET, RequestMethod.POST})
    public JSONObject adDataSearch(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonShow = new JSONObject();
        try {
            String searchCondition = request.getParameter("searchCondition");
            String searchSize = request.getParameter("searchSize");
            int page = Integer.valueOf(request.getParameter("page"));
            int size = Integer.valueOf(request.getParameter("size"));

            if (StringUtils.hasText(searchCondition)) {
                String searchBy = request.getParameter("searchValue");
                if ("0*0".equals(searchSize)) {
                    jsonShow = adsAdService.getAllAdByCondition(searchCondition, searchBy, "", page, size);
                } else {
                    jsonShow = adsAdService.getAllAdByCondition(searchCondition, searchBy, searchSize, page, size);
                }

            }
        } catch (Exception e) {
            LOG.error("adDataSearch error :", e.getMessage());
        }

        return jsonShow;
    }

    @RequestMapping(value = "/initAd", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView initAd(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new ModelAndView("adinfo");
        String adid = request.getParameter("adid");
        String planId = request.getParameter("planId");
        if (StringUtils.hasText(planId)) {
            mv.addObject("planId", planId);
        }

        AdsAd ad;
        if (StringUtils.hasText(adid)) {
            ad = adsAdService.findOne(Long.valueOf(adid));
            mv.addObject("initType", "编辑广告");
        } else {
            ad = new AdsAd();
            mv.addObject("initType", "新建广告");
        }
        Map<String, List<AdsPlan>> planOptions = adsPlanService.getAllAdsPlan();
        mv.addObject("adInfo", ad);
        mv.addObject("planOptions", planOptions);
        // 广告类型：1移动弹窗，2移动悬浮，3移动横幅
        mv.addObject("adType", new String[]{"1", "2", "3"});
        return mv;
    }

    @RequestMapping(value = "/getAdInfoById", method = {RequestMethod.POST})
    public Map<String, Object> getAdInfoById(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        String adid = request.getParameter("adid");
        AdsAd ad = adsAdService.findOne(Long.valueOf(adid));
        if (StringUtils.hasText(ad.getImageurl()) && !ad.getImageurl().startsWith("http")) {
            UnionSetting unionSetting = unionSettingService.findOne("img_server_domain");
            int offset = unionSetting.getSetvalue().length() - 1;
            String localImgUrl = ad.getImageurl();
            result.put("imagePreView", unionSetting.getSetvalue().substring(0, offset) + localImgUrl);
        } else {
            result.put("imagePreView", ad.getImageurl());
        }
        result.put("ad", ad);

        return result;
    }

    /**
     * “广告列表” -- 更改广告状态
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/adsAdSaveStatus", method = {RequestMethod.POST})
    public Map<String, Object> adsAdSaveStatus(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        String adid = request.getParameter("adid"), astatus = request.getParameter("astatus");
        AdsAd ad = adsAdService.findOne(Long.valueOf(adid));
        AdsPlan adsPlan = adsPlanService.findOne(ad.getPlanid());
        if (adsPlan.getPstatus() == 9 && "0".equals(astatus)) {
            result.put("error", "激活失败: 该广告所属计划的状态是锁定.");
            return result;
        }
        ad.setAstatus(Integer.valueOf(astatus));
        long status = adsAdService.save(ad);
        result.put("status", status);

        // 首页 点击立即处理 “被修改的广告”，更新完选中数据之后，  user_operate_log表更新, otype: 5
        int otype = 5;
        List<UserOperateLog> updateList = userOperateLogService.findAll(ad.getUid(), otype, ad.getAddtime());
        if (updateList != null && updateList.size() > 0) {
            updateList.forEach(userOperateLog -> {
                userOperateLog.setOstatus(1);
            });
        }
        userOperateLogService.saveList(updateList);

        return result;
    }

    @RequestMapping(value = "/saveAdInfo", method = {RequestMethod.POST, RequestMethod.GET})
    public Map<String, Object> savaAd(@RequestParam(value = "imageurl", required = false) MultipartFile[] files, HttpServletRequest request, ModelMap model) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 远程文件的话  一定要是http开头
            if (StringUtils.hasText(request.getParameter("urlfile")) && !request.getParameter("urlfile").startsWith("http")) {
                result.put("error", "远程文件url必须是以http开头的");
                return result;
            }
            // 广告地址 一定要是http开头
            if (StringUtils.hasText(request.getParameter("adurl")) && !request.getParameter("adurl").startsWith("http")) {
                result.put("error", "广告地址必须是以http开头的");
                return result;
            }

            // 处理上传的图片
            List<String> extendlist = new ArrayList<String>(Arrays.asList(".jpg", ".jpeg", ".png", ".gif"));// 允许的图片后缀名
            String parentDir = request.getServletContext().getRealPath("/");// 网站根目录
            // 保存上传文件的路径
            File parentPath = new File(parentDir, ApplicationConstant.AD_IMG_URL); // 存放图片文件的目录
            if (!parentPath.exists()) {
                parentPath.mkdirs();
                String os = System.getProperties().getProperty("os.name");
                if (os != null && os.toLowerCase().indexOf("linux") > -1) {
                    Runtime.getRuntime().exec("chmod 754 -R " + parentPath.getAbsolutePath());
                }
            }

            List<String> images = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                    String fileName = request.getParameter("adid") + "x" + request.getParameter("size").replace("*", "x")
                            + "x" + System.currentTimeMillis() + suffix;
                    if (!extendlist.contains(suffix)) {
                        LOG.error("upload file type error:", "file extend name: " + file.getOriginalFilename() + " invalidate");
                        result.put("error", "file extend name: " + file.getOriginalFilename() + " invalidate");
                        return result;
                    }

                    File targetFile = new File(parentPath, fileName);
                    // 检测是否存在目录
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    file.transferTo(targetFile);
                    /** 将文件的权限设置成Linux 744 **/
                    String os = System.getProperties().getProperty("os.name");
                    if (os != null && os.toLowerCase().indexOf("linux") > -1) {
                        Runtime.getRuntime().exec("chmod 754 -R " + targetFile.getAbsolutePath());
                    }

                    String imageurl = "/" + ApplicationConstant.AD_IMG_URL + fileName;
                    images.add(imageurl);
                }
            }
            long id = adsAdService.save(request, images);
            result.put("adid", id);
        } catch (Exception e) {
            LOG.error("saveAdInfo error:", e);
        }

        return result;
    }
}
