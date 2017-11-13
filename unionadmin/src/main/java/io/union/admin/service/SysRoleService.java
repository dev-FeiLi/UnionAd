package io.union.admin.service;

import io.union.admin.dao.SysAuthorityRepository;
import io.union.admin.dao.SysRoleRepository;
import io.union.admin.entity.SysAuthority;
import io.union.admin.entity.SysRole;
import io.union.admin.security.SecurityMetadataSourceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by Administrator on 2017/4/11.
 */
@Service
public class SysRoleService {
    final Logger LOG = LoggerFactory.getLogger(SysRoleService.class);

    @Autowired
    private SysRoleRepository sysRoleRepository;
    @Autowired
    private SysAuthorityRepository sysAuthorityRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<SysRole> findAll() {
        List<SysRole> list = null;
        try {
            list = sysRoleRepository.findAll();
        } catch (Exception e) {
            LOG.error("findAll error: ", e);
        } finally {
            return list;
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<SysRole> findAll(int page, int size) {
        Page<SysRole> list = null;
        try {
            Sort sort = new Sort(Sort.Direction.ASC, "roleId");
            Pageable pageable = new PageRequest(page, size, sort);
            list = sysRoleRepository.findAll(pageable);
        } catch (Exception e) {
            LOG.error("findAll with page error: ", e);
        } finally {
            return list;
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public SysRole findOne(Long id) {
        try {
            return sysRoleRepository.findOne(id);
        } catch (Exception e) {
            LOG.error("findOne error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Long save(SysRole role) {
        Long id = null;
        try {
            sysRoleRepository.save(role);
            id = role.getRoleId();
            loadAuthorityMapping();//每次修改角色的权限之后就需要更新系统的权限缓存
        } catch (Exception e) {
            LOG.error("save error: ", e);
        }
        return id;
    }

    // 刷新所有资源与权限的关系，该动作在系统启动之时，或者在新建或修改角色信息之后发生
    // 因为resourceMap是静态属性，不强制刷新则重新分配的权限不生效
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public void loadAuthorityMapping() throws Exception {
        Map<String, Collection<ConfigAttribute>> resourceTmpMap = new HashMap<>();
        List<SysRole> roleList = sysRoleRepository.findAll();
        for (SysRole role : roleList) {
            String authString = role.getRoleAuths();
            if (null == authString || "".equals(authString)) continue;
            String[] authIds = authString.split(",");
            for (String id : authIds) {
                SysAuthority authority = sysAuthorityRepository.findOne(Long.valueOf(id.trim()));
                String path = authority.getAuthUrl();
                Collection<ConfigAttribute> collection = resourceTmpMap.get(path);
                if (null == collection) {
                    collection = new ArrayList<>();
                }
                collection.add(new SecurityConfig(String.valueOf(role.getRoleId())));
                resourceTmpMap.put(path, collection);
            }
        }
        SecurityMetadataSourceImpl.resourceMap.clear();
        SecurityMetadataSourceImpl.resourceMap.putAll(resourceTmpMap);
    }
}
