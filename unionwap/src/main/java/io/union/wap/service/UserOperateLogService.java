package io.union.wap.service;

import io.union.wap.dao.UserOperateLogRepository;
import io.union.wap.entity.UserOperateLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserOperateLogService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserOperateLogRepository userOperateLogRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserOperateLog saveOne(UserOperateLog log) {
        try {
            return userOperateLogRepository.save(log);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }
}
