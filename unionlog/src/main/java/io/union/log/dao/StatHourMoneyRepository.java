package io.union.log.dao;

import io.union.log.entity.StatHourMoney;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/29.
 */
@Repository
public interface StatHourMoneyRepository extends JpaRepository<StatHourMoney, Long> {

    @Query(value = "select s.* from stat_hour_money s where date(s.add_time)=date(?1) and s.uid=?2 and s.siteid=?3 and s.planid=?4", nativeQuery = true)
    StatHourMoney findAllByUnique(String addtime, Long uid, Long siteid, Long planid);
}
