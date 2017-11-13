package io.union.js.controller;

import io.union.js.common.kafka.UnionProducer;
import io.union.js.common.redis.*;
import io.union.js.common.utils.ApplicationConstant;
import io.union.js.common.utils.IPSeeker;
import io.union.js.common.utils.NumberKey;
import io.union.js.common.utils.Tool;
import io.union.js.entity.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by Administrator on 2017/7/13.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
public class JSController {
    final Logger logger = LoggerFactory.getLogger(getClass());

    private final String cookieName = "viewadsids";
    private final String jsessionid = "JSESSIONID"; // OLD
    private final String dianyooSessionId = "DYSESSIONID"; // NEW

    @Autowired
    private CacheAdsAd cacheAdsAd;
    @Autowired
    private CacheAdsPlan cacheAdsPlan;
    @Autowired
    private CacheAdsUser cacheAdsUser;
    @Autowired
    private CacheAdsZone cacheAdsZone;
    @Autowired
    private CacheAdsSite cacheAdsSite;
    @Autowired
    private CacheAdsPop cacheAdsPop;
    @Autowired
    private CacheUnionSetting cacheUnionSetting;

    @Autowired
    private UnionProducer producer;
    @Value("${union.defautl.domain}")
    private String defaultDomain;
    private final long runLimitTime = 800;

