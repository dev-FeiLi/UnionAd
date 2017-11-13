package io.union.admin.service;

import io.union.admin.common.redis.CacheAdsUser;
import io.union.admin.common.zoo.ZooUtils;
import io.union.admin.dao.AdsAdvpayRepository;
import io.union.admin.dao.AdsUserRepository;
import io.union.admin.dao.AdvpayRepositoryCustomImpl;
import io.union.admin.entity.AdsAdvpay;
import io.union.admin.entity.AdsUser;
import io.union.admin.entity.SysManage;
import net.sf.json.JSONObject;
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
public class AdsAdvpayService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    private AdsAdvpayRepository adsAdvpayRepository;

    @Autowired
    private AdvpayRepositoryCustomImpl advpayRepositoryCustom;

    @Autowired
    private AdsUserRepository adsUserRepository;

    @Autowired
    private CacheAdsUser cacheAdsUser;

    @Autowired
    private ZooUtils zooutils;

    @Value("${spring.zookeeper.lock.user}")
    String userPath;

    public AdsAdvpayService(@Autowired AdsAdvpayRepository adsAdvpayRepository) {
        this.adsAdvpayRepository = adsAdvpayRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsAdvpay find(Long id) {
        try {
            return adsAdvpayRepository.findOne(id);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<AdsAdvpay> findAll(int page, int size) {
        Page<AdsAdvpay> list = null;
        if (size == 0) size = 20;
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        list = adsAdvpayRepository.findAll(pageable);

        return list;
    }

    public AdsAdvpay save(AdsAdvpay adsAdvpay) {
        try {
            return adsAdvpayRepository.save(adsAdvpay);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public double getTotolCharge() {
        List<BigDecimal> listCharge = adsAdvpayRepository.getTotolCharge();
        if (listCharge != null && listCharge.size() > 0) {
            if (listCharge.size() > 1) {
                return listCharge.get(0).subtract(listCharge.get(1)).doubleValue();
            } else {
                return listCharge.get(0).doubleValue();
            }
        }
        return 0;
    }

    public List<AdsAdvpay> getAdspayListBySearch(HttpServletRequest request) {
        List<AdsAdvpay> list = new ArrayList<>();
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String uid = request.getParameter("searchUid");
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
            list = advpayRepositoryCustom.findAll(params);
        } catch (Exception e) {
            logger.error("getAdspayListBySearch", e.getMessage());
        }

        return list;
    }

    /**
     * 广告主充值页面 save 方法
     *
     * @param model
     * @param manager
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Map<String, Object> rechargeAdvpay(Object model, SysManage manager) {
        JSONObject params = JSONObject.fromObject(model);
        String uid = params.getString("uid"), paytype = params.getString("paytype"),
                tocard = params.getString("tocard"), money = params.getString("money"),
                payinfo = params.getString("payinfo");
        Map<String, Object> result = new HashMap<>();
        InterProcessMutex lock = null;
        try {
            AdsUser user;
            lock = zooutils.buildLock(userPath + uid);
            if (lock.acquire(zooutils.TIMEOUT, TimeUnit.SECONDS)) {
                user = cacheAdsUser.single(Long.valueOf(uid));
                if (user == null || "".equals(user.getUsername())) {
                    user = adsUserRepository.findOne(Long.valueOf(uid));
                }
                AdsAdvpay adsAdvpay = new AdsAdvpay();
                adsAdvpay.setUid(Long.valueOf(uid));
                adsAdvpay.setUsername(user.getUsername());
                adsAdvpay.setPaytype(Integer.valueOf(paytype));
                adsAdvpay.setTocard(tocard);
                adsAdvpay.setMoney(Double.valueOf(money));
                adsAdvpay.setPayinfo(payinfo);
                adsAdvpay.setManId(manager.getManId());
                adsAdvpay.setManAccount(manager.getManAccount());
                adsAdvpay.setAddTime(new Date());

                // 计算广告商xmoney余额
                BigDecimal remain = new BigDecimal(user.getXmoney());
                BigDecimal increments = new BigDecimal(Double.valueOf(money));
                if ("2".equals(paytype) && increments.compareTo(remain) > 0) {
                    result.put("error", "扣除金额大于广告主余额");
                    return result;
                }
                // paytype :1-增加 2-扣除
                if ("1".equals(paytype)) {
                    user.setXmoney(remain.add(increments).doubleValue());
                } else if ("2".equals(paytype)) {
                    user.setXmoney(remain.subtract(increments).doubleValue());
                }
                // 更新数据库
                adsAdvpayRepository.save(adsAdvpay);
                adsUserRepository.save(user);
                cacheAdsUser.save(user);
            }
        } catch (Exception e) {
            logger.error("rechargeAdvpay error: ", e);
            result.put("error", e.getMessage());
        } finally {
            zooutils.release(lock);
        }

        return result;
    }

}
