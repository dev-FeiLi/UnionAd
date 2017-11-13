package io.union.log.service;

import io.union.log.dao.LogOsRepository;
import io.union.log.entity.LogOs;
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
public class LogOsService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LogOsRepository logOsRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public LogOs findOne(String cusOs, Long uid, String addtime) {
        try {
            return logOsRepository.findByOsInfo(cusOs, uid, addtime);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public LogOs saveOne(LogOs os) {
        try {
            return logOsRepository.save(os);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<LogOs> save(List<LogOs> list) {
        try {
            return logOsRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return new ArrayList<>();
    }
}
