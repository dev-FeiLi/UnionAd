package io.union.admin.dao;

import io.union.admin.entity.AdsArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Shell Li on 2017/8/28.
 */
@Repository
public interface AdsArticleRepository extends JpaRepository<AdsArticle, Long> {

    Page<AdsArticle> findAllByTitleContaining(String title, Pageable pageable);
}
