package io.union.log.service;

import io.union.log.dao.LogScreenRepository;
import io.union.log.entity.LogScreen;
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
public class LogScreenService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LogScreenRepository logScreenRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public LogScreen findOne(String cusScreen, Long uid, String addtime) {
        try {
            return logScreenRepository.findByScreenInfo(cusScreen, uid, addtime);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public LogScreen saveOne(LogScreen screen) {
        try {
            return logScreenRepository.save(screen);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<LogScreen> save(List<LogScreen> list) {
        try {
            return logScreenRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return new ArrayList<>();
    }
}
