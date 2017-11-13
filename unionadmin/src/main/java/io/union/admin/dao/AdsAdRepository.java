package io.union.admin.dao;

import io.union.admin.entity.AdsAd;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Shell Li on 2017/8/16.
 */
@Repository
public interface AdsAdRepository extends JpaRepository<AdsAd, Long> {
    Page<AdsAd> findAllByAstatusNot(Pageable pageable, int astatus);

    Page<AdsAd> findAllByWidthAndHeightAndAstatusNot(int width, int height, int astatus, Pageable pageable);

    Page<AdsAd> findAllByAdidAndAstatusNot(long adid, int astatus, Pageable pageable);

    Page<AdsAd> findAllByAdidAndWidthAndHeightAndAstatusNot(long adid, int width, int height, int astatus, Pageable pageable);

    Page<AdsAd> findAllByPlanidAndAstatusNot(long planid, int astatus, Pageable pageable);

    Page<AdsAd> findAllByPlanidAndWidthAndHeightAndAstatusNot(long planid, int width, int height, int astatus, Pageable pageable);

    Page<AdsAd> findAllByUidAndAstatusNot(long uid, int astatus, Pageable pageable);

    Page<AdsAd> findAllByUidAndWidthAndHeightAndAstatusNot(long uid, int width, int height, int astatus, Pageable pageable);

    Page<AdsAd> findAllByAdurlContainingAndAstatusNot(String adurl, int astatus, Pageable pageable);

    Page<AdsAd> findAllByAdurlContainingAndWidthAndHeightAndAstatusNot(String adurl, int width, int height, int astatus, Pageable pageable);

    List<AdsAd> findAllByPlanidAndAstatusNotIn(long planid, List<Integer> status);


    @Query(value = "select a.* from ads_ad a where uid=?1 and planid NOT IN (?2)", nativeQuery = true)
    List<AdsAd> findAllByUidAndPlanid(long uid, String str[]);

    @Query(value = "select a.* from ads_ad a where planid=?1 and uid NOT IN (?2)", nativeQuery = true)
    List<AdsAd> findAllByPlanidAndUid(long planid, String str[]);

    List<AdsAd> findAllByAdtypeAndWidthAndHeightAndAstatusNot(Integer adtype, Integer width, Integer height, Integer astatus);

    List<AdsAd> findAllByPlanidAndAstatusNot(long planid, int astatus);
}
