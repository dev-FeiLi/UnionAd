package io.union.admin.service;

import io.union.admin.dao.StatHourIpRepositoryCustomImpl;
import io.union.admin.dao.StatHourPVRepositoryCustomImpl;
import io.union.admin.dao.StatHourUVRepositoryCustomImpl;
import io.union.admin.entity.StatHourIp;
import io.union.admin.entity.StatHourPv;
import io.union.admin.entity.StatHourUv;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/7/26.
 */
@Service
public class StatHourReqsService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StatHourIpRepositoryCustomImpl statHourIpRepositoryCustom;
    @Autowired
    private StatHourPVRepositoryCustomImpl statHourPVRepositoryCustom;
    @Autowired
    private StatHourUVRepositoryCustomImpl statHourUVRepositoryCustom;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Map<String, Object> requestData(JSONObject params) throws Exception {
        SortedMap<String, Object> map = new TreeMap<>();
        Iterator it = params.keys();
        Map<String, Object> paramsMap1 = new HashMap<>();
        Map<String, Object> paramsMap2 = new HashMap<>();

        while (it.hasNext()) {
            String key = String.valueOf(it.next());
            String value = (String) params.get(key);
            if ("startDate".equals(key) && StringUtils.hasText(value)) {
                paramsMap1.put("addDate", new SimpleDateFormat("yyyy-MM-dd").parse(value));
                continue;
            } else if ("endDate".equals(key) && StringUtils.hasText(value)) {
                paramsMap2.put("addDate", new SimpleDateFormat("yyyy-MM-dd").parse(value));
                continue;
            }
            paramsMap1.put(key, value);
            paramsMap2.put(key, value);
        }

        // ip
        StatHourIp ip1 = statHourIpRepositoryCustom.findByCustomSearch(paramsMap1);
        StatHourIp ip2 = statHourIpRepositoryCustom.findByCustomSearch(paramsMap2);
        map.put(params.get("startDate") + "ip", getParamShow(ip1));
        map.put(params.get("endDate") + "ip", getParamShow(ip2));
        // pv
        StatHourPv pv1 = statHourPVRepositoryCustom.findByCustomSearch(paramsMap1);
        StatHourPv pv2 = statHourPVRepositoryCustom.findByCustomSearch(paramsMap2);
        map.put(params.get("startDate") + "pv", getParamShow(pv1));
        map.put(params.get("endDate") + "pv", getParamShow(pv2));
        // uv
        StatHourUv uv1 = statHourUVRepositoryCustom.findByCustomSearch(paramsMap1);
        StatHourUv uv2 = statHourUVRepositoryCustom.findByCustomSearch(paramsMap2);
        map.put(params.get("startDate") + "uv", getParamShow(uv1));
        map.put(params.get("endDate") + "uv", getParamShow(uv2));

        map.put("startDate", params.get("startDate"));
        map.put("endDate", params.get("endDate"));
        return map;
    }

    private List<String> getParamShow(Object obj) {
        List<String> data = new ArrayList<>();
        JSONObject json = JSONObject.fromObject(obj);
        json.remove("id");
        json.remove("uid");
        json.remove("siteid");
        json.remove("planid");
        json.remove("addTime");
        Iterator<String> it = json.keys();
        data.add(json.getString("hour0"));
        data.add(json.getString("hour1"));
        data.add(json.getString("hour2"));
        data.add(json.getString("hour3"));
        data.add(json.getString("hour4"));
        data.add(json.getString("hour5"));
        data.add(json.getString("hour6"));
        data.add(json.getString("hour7"));
        data.add(json.getString("hour8"));
        data.add(json.getString("hour9"));
        data.add(json.getString("hour10"));
        data.add(json.getString("hour11"));
        data.add(json.getString("hour12"));
        data.add(json.getString("hour13"));
        data.add(json.getString("hour14"));
        data.add(json.getString("hour15"));
        data.add(json.getString("hour16"));
        data.add(json.getString("hour17"));
        data.add(json.getString("hour18"));
        data.add(json.getString("hour19"));
        data.add(json.getString("hour20"));
        data.add(json.getString("hour21"));
        data.add(json.getString("hour22"));
        data.add(json.getString("hour23"));
        return data;
    }

}
