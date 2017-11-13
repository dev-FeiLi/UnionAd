package io.union.log.dao;

import io.union.log.entity.LogVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/27.
 */
@Repository
public interface LogVisitRepository extends JpaRepository<LogVisit, Long> {
}
