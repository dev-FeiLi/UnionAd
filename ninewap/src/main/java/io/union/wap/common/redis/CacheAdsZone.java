package io.union.wap.common.redis;

import io.union.wap.common.proto.AdsZoneProto;
import io.union.wap.entity.AdsZone;
import io.union.wap.service.AdsZoneService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Date;

/**
 * Created by Administrator on 2017/7/13.
 */
@Component
public class CacheAdsZone {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;
    @Autowired
    private AdsZoneService adsZoneService;

    private AdsZone build(AdsZoneProto.AdsZoneMessage message) {
        AdsZone zone = new AdsZone();
        zone.setZoneid(message.getZoneid());
        zone.setZonename(message.getZonename());
        zone.setUid(message.getUid());
        zone.setZstatus(message.getZstatus());
        zone.setPaytype(message.getPaytype());
        zone.setViewtype(message.getViewtype());
        zone.setAddTime(new Date(message.getAddTime()));
        if (message.getWidth() > 0) {
            zone.setWidth(message.getWidth());
        }
        if (message.getHeight() > 0) {
            zone.setHeight(message.getHeight());
        }
        if (null != message.getHcontrol()) {
            zone.setHcontrol(message.getHcontrol());
        }
        if (null != message.getViewadids()) {
            zone.setViewadids(message.getViewadids());
        }
        if (!StringUtils.isEmpty(message.getJdomain())) {
            zone.setJdomain(message.getJdomain());
        }
        if (!StringUtils.isEmpty(message.getIdomain())) {
            zone.setIdomain(message.getIdomain());
        }
        if (!StringUtils.isEmpty(message.getDescription())) {
            zone.setDescription(message.getDescription());
        }
        if (!StringUtils.isEmpty(message.getViewname())) {
            zone.setViewname(message.getViewname());
        }
        return zone;
    }

    private AdsZoneProto.AdsZoneMessage build(AdsZone zone) {
        AdsZoneProto.AdsZoneMessage.Builder builder = AdsZoneProto.AdsZoneMessage.newBuilder();
        builder.setZoneid(zone.getZoneid());
        builder.setZonename(zone.getZonename());
        builder.setUid(zone.getUid());
        builder.setZstatus(zone.getZstatus());
        builder.setPaytype(zone.getPaytype());
        builder.setViewtype(zone.getViewtype());
        builder.setAddTime(zone.getAddTime().getTime());
        if (null != zone.getWidth()) {
            builder.setWidth(zone.getWidth());
        }
        if (null != zone.getHeight()) {
            builder.setHeight(zone.getHeight());
        }
        if (null != zone.getHcontrol()) {
            builder.setHcontrol(zone.getHcontrol());
        }
        if (null != zone.getViewadids()) {
            builder.setViewadids(zone.getViewadids());
        }
        if (!StringUtils.isEmpty(zone.getJdomain())) {
            builder.setJdomain(zone.getJdomain());
        }
        if (!StringUtils.isEmpty(zone.getIdomain())) {
            builder.setIdomain(zone.getIdomain());
        }
        if (!StringUtils.isEmpty(zone.getDescription())) {
            builder.setDescription(zone.getDescription());
        }
        if (!StringUtils.isEmpty(zone.getViewname())) {
            builder.setViewname(zone.getViewname());
        }
        return builder.build();
    }

    public AdsZone save(AdsZone zone) {
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.ADS_ZONE_MAP), field = String.valueOf(zone.getZoneid());
            AdsZoneProto.AdsZoneMessage message = build(zone);
            jedis.hset(key.getBytes(), field.getBytes(), message.toByteArray());
            return zone;
        } catch (Exception e) {
            logger.error("save error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }
}
