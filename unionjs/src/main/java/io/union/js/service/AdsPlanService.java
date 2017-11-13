package io.union.js.service;

import io.union.js.dao.AdsPlanRepository;
import io.union.js.entity.AdsPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2017/7/13.
 */
@Service
public class AdsPlanService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdsPlanRepository adsPlanRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsPlan findOne(Long id) {
        try {
            return adsPlanRepository.findOne(id);
        } catch (Exception e) {
            logger.error("findOne id=" + id + " error:", e);
        }
        return null;
    }
}
