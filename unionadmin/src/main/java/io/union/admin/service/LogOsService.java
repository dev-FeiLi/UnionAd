package io.union.admin.service;

import io.union.admin.dao.LogOSRepositoryCustomImpl;
import io.union.admin.dao.LogOsRepository;
import io.union.admin.entity.LogOs;
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
public class LogOsService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LogOsRepository logOsRepository;
    @Autowired
    private LogOSRepositoryCustomImpl logOSRepositoryCustom;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public LogOs findOne(String cusOs, Long uid, String addtime) {
        try {
            return logOsRepository.findByOsInfo(cusOs, uid, addtime);
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
        List<LogOs> list = logOSRepositoryCustom.findAll(paramsMap);
        if (list != null && list.size() > 0) {
            for (LogOs lo : list) {
                if (map.containsKey(lo.getCusOs())) {
                    LogOs logOs = (LogOs) map.get(lo.getCusOs());
                    lo.setCusNum(logOs.getCusNum() + lo.getCusNum());
                    map.put(lo.getCusOs(), lo);
                } else {
                    map.put(lo.getCusOs(), lo);
                }
            }
        }

        return map;
    }

}
