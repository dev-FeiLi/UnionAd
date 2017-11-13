package io.union.admin.dao;

import io.union.admin.entity.AdsAffpay;
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
@Repository("AffpayRepositoryCustom")
public class AffpayRepositoryCustomImpl implements AffpayRepositoryCustom<AdsAffpay, Long> {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    public List<AdsAffpay> findAll(Map<String, Object> advpayParams) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<AdsAffpay> criteriaQuery = criteriaBuilder.createQuery(AdsAffpay.class);
            Root<AdsAffpay> root = criteriaQuery.from(AdsAffpay.class);

            List<Predicate> predicates = new ArrayList<>();
            if (null != advpayParams.get("startDate") && null != advpayParams.get("endDate")) {
                predicates.add(criteriaBuilder.between(root.get("addTime").as(Date.class), (Date) advpayParams.get("startDate"), (Date) advpayParams.get("endDate")));
            } else if (null != advpayParams.get("startDate")) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("addTime").as(Date.class), (Date) advpayParams.get("startDate")));
            } else if (null != advpayParams.get("endDate")) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("addTime").as(Date.class), (Date) advpayParams.get("endDate")));
            }

            if (null != advpayParams.get("uid")) {
                predicates.add(criteriaBuilder.equal(root.get("uid").as(Long.class), advpayParams.get("uid")));
            }
            if (null != advpayParams.get("pstatus")) {
                predicates.add(criteriaBuilder.equal(root.get("pstatus").as(Integer.class), advpayParams.get("pstatus")));
            }
            Predicate[] pre = new Predicate[predicates.size()];
            criteriaQuery.where(predicates.toArray(pre)).orderBy(criteriaBuilder.desc(root.get("id")));

            return entityManager.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }
}
