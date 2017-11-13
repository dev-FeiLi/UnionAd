package io.union.log.dao;

import io.union.log.entity.StatHourReqs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/26.
 */
@Repository
public interface StatHourReqsRepository extends JpaRepository<StatHourReqs, Long> {

    @Query(value = "select s.* from stat_hour_reqs s where s.zoneid=?1 and date(s.add_time)=date(?2)", nativeQuery = true)
    StatHourReqs findByZoneidAndAddTime(Long zoneid, String addTime);
}
