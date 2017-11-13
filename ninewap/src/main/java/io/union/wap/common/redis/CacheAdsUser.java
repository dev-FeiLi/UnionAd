package io.union.wap.common.redis;

import io.union.wap.common.proto.AdsUserProto;
import io.union.wap.entity.AdsUser;
import io.union.wap.service.AdsUserService;
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
 * Created by Administrator on 2017/8/2.
 */
@Component
public class CacheAdsUser {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisPool pool;
    @Autowired
    private AdsUserService adsUserService;

    private AdsUser build(AdsUserProto.AdsUserMessage message) {
        AdsUser user = new AdsUser();
        user.setUid(message.getUid());
        user.setUsername(message.getUsername());
        user.setPassword(message.getPassword());
        user.setUtype(message.getUtype());
        user.setRegtime(new Date(message.getRegtime()));
        user.setRegip(message.getRegip());
        user.setMoney(message.getMoney());
        user.setDaymoney(message.getDaymoney());
        user.setWeekmoney(message.getWeekmoney());
        user.setMonthmoney(message.getMonthmoney());
        user.setXmoney(message.getXmoney());
        user.setServiceid(message.getServiceid());
        user.setLimiturl(message.getLimiturl());
        user.setRuntype(message.getRuntype());
        user.setUstatus(message.getUstatus());
        user.setPaytype(message.getPaytype());
        user.setChartype(message.getChartype());
        user.setLimitdiv(message.getLimitdiv());
        user.setLimitdivheight(message.getLimitdivheight());
        user.setLimitpop(message.getLimitpop());
        if (null != message.getQq()) {
            user.setQq(message.getQq());
        }
        if (null != message.getMobile()) {
            user.setMobile(message.getMobile());
        }
        if (null != message.getIdcard()) {
            user.setIdcard(message.getIdcard());
        }
        if (null != message.getRealname()) {
            user.setRealname(message.getRealname());
        }
        if (null != message.getBankaccount()) {
            user.setBankaccount(message.getBankaccount());
        }
        if (null != message.getBankbranch()) {
            user.setBankbranch(message.getBankbranch());
        }
        if (null != message.getBankname()) {
            user.setBankname(message.getBankname());
        }
        if (null != message.getBanknum()) {
            user.setBanknum(message.getBanknum());
        }
        if (message.getLogintime() > 0) {
            user.setLogintime(new Date(message.getLogintime()));
        }
        if (null != message.getLoginip()) {
            user.setLoginip(message.getLoginip());
        }
        if (null != message.getDeduction()) {
            user.setDeduction(message.getDeduction());
        }
        if (null != message.getLimitplan()) {
            user.setLimitplan(message.getLimitplan());
        }
        if (null != message.getMemo()) {
            user.setMemo(message.getMemo());
        }
        return user;
    }

    private AdsUserProto.AdsUserMessage build(AdsUser user) {
        AdsUserProto.AdsUserMessage.Builder builder = AdsUserProto.AdsUserMessage.newBuilder();
        builder.setUid(user.getUid());
        builder.setUsername(user.getUsername());
        builder.setPassword(user.getPassword());
        builder.setUtype(user.getUtype());
        builder.setRegtime(user.getRegtime().getTime());
        builder.setRegip(user.getRegip());
        builder.setMoney(user.getMoney());
        builder.setDaymoney(user.getDaymoney());
        builder.setWeekmoney(user.getWeekmoney());
        builder.setMonthmoney(user.getMonthmoney());
        builder.setXmoney(user.getXmoney());
        builder.setServiceid(user.getServiceid());
        builder.setLimiturl(user.getLimiturl());
        builder.setRuntype(user.getRuntype());
        builder.setUstatus(user.getUstatus());
        builder.setPaytype(user.getPaytype());
        builder.setChartype(user.getChartype());
        builder.setLimitdiv(user.getLimitdiv());
        builder.setLimitdivheight(user.getLimitdivheight());
        builder.setLimitpop(user.getLimitpop());
        if (null != user.getQq()) {
            builder.setQq(user.getQq());
        }
        if (null != user.getMobile()) {
            builder.setMobile(user.getMobile());
        }
        if (null != user.getIdcard()) {
            builder.setIdcard(user.getIdcard());
        }
        if (null != user.getRealname()) {
            builder.setRealname(user.getRealname());
        }
        if (null != user.getBankaccount()) {
            builder.setBankaccount(user.getBankaccount());
        }
        if (null != user.getBankbranch()) {
            builder.setBankbranch(user.getBankbranch());
        }
        if (null != user.getBankname()) {
            builder.setBankname(user.getBankname());
        }
        if (null != user.getBanknum()) {
            builder.setBanknum(user.getBanknum());
        }
        if (null != user.getLogintime()) {
            builder.setLogintime(user.getLogintime().getTime());
        }
        if (null != user.getLoginip()) {
            builder.setLoginip(user.getLoginip());
        }
        if (null != user.getDeduction()) {
            builder.setDeduction(user.getDeduction());
        }
        if (null != user.getLimitplan()) {
            builder.setLimitplan(user.getLimitplan());
        }
        if (null != user.getMemo()) {
            builder.setMemo(user.getMemo());
        }
        return builder.build();
    }

