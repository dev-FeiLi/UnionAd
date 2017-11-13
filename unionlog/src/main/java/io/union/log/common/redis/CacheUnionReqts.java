package io.union.log.common.redis;

import io.union.log.common.proto.UnionReqtsProto;
import io.union.log.entity.UnionReqts;
import io.union.log.service.UnionReqtsService;
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
public class CacheUnionReqts {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;
    @Autowired
    private UnionReqtsService unionReqtsService;

    private UnionReqts build(UnionReqtsProto.UnionReqtsMessage message) {
        UnionReqts reqts = new UnionReqts();
        reqts.setAddTime(new Date(message.getAddTime()));
        reqts.setId(message.getId());
        reqts.setReqeusts(message.getRequests());
        reqts.setUid(message.getUid());
        reqts.setZoneid(message.getZoneid());
        return reqts;
    }

    private UnionReqtsProto.UnionReqtsMessage build(UnionReqts reqts) {
        UnionReqtsProto.UnionReqtsMessage.Builder builder = UnionReqtsProto.UnionReqtsMessage.newBuilder();
        builder.setAddTime(reqts.getAddTime().getTime());
        builder.setId(reqts.getId());
        builder.setRequests(reqts.getReqeusts());
        builder.setUid(reqts.getUid());
        builder.setZoneid(reqts.getZoneid());
        return builder.build();
    }

    public void save(UnionReqts reqts) {
        Jedis jedis = pool.pull();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String addTime = sdf.format(reqts.getAddTime());
            Long zoneid = reqts.getZoneid();
            String key = String.format(String.valueOf(RedisKeys.UNION_REQUESTS_DATE_MAP), addTime), filed = String.valueOf(zoneid);
            UnionReqtsProto.UnionReqtsMessage message = build(reqts);
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

    public void save(List<UnionReqts> list) {
        Jedis jedis = pool.pull();
        try {
            Pipeline pipeline = jedis.pipelined();
            list.forEach(reqts -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String addTime = sdf.format(reqts.getAddTime());
                Long zoneid = reqts.getZoneid();
                String key = String.format(String.valueOf(RedisKeys.UNION_REQUESTS_DATE_MAP), addTime), filed = String.valueOf(zoneid);
                UnionReqtsProto.UnionReqtsMessage message = build(reqts);
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

    public UnionReqts save(Long zoneid, String addTime) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.UNION_REQUESTS_DATE_MAP), addTime), filed = String.valueOf(zoneid);
            // 设置过期时间在隔天零点
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            jedis.expireAt(key, calendar.getTimeInMillis() / 1000);

            UnionReqts reqts = unionReqtsService.findOne(zoneid, addTime);
            if (null != reqts) {
                UnionReqtsProto.UnionReqtsMessage message = build(reqts);
                jedis.hset(key.getBytes(), filed.getBytes(), message.toByteArray());
                return reqts;
            } else {
                // 这里防止被SQL渗透，被request爆了数据库
                // 如果查出来是空，则在redis存入空字符串，表明id对应的对象在数据库中不存在
                // 下次继续请求的时候，则可以优先判断而不用查询数据库，直接返回错误信息
                jedis.hset(key.getBytes(), filed.getBytes(), "0".getBytes());
            }
        } catch (Exception e) {
            logger.error("save zoneid=" + zoneid + ", addtime=" + addTime, e);
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
    public UnionReqts single(Long zoneid, String addTime) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.UNION_REQUESTS_DATE_MAP), addTime), filed = String.valueOf(zoneid);
            byte[] value = jedis.hget(key.getBytes(), filed.getBytes());
            if (null != value && value.length > 0) {
                if (value.length > 1) {
                    UnionReqtsProto.UnionReqtsMessage message = UnionReqtsProto.UnionReqtsMessage.parseFrom(value);
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
