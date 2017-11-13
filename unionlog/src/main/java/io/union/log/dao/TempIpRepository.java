package io.union.log.dao;

import io.union.log.entity.TempIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/28.
 */
@Repository
public interface TempIpRepository extends JpaRepository<TempIp, Long> {

    @Query(value = "select s.* from temp_ip s where date(s.add_time)=date(?1) and s.tempip=?2 and s.planid=?3 and s.uid=?4", nativeQuery = true)
    TempIp findAllByAddTimeAndTempipAndPlanidAndUid(String addTime, String tempip, Long planid, Long uid);
}
