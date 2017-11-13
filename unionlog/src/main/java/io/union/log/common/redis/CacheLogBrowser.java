package io.union.log.common.redis;

import io.union.log.common.proto.LogBrowserProto;
import io.union.log.entity.LogBrowser;
import io.union.log.service.LogBrowserService;
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
public class CacheLogBrowser {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;
    @Autowired
    private LogBrowserService logBrowserService;

    private LogBrowserProto.LogBrowserMessage build(LogBrowser browser) {
        LogBrowserProto.LogBrowserMessage.Builder builder = LogBrowserProto.LogBrowserMessage.newBuilder();
        builder.setAddTime(browser.getAddTime().getTime());
        builder.setBrwName(browser.getBrwName());
        builder.setBrwVersion(browser.getBrwVersion());
        builder.setBrwPlat(browser.getBrwPlat());
        builder.setBrwNum(browser.getBrwNum());
        builder.setUid(browser.getUid());
        builder.setId(browser.getId());
        return builder.build();
    }

    private LogBrowser build(LogBrowserProto.LogBrowserMessage message) {
        LogBrowser browser = new LogBrowser();
        browser.setBrwNum(message.getBrwNum());
        browser.setBrwPlat(message.getBrwPlat());
        browser.setBrwVersion(message.getBrwVersion());
        browser.setBrwName(message.getBrwName());
        browser.setAddTime(new Date(message.getAddTime()));
        browser.setUid(message.getUid());
        browser.setId(message.getId());
        return browser;
    }

    public void save(LogBrowser browser) {
        Jedis jedis = pool.pull();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String brwName = browser.getBrwName(), brwOs = browser.getBrwPlat(), addtime = sdf.format(browser.getAddTime()), brwVersion = browser.getBrwVersion();
            String key = String.format(String.valueOf(RedisKeys.LOG_BROWSER_MAP), brwName, brwOs, addtime), filed = brwVersion;
            LogBrowserProto.LogBrowserMessage message = build(browser);
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

    public void save(List<LogBrowser> list) {
        Jedis jedis = pool.pull();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Pipeline pipeline = jedis.pipelined();
            list.forEach(browser -> {
                String brwName = browser.getBrwName(), brwOs = browser.getBrwPlat(), addtime = sdf.format(browser.getAddTime()), brwVersion = browser.getBrwVersion();
                String key = String.format(String.valueOf(RedisKeys.LOG_BROWSER_MAP), brwName, brwOs, addtime), filed = brwVersion;
                LogBrowserProto.LogBrowserMessage message = build(browser);
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

    public LogBrowser save(Long uid, String brwName, String brwOs, String brwVersion, String addtime) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.LOG_BROWSER_MAP), brwName, brwOs, addtime), filed = brwVersion;
            // 设置过期时间在隔天零点
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            jedis.expireAt(key, calendar.getTimeInMillis() / 1000);

            LogBrowser browser = logBrowserService.findOne(uid, brwName, brwOs, brwVersion, addtime);
            if (null != browser) {
                LogBrowserProto.LogBrowserMessage message = build(browser);
                jedis.hset(key.getBytes(), filed.getBytes(), message.toByteArray());
                return browser;
            } else {
                jedis.hset(key.getBytes(), filed.getBytes(), "0".getBytes());
            }

        } catch (Exception e) {
            logger.error("save name:" + brwName + ",os:" + brwOs + ",ver:" + brwVersion + ",time:" + addtime + " error:", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }

    public LogBrowser single(Long uid, String brwName, String brwOs, String brwVersion, String addtime) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.LOG_BROWSER_MAP), brwName, brwOs, addtime), filed = brwVersion;
            byte[] value = jedis.hget(key.getBytes(), filed.getBytes());
            if (null != value && value.length > 0) {
                if (value.length > 1) {
                    LogBrowserProto.LogBrowserMessage message = LogBrowserProto.LogBrowserMessage.parseFrom(value);
                    return build(message);
                }
            } else {
                return save(uid, brwName, brwOs, brwVersion, addtime);
            }
        } catch (Exception e) {
            logger.error("single name:" + brwName + ",os:" + brwOs + ",ver:" + brwVersion + ",time:" + addtime + " error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }
}
