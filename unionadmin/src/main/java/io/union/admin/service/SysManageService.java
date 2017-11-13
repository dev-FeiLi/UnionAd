package io.union.admin.service;

import io.union.admin.dao.SysManageRepository;
import io.union.admin.entity.SysManage;
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
public class SysManageService {
    final Logger LOG = LoggerFactory.getLogger(SysManageService.class);

    @Autowired
    private SysManageRepository sysManageRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<SysManage> findAll(int page, int size) {
        Page<SysManage> list = null;
        try {
            if (size == 0) size = 50;
            Sort sort = new Sort(Sort.Direction.ASC, "manId");
            Pageable pageable = new PageRequest(page, size, sort);
            list = sysManageRepository.findAll(pageable);
        } catch (Exception e) {
            LOG.error("findAll error: ", e);
        }
        return list;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public SysManage findOne(Long id) {
        try {
            return sysManageRepository.findOne(id);
        } catch (Exception e) {
            LOG.error("findOne error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public SysManage findByAccount(String account) {
        try {
            return sysManageRepository.findFirstByManAccount(account);
        } catch (Exception e) {
            LOG.error("findOne error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Long save(SysManage item) {
        Long id = null;
        try {
            sysManageRepository.save(item);
            id = item.getManId();
        } catch (Exception e) {
            LOG.error("save error: ", e);
        }
        return id;
    }
}
