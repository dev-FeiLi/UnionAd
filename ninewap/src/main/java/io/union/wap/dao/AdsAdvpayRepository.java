package io.union.wap.dao;

import io.union.wap.entity.AdsAdvpay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdsAdvpayRepository extends JpaRepository<AdsAdvpay, Long> {

    List<AdsAdvpay> findAllByUidEquals(Long uid);
}
