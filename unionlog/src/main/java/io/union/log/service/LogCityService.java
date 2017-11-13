package io.union.log.service;

import io.union.log.dao.LogCityRepository;
import io.union.log.entity.LogCity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/27.
 */
@Service
public class LogCityService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LogCityRepository logCityRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public LogCity findOne(String cusProvince, String cusIsp, Long uid, String addtime) {
        try {
            return logCityRepository.findByCityInfo(cusProvince, cusIsp, uid, addtime);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public LogCity saveOne(LogCity city) {
        try {
            return logCityRepository.save(city);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<LogCity> save(List<LogCity> list) {
        try {
            return logCityRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return new ArrayList<>();
    }
}
