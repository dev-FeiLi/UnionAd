package io.union.admin.dao;

import io.union.admin.entity.UserOperateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface UserOperateLogRepository extends JpaRepository<UserOperateLog, Long> {

    @Query("select new UserOperateLog(s.otype, count(0)) from UserOperateLog s where s.ostatus='0' and date(s.addTime)=date(?1) group by s.otype")
    List<UserOperateLog> findByAddTime(Date addTime);

    @Query(value = "select s.* from user_operate_log s where s.uid = ?1 and s.otype = ?2 and DATE(s.add_time)=DATE(?3)", nativeQuery = true)
    List<UserOperateLog> findAllByUidAndOtypeAndAddTime(long uid, int otype, Date addTime);
}
