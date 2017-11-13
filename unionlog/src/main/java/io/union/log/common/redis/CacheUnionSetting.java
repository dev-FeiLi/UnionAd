package io.union.log.common.redis;

import io.union.log.entity.UnionSetting;
import io.union.log.service.UnionSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

/**
 * Created by Administrator on 2017/7/19.
 */
@Component
public class CacheUnionSetting {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;
    @Autowired
    private UnionSettingService unionSettingService;

    public String single(String setkey) {
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.ADS_SETTING_MAP), filed = setkey;
            String value = jedis.hget(key, filed);
            if (null != value) return value;
            else {
                UnionSetting setting = unionSettingService.findOne(setkey);
                if (null != setting) {
                    value = setting.getSetvalue();
                    jedis.hset(key, filed, value);
                    return value;
                } else {
                    jedis.hset(key, filed, "");
                }
            }
        } catch (Exception e) {
            logger.error("single setkey=" + setkey + " error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }

    public void save(UnionSetting setting) {
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.ADS_SETTING_MAP), filed = setting.getSetkey(), value = setting.getSetvalue();
            if (null != value) {
                jedis.hset(key, filed, value);
            }
        } catch (Exception e) {
            logger.error("save error: ", e);
        } finally {
            pool.push(jedis);
        }
    }
}
