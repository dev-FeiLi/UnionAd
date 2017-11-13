package io.union.log.service;

import io.union.log.dao.StatHourMoneyRepository;
import io.union.log.entity.StatHourMoney;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/29.
 */
@Service
public class StatHourMoneyService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StatHourMoneyRepository statHourMoneyRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public StatHourMoney findByUnique(String addtime, Long uid, Long siteid, Long planid) {
        try {
            return statHourMoneyRepository.findAllByUnique(addtime, uid, siteid, planid);
        } catch (Exception e) {
            logger.error("find error:, ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public StatHourMoney saveOne(StatHourMoney money) {
        try {
            return statHourMoneyRepository.save(money);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<StatHourMoney> save(List<StatHourMoney> list) {
        try {
            return statHourMoneyRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return new ArrayList<>();
    }
}