    @RequestMapping(value = {"/{letter:^[f-j|m-o|q-t|w-z][a-z]*}-{zoneid:[\\d]*}-{random:[\\d]*}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView jsengine(HttpServletRequest request, HttpServletResponse response, @PathVariable String letter, @PathVariable Long zoneid, @PathVariable Long random) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setContentType("application/javascript; charset=utf-8");

        ModelAndView mav = new ModelAndView("error");
        try {
            long produceStartTime = System.currentTimeMillis();
            //这里存入kafka，以便计算请求数
            producer.send(ApplicationConstant.REQUEST_COUNT_TOPIC, "{\"zoneid\":" + zoneid + ", \"time\":" + System.currentTimeMillis() + "}");
            long produceStopTime = System.currentTimeMillis();
            if (produceStopTime - produceStartTime > runLimitTime) {
                logger.error("produce run time: " + (produceStopTime - produceStartTime) + " ms");
            }

            /*****************************************************************************************************/
            // Session 判断独立用户
            String sessionid = cookieGet(request, dianyooSessionId);
            if (StringUtils.isEmpty(sessionid)) {
                HttpSession session = request.getSession();
                sessionid = session.getId();
            }
            cookieSet(response, dianyooSessionId, sessionid);
            /*****************************************************************************************************/
            // 判断设备，判断搜索引擎蜘蛛
            String useragent = request.getHeader("User-Agent"), device = "0";
            useragent = StringUtils.isEmpty(useragent) ? "" : useragent.toLowerCase();
            if (useragent.contains("mac")) {
                device = "2";
            } else if (useragent.contains("android")) {
                device = "3";
            }
            if (Tool.isSpider(useragent)) {
                mav.addObject("errmsg", "403 FORBIDDEN");
                return mav;
            }
            /*****************************************************************************************************/
            long appendStartTime = System.currentTimeMillis();
            // 暗刷量JS
            String js = cacheUnionSetting.single("append_js");
            if (!StringUtils.isEmpty(js)) {
                List<String> jslist = new ArrayList<>();
                JSONArray array = JSONArray.fromObject(js);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject item = array.getJSONObject(i);
                    int status = item.getInt("state");
                    String limitDevice = item.getString("device"), deny = item.getString("deny"),
                            allow = item.getString("allow");
                    if (status != 0 || (!StringUtils.isEmpty(limitDevice) && !limitDevice.contains(device))
                            || (!StringUtils.isEmpty(deny) && deny.contains(String.valueOf(zoneid)))
                            || (!StringUtils.isEmpty(allow) && !allow.contains(String.valueOf(zoneid)))) {
                        continue;
                    }
                    jslist.add(item.getString("link"));
                }
                mav.addObject("jslist", jslist);
            }
            long appendStopTime = System.currentTimeMillis();
            if (appendStopTime - appendStartTime > runLimitTime) {
                logger.error("append run time: " + (appendStopTime - appendStartTime) + " ms");
            }
            /*****************************************************************************************************/

            long zoneStartTime = System.currentTimeMillis();
            // 从缓存中取出ads_zone，如果取出来的ads_zone是异常的话，则返回错误信息
            AdsZone zone = cacheAdsZone.single(zoneid);
            long zoneStopTime = System.currentTimeMillis();
            if (zoneStopTime - zoneStartTime > runLimitTime) {
                logger.error("zone run time: " + (zoneStopTime - zoneStartTime) + " ms");
            }
            if (null == zone || zone.getZoneid() == NumberKey.ZERO.getValue() || !zoneid.equals(zone.getZoneid())) {
                mav.addObject("errmsg", "no such zone id");
                return mav;
            }

            // 分别从ads_zone和request中取出接下来需要用的各个数据
            Long uid = zone.getUid(), time = System.currentTimeMillis();
            Integer viewtype = zone.getViewtype(), width = zone.getWidth(), height = zone.getHeight(), zstatus = zone.getZstatus();
            String ip = Tool.obtainRequestIp(request), hcontrol = zone.getHcontrol(), plans = zone.getViewadids(), host = request.getHeader("Referer");
            String jdomain = zone.getJdomain(), idomain = zone.getIdomain(), description = zone.getDescription(), viewname = zone.getViewname();
            String defaultCPVView = "h5CPVDefault", defaultCPCView = "h5CPCDefault";
            description = StringUtils.isEmpty(description) ? "7777777777777777" : description;

            // 如果ads_zone是被锁定的话，则返回错误信息
            if (zstatus == NumberKey.NINE.getValue()) {
                mav.addObject("errmsg", "zone id locked");
                return mav;
            }

            // 如果ads_zone中的uid是非法的，则返回错误信息
            if (null == uid || uid <= NumberKey.ZERO.getValue()) {
                mav.addObject("errmsg", "user not found");
                return mav;
            }
            // 从缓存中取出ads_user，如果取出来的ads_user是异常的话，则返回错误信息
            long affStartTime = System.currentTimeMillis();
            AdsUser user = cacheAdsUser.single(uid);
            long affStopTime = System.currentTimeMillis();
            if (affStopTime - affStartTime > runLimitTime) {
                logger.error("aff run time: " + (affStopTime - affStartTime) + " ms");
            }
            if (null == user || user.getUid() == NumberKey.ZERO.getValue() || !uid.equals(user.getUid())) {
                mav.addObject("errmsg", "no such user");
                return mav;
            }

            Integer ustatus = user.getUstatus(), limiturl = user.getLimiturl(), runtype = user.getRuntype();
            if (ustatus != NumberKey.ONE.getValue()) {
                mav.addObject("errmsg", "user locked");
                return mav;
            }
            // 域名限制
            if (limiturl == NumberKey.ZERO.getValue()) {
                List<AdsSite> sitelist = cacheAdsSite.list(uid);
                if (null == sitelist || sitelist.size() == 0) {
                    mav.addObject("errmsg", "site limit and site size zero");
                    return mav;
                }
                host = Tool.host(host);
                boolean siteMatch = false;
                for (AdsSite site : sitelist) {
                    String siteurl = site.getSiteurl();
                    if (host.contains(siteurl)) {
                        siteMatch = true;
                        break;
                    }
                }
                if (!siteMatch) {
                    mav.addObject("errmsg", "site limit and site not match");
                    return mav;
                }
            }

            long adStartTime = System.currentTimeMillis();
            List<AdsAd> list = cacheAdsAd.listByTypeAndSize(viewtype, width, height);
            long adStopTime = System.currentTimeMillis();
            if (adStopTime - adStartTime > runLimitTime) {
                logger.error("ad run time: " + (adStopTime - adStartTime) + " ms");
            }
            if (null == list || list.size() == 0) {
                mav.addObject("errmsg", "no ads");
                return mav;
            }
            List<AdsAd> resultlist = new ArrayList<>(), templist = new ArrayList<>();
            for (AdsAd ad : list) {
                if (null != ad && ad.getAstatus() == 0) {
                    resultlist.add(ad);
                }
            }
            if (runtype == 0) { // 有序随机
                String cookieAds = cookieGet(request, cookieName);
                if (StringUtils.isEmpty(cookieAds)) {
                    if (!StringUtils.isEmpty(plans)) {
                        String[] ads = plans.split(",");
                        for (String id : ads) {
                            if (!StringUtils.isEmpty(id) && !"0".equals(id)) {
                                long singleAdStartTime = System.currentTimeMillis();
                                AdsAd item = cacheAdsAd.single(Long.valueOf(id));
                                long singleAdStopTime = System.currentTimeMillis();
                                if (singleAdStopTime - singleAdStartTime > runLimitTime) {
                                    logger.error("single ad: " + id + " run time: " + (singleAdStopTime - singleAdStartTime) + " ms");
                                }
                                if (null != item && item.getAstatus() == 0) {
                                    templist.add(item);
                                }
                            }
                        }
                    }
                } else {
                    String[] ads = cookieAds.split(",");
                    for (String id : ads) {
                        if (!StringUtils.isEmpty(id) && !"0".equals(id)) {
                            long singleAdStartTime = System.currentTimeMillis();
                            AdsAd item = cacheAdsAd.single(Long.valueOf(id));
                            long singleAdStopTime = System.currentTimeMillis();
                            if (singleAdStopTime - singleAdStartTime > runLimitTime) {
                                logger.error("single ad: " + id + " run time: " + (singleAdStopTime - singleAdStartTime) + " ms");
                            }
                            if (null != item && item.getAstatus() == 0) {
                                templist.add(item);
                            }
                        }
                    }
                }
                if (templist.size() > 0) {
                    resultlist = templist;
                }
            }
            Iterator<AdsAd> iterator = resultlist.iterator();
            while (iterator.hasNext()) {
                AdsAd item = iterator.next();

                long singlePlanStartTime = System.currentTimeMillis();
                AdsPlan plan = cacheAdsPlan.single(item.getPlanid());
                long singlePlanStopTime = System.currentTimeMillis();
                if (singlePlanStopTime - singlePlanStartTime > runLimitTime) {
                    logger.error("single plan: " + item.getPlanid() + " run time: " + (singlePlanStopTime - singlePlanStartTime) + " ms");
                }
                // 是否存在该ID的计划
                if (null == plan || plan.getPlanid() == null || plan.getPlanid() == 0L) {
                    iterator.remove();
                    continue;
                }
                // 计划状态是否正常
                if (plan.getPstatus() != NumberKey.ZERO.getValue()) {
                    iterator.remove();
                    continue;
                }
                // 站长是否屏蔽该计划，这里只用字符串的contains，其实不严谨，暂时就这样
                String limitplan = user.getLimitplan();
                if (null != limitplan && limitplan.contains(String.valueOf(plan.getPlanid()))) {
                    iterator.remove();
                    continue;
                }
                // 计划是否屏蔽该站长，这里只用字符串的contains，其实不严谨，暂时就这样
                String limituid = plan.getLimituid();
                if (null != limituid && limituid.contains(String.valueOf(uid))) {
                    iterator.remove();
                    continue;
                }
                // 是否限制设备
                String limitdevice = plan.getLimitdevice();
                if (!StringUtils.isEmpty(limitdevice)) {
                    List<String> devices = new ArrayList<>(Arrays.asList(limitdevice.split(",")));
                    if (!devices.contains(device)) {
                        iterator.remove();
                        continue;
                    }
                }
                // 计划是否在有效时间内
                Date starttime = plan.getStarttime(), stoptime = plan.getStoptime();
                if (null != starttime && time < starttime.getTime()) {
                    iterator.remove();
                    continue;
                }
                if (null != stoptime && time > stoptime.getTime()) {
                    iterator.remove();
                    continue;
                }
                // 计划是否在周期时间内
                LocalDateTime dateTime = LocalDateTime.now();
                int week = dateTime.getDayOfWeek().getValue(), hour = dateTime.getHour();
                String limittime = plan.getLimittime();
                if (!StringUtils.isEmpty(limittime)) {
                    boolean islimit = true;
                    JSONObject timeJson = JSONObject.fromObject(limittime);
                    Object limitObject = timeJson.get(String.valueOf(week));
                    if (null != limitObject) {
                        JSONArray timeArray = JSONArray.fromObject(limitObject);
                        if (timeArray.contains(String.valueOf(hour))) {
                            islimit = false;
                        }
                    }
                    if (islimit) {
                        iterator.remove();
                        continue;
                    }
                }
                // 是否有地区限制
                IPSeeker ipSeeker = IPSeeker.getInstance();
                String area = ipSeeker.getCountry(ip), limitarea = plan.getLimitarea();
                boolean islimit = true;
                if (!StringUtils.isEmpty(limitarea)) {
                    String[] areas = limitarea.split(",");
                    for (String i : areas) {
                        if (area.contains(i)) {
                            islimit = false;
                        }
                    }
                } else {
                    islimit = false;
                }
                if (islimit) {
                    iterator.remove();
                    continue;
                }
                // 是否有网站类型限制
                String limittype = plan.getLimittype();
                boolean istypelimit = true;
                if (null != limittype && !"".equals(limittype)) {
                    // 允许投放的网站类型ID列表
                    // 如果站长是 不限制域名，而且域名没提交的话，那不显示该广告
                    List<String> types = new ArrayList<>(Arrays.asList(limittype.split(",")));
                    AdsSite site = cacheAdsSite.single(uid, host);
                    if (null != site && site.getSiteid() > 0) {
                        int sitetype = site.getSitetype();
                        if (types.contains(String.valueOf(sitetype))) {
                            istypelimit = false;
                        }
                    }
                } else {
                    istypelimit = false;
                }
                if (istypelimit) {
                    iterator.remove();
                }
            }
            if (null == resultlist || resultlist.size() == 0) {
                cookieSet(response, cookieName, "");
                mav.addObject("errmsg", "no ads found");
                return mav;
            }
            Long targetAdid = null;
            while (resultlist.size() > 0) {
                int index = weightRandom(resultlist);
                AdsAd ad = resultlist.get(index);
                Long tempAdid = ad.getAdid();
                AdsPlan plan = cacheAdsPlan.single(ad.getPlanid());
                // 是否需要申请，这个功能暂时不做 2017-08-13
                targetAdid = tempAdid;
                Long planid = plan.getPlanid();
                //user中暗层、暗弹，ad中的暗层、暗弹，plan中的暗层、暗弹，hcontrol等信息
                int userlimitpop = user.getLimitpop(), adlimitpop = ad.getLimitpop(), planlimitpop = plan.getLimitpop(),
                        userlimitdiv = user.getLimitdiv(), adlimitdiv = ad.getLimitdiv(), planlimitdiv = plan.getLimitdiv();
                int userdivheight = user.getLimitdivheight(), addivheight = ad.getLimitdivheight(), plandivheight = plan.getLimitdivheight();
                int divheight = 0, adeffect = ad.getAdeffect() == null ? 0 : ad.getAdeffect();
                double clickRate = plan.getClickRate() == null ? 0.0 : plan.getClickRate();
                boolean ispop = false, isdiv = false, isclick = isClick(clickRate);
                // 设置优先级：站长 > 广告 > 计划
                if (userlimitdiv > 0) {
                    divheight = userdivheight;
                    isdiv = islimitdiv("uid", userlimitdiv, uid, ip);
                } else if (adlimitdiv > 0) {
                    divheight = addivheight;
                    isdiv = islimitdiv("aid", adlimitdiv, tempAdid, ip);
                } else if (planlimitdiv > 0) {
                    divheight = plandivheight;
                    isdiv = islimitdiv("pid", planlimitdiv, planid, ip);
                }
                if (userlimitpop > 0) {
                    ispop = islimitpop("uid", userlimitpop, uid, ip);
                } else if (adlimitpop > 0) {
                    ispop = islimitpop("aid", adlimitpop, tempAdid, ip);
                } else if (planlimitpop > 0) {
                    ispop = islimitpop("pid", planlimitpop, planid, ip);
                }
                mav.addObject("ispop", ispop);
                mav.addObject("isclick", isclick);
                mav.addObject("islayer", isdiv);
                mav.addObject("layerheight", divheight);
                mav.addObject("planid", planid);
                mav.addObject("adid", tempAdid);
                mav.addObject("hcontrol", hcontrol);
                mav.addObject("width", width);
                mav.addObject("height", height);
                mav.addObject("description", description);
                mav.addObject("adeffect", adeffect);

                // 点击地址，图片地址，计算地址等广告信息存入模型中
                long clickSettingStartTime = System.currentTimeMillis();
                String clickDomain = cacheUnionSetting.single(String.valueOf(RedisKeys.ADS_SETTING_CLICK_DOMAIN)),
                        clickUri = cacheUnionSetting.single(String.valueOf(RedisKeys.ADS_SETTING_CLICK_URL));
                long clickSettingStopTime = System.currentTimeMillis();
                if (clickSettingStopTime - clickSettingStartTime > runLimitTime) {
                    logger.error("click setting run time: " + (clickSettingStopTime - clickSettingStartTime) + " ms");
                }

                if (StringUtils.isEmpty(clickDomain)) clickDomain = defaultDomain;
                if (!StringUtils.isEmpty(jdomain)) { // 广告位设置了站长二级域名
                    clickDomain = jdomain;
                }
                if (!clickDomain.endsWith("/")) {
                    clickDomain += "/";
                }

                long pvSettingStartTime = System.currentTimeMillis();
                String pvcountDomain = cacheUnionSetting.single(String.valueOf(RedisKeys.ADS_SETTING_COUNT_DOMAIN)),
                        pvcountUri = cacheUnionSetting.single(String.valueOf(RedisKeys.ADS_SETTING_COUNT_URL));
                long pvSettingStopTime = System.currentTimeMillis();
                if (pvSettingStopTime - pvSettingStartTime > runLimitTime) {
                    logger.error("pv setting run time: " + (pvSettingStopTime - pvSettingStartTime) + " ms");
                }

                if (StringUtils.isEmpty(pvcountDomain)) pvcountDomain = defaultDomain;
                if (!StringUtils.isEmpty(jdomain)) { // 广告位设置了站长二级域名
                    pvcountDomain = jdomain;
                }
                if (!pvcountDomain.endsWith("/")) {
                    pvcountDomain += "/";
                }
                String counturl = pvcountDomain + pvcountUri, clickurl = clickDomain + clickUri;

                long imageSettingStartTime = System.currentTimeMillis();
                String imageurl = ad.getImageurl(), imagedomain = cacheUnionSetting.single(String.valueOf(RedisKeys.ADS_SETTING_IMG_URL)),
                        closeimageurl = cacheUnionSetting.single(String.valueOf(RedisKeys.ADS_SETTING_CLOSE_IMAGE)),
                        adimageurl = cacheUnionSetting.single(String.valueOf(RedisKeys.ADS_SETTING_AD_ICON)),
                        jsdomain = cacheUnionSetting.single(String.valueOf(RedisKeys.ADS_SETTING_JS_UR)),
                        cnzzCode = cacheUnionSetting.single(String.valueOf(RedisKeys.ADS_SETTING_CNZZ));
                long imageSettingStopTime = System.currentTimeMillis();
                if (imageSettingStopTime - imageSettingStartTime > runLimitTime * 2) {
                    logger.error("image setting run time: " + (imageSettingStopTime - imageSettingStartTime) + " ms");
                }

                if (!StringUtils.isEmpty(cnzzCode)) {
                    mav.addObject("cnzzCode", jsdomain + cnzzCode);
                }

                if (!StringUtils.isEmpty(imageurl) && !imageurl.startsWith("http")) {
                    if (!StringUtils.isEmpty(idomain)) {  // 广告位设置了站长二级域名
                        imagedomain = idomain;
                    }
                    if (imageurl.startsWith("/") && imagedomain.endsWith("/")) {
                        imageurl = imageurl.substring(1);
                    }
                    imageurl = imagedomain + imageurl;
                }
                String tag = Base64Utils.encodeToString(String.valueOf(System.currentTimeMillis() + "|" + browserinfo(useragent) + "|" + zoneid + "|" + uid + "|" + tempAdid + "|" + planid + "|" + sessionid + "|" + device).getBytes());
                String adclickurl = clickurl + "?tag=" + tag, adcounturl = counturl + "?tag=" + tag + "&src=" + zone.getPaytype();

                // 判断该广告是什么形式，系统默认，还是JS调用，还是API返回的JSON格式
                int dataType = ad.getDataType();
                String clickAdUrl = ad.getAdurl();
                if (dataType == 1) { // 接入其他联盟的JS广告代码
                    List<String> imageTypes = new ArrayList<>(Arrays.asList(".jpg", ".png", ".gif", ".jpeg", ".bmp"));
                    int dotindex = imageurl.lastIndexOf(".");
                    if (dotindex > -1) {
                        String suffname = imageurl.substring(dotindex);
                        if (!imageTypes.contains(suffname)) {
                            mav.addObject("isScriptUrl", true);
                        }
                    } else {
                        mav.addObject("isScriptUrl", true);
                    }
                    if (StringUtils.isEmpty(imageurl) || StringUtils.equalsIgnoreCase(clickAdUrl, imageurl)) {
                        imageurl = clickAdUrl;
                    }
                } else if (dataType == 2) { // api方式JSON数据
                    String targetUrl = imageurl;
                    Map<String, String> params = new HashMap<>();
                    if (StringUtils.isEmpty(imageurl) || StringUtils.equalsIgnoreCase(clickAdUrl, imageurl)) {
                        targetUrl = clickAdUrl;
                    }
                    String urlParams = StringUtils.substringAfter(targetUrl, "?");
                    if (null != urlParams) {
                        String[] paramArray = StringUtils.split(urlParams, "&");
                        for (String item : paramArray) {
                            String[] pairs = item.split("=");
                            params.put(pairs[0], pairs[1]);
                        }
                    }
                    String result = APIController.dspJSONapiRequest(targetUrl, params);
                    try {
                        JSONObject resultJson = JSONObject.fromObject(result);
                        String code = resultJson.getString("code");
                        if ("200".equalsIgnoreCase(code)) {
                            JSONObject data = resultJson.getJSONObject("data");
                            imageurl = data.getString("imageurl");
                            adclickurl = adclickurl + "&jump=" + Base64Utils.encodeToString(data.getString("loadpage").getBytes());
                            JSONArray pvarray = data.getJSONArray("traceshow");
                            mav.addObject("pvupload", pvarray.getString(0).trim());
                            mav.addObject("isJson", true);
                        } else {
                            resultlist.remove(index);
                            continue;
                        }
                    } catch (Exception e) {
                        // logger.error("api error: " + e);
                    }
                }
                mav.addObject("elemName", getRandomString(5));
                mav.addObject("idname", getRandomString(8));
                mav.addObject("clickurl", adclickurl); // 落地页地址
                mav.addObject("imageurl", imageurl); // 广告图片的地址
                mav.addObject("counturl", adcounturl); // pv统计的地址
                mav.addObject("closeimageurl", closeimageurl); // 关闭按钮图片地址
                mav.addObject("adimageurl", adimageurl); // “广告”图标地址
                resultlist.remove(index);
                break;
            }
            // 顺序随机的轮播方式的话，写入cookie
            if (runtype == 0) {
                String allads = "";
                if (resultlist.size() > 0) {
                    List<Long> adids = new ArrayList<>();
                    for (AdsAd item : resultlist) {
                        adids.add(item.getAdid());
                    }
                    allads = StringUtils.join(adids, ",");
                }
                cookieSet(response, cookieName, allads);
            }
            // 如果所有的广告都不符合，则返回错误提示
            if (null == targetAdid) {
                mav.addObject("errmsg", "no match ad found");
                return mav;
            }
            // 依照广告类型，返回对应的视图
            if (viewtype == NumberKey.TWO.getValue()) {
                mav.setViewName(StringUtils.isEmpty(viewname) ? defaultCPVView : viewname);
            } else if (viewtype == NumberKey.THREE.getValue()) {
                mav.setViewName(StringUtils.isEmpty(viewname) ? defaultCPCView : viewname);
            } else {
                mav.addObject("errmsg", "view type not found");
            }
        } catch (Exception e) {
            mav.addObject("errmsg", "server exception found");
            logger.error("JSController zoneid=" + zoneid + " error: ", e);
        }
        return mav;
    }

