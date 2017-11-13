package io.union.admin.service;

import io.union.admin.common.redis.CacheAdsUser;
import io.union.admin.common.zoo.ZooUtils;
import io.union.admin.dao.AdsAffpayRepository;
import io.union.admin.dao.AdsUserRepository;
import io.union.admin.dao.AffpayRepositoryCustom;
import io.union.admin.entity.AdsAffpay;
import io.union.admin.entity.AdsUser;
import io.union.admin.entity.SysManage;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AdsAffpayService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    private AdsAffpayRepository adsAffpayRepository;

    @Autowired
    private AffpayRepositoryCustom affpayRepositoryCustom;

    @Autowired
    private AdsUserRepository adsUserRepository;

    @Autowired
    private CacheAdsUser cacheAdsUser;


    @Autowired
    private ZooUtils zooutils;

    @Value("${spring.zookeeper.lock.user}")
    String userPath;

    public AdsAffpayService(@Autowired AdsAffpayRepository adsAffpayRepository) {
        this.adsAffpayRepository = adsAffpayRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsAffpay find(Long id) {
        try {
            return adsAffpayRepository.findOne(id);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<AdsAffpay> findAll(int page, int size) {
        Page<AdsAffpay> list;
        if (size == 0) size = 20;
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        list = adsAffpayRepository.findAll(pageable);
        return list;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AdsAffpay save(AdsAffpay adsAffpay) {
        try {
            return adsAffpayRepository.save(adsAffpay);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    public List<AdsAffpay> getTotalMoney() {
        List<AdsAffpay> list = null;
        try {
            list = adsAffpayRepository.findToltalMoney();
            return list;
        } catch (Exception e) {
            logger.error("ads_affpay item is 0", e.getMessage());
        }
        return null;
    }


    public List<AdsAffpay> getAffpayListBySearch(HttpServletRequest request) {
        List<AdsAffpay> list = new ArrayList<>();
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String uid = request.getParameter("uid");
        String pstatus = request.getParameter("pstatus");
        Map<String, Object> params = new HashMap<>();

        try {
            if (StringUtils.hasText(startDate)) {
                params.put("startDate", new SimpleDateFormat("yyyy-MM-dd").parse(startDate));
            }
            if (StringUtils.hasText(endDate)) {
                params.put("endDate", new SimpleDateFormat("yyyy-MM-dd").parse(endDate));
            }
            if (StringUtils.hasText(uid)) {
                params.put("uid", uid);
            }
            if (!"all".equals(pstatus)) {
                params.put("pstatus", pstatus);
            }
            list = affpayRepositoryCustom.findAll(params);
        } catch (Exception e) {
            logger.error("getAffpayListBySearch", e.getMessage());
        }

        return list;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Map<String, Object> affPayMoney(Object modal, SysManage manager) {
        Map<String, Object> result = new HashMap<>();
        JSONObject params = JSONObject.fromObject(modal);
        if (!params.containsKey("uid") && !"".equals(params.getString("uid"))) {
            result.put("error", "请输入网站主ID");
            return result;
        }
        if (!params.containsKey("pstatus")) {
            result.put("error", "请选择状态");
            return result;
        }
        if (!params.containsKey("paytype")) {
            result.put("error", "请选择结算周期");
            return result;
        }
        String uid = params.getString("uid"), username = params.getString("username"),
                xmoney = params.getString("xmoney"), money = params.getString("money"),
                payinfo = params.getString("payinfo"), pstatus = params.getString("pstatus"),
                paytype = params.getString("paytype");

        InterProcessMutex lock = null;
        try {
            AdsUser user;
            lock = zooutils.buildLock(userPath + uid);
            if (lock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                user = cacheAdsUser.single(Long.valueOf(uid));
                if (user == null) {
                    user = adsUserRepository.findOne(Long.valueOf(uid));
                }

                AdsAffpay affpay = new AdsAffpay();
                affpay.setUid(Long.valueOf(uid));
                affpay.setUsername(username);
                affpay.setMoney(Double.valueOf(xmoney));
                affpay.setRealmoney(Double.valueOf(money));
                affpay.setPayinfo(payinfo);
                affpay.setManId(manager.getManId());
                affpay.setManAccount(manager.getManAccount());
                affpay.setPaytime(new Date());
                affpay.setAddTime(new Date());
                affpay.setPstatus(Integer.valueOf(pstatus));
                affpay.setPaytype(Integer.valueOf(paytype));

                // 计算网站主xmoney（未支付）
                if (1 == affpay.getPstatus()) {
                    BigDecimal unpaid = new BigDecimal(user.getXmoney());
                    BigDecimal realmoney = new BigDecimal(Double.valueOf(money));
                    if (realmoney.compareTo(unpaid) > 0) {
                        result.put("error", "支付金额大于网站主的未结算金额");
                        return result;
                    }
                    user.setXmoney(unpaid.subtract(realmoney).doubleValue());
                }
                // 更新数据库
                adsAffpayRepository.save(affpay);
                adsUserRepository.save(user);
                cacheAdsUser.save(user);
            }
        } catch (Exception e) {
            logger.error("affPayMoney error: ", e);
            result.put("error", e.getMessage());
        } finally {
            zooutils.release(lock);
        }

        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Map<String, Object> affPayMoneyEdit(Object modal, SysManage manager) {
        Map<String, Object> result = new HashMap<>();
        JSONObject params = JSONObject.fromObject(modal);
        String id = params.getString("affpayid"), uid = params.getString("uidEdit"),
                realmoney = params.getString("moneyEdit"), payinfo = params.getString("payinfoEdit");

        AdsAffpay affpay = find(Long.valueOf(id));
        // “未支付” 状态以外的不让编辑提交
        if (affpay.getPstatus() != 0) {
            result.put("error", "只能编辑未支付状态的记录");
            return result;
        }

        InterProcessMutex lock = null;
        try {
            AdsUser user;
            lock = zooutils.buildLock(userPath + uid);
            if (lock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                user = cacheAdsUser.single(Long.valueOf(uid));
                if (user == null) {
                    user = adsUserRepository.findOne(Long.valueOf(uid));
                }

                affpay.setRealmoney(Double.valueOf(realmoney));
                if (StringUtils.hasText(payinfo)) {
                    affpay.setPayinfo(payinfo);
                }

                if (1 == affpay.getPstatus()) {
                    // 计算网站主xmoney（未支付）
                    BigDecimal unpaid = new BigDecimal(user.getXmoney());
                    BigDecimal payMoney = new BigDecimal(Double.valueOf(realmoney));
                    if (payMoney.compareTo(unpaid) > 0) {
                        result.put("error", "支付金额大于网站主未结算金额");
                        return result;
                    }
                    user.setXmoney(unpaid.subtract(payMoney).doubleValue());
                    adsUserRepository.save(user);
                    cacheAdsUser.save(user);
                }
                // 更新数据库
                adsAffpayRepository.save(affpay);
            }
        } catch (Exception e) {
            logger.error("affPayMoneyEdit error: ", e);
            result.put("error", e.getMessage());
        } finally {
            zooutils.release(lock);
        }

        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Map<String, Object> batchUpdate(JSONObject obj, SysManage manager) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (!obj.containsKey("affpayIds")) {
                map.put("errorMsg", "请选择您需要处理的记录");
                return map;
            }
            if (!obj.containsKey("pstatus")) {
                map.put("errorMsg", "请选择批量操作类型");
                return map;
            }
            JSONArray affpayIds = obj.getJSONArray("affpayIds");
            int pstatusTotal = obj.getInt("pstatus");
            List<AdsAffpay> entityList = JSONArray.toList(affpayIds, new AdsAffpay(), new JsonConfig());
            entityList.forEach(item -> {
                item.setManId(manager.getManId());
                item.setManAccount(manager.getManAccount());
            });

            InterProcessMutex lock = null;
            if (entityList != null && entityList.size() > 0) {
                for (AdsAffpay payinfo : entityList) {
                    // “未支付”之外的状态， 则不处理，跳过
                    if (payinfo.getPstatus() != 0) {
                        continue;
                    }
                    try {
                        long uid = payinfo.getUid();
                        AdsUser user;
                        lock = zooutils.buildLock(userPath + uid);
                        if (lock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                            user = cacheAdsUser.single(Long.valueOf(uid));
                            // 只计算“未支付”状态的记录
                            if (payinfo.getPstatus() == 0 && pstatusTotal == 1) {
                                // 计算网站主xmoney（未支付）
                                BigDecimal unpaid = new BigDecimal(user.getXmoney());
                                BigDecimal payMoney = new BigDecimal(payinfo.getRealmoney());
                                // 扣掉网站主的未结算余额
                                if (payMoney.compareTo(unpaid) > 0) {
                                    user.setXmoney(0.0d);
                                } else {
                                    user.setXmoney(unpaid.subtract(payMoney).doubleValue());
                                }
                                payinfo.setPstatus(1);
                                payinfo.setPaytime(new Date());
                            }
                            adsUserRepository.save(user);
                            cacheAdsUser.save(user);
                        }
                    } catch (Exception e) {
                        logger.error("batchUpdate error: ", e);
                    } finally {
                        zooutils.release(lock);
                    }
                }
            }

            adsAffpayRepository.save(entityList);
        } catch (Exception e) {
            logger.error("batchUpdate error: ", e);
            map.put("errorMsg", e.getMessage());
        }

        return map;
    }

}
