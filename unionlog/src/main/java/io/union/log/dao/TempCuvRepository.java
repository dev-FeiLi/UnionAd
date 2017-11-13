package io.union.log.dao;

import io.union.log.entity.TempCuv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/28.
 */
@Repository
public interface TempCuvRepository extends JpaRepository<TempCuv, Long> {

    @Query(value = "select s.* from temp_cuv s where date(s.add_time)=date(?1) and s.tempuv=?2 and s.planid=?3 and s.uid=?4", nativeQuery = true)
    TempCuv findAllByAddTimeAndTempuvAndPlanidAndUid(String addTime, String tempuv, Long planid, Long uid);
}
