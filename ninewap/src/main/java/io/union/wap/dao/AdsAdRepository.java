package io.union.wap.dao;

import io.union.wap.entity.AdsAd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdsAdRepository extends JpaRepository<AdsAd, Long> {

    List<AdsAd> findAllByAstatusEquals(Integer astatus);
}
