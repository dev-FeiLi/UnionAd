package io.union.wap.dao;

import io.union.wap.entity.AdsPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdsPlanRepository extends JpaRepository<AdsPlan, Long> {

    List<AdsPlan> findAllByPstatusEquals(Integer pstatus);
}
