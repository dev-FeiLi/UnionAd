package io.union.admin.dao;

import io.union.admin.entity.AdsAdvpay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AdsAdvpayRepository extends JpaRepository<AdsAdvpay, Long> {

    @Query(value = "select sum(money) from ads_advpay group by paytype", nativeQuery = true)
    List<BigDecimal> getTotolCharge();
}
