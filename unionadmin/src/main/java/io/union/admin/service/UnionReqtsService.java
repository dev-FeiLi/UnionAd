package io.union.admin.service;

import io.union.admin.dao.UnionReqtsRepository;
import io.union.admin.entity.UnionReqts;
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
 * Created by Administrator on 2017/7/26.
 */
@Service
public class UnionReqtsService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UnionReqtsRepository unionReqtsRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public UnionReqts findbyaddtime(String addtime) {
        try {
            LocalDate localDate = LocalDate.parse(addtime);
            ZoneId zone = ZoneId.systemDefault();
            Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
            Date today = Date.from(instant);
            UnionReqts reqts = unionReqtsRepository.findTotalReqts(today);
            if (null != reqts && null != reqts.getReqeusts()) {
                return reqts;
            }
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }
}
