package io.union.log.common.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

/**
 * Created by Administrator on 2017/7/28.
 */
@Component
public class CacheLockPach {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;

    public void add(String lockPath) {
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.LOCK_TO_DEL);
            jedis.hincrBy(key, lockPath, 1);
        } catch (Exception e) {
            logger.error("add error: ", e);
        } finally {
            pool.push(jedis);
        }
    }
}
