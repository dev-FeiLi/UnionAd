package io.union.log.common.redis;

import io.union.log.common.proto.StatHourIpProto;
import io.union.log.entity.StatHourIp;
import io.union.log.service.StatHourIpService;
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
 * Created by Administrator on 2017/7/29.
 */
@Component
public class CacheStatHourIp {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;
    @Autowired
    private StatHourIpService statHourIpService;

    private StatHourIp build(StatHourIpProto.StatHourIpMessage message) {
        StatHourIp hourIp = new StatHourIp();
        hourIp.setId(message.getId());
        hourIp.setUid(message.getUid());
        hourIp.setSiteid(message.getSiteid());
        hourIp.setPlanid(message.getPlanid());
        hourIp.setAddTime(new Date(message.getAddTime()));
        hourIp.setHour0(message.getHour0());
        hourIp.setHour1(message.getHour1());
        hourIp.setHour2(message.getHour2());
        hourIp.setHour3(message.getHour3());
        hourIp.setHour4(message.getHour4());
        hourIp.setHour5(message.getHour5());
        hourIp.setHour6(message.getHour6());
        hourIp.setHour7(message.getHour7());
        hourIp.setHour8(message.getHour8());
        hourIp.setHour9(message.getHour9());
        hourIp.setHour10(message.getHour10());
        hourIp.setHour11(message.getHour11());
        hourIp.setHour12(message.getHour12());
        hourIp.setHour13(message.getHour13());
        hourIp.setHour14(message.getHour14());
        hourIp.setHour15(message.getHour15());
        hourIp.setHour16(message.getHour16());
        hourIp.setHour17(message.getHour17());
        hourIp.setHour18(message.getHour18());
        hourIp.setHour19(message.getHour19());
        hourIp.setHour20(message.getHour20());
        hourIp.setHour21(message.getHour21());
        hourIp.setHour22(message.getHour22());
        hourIp.setHour23(message.getHour23());
        return hourIp;
    }

    private StatHourIpProto.StatHourIpMessage build(StatHourIp hourIp) {
        StatHourIpProto.StatHourIpMessage.Builder builder = StatHourIpProto.StatHourIpMessage.newBuilder();
        builder.setId(hourIp.getId());
        builder.setUid(hourIp.getUid());
        builder.setSiteid(hourIp.getSiteid());
        builder.setPlanid(hourIp.getPlanid());
        builder.setAddTime(hourIp.getAddTime().getTime());
        builder.setHour0(hourIp.getHour0());
        builder.setHour1(hourIp.getHour1());
        builder.setHour2(hourIp.getHour2());
        builder.setHour3(hourIp.getHour3());
        builder.setHour4(hourIp.getHour4());
        builder.setHour5(hourIp.getHour5());
        builder.setHour6(hourIp.getHour6());
        builder.setHour7(hourIp.getHour7());
        builder.setHour8(hourIp.getHour8());
        builder.setHour9(hourIp.getHour9());
        builder.setHour10(hourIp.getHour10());
        builder.setHour11(hourIp.getHour11());
        builder.setHour12(hourIp.getHour12());
        builder.setHour13(hourIp.getHour13());
        builder.setHour14(hourIp.getHour14());
        builder.setHour15(hourIp.getHour15());
        builder.setHour16(hourIp.getHour16());
        builder.setHour17(hourIp.getHour17());
        builder.setHour18(hourIp.getHour18());
        builder.setHour19(hourIp.getHour19());
        builder.setHour20(hourIp.getHour20());
        builder.setHour21(hourIp.getHour21());
        builder.setHour22(hourIp.getHour22());
        builder.setHour23(hourIp.getHour23());
        return builder.build();
    }

    public void save(StatHourIp hourIp) {
        Jedis jedis = pool.pull();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String addtime = sdf.format(hourIp.getAddTime());
            Long uid = hourIp.getUid(), siteid = hourIp.getSiteid(), planid = hourIp.getPlanid();
            String key = String.format(String.valueOf(RedisKeys.STAT_HOUR_IP_DATE_MAP), uid, addtime), filed = siteid + ":" + planid;
            StatHourIpProto.StatHourIpMessage message = build(hourIp);
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

    public void save(List<StatHourIp> list) {
        Jedis jedis = pool.pull();
        try {
            Pipeline pipeline = jedis.pipelined();
            list.forEach(hourIp -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String addtime = sdf.format(hourIp.getAddTime());
                Long uid = hourIp.getUid(), siteid = hourIp.getSiteid(), planid = hourIp.getPlanid();
                String key = String.format(String.valueOf(RedisKeys.STAT_HOUR_IP_DATE_MAP), uid, addtime), filed = siteid + ":" + planid;
                StatHourIpProto.StatHourIpMessage message = build(hourIp);
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

    public StatHourIp save(Long uid, Long siteid, Long planid, String addtime) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.STAT_HOUR_IP_DATE_MAP), uid, addtime), filed = siteid + ":" + planid;
            // 设置过期时间在隔天零点
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            jedis.expireAt(key, calendar.getTimeInMillis() / 1000);

            StatHourIp hourIp = statHourIpService.findByUnique(addtime, uid, siteid, planid);
            if (null != hourIp) {
                StatHourIpProto.StatHourIpMessage message = build(hourIp);
                jedis.hset(key.getBytes(), filed.getBytes(), message.toByteArray());
                return hourIp;
            } else {
                jedis.hset(key.getBytes(), filed.getBytes(), "0".getBytes());
            }
        } catch (Exception e) {
            logger.error("save error: ", e);
        } finally {
            pool.push(jedis
            );
        }
        return null;
    }

    public StatHourIp single(Long uid, Long siteid, Long planid, String addtime) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.STAT_HOUR_IP_DATE_MAP), uid, addtime), filed = siteid + ":" + planid;
            byte[] value = jedis.hget(key.getBytes(), filed.getBytes());
            if (null != value && value.length > 0) {
                if (value.length > 1) {
                    StatHourIpProto.StatHourIpMessage message = StatHourIpProto.StatHourIpMessage.parseFrom(value);
                    return build(message);
                }
            } else {
                return save(uid, siteid, planid, addtime);
            }
        } catch (Exception e) {
            logger.error("single error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }
}
