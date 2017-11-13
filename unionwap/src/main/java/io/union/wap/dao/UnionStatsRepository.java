package io.union.wap.dao;

import io.union.wap.entity.UnionStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface UnionStatsRepository extends JpaRepository<UnionStats, Long> {

    // nativeSQL 会返回object[] 的结果集
    //@Query(value = "select s.add_time, s.paytype, sum(s.sumpay) from union_stats s where date(s.add_time)=date(?1) and s.uid=?2 group by date(s.add_time),s.paytype order by s.paytype, date(s.add_time)", nativeQuery = true)
    @Query(value = "select new UnionStats(s.addTime, s.paytype, sum(s.sumpay)) from UnionStats s where s.addTime>=?1 and s.uid=?2 group by s.addTime,s.paytype order by s.paytype, s.addTime")
    List<UnionStats> findAllByAffTimeAndUid(Date addtime, Long uid);
}
