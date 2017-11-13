package io.union.js.common.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/7/18.
 */
@Component
public class CacheAdsPop {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;

    public boolean pullpop(String type, Long id, String ip) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.ADS_POP_IP_MAP), type, id), filed = ip;
            long value = jedis.hset(key, filed, "0");
            // 设置过期时间在隔天零点，意思是每天清空一次
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            jedis.expireAt(key, calendar.getTimeInMillis() / 1000);
            if (value == 0) return true;
        } catch (Exception e) {
            logger.error("pullpop id=" + id + " error: ", e);
        } finally {
            pool.push(jedis);
        }
        return false;
    }

    public boolean pulldiv(String type, Long id, String ip) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.ADS_DIV_IP_MAP), type, id), filed = ip;
            long value = jedis.hset(key, filed, "0");
            // 设置过期时间在隔天零点，意思是每天清空一次
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            jedis.expireAt(key, calendar.getTimeInMillis() / 1000);
            if (value == 0) return true;
        } catch (Exception e) {
            logger.error("pulldiv id=" + id + " error: ", e);
        } finally {
            pool.push(jedis);
        }
        return false;
    }
}
