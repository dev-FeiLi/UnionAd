package io.union.js.dao;

import io.union.js.entity.AdsPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/11.
 */
@Repository
public interface AdsPlanRepository extends JpaRepository<AdsPlan, Long> {
}
