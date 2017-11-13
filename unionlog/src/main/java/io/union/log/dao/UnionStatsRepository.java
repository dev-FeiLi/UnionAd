package io.union.log.dao;

import io.union.log.entity.UnionStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/28.
 */
@Repository
public interface UnionStatsRepository extends JpaRepository<UnionStats, Long> {

    @Query(value = "select new UnionStats(s.planid, sum(s.sumadvpay)) from UnionStats s where date(s.addTime)=date(?1) and s.planid=?2")
    UnionStats sumByPlanidandTime(String addtime, Long planid);

    @Query(value = "select s.* from union_stats s where date(s.add_time)=date(?1) and s.uid=?2 and s.zoneid=?3 and s.siteid=?4 and s.advid=?5 and s.planid=?6 and s.adid=?7", nativeQuery = true)
    UnionStats findAllByUnique(String addtime, Long uid, Long zoneid, Long siteid, Long advid, Long planid, Long adid);
}
