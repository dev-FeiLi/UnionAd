package io.union.log.dao;

import io.union.log.entity.TempUv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/28.
 */
@Repository
public interface TempUvRepository extends JpaRepository<TempUv, Long> {

    @Query(value = "select s.* from temp_uv s where date(s.add_time)=date(?1) and s.tempuv=?2 and s.planid=?3 and s.uid=?4", nativeQuery = true)
    TempUv findAllByAddTimeAndTempuvAndPlanidAndUid(String addTime, String tempuv, Long planid, Long uid);
}
