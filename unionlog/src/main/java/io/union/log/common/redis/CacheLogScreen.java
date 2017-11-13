package io.union.log.common.redis;

import io.union.log.common.proto.LogScreenProto;
import io.union.log.entity.LogScreen;
import io.union.log.service.LogScreenService;
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
public class CacheLogScreen {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;
    @Autowired
    private LogScreenService logScreenService;

    private LogScreen build(LogScreenProto.LogScreenMessage message) {
        LogScreen screen = new LogScreen();
        screen.setAddTime(new Date(message.getAddTime()));
        screen.setCusNum(message.getCusNum());
        screen.setCusScreen(message.getCusScreen());
        screen.setId(message.getId());
        screen.setUid(message.getUid());
        return screen;
    }

    private LogScreenProto.LogScreenMessage build(LogScreen screen) {
        LogScreenProto.LogScreenMessage.Builder builder = LogScreenProto.LogScreenMessage.newBuilder();
        builder.setAddTime(screen.getAddTime().getTime());
        builder.setCusNum(screen.getCusNum());
        builder.setCusScreen(screen.getCusScreen());
        builder.setId(screen.getId());
        builder.setUid(screen.getUid());
        return builder.build();
    }

    public void save(LogScreen screen) {
        Jedis jedis = pool.pull();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String cusScreen = screen.getCusScreen(), addtime = sdf.format(screen.getAddTime());
            Long uid = screen.getUid();
            String key = String.format(String.valueOf(RedisKeys.LOG_SCREEN_MAP), cusScreen, addtime), filed = String.valueOf(uid);
            LogScreenProto.LogScreenMessage message = build(screen);
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

    public void save(List<LogScreen> list) {
        Jedis jedis = pool.pull();
        try {
            Pipeline pipeline = jedis.pipelined();
            list.forEach(screen -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String cusScreen = screen.getCusScreen(), addtime = sdf.format(screen.getAddTime());
                Long uid = screen.getUid();
                String key = String.format(String.valueOf(RedisKeys.LOG_SCREEN_MAP), cusScreen, addtime), filed = String.valueOf(uid);
                LogScreenProto.LogScreenMessage message = build(screen);
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

    public LogScreen save(String cusScreen, Long uid, String addtime) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.LOG_SCREEN_MAP), cusScreen, addtime), filed = String.valueOf(uid);
            // 设置过期时间在隔天零点
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            jedis.expireAt(key, calendar.getTimeInMillis() / 1000);

            LogScreen screen = logScreenService.findOne(cusScreen, uid, addtime);
            if (null != screen) {
                LogScreenProto.LogScreenMessage message = build(screen);
                jedis.hset(key.getBytes(), filed.getBytes(), message.toByteArray());
                return screen;
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

    public LogScreen single(String cusScreen, Long uid, String addtime) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.LOG_SCREEN_MAP), cusScreen, addtime), filed = String.valueOf(uid);
            byte[] value = jedis.hget(key.getBytes(), filed.getBytes());
            if (null != value && value.length > 0) {
                if (value.length > 1) {
                    LogScreenProto.LogScreenMessage message = LogScreenProto.LogScreenMessage.parseFrom(value);
                    return build(message);
                }
            } else {
                return save(cusScreen, uid, addtime);
            }
        } catch (Exception e) {
            logger.error("single error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }
}
