package io.union.js.controller;

import io.union.js.common.kafka.UnionProducer;
import io.union.js.common.redis.*;
import io.union.js.common.utils.ApplicationConstant;
import io.union.js.entity.*;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/25.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
public class BkController {
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
    private CacheAdsZone cacheAdsZone;

    @Autowired
    private UnionProducer producer;

    // ?zoneid=1001&adid=1021&sessionid=0C1E12B6B2C8F8F4D0A8AB7496DEC1F9
    // ?zoneid=1001&adid=1021&sessionid=0C1E12B6B2C8F8F4D0A8AB7496DEC1F9&regid=3434343&regtime=1564984875
    @RequestMapping(value = {"/{letter:^[b][a-z]*}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public void clickprocessor(HttpServletRequest request, HttpServletResponse response, @PathVariable(required = false) String letter) {
        response.setStatus(HttpServletResponse.SC_OK);
        try {
            String zid = request.getParameter("zoneid"), aid = request.getParameter("adid"), sessionid = request.getParameter("sessionid"),
                    regid = request.getParameter("regid"), regtime = request.getParameter("regtime"), ip = request.getParameter("ip");

            Long affuid = 0L, advuid = 0L, planid = 0L, zoneid = Long.valueOf(zid), adid = Long.valueOf(aid), siteid = 0L;
            String affname = "", advname = "", planname = "", adname = "";
            AdsZone zone = cacheAdsZone.single(zoneid);
            if (null != zone) {
                affuid = zone.getUid();
            }
            AdsUser user = cacheAdsUser.single(affuid);
            if (null != user && affuid.equals(user.getUid())) {
                affname = user.getUsername();
            }
            AdsAd ad = cacheAdsAd.single(adid);
            if (null != ad && adid.equals(ad.getAdid())) {
                adname = ad.getAdname();
                planid = ad.getPlanid();
            }
            AdsPlan plan = cacheAdsPlan.single(planid);
            if (null != plan && planid.equals(plan.getPlanid())) {
                planname = plan.getTitle();
                advuid = plan.getUid();
                advname = plan.getTitle();
            }
            AdsUser adv = cacheAdsUser.single(advuid);
            if (null != adv && advuid.equals(adv.getUid())) {
                advname = adv.getUsername();
            }

            LogEffect effect = new LogEffect();
            effect.setAddTime(new Date());
            effect.setAdid(adid);
            effect.setAdname(adname);
            effect.setAdvuid(advuid);
            effect.setAdvname(advname);
            effect.setAffuid(affuid);
            effect.setAffname(affname);
            effect.setClickip(ip);
            effect.setPlanid(planid);
            effect.setPlanname(planname);
            effect.setSiteid(siteid);
            effect.setZoneid(zoneid);
            effect.setSessionid(sessionid);
            effect.setRegid(regid);
            if (!StringUtils.isEmpty(regtime)) {
                effect.setRegTime(new Date(Long.valueOf(regtime)));
            }
            // 日志存入kafka
            producer.send(ApplicationConstant.EFFECT_COUNT_TOPIC, JSONObject.fromObject(effect).toString());

            // 打印回调日志
            logger.info("callback: " + request.getRequestURL() + "?" + request.getQueryString());
        } catch (Exception e) {
            logger.error("effect error: " + request.getQueryString());
            logger.error("effect error: " + e);
        }
    }
}
