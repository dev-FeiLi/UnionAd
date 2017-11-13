package io.union.admin.service;

import io.union.admin.dao.UnionSettingRepository;
import io.union.admin.entity.UnionSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/7/21.
 */
@Service
public class UnionSettingService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    private UnionSettingRepository unionSettingRepository;

    public UnionSettingService(@Autowired UnionSettingRepository unionSettingRepository) {
        this.unionSettingRepository = unionSettingRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public UnionSetting findOne(String vkey) {
        try {
            return unionSettingRepository.findOne(vkey);
        } catch (Exception e) {
            logger.error("findOne key=" + vkey + " error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<UnionSetting> findAll() {
        try {
            return unionSettingRepository.findAll();
        } catch (Exception e) {
            logger.error("find all error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UnionSetting save(UnionSetting entity) {
        try {
            return unionSettingRepository.save(entity);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<UnionSetting> save(List<UnionSetting> list) {
        try {
            return unionSettingRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }
}
