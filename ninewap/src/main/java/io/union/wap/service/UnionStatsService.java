package io.union.wap.service;

import io.union.wap.dao.UnionStatsRepository;
import io.union.wap.dao.UnionStatsRepositoryCustom;
import io.union.wap.entity.UnionStats;
import io.union.wap.model.AdvDataQuery;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class UnionStatsService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UnionStatsRepository unionStatsRepository;

    @Autowired
    private UnionStatsRepositoryCustom unionStatsRepositoryCustom;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<UnionStats> findAffListByTimeAndUid(String addtime, Long uid) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date time = sdf.parse(addtime);
            return unionStatsRepository.findAllByAffTimeAndUid(time, uid);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<UnionStats> findAdvByTime(Long uid, String starttime, String stoptime) {
        try {
            AdvDataQuery query = new AdvDataQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (null != starttime) {
                query.setStartTime(sdf.parse(starttime));
            } else { // 默认是今天
                query.setStartTime(sdf.parse(sdf.format(new Date())));
            }
            if (!StringUtils.isEmpty(stoptime)) {
                query.setStopTime(sdf.parse(stoptime));
            }
            query.setUid(uid);
            return unionStatsRepositoryCustom.findAll(query);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }
}
