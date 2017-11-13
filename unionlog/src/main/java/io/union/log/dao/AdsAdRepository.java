package io.union.log.dao;

import io.union.log.entity.AdsAd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/7/11.
 */
@Repository
public interface AdsAdRepository extends JpaRepository<AdsAd, Long> {

    List<AdsAd> findAllByAdtypeAndWidthAndHeight(Integer adtype, Integer width, Integer height);

    List<AdsAd> findAllByPlanidEquals(Long planid);
}
