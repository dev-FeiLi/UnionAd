package io.union.admin.service;

import io.union.admin.dao.SysAuthorityRepository;
import io.union.admin.entity.SysAuthority;
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

import java.util.List;

/**
 * Created by Administrator on 2017/4/8.
 */
@Service
public class SysAuthorityService {
    final Logger LOG = LoggerFactory.getLogger(SysAuthorityService.class);

    @Autowired
    private SysAuthorityRepository sysAuthorityRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<SysAuthority> findAll() {
        List<SysAuthority> list = null;
        try {
            list = sysAuthorityRepository.findAll();
        } catch (Exception e) {
            LOG.error("findAll error: ", e);
        } finally {
            return list;
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<SysAuthority> findAll(int page, int size) {
        Page<SysAuthority> list = null;
        try {
            if (size == 0) size = 50;
            Sort sort = new Sort(Sort.Direction.ASC, "authSort");
            Pageable pageable = new PageRequest(page, size, sort);
            list = sysAuthorityRepository.findAll(pageable);
        } catch (Exception e) {
            LOG.error("findAll with page error: ", e);
        } finally {
            return list;
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public SysAuthority findOne(Long id) {
        try {
            return sysAuthorityRepository.findOne(id);
        } catch (Exception e) {
            LOG.error("findOne error: ", e);
        }
        return null;
    }

    public List<SysAuthority> findByParent(Long parentId) {
        try {
            if (null != parentId) {
                return sysAuthorityRepository.findAllByAuthParent(parentId);
            }
        } catch (Exception e) {
            LOG.error("findByParent error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Long save(SysAuthority item) {
        Long id = null;
        try {
            sysAuthorityRepository.save(item);
            id = item.getAuthId();
        } catch (Exception e) {
            LOG.error("save error: ", e);
        }
        return id;
    }
}
