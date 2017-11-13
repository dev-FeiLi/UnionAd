package io.union.admin.common.redis;

import io.union.admin.entity.UnionStats;
import io.union.admin.service.UnionStatsService;
import org.apache.commons.lang.StringUtils;
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
public class CacheLimitMoney {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;
    @Autowired
    private UnionStatsService unionStatsService;

    public void add(Long planid, String addtime, Double advprice) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.LIMIT_MONEY_PLAN_DATE_MAP), addtime), filed = String.valueOf(planid);
            double value = jedis.hincrByFloat(key, filed, advprice);
            Double sumadvpay;
            if (advprice.equals(value)) {
                UnionStats stats = unionStatsService.sumByPage("planid", planid, 2, addtime, addtime);
                if (null != stats && null != stats.getSumadvpay()) {
                    sumadvpay = stats.getSumadvpay();
                    jedis.hset(key, filed, String.valueOf(sumadvpay));
                }
            }

            // 设置过期时间在隔天零点
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            jedis.expireAt(key, calendar.getTimeInMillis() / 1000);
        } catch (Exception e) {
            logger.error("add error: ", e);
        } finally {
            pool.push(jedis);
        }
    }

    public void subtract(Long planid, String addtime, Double advprice) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.LIMIT_MONEY_PLAN_DATE_MAP), addtime), filed = String.valueOf(planid);
            double value = jedis.hincrByFloat(key, filed, -1.0 * advprice);
            Double sumadvpay;
            if (value <= 0) {
                UnionStats stats = unionStatsService.sumByPage("planid", planid, 2, addtime, addtime);
                if (null != stats && null != stats.getSumadvpay()) {
                    sumadvpay = stats.getSumadvpay();
                    jedis.hset(key, filed, String.valueOf(sumadvpay));
                }
            }

            // 设置过期时间在隔天零点
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            jedis.expireAt(key, calendar.getTimeInMillis() / 1000);
        } catch (Exception e) {
            logger.error("subtract error: ", e);
        } finally {
            pool.push(jedis);
        }
    }

    public Double single(Long planid, String addtime) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.LIMIT_MONEY_PLAN_DATE_MAP), addtime), filed = String.valueOf(planid);
            String value = jedis.hget(key, filed);
            if (!StringUtils.isEmpty(value)) {
                Double sumadvpay = Double.valueOf(value);
                return sumadvpay;
            }
        } catch (Exception e) {
            logger.error("single error: ", e);
        } finally {
            pool.push(jedis);
        }
        return 0.0;
    }
}
