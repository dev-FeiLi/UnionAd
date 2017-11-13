package io.union.admin.dao;

import io.union.admin.entity.LogEffect;
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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Shell Li on 2017/11/1.
 */
@Repository("LogEffectRepositoryImpl")
public class LogEffectRepositoryImpl implements BaseRepositoryCustom<LogEffect, Long> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;


    public Page<LogEffect> findByConditions(Map<String, String> params, Pageable pageable) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<LogEffect> criteriaQuery = criteriaBuilder.createQuery(LogEffect.class);
            Root<LogEffect> root = criteriaQuery.from(LogEffect.class);

            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(params.get("regTime"))) {
                String[] dateRange = params.get("regTime").split("to");
                SimpleDateFormat sdm = new SimpleDateFormat("yyyy-MM-dd");
                Date startdate = sdm.parse(dateRange[0].trim());
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("regTime"), startdate));
                Date enddate = sdm.parse(dateRange[1].trim());
                Calendar c = Calendar.getInstance();
                c.setTime(enddate);
                c.add(Calendar.DAY_OF_MONTH, 1);
                predicates.add(criteriaBuilder.lessThan(root.get("regTime"), c.getTime()));
            }
            if (StringUtils.hasText(params.get("affuid"))) {
                predicates.add(criteriaBuilder.equal(root.get("affuid"), params.get("affuid")));
            }
            if (StringUtils.hasText(params.get("advuid"))) {
                predicates.add(criteriaBuilder.equal(root.get("advuid"), params.get("advuid")));
            }
            if (StringUtils.hasText(params.get("planid"))) {
                predicates.add(criteriaBuilder.equal(root.get("planid"), params.get("planid")));
            }

            Predicate[] pre = new Predicate[predicates.size()];
            criteriaQuery.where(predicates.toArray(pre)).orderBy(criteriaBuilder.desc(root.get("addTime")));

            TypedQuery<LogEffect> typedQuery = entityManager.createQuery(criteriaQuery);
            List<LogEffect> resultList = typedQuery.getResultList();
            if (null == resultList || resultList.size() == 0) {
                return new PageImpl<>(new ArrayList<>());
            }
            int totalElements = resultList.size();
            List<LogEffect> pageList = typedQuery.setFirstResult(pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();

            return new PageImpl<>(pageList, pageable, totalElements);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }

        return null;
    }

}
