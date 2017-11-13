package io.union.admin.common.redis;

import io.union.admin.common.proto.UnionStatsProto;
import io.union.admin.entity.UnionStats;
import io.union.admin.service.UnionStatsService;
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
public class CacheUnionStats {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;
    @Autowired
    private UnionStatsService unionStatsService;

    private UnionStats build(UnionStatsProto.UnionStatsMessage message) {
        UnionStats stats = new UnionStats();
        stats.setId(message.getId());
        stats.setUid(message.getUid());
        stats.setZoneid(message.getZoneid());
        stats.setSiteid(message.getSiteid());
        stats.setAdvid(message.getAdvid());
        stats.setPlanid(message.getPlanid());
        stats.setAdid(message.getAdid());
        stats.setAddTime(new Date(message.getAddTime()));
        stats.setViews(message.getViews());
        stats.setClicks(message.getClicks());
        stats.setClickip(message.getClickip());
        stats.setPaytype(message.getPaytype());
        stats.setPaynum(message.getPaynum());
        stats.setDedunum(message.getDedunum());
        stats.setSumpay(message.getSumpay());
        stats.setSumadvpay(message.getSumadvpay());
        stats.setSumprofit(message.getSumprofit());
        return stats;
    }

    private UnionStatsProto.UnionStatsMessage build(UnionStats stats) {
        UnionStatsProto.UnionStatsMessage.Builder builder = UnionStatsProto.UnionStatsMessage.newBuilder();
        builder.setId(stats.getId());
        builder.setUid(stats.getUid());
        builder.setZoneid(stats.getZoneid());
        builder.setSiteid(stats.getSiteid());
        builder.setAdvid(stats.getAdvid());
        builder.setPlanid(stats.getPlanid());
        builder.setAdid(stats.getAdid());
        builder.setAddTime(stats.getAddTime().getTime());
        builder.setViews(stats.getViews());
        builder.setClicks(stats.getClicks());
        builder.setClickip(stats.getClickip());
        builder.setPaytype(stats.getPaytype());
        builder.setPaynum(stats.getPaynum());
        builder.setDedunum(stats.getDedunum());
        builder.setSumpay(stats.getSumpay());
        builder.setSumadvpay(stats.getSumadvpay());
        builder.setSumprofit(stats.getSumprofit());
        return builder.build();
    }

    public void save(UnionStats stats) {
        Jedis jedis = pool.pull();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String addtime = sdf.format(stats.getAddTime());
            Long uid = stats.getUid(), zoneid = stats.getZoneid(), siteid = stats.getSiteid(), advid = stats.getAdvid(), planid = stats.getPlanid(), adid = stats.getAdid();
            String key = String.format(String.valueOf(RedisKeys.UNION_STATS_DATE_MAP), uid, zoneid, addtime), filed = siteid + ":" + advid + ":" + planid + ":" + adid;
            UnionStatsProto.UnionStatsMessage message = build(stats);
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

    public void save(List<UnionStats> list) {
        Jedis jedis = pool.pull();
        try {
            Pipeline pipeline = jedis.pipelined();
            list.forEach(stats -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String addtime = sdf.format(stats.getAddTime());
                Long uid = stats.getUid(), zoneid = stats.getZoneid(), siteid = stats.getSiteid(), advid = stats.getAdvid(), planid = stats.getPlanid(), adid = stats.getAdid();
                String key = String.format(String.valueOf(RedisKeys.UNION_STATS_DATE_MAP), uid, zoneid, addtime), filed = siteid + ":" + advid + ":" + planid + ":" + adid;
                UnionStatsProto.UnionStatsMessage message = build(stats);
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
}
