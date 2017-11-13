package io.union.log.common.redis;

import io.union.log.common.proto.LogCityProto;
import io.union.log.entity.LogCity;
import io.union.log.service.LogCityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/7/27.
 */
@Component
public class CacheLogCity {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;
    @Autowired
    private LogCityService logCityService;

    private LogCity build(LogCityProto.LogCityMessage message) {
        LogCity city = new LogCity();
        city.setAddTime(new Date(message.getAddTime()));
        city.setCusCity(message.getCusCity());
        city.setCusIsp(message.getCusIsp());
        city.setCusNum(message.getCusNum());
        city.setCusProvince(message.getCusProvince());
        city.setUid(message.getUid());
        city.setId(message.getId());
        return city;
    }

    private LogCityProto.LogCityMessage build(LogCity city) {
        LogCityProto.LogCityMessage.Builder builder = LogCityProto.LogCityMessage.newBuilder();
        builder.setAddTime(city.getAddTime().getTime());
        builder.setCusCity(city.getCusCity());
        builder.setCusIsp(city.getCusIsp());
        builder.setCusNum(city.getCusNum());
        builder.setCusProvince(city.getCusProvince());
        builder.setId(city.getId());
        builder.setUid(city.getUid());
        return builder.build();
    }

    public void save(LogCity city) {
        Jedis jedis = pool.pull();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String cusProvince = city.getCusProvince(), addtime = sdf.format(city.getAddTime()), cusIsp = city.getCusIsp();
            Long uid = city.getUid();
            String key = String.format(String.valueOf(RedisKeys.LOG_CITY_MAP), cusProvince, addtime), filed = uid + ":" + cusIsp;
            LogCityProto.LogCityMessage message = build(city);
            jedis.hset(key.getBytes(), filed.getBytes(), message.toByteArray());

            // 设置过期时间在隔天零点
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            jedis.expireAt(key, calendar.getTimeInMillis() / 1000);
        } catch (Exception e) {
            logger.error("save error: ", e);
        } finally {
            pool.push(jedis);
        }
    }

    public void save(List<LogCity> list) {
        Jedis jedis = pool.pull();
        try {
            Pipeline pipeline = jedis.pipelined();
            list.forEach(city -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String cusProvince = city.getCusProvince(), addtime = sdf.format(city.getAddTime()), cusIsp = city.getCusIsp();
                Long uid = city.getUid();
                String key = String.format(String.valueOf(RedisKeys.LOG_CITY_MAP), cusProvince, addtime), filed = uid + ":" + cusIsp;
                LogCityProto.LogCityMessage message = build(city);
                pipeline.hset(key.getBytes(), filed.getBytes(), message.toByteArray());

                // 设置过期时间在隔天零点
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, 2);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                pipeline.expireAt(key, calendar.getTimeInMillis() / 1000);
            });
            pipeline.sync();
        } catch (Exception e) {
            logger.error("save error: ", e);
        } finally {
            pool.push(jedis);
        }
    }

    public LogCity save(String cusProvince, String cusIsp, Long uid, String addtime) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.LOG_CITY_MAP), cusProvince, addtime), filed = uid + ":" + cusIsp;
            // 设置过期时间在隔天零点
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            jedis.expireAt(key, calendar.getTimeInMillis() / 1000);


            LogCity city = logCityService.findOne(cusProvince, cusIsp, uid, addtime);
            if (null != city) {
                LogCityProto.LogCityMessage message = build(city);
                jedis.hset(key.getBytes(), filed.getBytes(), message.toByteArray());
                return city;
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

    public LogCity single(String cusProvince, String cusIsp, Long uid, String addtime) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.LOG_CITY_MAP), cusProvince, addtime), filed = uid + ":" + cusIsp;
            byte[] value = jedis.hget(key.getBytes(), filed.getBytes());
            if (null != value && value.length > 0) {
                if (value.length > 1) {
                    LogCityProto.LogCityMessage message = LogCityProto.LogCityMessage.parseFrom(value);
                    return build(message);
                }
            } else {
                return save(cusProvince, cusIsp, uid, addtime);
            }
        } catch (Exception e) {
            logger.error("single error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;

    }
}
