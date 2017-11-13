package io.union.admin.service;

import io.union.admin.dao.StatsRepository;
import io.union.admin.dao.UnionStatsRepositoryCustomImpl;
import io.union.admin.entity.UnionStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/7/28.
 */
@Service
public class UnionStatsService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("unionStatsRepository")
    private UnionStatsRepositoryCustomImpl unionStatsRepository;
    @Autowired
    private StatsRepository statsRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public UnionStats findByTime(String addtime) {
        try {
            LocalDate localDate = LocalDate.parse(addtime);
            ZoneId zone = ZoneId.systemDefault();
            Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
            Date today = Date.from(instant);
            UnionStats stats = unionStatsRepository.findByAddTime(today);
            if (null != stats) {
                return stats;
            }
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<UnionStats> findByPage(String type, String searchId, Long idValue, String sortField, Integer paytype, String begintime, String endtime, Pageable pageable) {
        try {
            if (null == type || "".equals(type)) {
                return null;
            }
            return unionStatsRepository.findByCondition(type, searchId, idValue, sortField, paytype, begintime, endtime, pageable);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public UnionStats sumByPage(String searchId, Long idValue, Integer paytype, String begintime, String endtime) {
        try {
            return unionStatsRepository.sumByCondition(searchId, idValue, paytype, begintime, endtime);
        } catch (Exception e) {
            logger.error("sum error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public long save(UnionStats unionStats) {
        long id = 0;
        try {
            unionStats = statsRepository.save(unionStats);
            id = unionStats.getId();
        } catch (Exception e) {
            logger.error("save UnionStats error: ", e);
        }
        return id;
    }

    /**
     * 根据id删除导入功能表中的数据
     *
     * @param id
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void delete(Long id) {
        try {
            statsRepository.delete(id);
        } catch (Exception e) {
            logger.error("delete UnionStats error: ", e);
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public UnionStats findOne(Long id) {
        try {
            return statsRepository.findOne(id);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<UnionStats> unionStatsList(Long uid, Long planid, Long adid, Long advid, Long siteid, Long zoneid, String addTime) {
        try {
            List<UnionStats> list = statsRepository.findAll(uid, planid, adid, advid, siteid, zoneid, addTime);
            return list;
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }
}
