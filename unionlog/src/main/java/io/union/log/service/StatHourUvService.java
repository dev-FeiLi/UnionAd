package io.union.log.service;

import io.union.log.dao.StatHourUvRepository;
import io.union.log.entity.StatHourUv;
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
public class StatHourUvService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StatHourUvRepository statHourUvRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public StatHourUv findByUnique(String addtime, Long uid, Long siteid, Long planid) {
        try {
            return statHourUvRepository.findAllByUnique(addtime, uid, siteid, planid);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public StatHourUv saveOne(StatHourUv hourPv) {
        try {
            return statHourUvRepository.save(hourPv);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<StatHourUv> save(List<StatHourUv> list) {
        try {
            return statHourUvRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return new ArrayList<>();
    }
}
