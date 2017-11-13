package io.union.log.service;

import io.union.log.dao.UnionStatsRepository;
import io.union.log.entity.UnionStats;
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
public class UnionStatsService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UnionStatsRepository unionStatsRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public UnionStats findByPlanidandTime(Long planid, String addtime) {
        try {
            return unionStatsRepository.sumByPlanidandTime(addtime, planid);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public UnionStats findByUnique(String addtime, Long uid, Long zoneid, Long siteid, Long advid, Long planid, Long adid) {
        try {
            return unionStatsRepository.findAllByUnique(addtime, uid, zoneid, siteid, advid, planid, adid);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UnionStats saveOne(UnionStats stats) {
        try {
            return unionStatsRepository.save(stats);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<UnionStats> save(List<UnionStats> list) {
        try {
            return unionStatsRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return new ArrayList<>();
    }
}
