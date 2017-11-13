package io.union.admin.dao;

import io.union.admin.entity.LogCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/27.
 */
@Repository
public interface LogCityRepository extends JpaRepository<LogCity, Long> {

    @Query(value = "select * from log_city s where s.add_time=?4 and s.cus_province=?1 and s.cus_isp=?2 and s.uid=?3", nativeQuery = true)
    LogCity findByCityInfo(String cusProvince, String cusIsp, Long uid, String addtime);
}
