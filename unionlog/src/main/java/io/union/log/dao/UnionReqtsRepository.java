package io.union.log.dao;

import io.union.log.entity.UnionReqts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/26.
 */
@Repository
public interface UnionReqtsRepository extends JpaRepository<UnionReqts, Long> {

    @Query(value = "select s.* from union_reqts s where s.zoneid=?1 and date(s.add_time)=date(?2)", nativeQuery = true)
    UnionReqts findByZoneidAndAddTime(Long zoneid, String addTime);
}
