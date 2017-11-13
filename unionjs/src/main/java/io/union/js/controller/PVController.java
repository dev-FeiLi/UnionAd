package io.union.js.controller;

import io.union.js.common.kafka.UnionProducer;
import io.union.js.common.redis.*;
import io.union.js.common.utils.*;
import io.union.js.entity.*;
import net.sf.json.JSONObject;
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

import javax.servlet.http.HttpServletRequest;
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
public class PVController {
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
    private CacheAdsIPCheat cacheAdsIPCheat;
    @Autowired
    private CacheAdsUVCheat cacheAdsUVCheat;

    @Autowired
    private UnionProducer producer;

    @Value("${union.cheat.pv}")
    private Long unionCheatPv;
    @Value("${union.cheat.ip}")
    private Long unionCheatIp;
    @Value("${union.cheat.uv}")
    private Long unionCheatUv;
    @Value("${union.cheat.url}")
    private Long unionCheatUrl;
    private final String type = "cpv";
    private final long runLimitTime = 800;

    // ?tag=GPa/e65Bhi7pw9EjfGhieGUzhnhCKfwaF1qX+mqOlIqUeB9apF/gbze4VOqDog3EXgcFqOZeIQLGkB21gcLGgSSWTwJ8E7I93/aVJXtTl2g=&src=2&cli=aj0wJm09MCZmPTAmcj0mdT1odHRwJTNBJTJGJTJGd3d3LjlhZHMubmV0JTJGemhvbmd5aTIucGhwJnM9MzIweDU2OCZjPTEmaD00MTA4
    @RequestMapping(value = {"/{letter:^[puv][a-z]*}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public void pvprocessor(HttpServletRequest request, @PathVariable(required = false) String letter) {
        try {
            // Base64Utils.encodeToString(System.currentTimeMillis() + "|" + browserName + "|" + browserVersion + "|" + browserOS + "|" + zoneid + "|" + uid + "|" + tempAdid + "|" + planid + "|" + sessionid+ "|" + device)
            // BASE64.encode("j=1&p=20&m=10&f=20.101.11&r=urlencode(http://example.com/reffer)&u=urlencode(http://www.example.com/sitepage)&s=320x568&c=0&h=568&t=title)
            String tag = request.getParameter("tag"), cli = request.getParameter("cli"), userAgent = request.getHeader("User-Agent");
            // 出现了两次urldecode，导致+变成空格
            tag = tag.replaceAll(" ", "+");
            cli = cli.replaceAll(" ", "+");
            String adinfo = new String(Base64Utils.decodeFromString(tag)), cliinfo = new String(Base64Utils.decodeFromString(cli)), ip = Tool.obtainRequestIp(request), host = request.getHeader("Referer");
            String[] aditems = adinfo.split("\\|"), cliitemts = cliinfo.split("&");
            String browserName = aditems[1], browserVersion = aditems[2], browserOS = aditems[3],
                    zid = aditems[4], uid = aditems[5], aid = aditems[6], pid = aditems[7], sid = aditems[8], device = aditems[9];

            // 判断搜索引擎蜘蛛
            if (Tool.isSpider(userAgent)) {
                logger.error("U: " + uid + ", Z: " + zid + ", H: " + host + ", " + ip + " is spider");
                return;
            }

            Long userid = Long.valueOf(uid), zoneid = Long.valueOf(zid), adid = Long.valueOf(aid), planid = Long.valueOf(pid);

            long affStartTime = System.currentTimeMillis();
            AdsUser user = cacheAdsUser.single(userid);
            long affStopTime = System.currentTimeMillis();
            if (affStopTime - affStartTime > runLimitTime) {
                logger.error("aff run time: " + (affStopTime - affStartTime) + " ms");
            }

            if (null == user || !userid.equals(user.getUid())) {
                logger.error("user id=" + uid + " not found");
                return;
            }

            long planStartTime = System.currentTimeMillis();
            AdsPlan plan = cacheAdsPlan.single(planid);
            long planStopTime = System.currentTimeMillis();
            if (planStopTime - planStartTime > runLimitTime) {
                logger.error("plan run time: " + (planStopTime - planStartTime) + " ms");
            }

            if (null == plan || !planid.equals(plan.getPlanid())) {
                logger.error("uid: " + uid + ", zid: " + zid + ", plan id=" + pid + " not found");
                return;
            }
            // 计划状态是否正常
            if (plan.getPstatus() != NumberKey.ZERO.getValue()) {
                logger.error("uid: " + uid + ", zid: " + zid + ", plan id=" + pid + " plan locked, continue");
                return;
            }
            int paytype = plan.getPaytype();

            long adStartTime = System.currentTimeMillis();
            AdsAd ad = cacheAdsAd.single(adid);
            long adStopTime = System.currentTimeMillis();
            if (adStopTime - adStartTime > runLimitTime) {
                logger.error("ad run time: " + (adStopTime - adStartTime) + " ms");
            }

            if (null == ad || !adid.equals(ad.getAdid())) {
                logger.error("ad id=" + aid + " not found");
                return;
            }
            String java = "", flash = "", reffer = "", sitepage = "", screen = "", cookie = "", title = "";
            for (String param : cliitemts) {
                try {
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
                } catch (Exception e) {
                    logger.error(param + " parse error: " + e);
                }
            }
            host = Tool.host(host);
            Long siteid = 0L;

            long siteStartTime = System.currentTimeMillis();
            AdsSite site = cacheAdsSite.single(Long.valueOf(uid), host);
            long siteStopTime = System.currentTimeMillis();
            if (siteStopTime - siteStartTime > runLimitTime) {
                logger.error("site run time: " + (siteStopTime - siteStartTime) + " ms");
            }

            if (null != site && null != site.getSiteid()) {
                siteid = site.getSiteid();
            }
            IPSeeker ipSeeker = IPSeeker.getInstance();
            String country = ipSeeker.getCountry(ip), isp = ipSeeker.getArea(ip);
            String province = IPAreaMap.province(country), city = IPAreaMap.city(province, country);

            // 判断作弊
            long cheatpv = 0, cheatuv = 0, cheaturl = 0;
            long cheatuvpv = 0, cheatuvip = 0, cheatuvurl = 0;
            String doubt = "N";
            try {
                LocalDate nowdate = LocalDate.now();
                String today = nowdate.format(DateTimeFormatter.ISO_DATE);
                // IP 方面
                long ipStartTime = System.currentTimeMillis();
                cheatpv = cacheAdsIPCheat.plusPv(type, ip, today);
                cheatuv = cacheAdsIPCheat.pushUv(type, ip, today, sid);
                cheaturl = cacheAdsIPCheat.pushUrl(type, ip, today, host);
                long ipStopTime = System.currentTimeMillis();
                if (ipStopTime - ipStartTime > runLimitTime) {
                    logger.error("ipcheat run time: " + (ipStopTime - ipStartTime) + " ms");
                }

                // UV 方面
                long uvStartTime = System.currentTimeMillis();
                cheatuvpv = cacheAdsUVCheat.plusPv(type, sid, today);
                cheatuvip = cacheAdsUVCheat.pushIp(type, sid, today, ip);
                cheatuvurl = cacheAdsUVCheat.pushUrl(type, sid, today, host);
                long uvStopTime = System.currentTimeMillis();
                if (uvStopTime - uvStartTime > runLimitTime) {
                    logger.error("uvcheat run time: " + (uvStopTime - uvStartTime) + " ms");
                }

                if (unionCheatPv.compareTo(cheatpv) <= 0 || unionCheatUv.compareTo(cheatuv) <= 0
                        || unionCheatUrl.compareTo(cheaturl) <= 0 || unionCheatPv.compareTo(cheatuvpv) <= 0
                        || unionCheatIp.compareTo(cheatuvip) <= 0 || unionCheatUrl.compareTo(cheatuvurl) <= 0) {
                    doubt = "Y";
                }
            } catch (Exception e) {
                logger.error("cheat error: " + e);
            }

            LogVisit visit = new LogVisit();
            visit.setAddTime(new Date());
            visit.setAdid(adid);
            visit.setBrwName(browserName);
            visit.setBrwUa(userAgent);
            visit.setBrwVersion(browserVersion);
            visit.setCusCookie("1".equals(cookie) ? "Y" : "N");
            visit.setCusFlash(flash);
            visit.setCusJava("1".equals(java) ? "Y" : "N");
            visit.setCusOs(browserOS);
            visit.setCusIp(ip);
            visit.setCusProvince(province);
            visit.setCusCity(city);
            visit.setCusIsp(isp);
            visit.setCusScreen(screen);
            visit.setDeduction("N");
            visit.setJsessionid(sid);
            visit.setPageTitle(title);
            visit.setPlanid(planid);
            visit.setPaytype(paytype);
            visit.setRefererUrl(reffer);
            visit.setSiteid(siteid);
            visit.setSitePage(sitepage);
            visit.setUid(Long.valueOf(uid));
            visit.setZoneid(zoneid);
            visit.setPrice(0.0);
            visit.setAdvprice(0.0);
            visit.setDevice(Integer.valueOf(device));
            visit.setPvnum(cheatpv);
            visit.setUvnum(cheatuv);
            visit.setUrlnum(cheaturl);
            visit.setUvpvnum(cheatuvpv);
            visit.setUvipnum(cheatuvip);
            visit.setUvurlnum(cheatuvurl);
            visit.setDoubt(doubt);

            // 日志存入kafka
            long produceStartTime = System.currentTimeMillis();
            producer.send(ApplicationConstant.PV_COUNT_TOPIC, JSONObject.fromObject(visit).toString());
            long produceStopTime = System.currentTimeMillis();
            if (produceStopTime - produceStartTime > runLimitTime) {
                logger.error("produce run time: " + (produceStopTime - produceStartTime) + " ms");
            }
        } catch (Exception e) {
            logger.error("pv error: " + request.getHeader("User-Agent"));
            logger.error("pv error: " + request.getQueryString());
            logger.error("pv error: " + e);
        }
    }
}
