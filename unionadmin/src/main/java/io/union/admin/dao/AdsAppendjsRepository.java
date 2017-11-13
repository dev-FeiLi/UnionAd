package io.union.admin.dao;

import io.union.admin.entity.AdsAppendjs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdsAppendjsRepository extends JpaRepository<AdsAppendjs, Long> {
}
