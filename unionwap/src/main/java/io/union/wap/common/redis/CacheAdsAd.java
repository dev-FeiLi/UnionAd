package io.union.wap.common.redis;

import com.google.protobuf.InvalidProtocolBufferException;
import io.union.wap.common.proto.AdsAdProto;
import io.union.wap.entity.AdsAd;
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
public class CacheAdsAd {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;

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
