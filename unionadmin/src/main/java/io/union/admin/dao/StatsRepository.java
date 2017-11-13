package io.union.admin.dao;

import io.union.admin.entity.UnionStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/7/28.
 */
@Repository
public interface StatsRepository extends JpaRepository<UnionStats, Long> {
    @Query(value = "select u.* from union_stats u where u.uid=?1 and u.planid=?2  and adid=?3 and u.advid=?4 and u.siteid=?5 and u.zoneid=?6 and date(u.add_time)=date(?7)", nativeQuery = true)
    List<UnionStats> findAll(Long uid, Long planid, Long adid, Long advid, Long siteid, Long zoneid, String addTime);
}
