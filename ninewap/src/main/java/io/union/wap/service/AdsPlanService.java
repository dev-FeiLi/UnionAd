package io.union.wap.service;

import io.union.wap.dao.AdsPlanRepository;
import io.union.wap.entity.AdsPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsPlan> findByStatus(Integer pstatus) {
        try {
            return adsPlanRepository.findAllByPstatusEquals(pstatus);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }
}
