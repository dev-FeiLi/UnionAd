package io.union.log.common.redis;

import io.union.log.common.proto.AdsZoneProto;
import io.union.log.entity.AdsZone;
import io.union.log.service.AdsZoneService;
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

    public AdsZone save(Long id) {
        Jedis jedis = pool.pull();
        try {
            AdsZone zone = adsZoneService.findOne(id);
            String key = String.valueOf(RedisKeys.ADS_ZONE_MAP), field = String.valueOf(id);
            if (null != zone) {
                AdsZoneProto.AdsZoneMessage message = build(zone);
                jedis.hset(key.getBytes(), field.getBytes(), message.toByteArray());
                return zone;
            } else {
                // 这里防止被SQL渗透，被request爆了数据库
                // 如果查出来是空，则在redis存入空字符串，表明id对应的对象在数据库中不存在
                // 下次继续请求的时候，则可以优先判断而不用查询数据库，直接返回错误信息
                jedis.hset(key.getBytes(), field.getBytes(), "0".getBytes());
            }
        } catch (Exception e) {
            logger.error("save id=" + id + " error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }

    /**
     * 使用时，如果得到的对象不是null，也不能认定是正确的对象<br/>
     * 需要继续判断该对象的主键是否大于零，大于零是正确的对象<br/>
     * 等于零则是错误的对象，数据库已经限定无符号，所以不存在小于零
     *
     * @param id
     * @return
     */
    public AdsZone single(Long id) {
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.ADS_ZONE_MAP), field = String.valueOf(id);
            byte[] value = jedis.hget(key.getBytes(), field.getBytes());
            if (null != value && value.length > 0) {
                if (value.length > 1) {
                    AdsZoneProto.AdsZoneMessage message = AdsZoneProto.AdsZoneMessage.parseFrom(value);
                    return build(message);
                }
            } else {
                // 在redis中真正不存在该id对应的对象，才会去查询数据库
                return save(id);
            }
        } catch (Exception e) {
            logger.error("single id=" + id + " error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }
}
