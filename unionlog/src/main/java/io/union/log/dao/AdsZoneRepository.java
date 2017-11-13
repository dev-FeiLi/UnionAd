package io.union.log.dao;

import io.union.log.entity.AdsZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/11.
 */
@Repository
public interface AdsZoneRepository extends JpaRepository<AdsZone, Long> {
}
