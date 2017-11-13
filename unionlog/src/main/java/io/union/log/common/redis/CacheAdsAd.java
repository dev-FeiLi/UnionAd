package io.union.log.common.redis;

import io.union.log.common.proto.AdsAdProto;
import io.union.log.entity.AdsAd;
import io.union.log.service.AdsAdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public AdsAd save(Long id) {
        Jedis jedis = pool.pull();
        try {
            AdsAd ad = adsAdService.findOne(id);
            String key = String.valueOf(RedisKeys.ADS_AD_MAP), field = String.valueOf(id);
            if (null != ad) {
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
    public AdsAd single(Long id) {
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.ADS_AD_MAP), field = String.valueOf(id);
            byte[] value = jedis.hget(key.getBytes(), field.getBytes());
            if (null != value && value.length > 0) {
                if (value.length > 1) {
                    AdsAdProto.AdsAdMessage message = AdsAdProto.AdsAdMessage.parseFrom(value);
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

    public void save(List<AdsAd> list) {
        Jedis jedis = pool.pull();
        try {
            Pipeline pipeline = jedis.pipelined();
            String key = String.valueOf(RedisKeys.ADS_AD_MAP);
            for (AdsAd ad : list) {
                String field = String.valueOf(ad.getAdid());
                AdsAdProto.AdsAdMessage message = build(ad);
                pipeline.hset(key.getBytes(), field.getBytes(), message.toByteArray());

                int width = ad.getWidth(), height = ad.getHeight(), type = ad.getAdtype(), status = ad.getAstatus();
                String adkey = String.format(String.valueOf(RedisKeys.ADS_TYPE_SIZE_AD_MAP), type, width, height);
                if (status == 0) {
                    pipeline.hset(adkey.getBytes(), field.getBytes(), message.toByteArray());
                } else {
                    pipeline.hdel(adkey, field);
                }
            }
            pipeline.sync();
        } catch (Exception e) {
            logger.error("save error: ", e);
        } finally {
            pool.push(jedis);
        }
    }

    /**
     * 根据计划ID查询所属的ads_ad
     *
     * @param planid
     * @return
     */
    public List<AdsAd> listbypid(Long planid) {
        List<AdsAd> list = new ArrayList<>();
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.ADS_AD_MAP);
            Map<byte[], byte[]> map = jedis.hgetAll(key.getBytes());
            if (null != map && map.size() > 0) {
                map.forEach((k, v) -> {
                    try {
                        if (null != v && v.length > 0) {
                            if (v.length > 1) {
                                AdsAdProto.AdsAdMessage message = AdsAdProto.AdsAdMessage.parseFrom(v);
                                AdsAd ad = build(message);
                                if (planid.equals(ad.getPlanid())) {
                                    list.add(ad);
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error("list ad error: " + e);
                    }
                });
            }
        } catch (Exception e) {
            logger.error("list pid=" + planid + " error: ", e);
        } finally {
            pool.push(jedis);
        }
        return list;
    }
}
