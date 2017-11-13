package io.union.log.dao;

import io.union.log.entity.LogClicks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/31.
 */
@Repository
public interface LogClicksRepository extends JpaRepository<LogClicks, Long> {
}
