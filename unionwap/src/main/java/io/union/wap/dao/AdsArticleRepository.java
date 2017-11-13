package io.union.wap.dao;

import io.union.wap.entity.AdsAritcle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/8/2.
 */
@Repository
public interface AdsArticleRepository extends JpaRepository<AdsAritcle, Long> {

    @Query(value = "select s.* from ads_article s order by s.atop,s.asort desc,s.id desc limit 100", nativeQuery = true)
    List<AdsAritcle> findAllByLimit();
}
