package io.union.log.service;

import io.union.log.dao.AdsSiteRepository;
import io.union.log.entity.AdsSite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/7/17.
 */
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
            logger.error("findOne id=" + id + " error:", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsSite> findByUid(Long uid) {
        try {
            return adsSiteRepository.findAllByUidEquals(uid);
        } catch (Exception e) {
            logger.error("find list uid=" + uid + " error: ", e);
        }
        return null;
    }
}
