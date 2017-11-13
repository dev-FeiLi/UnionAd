package io.union.log.service;

import io.union.log.dao.StatHourPvRepository;
import io.union.log.entity.StatHourPv;
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
public class StatHourPvService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StatHourPvRepository statHourPvRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public StatHourPv findByUnique(String addtime, Long uid, Long siteid, Long planid) {
        try {
            return statHourPvRepository.findAllByUnique(addtime, uid, siteid, planid);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public StatHourPv saveOne(StatHourPv hourPv) {
        try {
            return statHourPvRepository.save(hourPv);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<StatHourPv> save(List<StatHourPv> list) {
        try {
            return statHourPvRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return new ArrayList<>();
    }
}
