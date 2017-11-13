package io.union.admin.dao;

import io.union.admin.entity.LogOs;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Shell Li on 2017/9/29.
 */
@Repository("LogOSRepositoryCustomImpl")
public class LogOSRepositoryCustomImpl implements LogRepositoryCustom<LogOs, Long> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    public List<LogOs> findAll(Map<String, String> params) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<LogOs> criteriaQuery = criteriaBuilder.createQuery(LogOs.class);
            Root<LogOs> root = criteriaQuery.from(LogOs.class);
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(params.get("uid"))) {
                predicates.add(criteriaBuilder.equal(root.get("uid").as(Long.class), params.get("uid")));
            }
            if (StringUtils.hasText(params.get("searchDate"))) {
                String dateStr = params.get("searchDate");
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                    predicates.add(criteriaBuilder.equal(root.get("addTime").as(Date.class), date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = formatter.format(new Date());
                predicates.add(criteriaBuilder.equal(root.get("addTime").as(Date.class), formatter.parse(dateString)));
            }
            Predicate[] pre = new Predicate[predicates.size()];
            criteriaQuery.where(predicates.toArray(pre));

            return entityManager.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            logger.error("find error:", e);
        }

        return null;
    }

}
