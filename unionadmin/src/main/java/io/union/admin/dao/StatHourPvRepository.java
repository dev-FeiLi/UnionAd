package io.union.admin.dao;

import io.union.admin.entity.StatHourPv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Created by Administrator on 2017/7/29.
 */
@Repository
public interface StatHourPvRepository extends JpaRepository<StatHourPv, Long> {

    @Query("select new StatHourPv(s.addTime, sum(s.hour0), sum(s.hour1), sum(s.hour2), sum(s.hour3), sum(s.hour4), sum(s.hour5)," +
            " sum(s.hour6), sum(s.hour7), sum(s.hour8), sum(s.hour9), sum(s.hour10), sum(s.hour11), sum(s.hour12), sum(s.hour13), " +
            "sum(s.hour14), sum(s.hour15), sum(s.hour16), sum(s.hour17), sum(s.hour18), sum(s.hour19), sum(s.hour20), sum(s.hour21), " +
            "sum(s.hour22), sum(s.hour23)) from StatHourPv s where s.addTime=?1")
    StatHourPv findTotalByAddTime(Date addTime);

}
