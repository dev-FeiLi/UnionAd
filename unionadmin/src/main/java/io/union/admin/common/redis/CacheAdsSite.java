package io.union.admin.common.redis;

import io.union.admin.common.proto.AdsSiteProto;
import io.union.admin.common.utils.NumberKey;
import io.union.admin.entity.AdsSite;
import io.union.admin.service.AdsSiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/17.
 */
@Component
public class CacheAdsSite {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;
    @Autowired
    private AdsSiteService adsSiteService;

    private AdsSite build(AdsSiteProto.AdsSiteMessage message) {
        AdsSite site = new AdsSite();
        site.setSiteid(message.getSiteid());
        site.setSitename(message.getSitename());
        site.setSiteurl(message.getSiteurl());
        site.setSitetype(message.getSitetype());
        site.setSstatus(message.getSstatus());
        site.setAddTime(new Date(message.getAddTime()));
        if (null != message.getBeian()) {
            site.setBeian(message.getBeian());
        }
        if (message.getDayip() > 0) {
            site.setDayip(message.getDayip());
        }
        if (message.getDaypv() > 0) {
            site.setDaypv(message.getDaypv());
        }
        return site;
    }

    private AdsSiteProto.AdsSiteMessage build(AdsSite site) {
        AdsSiteProto.AdsSiteMessage.Builder builder = AdsSiteProto.AdsSiteMessage.newBuilder();
        builder.setSiteid(site.getSiteid());
        builder.setSitename(site.getSitename());
        builder.setSiteurl(site.getSiteurl());
        builder.setSitetype(site.getSitetype());
        builder.setAddTime(site.getAddTime().getTime());
        builder.setSstatus(site.getSstatus());
        if (null != site.getBeian()) {
            builder.setBeian(site.getBeian());
        }
        if (null != site.getDayip() && site.getDayip() > 0) {
            builder.setDayip(site.getDayip());
        }
        if (null != site.getDaypv() && site.getDaypv() > 0) {
            builder.setDaypv(site.getDaypv());
        }
        return builder.build();
    }

