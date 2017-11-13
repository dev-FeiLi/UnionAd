package io.union.admin.dao;

import io.union.admin.entity.AdsClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdsClassRepository extends JpaRepository<AdsClass, Integer> {
}
