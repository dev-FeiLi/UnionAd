package io.union.admin.dao;

import io.union.admin.entity.SysManage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/4/11.
 */
@Repository
public interface SysManageRepository extends JpaRepository<SysManage, Long> {

    SysManage findFirstByManAccount(String manAccount);
}
