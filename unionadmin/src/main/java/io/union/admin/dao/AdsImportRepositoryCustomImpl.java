package io.union.admin.dao;

import io.union.admin.entity.AdsImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Shell Li on 2017/8/24.
 */
@Repository("AdsImportRepositoryCustom")
public class AdsImportRepositoryCustomImpl implements AdsImportRepositoryCustom<AdsImport, Long> {
    final Logger logger = LoggerFactory.getLogger(getClass());
    final static Integer ADS_IMPORT_ISTATUS = 8;//定义一个常量状态为8
    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    public List<AdsImport> findAll(Map<String, Object> adsImportParams) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<AdsImport> criteriaQuery = criteriaBuilder.createQuery(AdsImport.class);
            Root<AdsImport> root = criteriaQuery.from(AdsImport.class);
            List<Predicate> predicates = new ArrayList<>();
            if (null != adsImportParams.get("startDate") && null != adsImportParams.get("endDate")) {
                predicates.add(criteriaBuilder.between(root.get("statDate").as(Date.class), (Date) adsImportParams.get("startDate"), (Date) adsImportParams.get("endDate")));
            } else if (null != adsImportParams.get("startDate")) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("statDate").as(Date.class), (Date) adsImportParams.get("startDate")));
            } else if (null != adsImportParams.get("endDate")) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("statDate").as(Date.class), (Date) adsImportParams.get("endDate")));
            }

            if (null != adsImportParams.get("searchId")) {
                if ("uid".equals(adsImportParams.get("searchType"))) {
                    predicates.add(criteriaBuilder.equal(root.get("uid").as(Long.class), adsImportParams.get("searchId")));
                } else if ("planid".equals(adsImportParams.get("searchType"))) {
                    predicates.add(criteriaBuilder.equal(root.get("planid").as(Long.class), adsImportParams.get("searchId")));
                } else if ("advid".equals(adsImportParams.get("searchType"))) {
                    predicates.add(criteriaBuilder.equal(root.get("advid").as(Long.class), adsImportParams.get("searchId")));
                }
            }
            predicates.add(criteriaBuilder.notEqual(root.get("istatus").as(Integer.class), ADS_IMPORT_ISTATUS));
            Predicate[] pre = new Predicate[predicates.size()];
            criteriaQuery.where(predicates.toArray(pre)).orderBy(criteriaBuilder.desc(root.get("id")));

            return entityManager.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }
}
