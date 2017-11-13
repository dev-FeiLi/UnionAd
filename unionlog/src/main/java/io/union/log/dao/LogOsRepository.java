package io.union.log.dao;

import io.union.log.entity.LogOs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/27.
 */
@Repository
public interface LogOsRepository extends JpaRepository<LogOs, Long> {

    @Query(value = "select * from log_os s where s.add_time=?3 and s.cus_os=?1 and s.uid=?2", nativeQuery = true)
    LogOs findByOsInfo(String cusOs, Long uid, String addtime);
}
