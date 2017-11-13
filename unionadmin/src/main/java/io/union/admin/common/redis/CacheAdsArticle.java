package io.union.admin.common.redis;


import io.union.admin.common.proto.AdsArticleProto;
import io.union.admin.entity.AdsArticle;
import io.union.admin.service.AdsArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Date;

/**
 * Created by Administrator on 2017/8/2.
 */
@Component
public class CacheAdsArticle {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;
    @Autowired
    private AdsArticleService adsArticleService;

    private AdsArticleProto.AdsArticleMessage build(AdsArticle aritcle) {
        AdsArticleProto.AdsArticleMessage.Builder builder = AdsArticleProto.AdsArticleMessage.newBuilder();
        builder.setId(aritcle.getId());
        builder.setTitle(aritcle.getTitle());
        builder.setSummary(aritcle.getSummary());
        builder.setContent(aritcle.getContent());
        builder.setAtop(aritcle.getAtop());
        builder.setAsort(aritcle.getAsort());
        builder.setAstatus(aritcle.getAstatus());
        if (aritcle.getAddTime() != null) {
            builder.setAddTime(aritcle.getAddTime().getTime());
        }
        if (aritcle.getBeginTime() != null) {
            builder.setBeginTime(aritcle.getBeginTime().getTime());
        }
        if (aritcle.getEndTime() != null) {
            builder.setEndTime(aritcle.getEndTime().getTime());
        }
        return builder.build();
    }

    private AdsArticle build(AdsArticleProto.AdsArticleMessage message) {
        AdsArticle aritcle = new AdsArticle();
        aritcle.setId(message.getId());
        aritcle.setTitle(message.getTitle());
        aritcle.setSummary(message.getSummary());
        aritcle.setContent(message.getContent());
        aritcle.setAtop(message.getAtop());
        aritcle.setAsort(message.getAsort());
        aritcle.setAstatus(message.getAstatus());
        if (message.getAddTime() > 0) {
            aritcle.setAddTime(new Date(message.getAddTime()));
        }
        if (message.getBeginTime() > 0) {
            aritcle.setBeginTime(new Date(message.getBeginTime()));
        }
        if (message.getEndTime() > 0) {
            aritcle.setEndTime(new Date(message.getEndTime()));
        }
        return aritcle;
    }

    public AdsArticle save(AdsArticle aritcle) {
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.ADS_ARTICLE_MAP), filed = String.valueOf(aritcle.getId());
            AdsArticleProto.AdsArticleMessage message = build(aritcle);
            jedis.hset(key.getBytes(), filed.getBytes(), message.toByteArray());
            return aritcle;
        } catch (Exception e) {
            logger.error("save error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }

    public AdsArticle save(Long id) {
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.ADS_ARTICLE_MAP), filed = String.valueOf(id);
            AdsArticle aritcle = adsArticleService.findOne(id);
            if (null != aritcle) {
                AdsArticleProto.AdsArticleMessage message = build(aritcle);
                jedis.hset(key.getBytes(), filed.getBytes(), message.toByteArray());
                return aritcle;
            } else {
                jedis.hset(key.getBytes(), filed.getBytes(), "0".getBytes());
            }
        } catch (Exception e) {
            logger.error("save error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }

    public AdsArticle single(Long id) {
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.ADS_ARTICLE_MAP), filed = String.valueOf(id);
            byte[] value = jedis.hget(key.getBytes(), filed.getBytes());
            if (null != value && value.length > 0) {
                AdsArticleProto.AdsArticleMessage message = AdsArticleProto.AdsArticleMessage.parseFrom(value);
                return build(message);
            } else {
                return save(id);
            }
        } catch (Exception e) {
            logger.error("single error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }
}
