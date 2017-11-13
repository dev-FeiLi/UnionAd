package io.union.admin.service;

import io.union.admin.dao.SysOperateLogRepository;
import io.union.admin.entity.SysOperateLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2017/4/11.
 */
@Service
public class SysOperateLogService {
    final Logger LOG = LoggerFactory.getLogger(SysOperateLogService.class);

    @Autowired
    private SysOperateLogRepository sysOperateLogRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<SysOperateLog> findAll(int page, int size) {
        Page<SysOperateLog> list = null;
        try {
            if (size == 0) size = 50;
            Sort sort = new Sort(Sort.Direction.DESC, "optId");
            Pageable pageable = new PageRequest(page, size, sort);
            list = sysOperateLogRepository.findAll(pageable);
        } catch (Exception e) {
            LOG.error("findAll error: ", e);
        }
        return list;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public SysOperateLog findOne(Long id) {
        try {
            return sysOperateLogRepository.findOne(id);
        } catch (Exception e) {
            LOG.error("findOne error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Long save(SysOperateLog item) {
        Long id = null;
        try {
            sysOperateLogRepository.save(item);
            id = item.getOptId();
        } catch (Exception e) {
            LOG.error("save error: ", e);
        }
        return id;
    }
}
