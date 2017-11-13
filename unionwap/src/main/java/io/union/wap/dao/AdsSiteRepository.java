package io.union.wap.dao;

import io.union.wap.entity.AdsSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdsSiteRepository extends JpaRepository<AdsSite, Long> {

    List<AdsSite> findAllByUidEquals(Long uid);
}
