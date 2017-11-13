package io.union.js.service;

import io.union.js.dao.AdsAdRepository;
import io.union.js.entity.AdsAd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/7/12.
 */
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
            logger.error("findOne id=" + id + " error:", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsAd> findByTypeAndWidthAndHeight(Integer type, Integer width, Integer height) {
        List<AdsAd> list = null;
        try {
            list = adsAdRepository.findAllByAdtypeAndWidthAndHeight(type, width, height);
        } catch (Exception e) {
            logger.error("findAllByAdtypeAndWidthAndHeight error: ", e);
        }
        return list;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsAd> findAll() {
        List<AdsAd> list = null;
        try {
            list = adsAdRepository.findAll();
        } catch (Exception e) {
            logger.error("findAll error: ", e);
        }
        return list;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsAd> findAll(Long planid) {
        List<AdsAd> list = null;
        try {
            list = adsAdRepository.findAllByPlanidEquals(planid);
        } catch (Exception e) {
            logger.error("findAll planid=" + planid + " error: ", e);
        }
        return list;
    }
}
