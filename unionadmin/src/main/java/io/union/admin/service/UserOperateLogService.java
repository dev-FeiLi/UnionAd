package io.union.admin.service;

import io.union.admin.dao.UserOperateLogRepository;
import io.union.admin.entity.UserOperateLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class UserOperateLogService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserOperateLogRepository userOperateLogRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<UserOperateLog> findAllTypeCount(String addTime) {
        try {
            LocalDate localDate = LocalDate.parse(addTime);
            ZoneId zone = ZoneId.systemDefault();
            Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
            Date today = Date.from(instant);
            return userOperateLogRepository.findByAddTime(today);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<UserOperateLog> findAll(long uid, int otype, Date time) {
        try {
            List<UserOperateLog> list = userOperateLogRepository.findAllByUidAndOtypeAndAddTime(uid, otype, time);
            return list;
        } catch (Exception e) {
            logger.error("findOne error:", e);
        }
        return null;
    }

    public void saveList(List<UserOperateLog> list) {
        try {
            if (list != null && list.size() > 0) userOperateLogRepository.save(list);
        } catch (Exception e) {
            logger.error("saveList error:", e);
        }
    }
}
