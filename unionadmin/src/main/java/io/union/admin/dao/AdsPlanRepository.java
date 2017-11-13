package io.union.admin.dao;

import io.union.admin.entity.AdsPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Shell Li on 2017/8/8.
 */
@Repository
public interface AdsPlanRepository extends JpaRepository<AdsPlan, Long> {

//    List<AdsPlan> findAll();

    Page<AdsPlan> findAll(Pageable pageable);

    Page<AdsPlan> findAllPlanByPayType(Integer payType, Pageable pageable);

    Page<AdsPlan> findAllByTitleContaining(String title, Pageable pageable);

    Page<AdsPlan> findAllByTitleContainingAndPstatus(String title, Integer pstatus, Pageable pageable);

    Page<AdsPlan> findAllByPlanId(long planId, Pageable pageable);

    Page<AdsPlan> findAllByPlanIdAndPstatus(long planId, Integer pstatus, Pageable pageable);

    Page<AdsPlan> findAllByUid(long planId, Pageable pageable);

    Page<AdsPlan> findAllByUidAndPstatus(long planId, Integer pstatus, Pageable pageable);


    Page<AdsPlan> findAllByPstatus(Integer pstatus, Pageable pageable);

    Page<AdsPlan> findAllByPayTypeAndTitleContaining(Integer payType, String title, Pageable pageable);

    Page<AdsPlan> findAllByPayTypeAndTitleContainingAndPstatus(Integer payType, String title, Integer pstatus, Pageable pageable);

    Page<AdsPlan> findAllByPayTypeAndPlanId(Integer payType, long planId, Pageable pageable);

    Page<AdsPlan> findAllByPayTypeAndPlanIdAndPstatus(Integer payType, long planId, Integer status, Pageable pageable);

    Page<AdsPlan> findAllByPayTypeAndUid(Integer payType, long uid, Pageable pageable);

    Page<AdsPlan> findAllByPayTypeAndUidAndPstatus(Integer payType, long uid, Integer pstatus, Pageable pageable);

    Page<AdsPlan> findAllByPayTypeAndPstatus(Integer payType, Integer pstatus, Pageable pageable);


}
