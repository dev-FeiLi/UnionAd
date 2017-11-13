package io.union.admin.dao;

import io.union.admin.entity.AdsAffpay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdsAffpayRepository extends JpaRepository<AdsAffpay, Long> {

    @Query(value = "select new AdsAffpay(sum(s.money), sum(s.realmoney)) from AdsAffpay s")
    List<AdsAffpay> findToltalMoney();
}
