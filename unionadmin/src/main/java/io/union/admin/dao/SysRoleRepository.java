package io.union.admin.dao;

import io.union.admin.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/4/11.
 */
@Repository
public interface SysRoleRepository extends JpaRepository<SysRole, Long> {
}
