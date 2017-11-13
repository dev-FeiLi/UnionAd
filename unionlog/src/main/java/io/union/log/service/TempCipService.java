package io.union.log.service;

import io.union.log.dao.TempCipRepository;
import io.union.log.entity.TempCip;
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
public class TempCipService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TempCipRepository tempCipRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public TempCip findOne(String addTime, String tempip, Long planid, Long uid) {
        try {
            return tempCipRepository.findAllByAddTimeAndTempipAndPlanidAndUid(addTime, tempip, planid, uid);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TempCip saveOne(TempCip tempCip) {
        try {
            return tempCipRepository.save(tempCip);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<TempCip> save(List<TempCip> list) {
        try {
            return tempCipRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return new ArrayList<>();
    }
}
