package io.union.admin.dao;

import io.union.admin.entity.AdsZone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdsZoneRepository extends JpaRepository<AdsZone, Long> {

    List<AdsZone> findAllByUidEquals(Long uid);

    Page<AdsZone> findAll(Pageable pageable);

    Page<AdsZone> findAllByAndZstatusNot(Integer zstatus, Pageable pageable);

    /**
     * 根据广告类型查询出所有的广告位出来
     *
     * @param viewtype
     * @param pageable
     * @return
     */
    Page<AdsZone> findByViewtypeAndZstatusNot(Integer viewtype, Integer zstatus, Pageable pageable);

    /**
     * 根据广告位的标题模糊查询广告位的信息
     *
     * @param zonename
     * @param pageable
     * @return
     */
    Page<AdsZone> findByZonenameContainingAndZstatusNot(String zonename, Integer zstatus, Pageable pageable);

    /**
     * 根据广告编号模糊查询广告位的信息
     *
     * @param zoneid
     * @param pageable
     * @return
     */
    Page<AdsZone> findByZoneidAndZstatusNot(Long zoneid, Integer zstatus, Pageable pageable);

    Page<AdsZone> findByUidAndZstatusNot(Long uid, Integer zstatus, Pageable pageable);

    /**
     * 根据广告类型和广告标题模糊查询
     *
     * @param viewtype
     * @param pageable
     * @return
     */
    Page<AdsZone> findByViewtypeAndZonenameContainingAndZstatusNot(Integer viewtype, String zonename, Integer zstatus, Pageable pageable);

    /**
     * 根据广告类型和广告编号进行模糊查询
     *
     * @param viewtype
     * @param zoneid
     * @param pageable
     * @return
     */
    Page<AdsZone> findByViewtypeAndZoneidAndZstatusNot(Integer viewtype, Long zoneid, Integer zstatus, Pageable pageable);

    Page<AdsZone> findByViewtypeAndUidAndZstatusNot(Integer viewtype, Long uid, Integer zstatus, Pageable pageable);

    /**
     * 根据状态去查询广告位数据
     *
     * @param zstatus
     * @param pageable
     * @return
     */
    Page<AdsZone> findByZstatus(Integer zstatus, Pageable pageable);

    /**
     * 根据计费模式去查询广告位数据
     *
     * @param paytype
     * @param pageable
     * @return
     */
    Page<AdsZone> findByPaytypeAndZstatusNot(Integer paytype, Integer zstatus, Pageable pageable);
}
