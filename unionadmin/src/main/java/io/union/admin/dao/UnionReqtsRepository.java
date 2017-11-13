package io.union.admin.dao;

import io.union.admin.entity.UnionReqts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface UnionReqtsRepository extends JpaRepository<UnionReqts, Long> {

    @Query("select new UnionReqts(s.addTime, sum(s.reqeusts)) from UnionReqts s where s.addTime=?1")
    UnionReqts findTotalReqts(Date addTime);
}
