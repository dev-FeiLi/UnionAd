package io.union.admin.common.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by Administrator on 2017/7/11.
 */
@Component
public class RedisPool {

    private static JedisPool jedisPool;

    public RedisPool(@Value("${spring.redis.host}") String host,
                     @Value("${spring.redis.password}") String password,
                     @Value("${spring.redis.port}") int port,
                     @Value("${spring.redis.pool.max-active}") int maxactive,
                     @Value("${spring.redis.pool.min-idle}") int idle,
                     @Value("${spring.redis.pool.max-wait}") long maxwait,
                     @Value("${spring.redis.timeout}") int timeout) {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxactive);
        jedisPoolConfig.setMaxIdle(idle);
        jedisPoolConfig.setMaxWaitMillis(maxwait);
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setTestOnReturn(true);
        jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
    }

    public Jedis pull() {
        return jedisPool.getResource();
    }

    public void push(Jedis jedis) {
        if (null != jedis) {
            jedis.close();
        }
    }
}
