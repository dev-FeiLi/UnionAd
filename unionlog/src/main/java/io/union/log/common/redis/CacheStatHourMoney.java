package io.union.log.common.redis;

import io.union.log.common.proto.StatHourMoneyProto;
import io.union.log.entity.StatHourMoney;
import io.union.log.service.StatHourMoneyService;
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
public class CacheStatHourMoney {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;
    @Autowired
    private StatHourMoneyService statHourMoneyService;

    private StatHourMoney build(StatHourMoneyProto.StatHourMoneyMessage message) {
        StatHourMoney hourMoney = new StatHourMoney();
        hourMoney.setId(message.getId());
        hourMoney.setUid(message.getUid());
        hourMoney.setSiteid(message.getSiteid());
        hourMoney.setPlanid(message.getPlanid());
        hourMoney.setAddTime(new Date(message.getAddTime()));
        hourMoney.setHour0(message.getHour0());
        hourMoney.setHour1(message.getHour1());
        hourMoney.setHour2(message.getHour2());
        hourMoney.setHour3(message.getHour3());
        hourMoney.setHour4(message.getHour4());
        hourMoney.setHour5(message.getHour5());
        hourMoney.setHour6(message.getHour6());
        hourMoney.setHour7(message.getHour7());
        hourMoney.setHour8(message.getHour8());
        hourMoney.setHour9(message.getHour9());
        hourMoney.setHour10(message.getHour10());
        hourMoney.setHour11(message.getHour11());
        hourMoney.setHour12(message.getHour12());
        hourMoney.setHour13(message.getHour13());
        hourMoney.setHour14(message.getHour14());
        hourMoney.setHour15(message.getHour15());
        hourMoney.setHour16(message.getHour16());
        hourMoney.setHour17(message.getHour17());
        hourMoney.setHour18(message.getHour18());
        hourMoney.setHour19(message.getHour19());
        hourMoney.setHour20(message.getHour20());
        hourMoney.setHour21(message.getHour21());
        hourMoney.setHour22(message.getHour22());
        hourMoney.setHour23(message.getHour23());
        return hourMoney;
    }

    private StatHourMoneyProto.StatHourMoneyMessage build(StatHourMoney hourMoney) {
        StatHourMoneyProto.StatHourMoneyMessage.Builder builder = StatHourMoneyProto.StatHourMoneyMessage.newBuilder();
        builder.setId(hourMoney.getId());
        builder.setUid(hourMoney.getUid());
        builder.setSiteid(hourMoney.getSiteid());
        builder.setPlanid(hourMoney.getPlanid());
        builder.setAddTime(hourMoney.getAddTime().getTime());
        builder.setHour0(hourMoney.getHour0());
        builder.setHour1(hourMoney.getHour1());
        builder.setHour2(hourMoney.getHour2());
        builder.setHour3(hourMoney.getHour3());
        builder.setHour4(hourMoney.getHour4());
        builder.setHour5(hourMoney.getHour5());
        builder.setHour6(hourMoney.getHour6());
        builder.setHour7(hourMoney.getHour7());
        builder.setHour8(hourMoney.getHour8());
        builder.setHour9(hourMoney.getHour9());
        builder.setHour10(hourMoney.getHour10());
        builder.setHour11(hourMoney.getHour11());
        builder.setHour12(hourMoney.getHour12());
        builder.setHour13(hourMoney.getHour13());
        builder.setHour14(hourMoney.getHour14());
        builder.setHour15(hourMoney.getHour15());
        builder.setHour16(hourMoney.getHour16());
        builder.setHour17(hourMoney.getHour17());
        builder.setHour18(hourMoney.getHour18());
        builder.setHour19(hourMoney.getHour19());
        builder.setHour20(hourMoney.getHour20());
        builder.setHour21(hourMoney.getHour21());
        builder.setHour22(hourMoney.getHour22());
        builder.setHour23(hourMoney.getHour23());
        return builder.build();
    }

    public void save(StatHourMoney hourIp) {
        Jedis jedis = pool.pull();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String addtime = sdf.format(hourIp.getAddTime());
            Long uid = hourIp.getUid(), siteid = hourIp.getSiteid(), planid = hourIp.getPlanid();
            String key = String.format(String.valueOf(RedisKeys.STAT_HOUR_MONEY_DATE_MAP), uid, addtime), filed = siteid + ":" + planid;
            StatHourMoneyProto.StatHourMoneyMessage message = build(hourIp);
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

    public void save(List<StatHourMoney> list) {
        Jedis jedis = pool.pull();
        try {
            Pipeline pipeline = jedis.pipelined();
            list.forEach(hourIp -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String addtime = sdf.format(hourIp.getAddTime());
                Long uid = hourIp.getUid(), siteid = hourIp.getSiteid(), planid = hourIp.getPlanid();
                String key = String.format(String.valueOf(RedisKeys.STAT_HOUR_MONEY_DATE_MAP), uid, addtime), filed = siteid + ":" + planid;
                StatHourMoneyProto.StatHourMoneyMessage message = build(hourIp);
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

    public StatHourMoney save(Long uid, Long siteid, Long planid, String addtime) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.STAT_HOUR_MONEY_DATE_MAP), uid, addtime), filed = siteid + ":" + planid;
            // 设置过期时间在隔天零点
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            jedis.expireAt(key, calendar.getTimeInMillis() / 1000);

            StatHourMoney hourIp = statHourMoneyService.findByUnique(addtime, uid, siteid, planid);
            if (null != hourIp) {
                StatHourMoneyProto.StatHourMoneyMessage message = build(hourIp);
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

    public StatHourMoney single(Long uid, Long siteid, Long planid, String addtime) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.STAT_HOUR_MONEY_DATE_MAP), uid, addtime), filed = siteid + ":" + planid;
            byte[] value = jedis.hget(key.getBytes(), filed.getBytes());
            if (null != value && value.length > 0) {
                if (value.length > 1) {
                    StatHourMoneyProto.StatHourMoneyMessage message = StatHourMoneyProto.StatHourMoneyMessage.parseFrom(value);
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
