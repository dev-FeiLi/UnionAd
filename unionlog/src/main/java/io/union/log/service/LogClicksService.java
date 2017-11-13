package io.union.log.service;

import io.union.log.dao.LogClicksRepository;
import io.union.log.entity.LogClicks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/31.
 */
@Service
public class LogClicksService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LogClicksRepository logClicksRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public LogClicks saveOne(LogClicks clicks) {
        try {
            return logClicksRepository.save(clicks);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<LogClicks> save(List<LogClicks> list) {
        try {
            return logClicksRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return new ArrayList<>();
    }
}
