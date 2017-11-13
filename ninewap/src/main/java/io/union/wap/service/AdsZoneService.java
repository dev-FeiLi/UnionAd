package io.union.wap.service;

import io.union.wap.dao.AdsZoneRepository;
import io.union.wap.entity.AdsZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdsZoneService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdsZoneRepository adsZoneRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsZone findOne(Long id) {
        try {
            return adsZoneRepository.findOne(id);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsZone> findByUid(Long uid) {
        try {
            return adsZoneRepository.findAllByUidEquals(uid);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AdsZone saveOne(AdsZone zone) {
        try {
            return adsZoneRepository.save(zone);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }
}
