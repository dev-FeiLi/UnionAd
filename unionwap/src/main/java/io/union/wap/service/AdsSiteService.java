package io.union.wap.service;

import io.union.wap.dao.AdsSiteRepository;
import io.union.wap.entity.AdsSite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdsSiteService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdsSiteRepository adsSiteRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsSite findOne(Long id) {
        try {
            return adsSiteRepository.findOne(id);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsSite> findByUid(Long uid) {
        try {
            return adsSiteRepository.findAllByUidEquals(uid);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AdsSite saveOne(AdsSite site) {
        try {
            return adsSiteRepository.save(site);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }
}
