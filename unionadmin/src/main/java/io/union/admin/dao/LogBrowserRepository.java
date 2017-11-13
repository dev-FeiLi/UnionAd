package io.union.admin.dao;

import io.union.admin.entity.LogBrowser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/27.
 */
@Repository
public interface LogBrowserRepository extends JpaRepository<LogBrowser, Long> {

    @Query(value = "select * from log_browser s where date(s.add_time)=date(?5) and s.brw_name=?1 and s.brw_plat=?2 and s.brw_version=?3 and s.uid=?4", nativeQuery = true)
    LogBrowser findByBrowserInfo(String brwName, String brwOs, String brwVersion, Long uid, String addtime);
}
