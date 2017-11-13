package io.union.js.common.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Calendar;

@Component
public class CacheAdsIPCheat {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;

    public long plusPv(String type, String ip, String date) {
        Jedis jedis = pool.pull();
        long newValue = 0;
        try {
            String key = String.format(String.valueOf(RedisKeys.ADS_CHEAT_IPPV), type, date), filed = ip;
            newValue = jedis.hincrBy(key, filed, 1);
            // 设置过期时间在隔天零点，意思是每天清空一次
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            jedis.expireAt(key, calendar.getTimeInMillis() / 1000);
        } catch (Exception e) {
            logger.error("pluspv error: ", e);
        } finally {
            pool.push(jedis);
        }
        return newValue;
    }

    public long pushUrl(String type, String ip, String date, String domain) {
        Jedis jedis = pool.pull();
        long newValue = 0;
        try {
            String key = String.format(String.valueOf(RedisKeys.ADS_CHEAT_IPURL), type, ip, date), filed = domain;
            jedis.hset(key, filed, "0");
            newValue = jedis.hlen(key);
            // 设置过期时间在隔天零点，意思是每天清空一次
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            jedis.expireAt(key, calendar.getTimeInMillis() / 1000);
        } catch (Exception e) {
            logger.error("pushurl error: ", e);
        } finally {
            pool.push(jedis);
        }
        return newValue;
    }

    public long pushUv(String type, String ip, String date, String uv) {
        Jedis jedis = pool.pull();
        long newValue = 0;
        try {
            String key = String.format(String.valueOf(RedisKeys.ADS_CHEAT_IPUV), type, ip, date), filed = uv;
            jedis.hset(key, filed, "0");
            newValue = jedis.hlen(key);
            // 设置过期时间在隔天零点，意思是每天清空一次
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            jedis.expireAt(key, calendar.getTimeInMillis() / 1000);
        } catch (Exception e) {
            logger.error("pushUv error: ", e);
        } finally {
            pool.push(jedis);
        }
        return newValue;
    }
}
