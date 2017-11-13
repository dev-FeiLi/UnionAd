package io.union.wap.dao;

import io.union.wap.entity.AdsAffpay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdsAffpayRepository extends JpaRepository<AdsAffpay, Long> {

    List<AdsAffpay> findAllByUidEquals(Long uid);
}
