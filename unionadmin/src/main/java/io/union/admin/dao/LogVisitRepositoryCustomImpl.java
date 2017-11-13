package io.union.admin.dao;

import io.union.admin.entity.LogVisit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Shell Li on 2017/10/11.
 */
@Repository("LogVisitRepositoryCustomImpl")
public class LogVisitRepositoryCustomImpl implements LogVisitRepositoryCustom<LogVisit, Long> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    public Page<LogVisit> findByConditions(Map<String, String> params, Pageable pageable) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<LogVisit> criteriaQuery = criteriaBuilder.createQuery(LogVisit.class);
            Root<LogVisit> root = criteriaQuery.from(LogVisit.class);

            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(params.get("date"))) {
                String dateRange = params.get("date");
                predicates.add(criteriaBuilder.like(root.get("addTime"), dateRange+"%"));
            }
            if (StringUtils.hasText(params.get("uid"))) {
                predicates.add(criteriaBuilder.equal(root.get("uid"), params.get("uid")));
            }
            if (StringUtils.hasText(params.get("cusIp"))) {
                predicates.add(criteriaBuilder.equal(root.get("cusIp"), params.get("cusIp")));
            }
            if (StringUtils.hasText(params.get("adid"))) {
                predicates.add(criteriaBuilder.equal(root.get("adid"), params.get("adid")));
            }
            if (StringUtils.hasText(params.get("planid"))) {
                predicates.add(criteriaBuilder.equal(root.get("planid"), params.get("planid")));
            }

            Predicate[] pre = new Predicate[predicates.size()];
            criteriaQuery.where(predicates.toArray(pre)).orderBy(criteriaBuilder.desc(root.get("addTime")));

            TypedQuery<LogVisit> typedQuery = entityManager.createQuery(criteriaQuery);
            List<LogVisit> resultList = typedQuery.getResultList();
            if (null == resultList || resultList.size() == 0) {
                return new PageImpl<>(new ArrayList<>());
            }
            int totalElements = resultList.size();
            List<LogVisit> pageList = typedQuery.setFirstResult(pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();

            return new PageImpl<>(pageList, pageable, totalElements);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }

        return null;
    }

}
