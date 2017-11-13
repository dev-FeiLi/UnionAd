package io.union.log.common.redis;

import io.union.log.common.proto.LogOsProto;
import io.union.log.entity.LogOs;
import io.union.log.service.LogOsService;
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
public class CacheLogOs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;
    @Autowired
    private LogOsService logOsService;

    private LogOs build(LogOsProto.LogOsMessage message) {
        LogOs os = new LogOs();
        os.setAddTime(new Date(message.getAddTime()));
        os.setCusNum(message.getCusNum());
        os.setCusOs(message.getCusOs());
        os.setId(message.getId());
        os.setUid(message.getUid());
        return os;
    }

    private LogOsProto.LogOsMessage build(LogOs os) {
        LogOsProto.LogOsMessage.Builder builder = LogOsProto.LogOsMessage.newBuilder();
        builder.setAddTime(os.getAddTime().getTime());
        builder.setCusNum(os.getCusNum());
        builder.setCusOs(os.getCusOs());
        builder.setId(os.getId());
        builder.setUid(os.getUid());
        return builder.build();
    }

    public void save(LogOs os) {
        Jedis jedis = pool.pull();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String cusOs = os.getCusOs(), addtime = sdf.format(os.getAddTime());
            Long uid = os.getUid();
            String key = String.format(String.valueOf(RedisKeys.LOG_OS_MAP), cusOs, addtime), filed = String.valueOf(uid);
            LogOsProto.LogOsMessage message = build(os);
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

    public void save(List<LogOs> list) {
        Jedis jedis = pool.pull();
        try {
            Pipeline pipeline = jedis.pipelined();
            list.forEach(os -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String cusOs = os.getCusOs(), addtime = sdf.format(os.getAddTime());
                Long uid = os.getUid();
                String key = String.format(String.valueOf(RedisKeys.LOG_OS_MAP), cusOs, addtime), filed = String.valueOf(uid);
                LogOsProto.LogOsMessage message = build(os);
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

    public LogOs save(String cusOs, Long uid, String addtime) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.LOG_OS_MAP), cusOs, addtime), filed = String.valueOf(uid);
            // 设置过期时间在隔天零点
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            jedis.expireAt(key, calendar.getTimeInMillis() / 1000);

            LogOs os = logOsService.findOne(cusOs, uid, addtime);
            if (null != os) {
                LogOsProto.LogOsMessage message = build(os);
                jedis.hset(key.getBytes(), filed.getBytes(), message.toByteArray());
                return os;
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

    public LogOs single(String cusOs, Long uid, String addtime) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.LOG_OS_MAP), cusOs, addtime), filed = String.valueOf(uid);
            byte[] value = jedis.hget(key.getBytes(), filed.getBytes());
            if (null != value && value.length > 0) {
                if (value.length > 1) {
                    LogOsProto.LogOsMessage message = LogOsProto.LogOsMessage.parseFrom(value);
                    return build(message);
                }
            } else {
                return save(cusOs, uid, addtime);
            }
        } catch (Exception e) {
            logger.error("single error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }

}
