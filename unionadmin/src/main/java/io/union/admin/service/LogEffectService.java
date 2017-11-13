package io.union.admin.service;

import io.union.admin.dao.LogEffectRepositoryImpl;
import io.union.admin.entity.LogEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Shell Li on 2017/11/1.
 */
@Service
public class LogEffectService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public LogEffectRepositoryImpl logEffectRepository;

    public Page<LogEffect> findByPage(Map<String, String> params, Pageable pageable){
        try {
            return logEffectRepository.findByConditions(params, pageable);
        } catch(Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }
}