    public AdsUser save(AdsUser user) {
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.ADS_USER_MAP), field = String.valueOf(user.getUid());
            AdsUserProto.AdsUserMessage message = build(user);
            jedis.hset(key.getBytes(), field.getBytes(), message.toByteArray());

            // 用户名对应的ID，供登录使用
            String ukey = String.valueOf(RedisKeys.ADS_USER_NAME_ID_MAP);
            jedis.hset(ukey, user.getUsername(), field);

            // 如果是客服或者商务的话
            if (user.getUtype() == 3 || user.getUtype() == 4) {
                String kkey = String.valueOf(RedisKeys.ADS_KEFU_ID_MAP);
                jedis.hset(kkey.getBytes(), field.getBytes(), message.toByteArray());
            }
            return user;
        } catch (Exception e) {
            logger.error("save error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }

    public AdsUser save(Long id) {
        Jedis jedis = pool.pull();
        try {
            AdsUser user = adsUserService.findOne(id);
            String key = String.valueOf(RedisKeys.ADS_USER_MAP), field = String.valueOf(id);
            if (null != user) {
                AdsUserProto.AdsUserMessage message = build(user);
                jedis.hset(key.getBytes(), field.getBytes(), message.toByteArray());
                return user;
            } else {
                // 这里防止被SQL渗透，被request爆了数据库
                // 如果查出来是空，则在redis存入空字符串，表明id对应的对象在数据库中不存在
                // 下次继续请求的时候，则可以优先判断而不用查询数据库，直接返回错误信息
                jedis.hset(key.getBytes(), field.getBytes(), "0".getBytes());
            }
        } catch (Exception e) {
            logger.error("save id=" + id + " error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }

    public AdsUser single(Long id) {
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.ADS_USER_MAP), field = String.valueOf(id);
            byte[] value = jedis.hget(key.getBytes(), field.getBytes());
            if (null != value && value.length > 0) {
                if (value.length > 1) {
                    AdsUserProto.AdsUserMessage message = AdsUserProto.AdsUserMessage.parseFrom(value);
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

    public AdsUser single(String username) {
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.ADS_USER_NAME_ID_MAP), filed = username;
            String value = jedis.hget(key, filed);
            if (null != value && !"".equals(value)) {
                Long uid = Long.valueOf(value);
                return single(uid);
            } else {
                AdsUser user = adsUserService.findByUsername(username);
                if (null != user) {
                    return save(user);
                }
            }
        } catch (Exception e) {
            logger.error("single " + username + " error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }

    public List<AdsUser> service() {
        Jedis jedis = pool.pull();
        try {
            String key = String.valueOf(RedisKeys.ADS_KEFU_ID_MAP);
            Map<byte[], byte[]> map = jedis.hgetAll(key.getBytes());
            List<AdsUser> list = new ArrayList<>();
            if (null != map && map.size() > 0) {
                map.forEach((k, v) -> {
                    try {
                        String uid = new String(k);
                        Long id = Long.valueOf(uid);
                        AdsUser item = single(id);
                        if (null != item) {
                            list.add(item);
                        }
                    } catch (Exception e) {
                        logger.error("service map key not number");
                    }

                });
            } else {
                List<AdsUser> list3 = adsUserService.findAllByUtype(3), list4 = adsUserService.findAllByUtype(4);
                if (null != list3 && list3.size() > 0) {
                    list.addAll(list3);
                }
                if (null != list4 && list4.size() > 0) {
                    list.addAll(list4);
                }
                for (AdsUser user : list) {
                    Long uid = user.getUid();
                    jedis.hset(key.getBytes(), String.valueOf(uid).getBytes(), build(user).toByteArray());
                }
            }
            return list;
        } catch (Exception e) {
            logger.error("service error: ", e);
        } finally {
            pool.push(jedis);
        }
        return null;
    }
}
