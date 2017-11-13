package io.union.wap.dao;

import io.union.wap.entity.AdsZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdsZoneRepository extends JpaRepository<AdsZone, Long> {

    List<AdsZone> findAllByUidEquals(Long uid);
}
