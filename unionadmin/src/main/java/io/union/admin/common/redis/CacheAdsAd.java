package io.union.admin.common.redis;

import com.google.protobuf.InvalidProtocolBufferException;
import io.union.admin.common.proto.AdsAdProto;
import io.union.admin.entity.AdsAd;
import io.union.admin.service.AdsAdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * Created by Administrator on 2017/7/11.
 */
@Component
public class CacheAdsAd {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;
    @Autowired
    private AdsAdService adsAdService;

    private AdsAd build(AdsAdProto.AdsAdMessage proto) {
        AdsAd adsAd = new AdsAd();
        adsAd.setAdid(proto.getAdid());
        adsAd.setAdname(proto.getAdname());
        adsAd.setUid(proto.getUid());
        adsAd.setPlanid(proto.getPlanid());
        adsAd.setAdtype(proto.getAdtype());
        adsAd.setAstatus(proto.getAstatus());
        adsAd.setDeduction(proto.getDeduction());
        adsAd.setAddtime(new Date(proto.getAddTime()));
        adsAd.setAdurl(proto.getAdurl());
        adsAd.setImageurl(proto.getImageurl());
        adsAd.setLimitdiv(proto.getLimitdiv());
        adsAd.setLimitdivheight(proto.getLimitdivheight());
        adsAd.setLimitpop(proto.getLimitpop());
        adsAd.setAdeffect(proto.getAdeffect());
        adsAd.setDataType(proto.getDataType());
        if (proto.getWidth() > 0) {
            adsAd.setWidth(proto.getWidth());
        }
        if (proto.getHeight() > 0) {
            adsAd.setHeight(proto.getHeight());
        }
        return adsAd;
    }

    private AdsAdProto.AdsAdMessage build(AdsAd ad) {
        AdsAdProto.AdsAdMessage.Builder builder = AdsAdProto.AdsAdMessage.newBuilder();
        builder.setAdid(ad.getAdid());
        builder.setAdname(ad.getAdname());
        builder.setAdtype(ad.getAdtype());
        builder.setPlanid(ad.getPlanid());
        builder.setUid(ad.getUid());
        builder.setAstatus(ad.getAstatus());
        builder.setAddTime(ad.getAddtime().getTime());
        builder.setLimitdiv(ad.getLimitdiv());
        builder.setLimitdivheight(ad.getLimitdivheight());
        builder.setLimitpop(ad.getLimitpop());
        if (null != ad.getAdurl()) {
            builder.setAdurl(ad.getAdurl());
        }
        if (null != ad.getImageurl()) {
            builder.setImageurl(ad.getImageurl());
        }
        if (null != ad.getWidth()) {
            builder.setWidth(ad.getWidth());
        }
        if (null != ad.getHeight()) {
            builder.setHeight(ad.getHeight());
        }
        if (null != ad.getDeduction()) {
            builder.setDeduction(ad.getDeduction());
        }
        if (null != ad.getAdeffect()) {
            builder.setAdeffect(ad.getAdeffect());
        }
        if (null != ad.getDataType()) {
            builder.setDataType(ad.getDataType());
        }
        return builder.build();
    }

    public AdsAd save(AdsAd ad) {
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.ADS_AD_MAP), field = String.valueOf(ad.getAdid());
            AdsAdProto.AdsAdMessage message = build(ad);
            jedis.hset(key.getBytes(), field.getBytes(), message.toByteArray());

            int width = ad.getWidth(), height = ad.getHeight(), type = ad.getAdtype(), status = ad.getAstatus();
            String adkey = String.format(String.valueOf(RedisKeys.ADS_TYPE_SIZE_AD_MAP), type, width, height);
            if (status == 0) {
                jedis.hset(adkey.getBytes(), field.getBytes(), message.toByteArray());
            } else {
                jedis.hdel(adkey, field);
            }
            return ad;
        } catch (Exception e) {
            logger.error("save  error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }

    public AdsAd single(long adid) {
        AdsAd ad = null;
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.ADS_AD_MAP), field = String.valueOf(adid);
            byte[] tmp = jedis.hget(key.getBytes(), field.getBytes());

            if (tmp != null && tmp.length > 1) {
                AdsAdProto.AdsAdMessage message = AdsAdProto.AdsAdMessage.parseFrom(tmp);
                ad = build(message);
            }
        } catch (Exception e) {
            logger.error("singe error:", e);
        } finally {
            pool.push(jedis);
        }

        return ad;
    }

    public List<AdsAd> listByTypeAndSize(Integer type, Integer width, Integer height) {
        List<AdsAd> list = new ArrayList<>();
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.ADS_TYPE_SIZE_AD_MAP), type, width, height);
            Map<byte[], byte[]> value = jedis.hgetAll(key.getBytes());
            if (null != value && value.size() > 0) {
                for (Map.Entry<byte[], byte[]> entry : value.entrySet()) {
                    try {
                        AdsAdProto.AdsAdMessage message = AdsAdProto.AdsAdMessage.parseFrom(entry.getValue());
                        AdsAd ad = build(message);
                        if (ad.getAstatus() == 8)
                            continue;
                        list.add(ad);
                    } catch (Exception e) {
                        // 高并发的情况下会打印一堆无用日志，这里不打印
                    }
                }
            } else {
                List<AdsAd> adlist = adsAdService.findByTypeAndWidthAndHeight(type, width, height);
                if (null != adlist && adlist.size() > 0) {
                    adlist.forEach(ad -> {
                        String field = String.valueOf(ad.getAdid());
                        jedis.hset(key.getBytes(), field.getBytes(), build(ad).toByteArray());
                        list.add(ad);
                    });
                }
            }
        } catch (Exception e) {
            logger.error("listByTypeAndSize type=" + type + " width=" + width + " height=" + height + " error: ", e);
        } finally {
            pool.push(jedis);
        }
        return list;
    }

    public Map<Long, AdsAd> map() {
        Jedis jedis = pool.pull();
        try {
            Map<Long, AdsAd> result = new HashMap<>();
            String key = String.valueOf(RedisKeys.ADS_AD_MAP);
            Map<byte[], byte[]> map = jedis.hgetAll(key.getBytes());
            if (null != map && map.size() > 0) {
                map.forEach((k, v) -> {
                    try {
                        if (null != v && v.length > 1) {
                            AdsAdProto.AdsAdMessage message = AdsAdProto.AdsAdMessage.parseFrom(v);
                            AdsAd ad = build(message);
                            if (ad.getAstatus() == 8)
                                return;
                            result.put(ad.getAdid(), ad);
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
