package io.union.admin.dao;

import io.union.admin.entity.SysAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/4/8.
 */
@Repository
public interface SysAuthorityRepository extends JpaRepository<SysAuthority, Long> {

    List<SysAuthority> findAllByAuthParent(Long authParent);
}
