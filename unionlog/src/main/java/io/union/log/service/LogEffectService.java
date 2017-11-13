package io.union.log.service;

import io.union.log.dao.LogEffectRepository;
import io.union.log.entity.LogEffect;
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
public class LogEffectService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LogEffectRepository logEffectRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public LogEffect save(LogEffect effect) {
        try {
            return logEffectRepository.save(effect);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<LogEffect> save(List<LogEffect> list) {
        try {
            return logEffectRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return new ArrayList<>();
    }
}
