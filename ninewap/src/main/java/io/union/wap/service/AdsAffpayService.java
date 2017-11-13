package io.union.wap.service;


import io.union.wap.dao.AdsAffpayRepository;
import io.union.wap.entity.AdsAffpay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdsAffpayService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdsAffpayRepository adsAffpayRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsAffpay> findByUid(Long uid) {
        try {
            return adsAffpayRepository.findAllByUidEquals(uid);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }
}
