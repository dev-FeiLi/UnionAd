package io.union.wap.service;

import io.union.wap.dao.AdsClassRepository;
import io.union.wap.entity.AdsClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdsClassService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdsClassRepository adsClassRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsClass findOne(Long id) {
        try {
            return adsClassRepository.findOne(id);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsClass> findAll() {
        try {
            return adsClassRepository.findAll();
        } catch (Exception e) {
            logger.error("find all error: ", e);
        }
        return null;
    }
}
