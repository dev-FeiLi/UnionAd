package io.union.admin.dao;

import io.union.admin.entity.LogScreen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/27.
 */
@Repository
public interface LogScreenRepository extends JpaRepository<LogScreen, Long> {

    @Query(value = "select * from log_screen s where s.add_time=?3 and s.cus_screen=?1 and s.uid=?2", nativeQuery = true)
    LogScreen findByScreenInfo(String cusScreen, Long uid, String addtime);
}
