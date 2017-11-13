package io.union.admin.dao;

import io.union.admin.entity.UnionStats;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository("unionStatsRepository")
public class UnionStatsRepositoryCustomImpl implements UnionStatsRepositoryCustom<UnionStats, Long> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    // @Query("select new UnionStats(sum(s.paynum), sum(s.sumpay),sum(s.sumadvpay),sum(s.sumprofit), s.addTime) from UnionStats s where s.addTime=?1")
    public UnionStats findByAddTime(Date addTime) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<UnionStats> criteriaQuery = criteriaBuilder.createQuery(UnionStats.class);
            Root<UnionStats> root = criteriaQuery.from(UnionStats.class);

            criteriaQuery.multiselect(criteriaBuilder.sum(root.<Long>get("paynum")), criteriaBuilder.sum(root.<Double>get("sumpay")), criteriaBuilder.sum(root.<Double>get("sumadvpay")),
                    criteriaBuilder.sum(root.<Double>get("sumprofit")), root.<Date>get("addTime"));
            List<Predicate> predicates = new ArrayList<>();
            if (null != addTime) {
                predicates.add(criteriaBuilder.equal(root.<Date>get("addTime"), addTime));
            }
            Predicate[] pre = new Predicate[predicates.size()];
            criteriaQuery.where(predicates.toArray(pre));

            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    public Page<UnionStats> findByCondition(String type, String searchId, Long idValue, String sortField, Integer paytype, String begintime, String endtime, Pageable pageable) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<UnionStats> criteriaQuery = criteriaBuilder.createQuery(UnionStats.class);
            Root<UnionStats> root = criteriaQuery.from(UnionStats.class);

            criteriaQuery.multiselect(root.<Date>get("addTime"), root.<Long>get(type), criteriaBuilder.sum(root.<Long>get("views")),
                    criteriaBuilder.sum(root.<Long>get("clicks")), criteriaBuilder.sum(root.<Long>get("clickip")), criteriaBuilder.sum(root.<Long>get("paynum")),
                    criteriaBuilder.sum(root.<Long>get("dedunum")), criteriaBuilder.sum(root.<Double>get("sumpay")), criteriaBuilder.sum(root.<Double>get("sumadvpay")),
                    criteriaBuilder.sum(root.<Double>get("sumprofit")));

            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(begintime)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date startTime = sdf.parse(begintime);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("addTime"), startTime));
            }
            if (!StringUtils.isEmpty(endtime)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date stopTime = sdf.parse(endtime);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("addTime"), stopTime));
            }
            if (null != idValue && idValue > 0) {
                predicates.add(criteriaBuilder.equal(root.get(searchId), idValue));
            }
            if (null != paytype && paytype > 0) {
                predicates.add(criteriaBuilder.equal(root.get("paytype"), paytype));
            }
            Predicate[] pre = new Predicate[predicates.size()];
            criteriaQuery.where(predicates.toArray(pre));

            criteriaQuery.groupBy(root.get("addTime"), root.get(type));
            if (!StringUtils.isEmpty(sortField)) {
                criteriaQuery.orderBy(criteriaBuilder.desc(criteriaBuilder.sum(root.get(sortField))));
            } else {
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("addTime")));
            }

            TypedQuery<UnionStats> typedQuery = entityManager.createQuery(criteriaQuery);
            List<UnionStats> resultList = typedQuery.getResultList();
            if (null == resultList || resultList.size() == 0) {
                return new PageImpl<>(new ArrayList<>());
            }
            int totalElements = resultList.size();
            List<UnionStats> pageList = typedQuery.setFirstResult(pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();

            return new PageImpl<>(pageList, pageable, totalElements);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    public UnionStats sumByCondition(String searchId, Long idValue, Integer paytype, String begintime, String endtime) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<UnionStats> criteriaQuery = criteriaBuilder.createQuery(UnionStats.class);
            Root<UnionStats> root = criteriaQuery.from(UnionStats.class);

            criteriaQuery.multiselect(criteriaBuilder.sum(root.<Long>get("views")),
                    criteriaBuilder.sum(root.<Long>get("clicks")), criteriaBuilder.sum(root.<Long>get("clickip")), criteriaBuilder.sum(root.<Long>get("paynum")),
                    criteriaBuilder.sum(root.<Long>get("dedunum")), criteriaBuilder.sum(root.<Double>get("sumpay")), criteriaBuilder.sum(root.<Double>get("sumadvpay")),
                    criteriaBuilder.sum(root.<Double>get("sumprofit")));

            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(begintime)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date startTime = sdf.parse(begintime);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("addTime"), startTime));
            }
            if (!StringUtils.isEmpty(endtime)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date stopTime = sdf.parse(endtime);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("addTime"), stopTime));
            }
            if (null != idValue && idValue > 0) {
                predicates.add(criteriaBuilder.equal(root.get(searchId), idValue));
            }
            if (null != paytype && paytype > 0) {
                predicates.add(criteriaBuilder.equal(root.get("paytype"), paytype));
            }
            Predicate[] pre = new Predicate[predicates.size()];
            criteriaQuery.where(predicates.toArray(pre));

            TypedQuery<UnionStats> typedQuery = entityManager.createQuery(criteriaQuery);
            return typedQuery.getSingleResult();
        } catch (Exception e) {
            logger.error("sum error: ", e);
        }
        return null;
    }
}
