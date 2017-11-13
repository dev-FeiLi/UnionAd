package io.union.log.dao;

import io.union.log.entity.AdsSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/7/17.
 */
@Repository
public interface AdsSiteRepository extends JpaRepository<AdsSite, Long> {

    List<AdsSite> findAllByUidEquals(Long uid);
}
