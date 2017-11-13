package io.union.admin.dao;

import io.union.admin.entity.AdsSite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdsSiteRepository extends JpaRepository<AdsSite, Long> {

    List<AdsSite> findAllByUidEquals(Long uid);

    //    Page<AdsSite> findAll(Pageable pageable);
    Page<AdsSite> findAllBySstatusNot(Integer sstatus, Pageable pageable);

    /**
     * 根据网站类型分页查询出网站信息
     *
     * @param sitetype
     * @return
     */
    Page<AdsSite> findBySitetypeAndSstatusNot(Integer sitetype, Integer sstatus, Pageable pageable);

    /**
     * 根据uid查询出网站信息
     *
     * @param uid
     * @param sstatus
     * @param pageable
     * @return
     */
    Page<AdsSite> findByUidAndSstatusNot(Integer uid, Integer sstatus, Pageable pageable);

    /**
     * 根据网站类型和网站的编号查询网站信息
     *
     * @param sitetype
     * @param uid
     * @param sstatus
     * @param pageable
     * @return
     */
    Page<AdsSite> findBySitetypeAndUidAndSstatusNot(Integer sitetype, Integer uid, Integer sstatus, Pageable pageable);

    //根据网站名称模糊搜索
    Page<AdsSite> findBySitenameContainingAndSstatusNot(String sitename, Integer sstatus, Pageable pageable);

    //根据网站地址模糊查询网站信息
    Page<AdsSite> findBySiteurlContainingAndSstatusNot(String siteurl, Integer sstatus, Pageable pageable);

    //根据站长名称查询网站信息
    Page<AdsSite> findByUid(Long uid, Pageable pageable);

    //根据站长名称查询网站信息
    Page<AdsSite> findByUidAndSstatusNot(Long uid, Integer sstatus, Pageable pageable);
//    //根据用户名模糊查询出所有的用户信息
//    Page<AdsSite> findByUsername(String userName,Pageable pageable);

    /**
     * 根据网站类型和网站地址进行模糊搜索
     *
     * @param sitetype
     * @param siteurl
     * @param pageable
     * @return
     */
    Page<AdsSite> findBySitetypeAndSiteurlContainingAndSstatusNot(Integer sitetype, String siteurl, Integer sstatus, Pageable pageable);

    /**
     * 根据网站类型和网站名称进行模糊搜索
     *
     * @param sitetype
     * @param siteurl
     * @param pageable
     * @return
     */
    Page<AdsSite> findBySitetypeAndSitenameContainingAndSstatusNot(Integer sitetype, String siteurl, Integer sstatus, Pageable pageable);

    /**
     * 根据网站类型和站长名称进行模糊搜索
     *
     * @param sitetype
     * @param uid
     * @param pageable
     * @return
     */
    Page<AdsSite> findBySitetypeAndUidAndSstatusNot(Integer sitetype, Long uid, Integer sstatus, Pageable pageable);

    /**
     * 根据状态查询出包含该状态的信息
     *
     * @param sstatus
     * @param pageable
     * @return
     */
    Page<AdsSite> findBySstatus(Integer sstatus, Pageable pageable);

    /**
     * 根据网址查询是否含有该网站信息
     *
     * @param siteurl
     * @return
     */
    AdsSite findBySiteurl(String siteurl);
}
