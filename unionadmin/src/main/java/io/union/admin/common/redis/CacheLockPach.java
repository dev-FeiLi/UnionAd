package io.union.admin.common.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * Created by Administrator on 2017/7/28.
 */
@Component
public class CacheLockPach {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;

    public Set<String> pop() {
        Jedis jedis = pool.pull();
        Set<String> fields = null;
        try {
            String key = String.valueOf(RedisKeys.LOCK_TO_DEL);
            fields = jedis.hkeys(key);
        } catch (Exception e) {
            logger.error("pop error: ", e);
        } finally {
            pool.push(jedis);
        }
        return fields;
    }

    public void delete(String field) {
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.LOCK_TO_DEL);
            jedis.hdel(key, field);
        } catch (Exception e) {
            logger.error("delete error: ", e);
        } finally {
            pool.push(jedis);
        }
    }
}
