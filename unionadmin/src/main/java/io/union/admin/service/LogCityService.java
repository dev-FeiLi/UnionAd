package io.union.admin.service;

import io.union.admin.dao.LogCityRepository;
import io.union.admin.dao.LogCityRepositoryCustomImpl;
import io.union.admin.entity.LogCity;
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
public class LogCityService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LogCityRepository logCityRepository;
    @Autowired
    private LogCityRepositoryCustomImpl logCityRepositoryCustom;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public LogCity findOne(String cusProvince, String cusIsp, Long uid, String addtime) {
        try {
            return logCityRepository.findByCityInfo(cusProvince, cusIsp, uid, addtime);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Map<String, Object> searchData(JSONObject params) {
        Map<String, Object> provinces = new HashMap<>();
        Iterator<String> it = params.keys();
        Map<String, String> paramsMap = new HashMap<>();
        while (it.hasNext()) {
            String key = it.next();
            String value = params.getString(key);
            paramsMap.put(key, value);
        }

        List<LogCity> list = logCityRepositoryCustom.findAll(paramsMap);
        if (list != null && list.size() > 0) {
            for (LogCity lc : list) {
                if (provinces.containsKey(lc.getCusProvince())) {
                    LogCity logCity = (LogCity) provinces.get(lc.getCusProvince());
                    lc.setCusCity(new StringBuffer(logCity.getCusCity()).append(",").append(lc.getCusCity()).append("(").append(lc.getCusNum()).append(")").toString());
                    lc.setCusNum(logCity.getCusNum() + lc.getCusNum());
                    provinces.put(lc.getCusProvince(), lc);
                } else {
                    lc.setCusCity(new StringBuffer(lc.getCusCity()).append("(").append(lc.getCusNum()).append(")").toString());
                    provinces.put(lc.getCusProvince(), lc);
                }
            }
        }

        return provinces;
    }
}
