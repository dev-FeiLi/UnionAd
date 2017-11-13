package io.union.admin.service;

import io.union.admin.dao.StatHourPvRepository;
import io.union.admin.entity.StatHourPv;
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

/**
 * Created by Administrator on 2017/7/29.
 */
@Service
public class StatHourPvService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StatHourPvRepository statHourPvRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public StatHourPv findbytime(String addtime) {
        try {
            LocalDate localDate = LocalDate.parse(addtime);
            ZoneId zone = ZoneId.systemDefault();
            Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
            Date today = Date.from(instant);
            StatHourPv hourPv = statHourPvRepository.findTotalByAddTime(today);
            if (null != hourPv && null != hourPv.getAddTime()) {
                return hourPv;
            }
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

}
