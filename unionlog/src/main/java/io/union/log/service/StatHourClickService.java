package io.union.log.service;

import io.union.log.dao.StatHourClickRepository;
import io.union.log.entity.StatHourClick;
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
public class StatHourClickService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StatHourClickRepository statHourClickRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public StatHourClick findByUnique(String addtime, Long uid, Long siteid, Long planid) {
        try {
            return statHourClickRepository.findAllByUnique(addtime, uid, siteid, planid);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public StatHourClick saveOne(StatHourClick hourIp) {
        try {
            return statHourClickRepository.save(hourIp);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<StatHourClick> save(List<StatHourClick> list) {
        try {
            return statHourClickRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return new ArrayList<>();
    }
}
