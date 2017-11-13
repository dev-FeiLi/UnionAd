package io.union.wap.dao;

import io.union.wap.entity.AdsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/8/1.
 */
@Repository
public interface AdsUserRepository extends JpaRepository<AdsUser, Long> {

    AdsUser findAdsUserByUsername(String username);

    List<AdsUser> findAllByUtypeEqualsOrderByUid(Integer utype);
}
