package io.union.wap.service;

import io.union.wap.dao.AdsAdRepository;
import io.union.wap.entity.AdsAd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdsAdService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdsAdRepository adsAdRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsAd findOne(Long id) {
        try {
            return adsAdRepository.findOne(id);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsAd> findByStatus(Integer astatus) {
        try {
            return adsAdRepository.findAllByAstatusEquals(astatus);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AdsAd save(AdsAd ad) {
        try {
            return adsAdRepository.save(ad);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }
}
