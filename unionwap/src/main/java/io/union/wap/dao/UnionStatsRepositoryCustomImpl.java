package io.union.wap.dao;

import io.union.wap.entity.UnionStats;
import io.union.wap.model.AdvDataQuery;
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
import java.util.List;

// 参考 http://www.jianshu.com/p/73f48095a7bf
@Repository("unionStatsRepositoryCustom")
public class UnionStatsRepositoryCustomImpl implements UnionStatsRepositoryCustom<UnionStats, Long> {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    public List<UnionStats> findAll(AdvDataQuery advDataQuery) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<UnionStats> criteriaQuery = criteriaBuilder.createQuery(UnionStats.class);
            Root<UnionStats> root = criteriaQuery.from(UnionStats.class);

            List<Predicate> predicates = new ArrayList<>();
            criteriaQuery.multiselect(root.get("addTime"), root.get("planid"),
                    criteriaBuilder.sum(root.get("views")), criteriaBuilder.sum(root.get("clicks")),
                    criteriaBuilder.sum(root.get("clickip")), criteriaBuilder.sum(root.get("paynum")),
                    criteriaBuilder.sum(root.get("dedunum")), criteriaBuilder.sum(root.get("sumadvpay")));


            if (null != advDataQuery.getStartTime() && null != advDataQuery.getStopTime()) {
                predicates.add(criteriaBuilder.between(root.get("addTime"), advDataQuery.getStartTime(), advDataQuery.getStopTime()));
            } else if (null != advDataQuery.getStartTime()) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("addTime"), advDataQuery.getStartTime()));
            } else if (null != advDataQuery.getStopTime()) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("addTime"), advDataQuery.getStopTime()));
            }
            predicates.add(criteriaBuilder.equal(root.get("advid"), advDataQuery.getUid()));

            Predicate[] pre = new Predicate[predicates.size()];
            criteriaQuery.where(predicates.toArray(pre));
            criteriaQuery.groupBy(root.get("addTime"), root.get("planid"));
            criteriaQuery.orderBy(criteriaBuilder.desc(root.get("addTime")));

            return entityManager.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }
}
