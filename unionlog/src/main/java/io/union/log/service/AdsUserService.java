package io.union.log.service;

import io.union.log.dao.AdsUserRepository;
import io.union.log.entity.AdsUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/13.
 */
@Service
public class AdsUserService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdsUserRepository adsUserRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsUser findOne(Long id) {
        try {
            return adsUserRepository.findOne(id);
        } catch (Exception e) {
            logger.error("findOne id=" + id + " error:", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AdsUser saveOne(AdsUser user) {
        try {
            return adsUserRepository.save(user);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<AdsUser> save(List<AdsUser> list) {
        try {
            return adsUserRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return new ArrayList<>();
    }
}
