package io.union.admin.dao;

import io.union.admin.entity.StatHourClickip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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
 * Created by Shell Li on 2017/9/25.
 */
@Repository("StatHourClickIpRepositoryCustomImpl")
public class StatHourClickIpRepositoryCustomImpl implements StatHourRepositoryCustom<StatHourClickip, Long> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    public StatHourClickip findByCustomSearch(Map<String, Object> params) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<StatHourClickip> criteriaQuery = criteriaBuilder.createQuery(StatHourClickip.class);
            Root<StatHourClickip> root = criteriaQuery.from(StatHourClickip.class);

            criteriaQuery.multiselect(criteriaBuilder.sum(root.<Long>get("hour0")), criteriaBuilder.sum(root.get("hour1")),
                    criteriaBuilder.sum(root.<Long>get("hour2")), criteriaBuilder.sum(root.<Long>get("hour3")),
                    criteriaBuilder.sum(root.<Long>get("hour4")), criteriaBuilder.sum(root.<Long>get("hour5")),
                    criteriaBuilder.sum(root.<Long>get("hour6")), criteriaBuilder.sum(root.<Long>get("hour7")),
                    criteriaBuilder.sum(root.<Long>get("hour8")), criteriaBuilder.sum(root.<Long>get("hour9")),
                    criteriaBuilder.sum(root.<Long>get("hour10")), criteriaBuilder.sum(root.<Long>get("hour11")),
                    criteriaBuilder.sum(root.<Long>get("hour12")), criteriaBuilder.sum(root.<Long>get("hour13")),
                    criteriaBuilder.sum(root.<Long>get("hour14")), criteriaBuilder.sum(root.<Long>get("hour15")),
                    criteriaBuilder.sum(root.<Long>get("hour16")), criteriaBuilder.sum(root.<Long>get("hour17")),
                    criteriaBuilder.sum(root.<Long>get("hour18")), criteriaBuilder.sum(root.<Long>get("hour19")),
                    criteriaBuilder.sum(root.<Long>get("hour20")), criteriaBuilder.sum(root.<Long>get("hour21")),
                    criteriaBuilder.sum(root.<Long>get("hour22")), criteriaBuilder.sum(root.<Long>get("hour23")));

            List<Predicate> predicates = new ArrayList<>();
            if (params.get("addDate") != null) {
                predicates.add(criteriaBuilder.equal(root.get("addTime").as(Date.class), (Date) params.get("addDate")));
            }
            if (StringUtils.hasText(params.get("uid").toString())) {
                predicates.add(criteriaBuilder.equal(root.get("uid").as(Long.class), params.get("uid")));
            }
            if (StringUtils.hasText(params.get("siteid").toString())) {
                predicates.add(criteriaBuilder.equal(root.get("siteid").as(Long.class), params.get("siteid")));
            }
            if (StringUtils.hasText(params.get("planid").toString())) {
                predicates.add(criteriaBuilder.equal(root.get("planid").as(Long.class), params.get("planid")));
            }
            Predicate[] pre = new Predicate[predicates.size()];
            criteriaQuery.where(predicates.toArray(pre));

            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (Exception e) {
            logger.error("find error:", e);
        }
        return null;
    }
}
