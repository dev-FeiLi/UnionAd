package io.union.log.service;

import io.union.log.dao.LogBrowserRepository;
import io.union.log.entity.LogBrowser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/27.
 */
@Service
public class LogBrowserService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LogBrowserRepository logBrowserRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public LogBrowser saveOne(LogBrowser browser) {
        try {
            return logBrowserRepository.save(browser);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<LogBrowser> save(List<LogBrowser> list) {
        try {
            return logBrowserRepository.save(list);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return new ArrayList<>();
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public LogBrowser findOne(Long uid, String brwName, String brwOs, String brwVersion, String addtime) {
        try {
            return logBrowserRepository.findByBrowserInfo(brwName, brwOs, brwVersion, uid, addtime);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }
}
