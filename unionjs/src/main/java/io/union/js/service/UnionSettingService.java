package io.union.js.service;

import io.union.js.dao.UnionSettingRepository;
import io.union.js.entity.UnionSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2017/7/21.
 */
@Service
public class UnionSettingService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UnionSettingRepository unionSettingRepository;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public UnionSetting findOne(String vkey) {
        try {
            return unionSettingRepository.findOne(vkey);
        } catch (Exception e) {
            logger.error("findOne key=" + vkey + " error: ", e);
        }
        return null;
    }
}
