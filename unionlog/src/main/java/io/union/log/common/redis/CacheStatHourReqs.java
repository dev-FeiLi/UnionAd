package io.union.log.common.redis;

import io.union.log.common.proto.StatHourReqsProto;
import io.union.log.entity.StatHourReqs;
import io.union.log.service.StatHourReqsService;
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
 * Created by Administrator on 2017/7/26.
 */
@Component
public class CacheStatHourReqs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;
    @Autowired
    private StatHourReqsService statHourReqsService;

    private StatHourReqs build(StatHourReqsProto.StatHourReqsMessage message) {
        StatHourReqs reqs = new StatHourReqs();
        reqs.setId(message.getId());
        reqs.setUid(message.getUid());
        reqs.setZoneid(message.getZoneid());
        reqs.setAddTime(new Date(message.getAddTime()));
        reqs.setHour0(message.getHour0());
        reqs.setHour1(message.getHour1());
        reqs.setHour2(message.getHour2());
        reqs.setHour3(message.getHour3());
        reqs.setHour4(message.getHour4());
        reqs.setHour5(message.getHour5());
        reqs.setHour6(message.getHour6());
        reqs.setHour7(message.getHour7());
        reqs.setHour8(message.getHour8());
        reqs.setHour9(message.getHour9());
        reqs.setHour10(message.getHour10());
        reqs.setHour11(message.getHour11());
        reqs.setHour12(message.getHour12());
        reqs.setHour13(message.getHour13());
        reqs.setHour14(message.getHour14());
        reqs.setHour15(message.getHour15());
        reqs.setHour16(message.getHour16());
        reqs.setHour17(message.getHour17());
        reqs.setHour18(message.getHour18());
        reqs.setHour19(message.getHour19());
        reqs.setHour20(message.getHour20());
        reqs.setHour21(message.getHour21());
        reqs.setHour22(message.getHour22());
        reqs.setHour23(message.getHour23());
        return reqs;
    }

    private StatHourReqsProto.StatHourReqsMessage build(StatHourReqs reqs) {
        StatHourReqsProto.StatHourReqsMessage.Builder builder = StatHourReqsProto.StatHourReqsMessage.newBuilder();
        builder.setId(reqs.getId());
        builder.setUid(reqs.getUid());
        builder.setZoneid(reqs.getZoneid());
        builder.setAddTime(reqs.getAddTime().getTime());
        builder.setHour0(reqs.getHour0());
        builder.setHour1(reqs.getHour1());
        builder.setHour2(reqs.getHour2());
        builder.setHour3(reqs.getHour3());
        builder.setHour4(reqs.getHour4());
        builder.setHour5(reqs.getHour5());
        builder.setHour6(reqs.getHour6());
        builder.setHour7(reqs.getHour7());
        builder.setHour8(reqs.getHour8());
        builder.setHour9(reqs.getHour9());
        builder.setHour10(reqs.getHour10());
        builder.setHour11(reqs.getHour11());
        builder.setHour12(reqs.getHour12());
        builder.setHour13(reqs.getHour13());
        builder.setHour14(reqs.getHour14());
        builder.setHour15(reqs.getHour15());
        builder.setHour16(reqs.getHour16());
        builder.setHour17(reqs.getHour17());
        builder.setHour18(reqs.getHour18());
        builder.setHour19(reqs.getHour19());
        builder.setHour20(reqs.getHour20());
        builder.setHour21(reqs.getHour21());
        builder.setHour22(reqs.getHour22());
        builder.setHour23(reqs.getHour23());
        return builder.build();
    }

    public void save(StatHourReqs reqs) {
        Jedis jedis = pool.pull();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String addTime = sdf.format(reqs.getAddTime());
            Long zoneid = reqs.getZoneid();
            String key = String.format(String.valueOf(RedisKeys.STAT_HOUR_REQS_DATE_MAP), addTime), filed = String.valueOf(zoneid);
            StatHourReqsProto.StatHourReqsMessage message = build(reqs);
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

    public void save(List<StatHourReqs> list) {
        Jedis jedis = pool.pull();
        try {
            Pipeline pipeline = jedis.pipelined();
            list.forEach(reqs -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String addTime = sdf.format(reqs.getAddTime());
                Long zoneid = reqs.getZoneid();
                String key = String.format(String.valueOf(RedisKeys.STAT_HOUR_REQS_DATE_MAP), addTime), filed = String.valueOf(zoneid);
                StatHourReqsProto.StatHourReqsMessage message = build(reqs);
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

    public StatHourReqs save(Long zoneid, String addTime) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.STAT_HOUR_REQS_DATE_MAP), addTime), filed = String.valueOf(zoneid);
            // 设置过期时间在隔天零点
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            jedis.expireAt(key, calendar.getTimeInMillis() / 1000);

            StatHourReqs reqs = statHourReqsService.findOne(zoneid, addTime);
            if (null != reqs) {
                StatHourReqsProto.StatHourReqsMessage message = build(reqs);
                jedis.hset(key.getBytes(), filed.getBytes(), message.toByteArray());
                return reqs;
            } else {
                // 这里防止被SQL渗透，被request爆了数据库
                // 如果查出来是空，则在redis存入空字符串，表明id对应的对象在数据库中不存在
                // 下次继续请求的时候，则可以优先判断而不用查询数据库，直接返回错误信息
                jedis.hset(key.getBytes(), filed.getBytes(), "0".getBytes());
            }
        } catch (Exception e) {
            logger.error("save error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }

    /**
     * 使用时，如果得到的对象不是null，也不能认定是正确的对象<br/>
     * 需要继续判断该对象的主键是否大于零，大于零是正确的对象<br/>
     * 等于零则是错误的对象，数据库已经限定无符号，所以不存在小于零
     *
     * @param zoneid
     * @param addTime
     * @return
     */
    public StatHourReqs single(Long zoneid, String addTime) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.STAT_HOUR_REQS_DATE_MAP), addTime), filed = String.valueOf(zoneid);
            byte[] value = jedis.hget(key.getBytes(), filed.getBytes());
            if (null != value && value.length > 0) {
                if (value.length > 1) {
                    StatHourReqsProto.StatHourReqsMessage message = StatHourReqsProto.StatHourReqsMessage.parseFrom(value);
                    return build(message);
                }
            } else {
                return save(zoneid, addTime);
            }
        } catch (Exception e) {
            logger.error("single zoneid=" + zoneid + ", addtime=" + addTime + " error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }
}
