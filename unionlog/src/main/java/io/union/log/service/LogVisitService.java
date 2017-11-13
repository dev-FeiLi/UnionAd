package io.union.log.service;

import io.union.log.dao.LogVisitRepository;
import io.union.log.entity.LogVisit;
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
public class LogVisitService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LogVisitRepository logVisitRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public LogVisit saveOne(LogVisit visit) {
        try {
            return logVisitRepository.save(visit);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<LogVisit> save(List<LogVisit> list) {
        try {
            return logVisitRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return new ArrayList<>();
    }
}
