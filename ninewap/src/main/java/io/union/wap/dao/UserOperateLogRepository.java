package io.union.wap.dao;

import io.union.wap.entity.UserOperateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOperateLogRepository extends JpaRepository<UserOperateLog, Long> {
}
