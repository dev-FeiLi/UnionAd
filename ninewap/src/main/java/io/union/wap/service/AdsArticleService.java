package io.union.wap.service;

import io.union.wap.dao.AdsArticleRepository;
import io.union.wap.entity.AdsAritcle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/8/2.
 */
@Service
public class AdsArticleService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdsArticleRepository adsArticleRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsAritcle findOne(Long id) {
        try {
            return adsArticleRepository.findOne(id);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsAritcle> findAll() {
        try {
            return adsArticleRepository.findAllByLimit();
        } catch (Exception e) {
            logger.error("find all error: ", e);
        }
        return null;
    }
}
