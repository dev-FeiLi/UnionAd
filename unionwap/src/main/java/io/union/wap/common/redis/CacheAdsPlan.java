package io.union.wap.common.redis;

import com.google.protobuf.InvalidProtocolBufferException;
import io.union.wap.common.proto.AdsPlanProto;
import io.union.wap.entity.AdsPlan;
import io.union.wap.service.AdsPlanService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/11.
 */
@Component
public class CacheAdsPlan {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;
    @Autowired
    private AdsPlanService adsPlanService;

    private AdsPlan build(AdsPlanProto.AdsPlanMessage message) {
        AdsPlan plan = new AdsPlan();
        plan.setPlanid(message.getPlanid());
        plan.setTitle(message.getTitle());
        plan.setUid(message.getUid());
        plan.setUsername(message.getUsername());
        plan.setPrice(message.getPrice());
        plan.setUsertotal(message.getUsertotal());
        plan.setLimitmoney(message.getLimitmoney());
        plan.setPaytype(message.getPaytype());
        plan.setIsaudit(message.getIsaudit());
        plan.setLimitpop(message.getLimitpop());
        plan.setLimitdiv(message.getLimitdiv());
        plan.setLimitdivheight(message.getLimitdivheight());
        plan.setPstatus(message.getPstatus());
        plan.setAddTime(new Date(message.getAddTime()));
        plan.setDeduction(message.getDeduction());
        plan.setClickRate(message.getClickRate());
        if (null != message.getLimitarea()) {
            plan.setLimitarea(message.getLimitarea());
        }
        if (null != message.getLimittime()) {
            plan.setLimittime(message.getLimittime());
        }
        if (null != message.getLimitdevice()) {
            plan.setLimitdevice(message.getLimitdevice());
        }
        if (null != message.getLimituid()) {
            plan.setLimituid(message.getLimituid());
        }
        if (null != message.getLimitsite()) {
            plan.setLimitsite(message.getLimitsite());
        }
        if (null != message.getLimittype()) {
            plan.setLimittype(message.getLimittype());
        }
        if (message.getStarttime() > 0) {
            plan.setStarttime(new Date(message.getStarttime()));
        }
        if (message.getStoptime() > 0) {
            plan.setStoptime(new Date(message.getStoptime()));
        }
        if (!StringUtils.isEmpty(message.getAdsUrl())) {
            plan.setAdsUrl(message.getAdsUrl());
        }
        if (message.getPriority() > 0) {
            plan.setPriority(message.getPriority());
        }
        if (!StringUtils.isEmpty(message.getIsnotify())) {
            plan.setIsnotify(message.getIsnotify());
        } else {
            plan.setIsnotify("N");
        }
        return plan;
    }

    private AdsPlanProto.AdsPlanMessage build(AdsPlan plan) {
        AdsPlanProto.AdsPlanMessage.Builder builder = AdsPlanProto.AdsPlanMessage.newBuilder();
        builder.setPlanid(plan.getPlanid());
        builder.setTitle(plan.getTitle());
        builder.setUid(plan.getUid());
        builder.setUsername(plan.getUsername());
        builder.setPrice(plan.getPrice());
        builder.setUsertotal(plan.getUsertotal());
        builder.setLimitmoney(plan.getLimitmoney());
        builder.setPaytype(plan.getPaytype());
        builder.setIsaudit(plan.getIsaudit());
        builder.setLimitpop(plan.getLimitpop());
        builder.setLimitdiv(plan.getLimitdiv());
        builder.setLimitdivheight(plan.getLimitdivheight());
        builder.setPstatus(plan.getPstatus());
        builder.setAddTime(plan.getAddTime().getTime());
        builder.setDeduction(plan.getDeduction());
        if (null != plan.getLimitarea()) {
            builder.setLimitarea(plan.getLimitarea());
        }
        if (null != plan.getLimittime()) {
            builder.setLimittime(plan.getLimittime());
        }
        if (null != plan.getLimitdevice()) {
            builder.setLimitdevice(plan.getLimitdevice());
        }
        if (null != plan.getLimituid()) {
            builder.setLimituid(plan.getLimituid());
        }
        if (null != plan.getLimitsite()) {
            builder.setLimitsite(plan.getLimitsite());
        }
        if (null != plan.getLimittype()) {
            builder.setLimittype(plan.getLimittype());
        }
        if (null != plan.getStarttime()) {
            builder.setStarttime(plan.getStarttime().getTime());
        }
        if (null != plan.getStoptime()) {
            builder.setStoptime(plan.getStoptime().getTime());
        }
        if (!StringUtils.isEmpty(plan.getAdsUrl())) {
            builder.setAdsUrl(plan.getAdsUrl());
        }
        if (null != plan.getPriority() && plan.getPriority() > 0) {
            builder.setPriority(plan.getPriority());
        }
        if (!StringUtils.isEmpty(plan.getIsnotify())) {
            builder.setIsnotify(plan.getIsnotify());
        } else {
            builder.setIsnotify("N");
        }
        if (null != plan.getClickRate()) {
            builder.setClickRate(plan.getClickRate());
        }
        return builder.build();
    }

    public void save(AdsPlan plan) {
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.ADS_PLAN_MAP), field = String.valueOf(plan.getPlanid());
            AdsPlanProto.AdsPlanMessage message = build(plan);
            jedis.hset(key.getBytes(), field.getBytes(), message.toByteArray());
        } catch (Exception e) {
            logger.error("save error: ", e);
        } finally {
            pool.push(jedis);
        }
    }

    public Map<Long, AdsPlan> map() {
        Jedis jedis = pool.pull();
        try {
            Map<Long, AdsPlan> result = new HashMap<>();
            String key = String.valueOf(RedisKeys.ADS_PLAN_MAP);
            Map<byte[], byte[]> map = jedis.hgetAll(key.getBytes());
            if (null != map && map.size() > 0) {
                map.forEach((k, v) -> {
                    try {
                        if (null != v && v.length > 1) {
                            AdsPlanProto.AdsPlanMessage message = AdsPlanProto.AdsPlanMessage.parseFrom(v);
                            AdsPlan plan = build(message);
                            result.put(plan.getPlanid(), plan);
                        }
                    } catch (InvalidProtocolBufferException e) {
                        logger.error("parse error: ", e);
                    }
                });
                return result;
            }
        } catch (Exception e) {
            logger.error("map error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }
}
