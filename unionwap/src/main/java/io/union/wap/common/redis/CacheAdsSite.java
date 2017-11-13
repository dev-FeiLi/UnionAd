package io.union.wap.common.redis;

import io.union.wap.common.proto.AdsSiteProto;
import io.union.wap.entity.AdsSite;
import io.union.wap.service.AdsSiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Date;

/**
 * Created by Administrator on 2017/7/17.
 */
@Component
public class CacheAdsSite {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;
    @Autowired
    private AdsSiteService adsSiteService;

    private AdsSite build(AdsSiteProto.AdsSiteMessage message) {
        AdsSite site = new AdsSite();
        site.setSiteid(message.getSiteid());
        site.setSitename(message.getSitename());
        site.setSiteurl(message.getSiteurl());
        site.setSitetype(message.getSitetype());
        site.setSstatus(message.getSstatus());
        site.setAddTime(new Date(message.getAddTime()));
        if (null != message.getBeian()) {
            site.setBeian(message.getBeian());
        }
        if (message.getDayip() > 0) {
            site.setDayip(message.getDayip());
        }
        if (message.getDaypv() > 0) {
            site.setDaypv(message.getDaypv());
        }
        return site;
    }

    private AdsSiteProto.AdsSiteMessage build(AdsSite site) {
        AdsSiteProto.AdsSiteMessage.Builder builder = AdsSiteProto.AdsSiteMessage.newBuilder();
        builder.setSiteid(site.getSiteid());
        builder.setSitename(site.getSitename());
        builder.setSiteurl(site.getSiteurl());
        builder.setSitetype(site.getSitetype());
        builder.setAddTime(site.getAddTime().getTime());
        builder.setSstatus(site.getSstatus());
        if (null != site.getBeian()) {
            builder.setBeian(site.getBeian());
        }
        if (null != site.getDayip() && site.getDayip() > 0) {
            builder.setDayip(site.getDayip());
        }
        if (null != site.getDaypv() && site.getDaypv() > 0) {
            builder.setDaypv(site.getDaypv());
        }
        return builder.build();
    }

    public AdsSite save(AdsSite site) {
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.ADS_SITE_MAP), field = String.valueOf(site.getSiteid());
            AdsSiteProto.AdsSiteMessage message = build(site);
            jedis.hset(key.getBytes(), field.getBytes(), message.toByteArray());

            String uidkey = String.format(String.valueOf(RedisKeys.ADS_UID_SITE_MAP), site.getUid()), url = site.getSiteurl();
            jedis.hset(uidkey, url, field);
            return site;
        } catch (Exception e) {
            logger.error("save  error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }
}
