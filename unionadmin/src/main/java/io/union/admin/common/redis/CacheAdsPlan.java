package io.union.admin.common.redis;

import com.google.protobuf.InvalidProtocolBufferException;
import io.union.admin.common.proto.AdsPlanProto;
import io.union.admin.entity.AdsPlan;
import io.union.admin.service.AdsPlanService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
        plan.setPlanId(message.getPlanid());
        plan.setTitle(message.getTitle());
        plan.setUid(message.getUid());
        plan.setUserName(message.getUsername());
        plan.setPrice(message.getPrice());
        plan.setUserTotal(message.getUsertotal());
        plan.setLimitMoney(message.getLimitmoney());
        plan.setPayType(message.getPaytype());
        plan.setIsAudit(message.getIsaudit());
        plan.setLimitPop(message.getLimitpop());
        plan.setLimitDiv(message.getLimitdiv());
        plan.setLimitDivHeight(message.getLimitdivheight());
        plan.setPstatus(message.getPstatus());
        plan.setAddTime(new Date(message.getAddTime()));
        plan.setDeduction(message.getDeduction());
        plan.setAdsUrl(message.getAdsUrl());
        plan.setPriority(message.getPriority());
        plan.setClickRate(message.getClickRate());
        if (null != message.getLimitarea()) {
            plan.setLimitArea(message.getLimitarea());
        }
        if (null != message.getLimittime()) {
            plan.setLimitTime(message.getLimittime());
        }
        if (null != message.getLimitdevice()) {
            plan.setLimitDevice(message.getLimitdevice());
        }
        if (null != message.getLimituid()) {
            plan.setLimitUid(message.getLimituid());
        }
        if (null != message.getLimitsite()) {
            plan.setLimitSite(message.getLimitsite());
        }
        if (null != message.getLimittype()) {
            plan.setLimitType(message.getLimittype());
        }
        if (message.getStarttime() > 0) {
            plan.setStartTime(new Date(message.getStarttime()));
        }
        if (message.getStoptime() > 0) {
            plan.setStopTime(new Date(message.getStoptime()));
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
        builder.setPlanid(plan.getPlanId());
        builder.setTitle(plan.getTitle());
        builder.setUid(plan.getUid());
        builder.setUsername(plan.getUserName());
        builder.setPrice(plan.getPrice());
        builder.setUsertotal(plan.getUserTotal());
        builder.setLimitmoney(plan.getLimitMoney());
        builder.setPaytype(plan.getPayType());
        builder.setIsaudit(plan.getIsAudit());
        builder.setLimitpop(plan.getLimitPop());
        builder.setLimitdiv(plan.getLimitDiv());
        builder.setLimitdivheight(plan.getLimitDivHeight());
        builder.setPstatus(plan.getPstatus());
        builder.setAddTime(plan.getAddTime().getTime());
        builder.setDeduction(plan.getDeduction());
        builder.setPriority(plan.getPriority());
        if (null != plan.getAdsUrl()) {
            builder.setAdsUrl(plan.getAdsUrl());
        }
        if (null != plan.getLimitArea()) {
            builder.setLimitarea(plan.getLimitArea());
        }
        if (null != plan.getLimitTime()) {
            builder.setLimittime(plan.getLimitTime());
        }
        if (null != plan.getLimitDevice()) {
            builder.setLimitdevice(plan.getLimitDevice());
        }
        if (null != plan.getLimitUid()) {
            builder.setLimituid(plan.getLimitUid());
        }
        if (null != plan.getLimitSite()) {
            builder.setLimitsite(plan.getLimitSite());
        }
        if (null != plan.getLimitType()) {
            builder.setLimittype(plan.getLimitType());
        }
        if (null != plan.getStartTime()) {
            builder.setStarttime(plan.getStartTime().getTime());
        }
        if (null != plan.getStopTime()) {
            builder.setStoptime(plan.getStopTime().getTime());
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
            String key = String.valueOf(RedisKeys.ADS_PLAN_MAP), field = String.valueOf(plan.getPlanId());
            AdsPlanProto.AdsPlanMessage message = build(plan);
            jedis.hset(key.getBytes(), field.getBytes(), message.toByteArray());
        } catch (Exception e) {
            logger.error("save error: ", e);
        } finally {
            pool.push(jedis);
        }
    }

    public void save(List<AdsPlan> list) {
        Jedis jedis = pool.pull();
        try {
            Pipeline pipeline = jedis.pipelined();
            String key = String.valueOf(RedisKeys.ADS_PLAN_MAP);
            list.forEach(plan -> {
                String field = String.valueOf(plan.getPlanId());
                AdsPlanProto.AdsPlanMessage message = build(plan);
                pipeline.hset(key.getBytes(), field.getBytes(), message.toByteArray());
            });
            pipeline.sync();
        } catch (Exception e) {
            logger.error("save error: ", e);
        } finally {
            pool.push(jedis);
        }
    }

    public AdsPlan singe(long planId) {
        AdsPlan adsPlan = null;
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.ADS_PLAN_MAP), field = String.valueOf(planId);

            byte[] tmp = jedis.hget(key.getBytes(), field.getBytes());
            if (tmp != null && tmp.length > 1) {
                AdsPlanProto.AdsPlanMessage message = AdsPlanProto.AdsPlanMessage.parseFrom(tmp);
                adsPlan = build(message);
            }
        } catch (Exception e) {
            logger.error("singe error", e);
        } finally {
            pool.push(jedis);
        }
        return adsPlan;
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
                            result.put(plan.getPlanId(), plan);
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
