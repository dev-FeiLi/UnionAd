package io.union.admin.service;

import io.union.admin.dao.LogBrowserRepository;
import io.union.admin.dao.LogBrowserRepositoryCustomImpl;
import io.union.admin.entity.LogBrowser;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/27.
 */
@Service
public class LogBrowserService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LogBrowserRepository logBrowserRepository;
    @Autowired
    private LogBrowserRepositoryCustomImpl logBrowserRepositoryCustom;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public LogBrowser findOne(Long uid, String brwName, String brwOs, String brwVersion, String addtime) {
        try {
            return logBrowserRepository.findByBrowserInfo(brwName, brwOs, brwVersion, uid, addtime);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Map<String, Object> searchData(JSONObject params) {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> it = params.keys();
        Map<String, String> paramsMap = new HashMap<>();
        while (it.hasNext()) {
            String key = it.next();
            String value = params.getString(key);
            paramsMap.put(key, value);
        }
        List<LogBrowser> list = logBrowserRepositoryCustom.findAll(paramsMap);
        if (list != null && list.size() > 0) {
            for (LogBrowser lb : list) {
                if (map.containsKey(lb.getBrwName())) {
                    LogBrowser logBrowser = (LogBrowser) map.get(lb.getBrwName());
                    lb.setBrwNum(logBrowser.getBrwNum() + lb.getBrwNum());
                    map.put(lb.getBrwName(), lb);
                } else {
                    map.put(lb.getBrwName(), lb);
                }
            }
        }

        return map;
    }
}
