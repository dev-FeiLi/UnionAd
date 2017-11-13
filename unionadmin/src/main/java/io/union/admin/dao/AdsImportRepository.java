package io.union.admin.dao;

import io.union.admin.entity.AdsImport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Shell Li on 2017/8/16.
 */
@Repository
public interface AdsImportRepository extends JpaRepository<AdsImport, Long> {
    /**
     * 查询出不包含状态为8的记录
     *
     * @param istatus
     * @param pageable
     * @return
     */
    Page<AdsImport> findAllByIstatusNot(Integer istatus, Pageable pageable);
}
