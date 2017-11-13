package io.union.wap.dao;

import io.union.wap.entity.AdsClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdsClassRepository extends JpaRepository<AdsClass, Long> {
}
