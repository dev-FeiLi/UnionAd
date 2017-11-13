package io.union.wap.service;

import io.union.wap.dao.AdsUserRepository;
import io.union.wap.entity.AdsUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/8/1.
 */
@Service
public class AdsUserService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdsUserRepository adsUserRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsUser findOne(Long uid) {
        try {
            return adsUserRepository.findOne(uid);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsUser findByUsername(String username) {
        try {
            return adsUserRepository.findAdsUserByUsername(username);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsUser> findAllByUtype(Integer utype) {
        try {
            return adsUserRepository.findAllByUtypeEqualsOrderByUid(utype);
        } catch (Exception e) {
            logger.error("find error: ", e);
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
}