    private String cookieGet(HttpServletRequest request, String cookieName) {
        try {
            Cookie[] cookies = request.getCookies();
            if (null != cookies && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    if (cookieName.equals(cookie.getName())) {
                        return URLDecoder.decode(cookie.getValue(), "utf-8");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("get cookie error: ", e);
        }
        return null;
    }

    private void cookieSet(HttpServletResponse response, String cookieName, String cookieValue) {
        try {
            cookieValue = URLEncoder.encode(cookieValue, "utf-8");
            Cookie cookie = new Cookie(cookieName, cookieValue);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60);
            response.addCookie(cookie);
        } catch (Exception e) {
            logger.error("cokkie: " + cookieName + ", value: " + cookieValue);
            logger.error("set cookie error: ", e);
        }
    }

    // 暗层
    private boolean islimitdiv(String type, int limitdiv, Long id, String ip) {
        int defaultRadio = 200;
        if (limitdiv == 2) {
            return !cacheAdsPop.pulldiv(type, id, ip);
        } else if (limitdiv == 3) {
            // 随机的话，也要有一个比例，默认20%
            int divrandom = RandomUtils.nextInt(1000);
            if (divrandom < defaultRadio) {
                return true;
            }
        } else if (limitdiv == 4) {
            return true;
        }
        return false;
    }

    // 暗弹
    private boolean islimitpop(String type, int limitpop, Long id, String ip) {
        int defaultRadio = 200;
        if (limitpop == 2) {
            return !cacheAdsPop.pullpop(type, id, ip);
        } else if (limitpop == 3) {
            // 随机的话，也要有一个比例，默认20%
            int divrandom = RandomUtils.nextInt(1000);
            if (divrandom < defaultRadio) {
                return true;
            }
        } else if (limitpop == 4) {
            return true;
        }
        return false;
    }

    // 人为增加点击
    private boolean isClick(double rate) {
        int random = RandomUtils.nextInt(10000), intrate = Double.valueOf(rate * 100).intValue();
        if (random < intrate) return true;
        return false;
    }

    private String browserinfo(String useragent) {
        String browserName = "unknow", browserVersion = "unknow", browserOS = "unknow";
        try {
            String ua = useragent.toLowerCase();
            String[] browsers = {"ucbrowser", "micromessenger", "qqbrowser", "miuibrowser", "baidubrowser", "sogou", "liebao", "chrome", "safari", "edge", "msie"};
            String[] oss = {"android", "iphone", "ipad", "ipod", "itouch", "linux", "mac os", "windows"};
            for (String b : browsers) {
                if (ua.contains(b)) {
                    browserName = b;
                    break;
                }
            }
            for (String o : oss) {
                if (ua.contains(o)) {
                    browserOS = o;
                    break;
                }
            }
            // 只判断chrome 和 safari 的版本号
            int index = ua.indexOf("chrome");
            if (index == -1) index = ua.indexOf("safari");
            if (index >= 0) {
                String ver = ua.substring(index);
                String[] pairs = ver.split(" ");
                if (pairs.length > 0) {
                    String version = pairs[0];
                    String[] vs = version.split("/");
                    if (vs.length == 2) {
                        String bv = vs[1];
                        int length = bv.length(), dex = length;
                        for (int i = 0; i < length; i++) {
                            char c = bv.charAt(i);
                            if (!Character.isDigit(c) && c != '.') {
                                dex = i;
                                break;
                            }
                        }
                        browserVersion = bv.substring(0, dex);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("browserinfo error: ", e);
        }
        return browserName + "|" + browserVersion + "|" + browserOS;
    }

    // 广告权重
    private int weightRandom(List<AdsAd> list) {
        int index = 0;
        try {
            List<Long> nodes = new ArrayList<>();
            Long total = 0L;
            for (AdsAd item : list) {
                AdsPlan plan = cacheAdsPlan.single(item.getPlanid());
                long priority = plan.getPriority() == null ? 0 : plan.getPriority();
                total += priority * 1000;
                nodes.add(total);
            }
            Double random = RandomUtils.nextDouble() * total;
            long rate = random.longValue();
            for (int i = 0; i < nodes.size(); i++) {
                long item = nodes.get(i);
                if (item >= rate) {
                    index = i;
                    break;
                }
            }
        } catch (Exception e) {
            logger.debug("weight random error: ", e);
        }
        return index;
    }

    // html中广告的div属性id，随机生成
    private String getRandomString(int length) {
        //随机字符串的随机字符库
        String KeyString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        int len = KeyString.length();
        for (int i = 0; i < length; i++) {
            sb.append(KeyString.charAt((int) Math.round(Math.random() * (len - 1))));
        }
        return sb.toString();
    }
}
