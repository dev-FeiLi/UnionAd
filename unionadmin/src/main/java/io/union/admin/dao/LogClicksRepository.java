package io.union.admin.dao;

import io.union.admin.entity.LogClicks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/31.
 */
@Repository
public interface LogClicksRepository extends JpaRepository<LogClicks, Long> {
}
