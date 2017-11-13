package io.union.log.common.redis;

import io.union.log.common.proto.StatHourPvProto;
import io.union.log.entity.StatHourPv;
import io.union.log.service.StatHourPvService;
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
public class CacheStatHourPv {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;
    @Autowired
    private StatHourPvService statHourPvService;

    private StatHourPv build(StatHourPvProto.StatHourPvMessage message) {
        StatHourPv hourPv = new StatHourPv();
        hourPv.setId(message.getId());
        hourPv.setUid(message.getUid());
        hourPv.setSiteid(message.getSiteid());
        hourPv.setPlanid(message.getPlanid());
        hourPv.setAddTime(new Date(message.getAddTime()));
        hourPv.setHour0(message.getHour0());
        hourPv.setHour1(message.getHour1());
        hourPv.setHour2(message.getHour2());
        hourPv.setHour3(message.getHour3());
        hourPv.setHour4(message.getHour4());
        hourPv.setHour5(message.getHour5());
        hourPv.setHour6(message.getHour6());
        hourPv.setHour7(message.getHour7());
        hourPv.setHour8(message.getHour8());
        hourPv.setHour9(message.getHour9());
        hourPv.setHour10(message.getHour10());
        hourPv.setHour11(message.getHour11());
        hourPv.setHour12(message.getHour12());
        hourPv.setHour13(message.getHour13());
        hourPv.setHour14(message.getHour14());
        hourPv.setHour15(message.getHour15());
        hourPv.setHour16(message.getHour16());
        hourPv.setHour17(message.getHour17());
        hourPv.setHour18(message.getHour18());
        hourPv.setHour19(message.getHour19());
        hourPv.setHour20(message.getHour20());
        hourPv.setHour21(message.getHour21());
        hourPv.setHour22(message.getHour22());
        hourPv.setHour23(message.getHour23());
        return hourPv;
    }

    private StatHourPvProto.StatHourPvMessage build(StatHourPv hourPv) {
        StatHourPvProto.StatHourPvMessage.Builder builder = StatHourPvProto.StatHourPvMessage.newBuilder();
        builder.setId(hourPv.getId());
        builder.setUid(hourPv.getUid());
        builder.setSiteid(hourPv.getSiteid());
        builder.setPlanid(hourPv.getPlanid());
        builder.setAddTime(hourPv.getAddTime().getTime());
        builder.setHour0(hourPv.getHour0());
        builder.setHour1(hourPv.getHour1());
        builder.setHour2(hourPv.getHour2());
        builder.setHour3(hourPv.getHour3());
        builder.setHour4(hourPv.getHour4());
        builder.setHour5(hourPv.getHour5());
        builder.setHour6(hourPv.getHour6());
        builder.setHour7(hourPv.getHour7());
        builder.setHour8(hourPv.getHour8());
        builder.setHour9(hourPv.getHour9());
        builder.setHour10(hourPv.getHour10());
        builder.setHour11(hourPv.getHour11());
        builder.setHour12(hourPv.getHour12());
        builder.setHour13(hourPv.getHour13());
        builder.setHour14(hourPv.getHour14());
        builder.setHour15(hourPv.getHour15());
        builder.setHour16(hourPv.getHour16());
        builder.setHour17(hourPv.getHour17());
        builder.setHour18(hourPv.getHour18());
        builder.setHour19(hourPv.getHour19());
        builder.setHour20(hourPv.getHour20());
        builder.setHour21(hourPv.getHour21());
        builder.setHour22(hourPv.getHour22());
        builder.setHour23(hourPv.getHour23());
        return builder.build();
    }

    public void save(StatHourPv hourIp) {
        Jedis jedis = pool.pull();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String addtime = sdf.format(hourIp.getAddTime());
            Long uid = hourIp.getUid(), siteid = hourIp.getSiteid(), planid = hourIp.getPlanid();
            String key = String.format(String.valueOf(RedisKeys.STAT_HOUR_PV_DATE_MAP), uid, addtime), filed = siteid + ":" + planid;
            StatHourPvProto.StatHourPvMessage message = build(hourIp);
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

    public void save(List<StatHourPv> list) {
        Jedis jedis = pool.pull();
        try {
            Pipeline pipeline = jedis.pipelined();
            list.forEach(hourIp -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String addtime = sdf.format(hourIp.getAddTime());
                Long uid = hourIp.getUid(), siteid = hourIp.getSiteid(), planid = hourIp.getPlanid();
                String key = String.format(String.valueOf(RedisKeys.STAT_HOUR_PV_DATE_MAP), uid, addtime), filed = siteid + ":" + planid;
                StatHourPvProto.StatHourPvMessage message = build(hourIp);
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

    public StatHourPv save(Long uid, Long siteid, Long planid, String addtime) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.STAT_HOUR_PV_DATE_MAP), uid, addtime), filed = siteid + ":" + planid;
            // 设置过期时间在隔天零点
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            jedis.expireAt(key, calendar.getTimeInMillis() / 1000);

            StatHourPv hourIp = statHourPvService.findByUnique(addtime, uid, siteid, planid);
            if (null != hourIp) {
                StatHourPvProto.StatHourPvMessage message = build(hourIp);
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

    public StatHourPv single(Long uid, Long siteid, Long planid, String addtime) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.STAT_HOUR_PV_DATE_MAP), uid, addtime), filed = siteid + ":" + planid;
            byte[] value = jedis.hget(key.getBytes(), filed.getBytes());
            if (null != value && value.length > 0) {
                if (value.length > 1) {
                    StatHourPvProto.StatHourPvMessage message = StatHourPvProto.StatHourPvMessage.parseFrom(value);
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
