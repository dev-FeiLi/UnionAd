package io.union.log.service;

import io.union.log.dao.TempCuvRepository;
import io.union.log.entity.TempCuv;
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
public class TempCuvService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TempCuvRepository tempCuvRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public TempCuv findOne(String addTime, String tempuv, Long planid, Long uid) {
        try {
            return tempCuvRepository.findAllByAddTimeAndTempuvAndPlanidAndUid(addTime, tempuv, planid, uid);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TempCuv saveOne(TempCuv uv) {
        try {
            return tempCuvRepository.save(uv);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<TempCuv> save(List<TempCuv> list) {
        try {
            return tempCuvRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return new ArrayList<>();
    }
}
