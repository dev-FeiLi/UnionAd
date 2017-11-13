package io.union.log.service;

import io.union.log.dao.UnionReqtsRepository;
import io.union.log.entity.UnionReqts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */
@Service
public class UnionReqtsService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UnionReqtsRepository unionReqtsRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public UnionReqts findOne(Long zoneid, String addTime) {
        try {
            return unionReqtsRepository.findByZoneidAndAddTime(zoneid, addTime);
        } catch (Exception e) {
            logger.error("findone error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UnionReqts saveOne(UnionReqts reqts) {
        try {
            return unionReqtsRepository.save(reqts);
        } catch (Exception e) {
            logger.error("saveOne error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<UnionReqts> save(List<UnionReqts> list) {
        try {
            return unionReqtsRepository.save(list);
        } catch (Exception e) {
            logger.error("saveOne error: ", e);
        }
        return new ArrayList<>();
    }
}
