package io.union.log.service;

import io.union.log.dao.StatHourIpRepository;
import io.union.log.entity.StatHourIp;
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
public class StatHourIpService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StatHourIpRepository statHourIpRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public StatHourIp findByUnique(String addtime, Long uid, Long siteid, Long planid) {
        try {
            return statHourIpRepository.findAllByUnique(addtime, uid, siteid, planid);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public StatHourIp saveOne(StatHourIp hourIp) {
        try {
            return statHourIpRepository.save(hourIp);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<StatHourIp> save(List<StatHourIp> list) {
        try {
            return statHourIpRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return new ArrayList<>();
    }
}
