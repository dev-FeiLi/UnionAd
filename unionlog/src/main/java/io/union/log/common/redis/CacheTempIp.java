package io.union.log.common.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/7/28.
 */
@Component
public class CacheTempIp {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;

    public boolean exists(String tempip, Long planid, Long uid, String addtime) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.TEMP_IP_DATE_MAP), planid, addtime), filed = uid + ":" + tempip;
            // 先存了再说，如果返回是0，表示该field已经存在，1表示不存在
            long result = jedis.hset(key, filed, "0");
            // 设置过期时间在隔天零点
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            jedis.expireAt(key, calendar.getTimeInMillis() / 1000);
            if (result == 0) {
                return true;
            }
        } catch (Exception e) {
            logger.error("exists error: ", e);
        } finally {
            pool.push(jedis);
        }
        return false;
    }
}
