package io.union.log.service;

import io.union.log.dao.StatHourClickuvRepository;
import io.union.log.entity.StatHourClickuv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/29.
 */
@Service
public class StatHourClickuvService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StatHourClickuvRepository statHourClickuvRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public StatHourClickuv findByUnique(String addtime, Long uid, Long siteid, Long planid) {
        try {
            return statHourClickuvRepository.findAllByUnique(addtime, uid, siteid, planid);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public StatHourClickuv saveOne(StatHourClickuv hourIp) {
        try {
            return statHourClickuvRepository.save(hourIp);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<StatHourClickuv> save(List<StatHourClickuv> list) {
        try {
            return statHourClickuvRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return new ArrayList<>();
    }
}
