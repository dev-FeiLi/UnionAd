package io.union.wap.service;

import io.union.wap.dao.UnionSettingRepository;
import io.union.wap.entity.UnionSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2017/8/2.
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
