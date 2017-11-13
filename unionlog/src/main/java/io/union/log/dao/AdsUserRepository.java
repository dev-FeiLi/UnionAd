package io.union.log.dao;

import io.union.log.entity.AdsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/11.
 */
@Repository
public interface AdsUserRepository extends JpaRepository<AdsUser, Long> {
}