    public AdsSite save(AdsSite adsSite) {
        Jedis jedis = pool.pull();
        try {
            // 保存到adssitemap 所有的网站
            String key = String.valueOf(RedisKeys.ADS_SITE_MAP), field = String.valueOf(adsSite.getSiteid());
            AdsSiteProto.AdsSiteMessage message = build(adsSite);
            jedis.hset(key.getBytes(), field.getBytes(), message.toByteArray());

            // 保存到站长名下的网站
            int status = adsSite.getSstatus();
            String uidkey = String.format(String.valueOf(RedisKeys.ADS_UID_SITE_MAP), adsSite.getUid()), url = adsSite.getSiteurl();
            if (status == 0) {
                jedis.hset(uidkey.getBytes(), url.getBytes(), message.toByteArray());
            } else {
                jedis.hdel(uidkey, url);
            }
            return adsSite;
        } catch (Exception e) {
            logger.error("save error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }

    public AdsSite save(Long siteid) {
        Jedis jedis = pool.pull();
        try {
            AdsSite site = adsSiteService.findOne(siteid);
            String key = String.valueOf(RedisKeys.ADS_SITE_MAP), field = String.valueOf(siteid);
            if (null != site) {
                AdsSiteProto.AdsSiteMessage message = build(site);
                jedis.hset(key.getBytes(), field.getBytes(), message.toByteArray());

                int status = site.getSstatus();
                if (status == 0) {
                    String uidkey = String.format(String.valueOf(RedisKeys.ADS_UID_SITE_MAP), site.getUid()), url = site.getSiteurl();
                    jedis.hset(uidkey.getBytes(), url.getBytes(), message.toByteArray());
                }
                return site;
            } else {
                // 这里防止被SQL渗透，被request爆了数据库
                // 如果查出来是空，则在redis存入空字符串，表明id对应的对象在数据库中不存在
                // 下次继续请求的时候，则可以优先判断而不用查询数据库，直接返回错误信息
                jedis.hset(key.getBytes(), field.getBytes(), "0".getBytes());
            }
        } catch (Exception e) {
            logger.error("save id=" + siteid + " error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }

    public List<AdsSite> saveByUid(Long uid) {
        Jedis jedis = pool.pull();
        try {
            List<AdsSite> list = adsSiteService.findByUid(uid);
            String key = String.valueOf(RedisKeys.ADS_SITE_MAP), uidkey = String.format(String.valueOf(RedisKeys.ADS_UID_SITE_MAP), uid);
            if (null != list && list.size() > 0) {
                for (AdsSite site : list) {
                    String field = String.valueOf(site.getSiteid()), url = site.getSiteurl();
                    AdsSiteProto.AdsSiteMessage message = build(site);
                    jedis.hset(key.getBytes(), field.getBytes(), message.toByteArray());
                    if (site.getSstatus() == 0) {
                        jedis.hset(uidkey.getBytes(), url.getBytes(), message.toByteArray());
                    }
                }
                return list;
            } else {
                // 这里防止被SQL渗透，被request爆了数据库
                // 如果查出来是空，则在redis存入空字符串，表明id对应的对象在数据库中不存在
                // 下次继续请求的时候，则可以优先判断而不用查询数据库，直接返回错误信息
                jedis.hset(key, String.valueOf(NumberKey.ZERO.getValue()), "0");
                jedis.hset(uidkey, String.valueOf(NumberKey.ZERO.getValue()), String.valueOf(NumberKey.ZERO.getValue()));
            }
        } catch (Exception e) {
            logger.error("save by uid=" + uid + " error: ", e);
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
     * @param id
     * @return
     */
    public AdsSite single(Long id) {
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.ADS_SITE_MAP), field = String.valueOf(id);
            byte[] value = jedis.hget(key.getBytes(), field.getBytes());
            if (null != value && value.length > 0) {
                if (value.length > 1) {
                    AdsSiteProto.AdsSiteMessage message = AdsSiteProto.AdsSiteMessage.parseFrom(value);
                    return build(message);
                }
            } else {
                // 在redis中真正不存在该id对应的对象，才会去查询数据库
                return save(id);
            }
        } catch (Exception e) {
            logger.error("single id=" + id + " error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }

    public List<AdsSite> list(Long uid) {
        List<AdsSite> list = new ArrayList<>();
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.ADS_UID_SITE_MAP), uid);
            Map<byte[], byte[]> value = jedis.hgetAll(key.getBytes());
            if (null != value && value.size() > 0) {
                for (Map.Entry<byte[], byte[]> entry : value.entrySet()) {
                    AdsSiteProto.AdsSiteMessage message = AdsSiteProto.AdsSiteMessage.parseFrom(entry.getValue());
                    list.add(build(message));
                }
            } else {
                // 在redis中真正不存在该id对应的对象，才会去查询数据库
                return saveByUid(uid);
            }
        } catch (Exception e) {
            logger.error("list uid=" + uid + " error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }

    public AdsSite single(Long uid, String url) {
        Jedis jedis = pool.pull();
        try {
            String key = String.format(String.valueOf(RedisKeys.ADS_UID_SITE_MAP), uid), field = url;
            long length = jedis.hlen(key);
            if (length == 0) {
                List<AdsSite> list = saveByUid(uid);
                if (null != list && list.size() > 0) {
                    for (AdsSite site : list) {
                        if (url.equals(site.getSiteurl())) {
                            return site;
                        }
                    }
                }
            } else {
                AdsSite site = null;
                Map<byte[], byte[]> map = jedis.hgetAll(key.getBytes());
                for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
                    try {
                        String host = new String(entry.getKey());
                        if (host.contains(url)) {
                            AdsSiteProto.AdsSiteMessage message = AdsSiteProto.AdsSiteMessage.parseFrom(entry.getValue());
                            site = build(message);
                            break;
                        }
                    } catch (Exception e) {
                        // 在高并发情况下打印错误日志比较糟糕吧
                    }
                }
                return site;
            }
        } catch (Exception e) {
            logger.error("single uid=" + uid + ", url=" + url + " error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }
}
