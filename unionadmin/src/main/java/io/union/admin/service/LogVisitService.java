package io.union.admin.service;

import io.union.admin.dao.LogVisitRepository;
import io.union.admin.dao.LogVisitRepositoryCustomImpl;
import io.union.admin.entity.LogVisit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Administrator on 2017/7/27.
 */
@Service
public class LogVisitService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LogVisitRepository logVisitRepository;
    @Autowired
    private LogVisitRepositoryCustomImpl logVisitRepositoryCustom;

    /**
     * 分页获取log_visit 信息
     *
     * @param params
     * @param pageable
     * @return
     */
    public Page<LogVisit> findByPage(Map<String, String> params, Pageable pageable) {
        try {
            return logVisitRepositoryCustom.findByConditions(params, pageable);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

}
