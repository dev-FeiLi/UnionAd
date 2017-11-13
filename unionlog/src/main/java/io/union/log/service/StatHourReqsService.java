package io.union.log.service;

import io.union.log.dao.StatHourReqsRepository;
import io.union.log.entity.StatHourReqs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
@Service
public class StatHourReqsService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StatHourReqsRepository statHourReqsRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public StatHourReqs findOne(Long zoneid, String addTime) {
        try {
            return statHourReqsRepository.findByZoneidAndAddTime(zoneid, addTime);
        } catch (Exception e) {
            logger.error("findone error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public StatHourReqs saveOne(StatHourReqs reqs) {
        try {
            return statHourReqsRepository.save(reqs);
        } catch (Exception e) {
            logger.error("saveOne error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<StatHourReqs> save(List<StatHourReqs> list) {
        try {
            return statHourReqsRepository.save(list);
        } catch (Exception e) {
            logger.error("saveOne error: ", e);
        }
        return new ArrayList<>();
    }
}
