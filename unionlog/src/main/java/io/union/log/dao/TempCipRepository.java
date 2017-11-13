package io.union.log.dao;

import io.union.log.entity.TempCip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/28.
 */
@Repository
public interface TempCipRepository extends JpaRepository<TempCip, Long> {

    @Query(value = "select s.* from temp_cip s where date(s.add_time)=date(?1) and s.tempip=?2 and s.planid=?3 and s.uid=?4", nativeQuery = true)
    TempCip findAllByAddTimeAndTempipAndPlanidAndUid(String addTime, String tempip, Long planid, Long uid);
}
