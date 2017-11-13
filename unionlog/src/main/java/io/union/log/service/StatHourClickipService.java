package io.union.log.service;

import io.union.log.dao.StatHourClickipRepository;
import io.union.log.entity.StatHourClickip;
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
public class StatHourClickipService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StatHourClickipRepository statHourClickipRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public StatHourClickip findByUnique(String addtime, Long uid, Long siteid, Long planid) {
        try {
            return statHourClickipRepository.findAllByUnique(addtime, uid, siteid, planid);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public StatHourClickip saveOne(StatHourClickip hourIp) {
        try {
            return statHourClickipRepository.save(hourIp);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<StatHourClickip> save(List<StatHourClickip> list) {
        try {
            return statHourClickipRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return new ArrayList<>();
    }
}
