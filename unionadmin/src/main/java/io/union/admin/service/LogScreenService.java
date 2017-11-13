package io.union.admin.service;

import com.google.common.collect.Maps;
import io.union.admin.dao.LogScreenRepository;
import io.union.admin.dao.LogScreenRepositoryCustomImpl;
import io.union.admin.entity.LogScreen;
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
public class LogScreenService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LogScreenRepository logScreenRepository;
    @Autowired
    private LogScreenRepositoryCustomImpl logScreenRepositoryCustom;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public LogScreen findOne(String cusScreen, Long uid, String addtime) {
        try {
            return logScreenRepository.findByScreenInfo(cusScreen, uid, addtime);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Map<String, Object> searchData(JSONObject params) {
        Iterator<String> it = params.keys();
        Map<String, String> paramsMap = new HashMap<>();
        while (it.hasNext()) {
            String key = it.next();
            String value = params.getString(key);
            paramsMap.put(key, value);
        }

        List<LogScreen> list = logScreenRepositoryCustom.findAll(paramsMap);
        Map<String, Object> map = Maps.newLinkedHashMap();
        if (list != null && list.size() > 0) {
            for (LogScreen ls : list) {
                if (map.containsKey(ls.getCusScreen())) {
                    LogScreen logScreen = (LogScreen) map.get(ls.getCusScreen());
                    ls.setCusNum(logScreen.getCusNum() + ls.getCusNum());
                    map.put(ls.getCusScreen(), ls);
                } else {
                    map.put(ls.getCusScreen(), ls);
                }
            }
        }

        return map;
    }
}
