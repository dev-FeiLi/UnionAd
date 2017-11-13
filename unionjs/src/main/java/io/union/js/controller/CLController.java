package io.union.js.controller;

import io.union.js.common.kafka.UnionProducer;
import io.union.js.common.redis.*;
import io.union.js.common.utils.*;
import io.union.js.entity.*;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/25.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
public class CLController {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CacheAdsSite cacheAdsSite;
    @Autowired
    private CacheAdsPlan cacheAdsPlan;
    @Autowired
    private CacheAdsUser cacheAdsUser;
    @Autowired
    private CacheAdsAd cacheAdsAd;
    @Autowired
    private CacheUnionSetting cacheUnionSetting;
    @Autowired
    private CacheAdsIPCheat cacheAdsIPCheat;

    @Autowired
    private UnionProducer producer;

    @Value("${union.cheat.pv}")
    private Long unionCheatPv;
    @Value("${union.cheat.uv}")
    private Long unionCheatUv;
    @Value("${union.cheat.url}")
    private Long unionCheatUrl;
    private final String type = "cpc";
    private final long runLimitTime = 800;

    // ?tag=GPa/e65Bhi7pw9EjfGhieGUzhnhCKfwaF1qX+mqOlIqUeB9apF/gbze4VOqDog3EXgcFqOZeIQLGkB21gcLGgSSWTwJ8E7I93/aVJXtTl2g=&cli=aj0wJm09MCZmPTAmcj0mdT1odHRwJTNBJTJGJTJGd3d3LjlhZHMubmV0JTJGemhvbmd5aTIucGhwJnM9MzIweDU2OCZjPTEmaD00MTA4&pos=152,78
    @RequestMapping(value = {"/{letter:^[clk][a-z]*}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView clickprocessor(HttpServletRequest request, HttpServletResponse response, @PathVariable(required = false) String letter) {
        ModelAndView mav = new ModelAndView("redirect:/error");
        try {
            // logger.info("adclick query: " + request.getQueryString()); // TODO 这里记录点击请求，在日志中统计总点击数，下次大更新去掉

            // Base64Utils.encodeToString(System.currentTimeMillis() + "|" + browserName + "|" + browserVersion + "|" + browserOS + "|" + zoneid + "|" + uid + "|" + tempAdid + "|" + planid + "|" + sessionid + "|" + device)
            // BASE64.encode("j=1&p=20&m=10&f=20.101.11&r=urlencode(http://example.com/reffer)&u=urlencode(http://www.example.com/sitepage)&s=320x568&c=0&h=568&t=title)
            String tag = request.getParameter("tag"), cli = request.getParameter("cli"), pos = request.getParameter("pos"),
                    orgurl = request.getParameter("jump"), userAgent = request.getHeader("User-Agent");
            // 出现了两次urldecode，导致+变成空格
            tag = tag.replaceAll(" ", "+");
            cli = cli.replaceAll(" ", "+");
            String adinfo = new String(Base64Utils.decodeFromString(tag)), cliinfo = new String(Base64Utils.decodeFromString(cli)), ip = Tool.obtainRequestIp(request), host = request.getHeader("Referer");
            String[] aditems = adinfo.split("\\|"), cliitemts = cliinfo.split("&");
            String adurl, browserName = aditems[1], browserVersion = aditems[2], browserOS = aditems[3],
                    zid = aditems[4], uid = aditems[5], aid = aditems[6], pid = aditems[7], sid = aditems[8], device = aditems[9];

            int paytype;
            Long userid = Long.valueOf(uid), zoneid = Long.valueOf(zid), adid = Long.valueOf(aid), planid = Long.valueOf(pid);

            // logger.info("adclick details: U->" + userid + ", Z->" + zoneid + ", P->" + planid + ", A->" + adid); // TODO 这里记录详细点击日志，可分别统计，下次更新去掉

            long affStartTime = System.currentTimeMillis();
            AdsUser user = cacheAdsUser.single(userid);
            long affStopTime = System.currentTimeMillis();
            if (affStopTime - affStartTime > runLimitTime) {
                logger.error("aff run time: " + (affStopTime - affStartTime) + " ms");
            }

            if (null == user || !userid.equals(user.getUid())) {
                logger.error("user id=" + uid + " not found");
                return mav;
            }

            long planStartTime = System.currentTimeMillis();
            AdsPlan plan = cacheAdsPlan.single(planid);
            long planStopTime = System.currentTimeMillis();
            if (planStopTime - planStartTime > runLimitTime) {
                logger.error("plan run time: " + (planStopTime - planStartTime) + " ms");
            }

            if (null == plan || !planid.equals(plan.getPlanid())) {
                logger.error("uid: " + uid + ", zid: " + zid + ", plan id=" + pid + " not found");
                return mav;
            }

            long adStartTime = System.currentTimeMillis();
            AdsAd ad = cacheAdsAd.single(adid);
            long adStopTime = System.currentTimeMillis();
            if (adStopTime - adStartTime > runLimitTime) {
                logger.error("ad run time: " + (adStopTime - adStartTime) + " ms");
            }

            if (null == ad || !adid.equals(ad.getAdid())) {
                logger.error("ad id=" + aid + " not found");
                return mav;
            }
            adurl = ad.getAdurl();
            if (!adurl.startsWith("http")) {
                adurl = "http://" + adurl; // 不清楚是http，还是https，都统一http
            }
            if (!StringUtils.isEmpty(orgurl)) {
                adurl = new String(Base64Utils.decodeFromString(orgurl));
            }
            String isNotify = plan.getIsnotify();
            if (StringUtils.equalsIgnoreCase(isNotify, "Y")) {
                long notifyStartTime = System.currentTimeMillis();
                String notifyDomain = cacheUnionSetting.single(String.valueOf(RedisKeys.ADS_SETTING_COUNT_DOMAIN)), notifyUri = "bnotify";
                long notifyStopTime = System.currentTimeMillis();
                if (notifyStopTime - notifyStartTime > runLimitTime) {
                    logger.error("notify run time: " + (notifyStopTime - notifyStartTime) + " ms");
                }

                notifyDomain += notifyDomain.endsWith("/") ? "" : "/";
                String params = "zoneid=" + zid + "&adid=" + aid + "&sessionid=" + sid + "&ip=" + ip + "&ts=" + System.currentTimeMillis();
                String notifyUrl = Base64Utils.encodeToUrlSafeString((notifyDomain + notifyUri + "?" + params).getBytes());
                adurl = adurl + (adurl.contains("?") ? "&" : "?") + "notify=" + notifyUrl;

                //logger.info("jump url: " + adurl);
            }
            // 计划状态是否正常
            if (plan.getPstatus() != NumberKey.ZERO.getValue()) {
                logger.error("uid: " + uid + ", zid: " + zid + ", plan id=" + pid + " plan locked");
                mav.addObject("jumpToUrl", adurl.replaceAll(" ", ""));
                mav.setViewName("jumpto");// 非正常状态，也给跳落地页，算是给广告主补量
                return mav;
            }
            paytype = plan.getPaytype();
            String java = "", flash = "", reffer = "", sitepage = "", screen = "", cookie = "", title = "";
            for (String param : cliitemts) {
                String[] pair = param.split("=");
                if (pair.length == 2) {
                    if ("j".equals(pair[0])) java = pair[1];
                    else if ("f".equals(pair[0])) flash = pair[1];
                    else if ("r".equals(pair[0])) reffer = URLDecoder.decode(pair[1], "utf-8");
                    else if ("u".equals(pair[0])) sitepage = URLDecoder.decode(pair[1], "utf-8");
                    else if ("s".equals(pair[0])) screen = pair[1];
                    else if ("t".equals(pair[0])) title = URLDecoder.decode(pair[1], "utf-8");
                    else if ("c".equals(pair[0])) cookie = pair[1];
                }
            }
            // 点击坐标
            if (null == pos || pos.indexOf("undefined") > 0) {
                pos = "0,0";
            }
            host = Tool.host(host);
            Long siteid = 0L;

            long siteStartTime = System.currentTimeMillis();
            AdsSite site = cacheAdsSite.single(Long.valueOf(uid), host);
            long siteStopTime = System.currentTimeMillis();
            if (siteStopTime - siteStartTime > runLimitTime) {
                logger.error("site run time: " + (siteStopTime - siteStartTime) + " ms");
            }

            if (null != site && null != site.getSiteid() && site.getSiteid() > 0) {
                siteid = site.getSiteid();
            }
            IPSeeker ipSeeker = IPSeeker.getInstance();
            String country = ipSeeker.getCountry(ip), isp = ipSeeker.getArea(ip);
            String province = IPAreaMap.province(country), city = IPAreaMap.city(province, country);

            long cheatpv = 0, cheatuv = 0, cheaturl = 0;
            String doubt = "N";
            try {
                LocalDate nowdate = LocalDate.now();
                String today = nowdate.format(DateTimeFormatter.ISO_DATE);
                long ipStartTime = System.currentTimeMillis();
                cheatpv = cacheAdsIPCheat.plusPv(type, ip, today);
                cheatuv = cacheAdsIPCheat.pushUv(type, ip, today, sid);
                cheaturl = cacheAdsIPCheat.pushUrl(type, ip, today, host);
                long ipStopTime = System.currentTimeMillis();
                if (ipStopTime - ipStartTime > runLimitTime) {
                    logger.error("ipcheat run time: " + (ipStopTime - ipStartTime) + " ms");
                }

                if (unionCheatPv.compareTo(cheatpv) <= 0 || unionCheatUv.compareTo(cheatuv) <= 0 || unionCheatUrl.compareTo(cheaturl) <= 0) {
                    doubt = "Y";
                }
            } catch (Exception e) {
                logger.error("cheat error: " + e);
            }

            LogClicks clicks = new LogClicks();
            clicks.setAddTime(new Date());
            clicks.setAdid(adid);
            clicks.setBrwName(browserName);
            clicks.setBrwUa(userAgent);
            clicks.setBrwVersion(browserVersion);
            clicks.setClickPos(pos);
            clicks.setCusCookie("1".equals(cookie) ? "Y" : "N");
            clicks.setCusFlash(flash);
            clicks.setCusIp(ip);
            clicks.setCusProvince(province);
            clicks.setCusCity(city);
            clicks.setCusIsp(isp);
            clicks.setCusJava("1".equals(java) ? "Y" : "N");
            clicks.setCusOs(browserOS);
            clicks.setCusScreen(screen);
            clicks.setDeduction("N");
            clicks.setJsessionid(sid);
            clicks.setPageTitle(title);
            clicks.setPlanid(planid);
            clicks.setPaytype(paytype);
            clicks.setRefererUrl(reffer);
            clicks.setSiteid(siteid);
            clicks.setSitePage(sitepage);
            clicks.setUid(Long.valueOf(uid));
            clicks.setZoneid(zoneid);
            clicks.setPrice(0.0);
            clicks.setAdvprice(0.0);
            clicks.setDevice(Integer.valueOf(device));
            clicks.setPvnum(cheatpv);
            clicks.setUvnum(cheatuv);
            clicks.setUrlnum(cheaturl);
            clicks.setDoubt(doubt);
            // 日志存入kafka
            long produceStartTime = System.currentTimeMillis();
            producer.send(ApplicationConstant.CLICK_COUNT_TOPIC, JSONObject.fromObject(clicks).toString());
            long produceStopTime = System.currentTimeMillis();
            if (produceStopTime - produceStartTime > runLimitTime) {
                logger.error("produce run time: " + (produceStopTime - produceStartTime) + " ms");
            }

            // 跳转到落地页
            //response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            //mav.setViewName("redirect:" + adurl);
            mav.addObject("jumpToUrl", adurl.replaceAll(" ", ""));
            mav.setViewName("jumpto");
        } catch (Exception e) {
            logger.error("click error: " + request.getHeader("User-Agent"));
            logger.error("click error: " + request.getQueryString());
            logger.error("click error: " + e);
        }
        return mav;
    }
}
