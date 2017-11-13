package io.union.log.service;

import io.union.log.dao.TempUvRepository;
import io.union.log.entity.TempUv;
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
public class TempUvService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TempUvRepository tempUvRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public TempUv findOne(String addTime, String tempuv, Long planid, Long uid) {
        try {
            return tempUvRepository.findAllByAddTimeAndTempuvAndPlanidAndUid(addTime, tempuv, planid, uid);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TempUv saveOne(TempUv uv) {
        try {
            return tempUvRepository.save(uv);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<TempUv> save(List<TempUv> list) {
        try {
            return tempUvRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return new ArrayList<>();
    }
}
