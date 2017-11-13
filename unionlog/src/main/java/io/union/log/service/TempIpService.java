package io.union.log.service;

import io.union.log.dao.TempIpRepository;
import io.union.log.entity.TempIp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/28.
 */
@Service
public class TempIpService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TempIpRepository tempIpRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public TempIp findOne(String addTime, String tempip, Long planid, Long uid) {
        try {
            return tempIpRepository.findAllByAddTimeAndTempipAndPlanidAndUid(addTime, tempip, planid, uid);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TempIp saveOne(TempIp tempIp) {
        try {
            return tempIpRepository.save(tempIp);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<TempIp> save(List<TempIp> list) {
        try {
            return tempIpRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return new ArrayList<>();
    }
}
