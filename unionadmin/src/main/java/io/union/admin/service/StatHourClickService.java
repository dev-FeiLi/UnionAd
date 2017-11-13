package io.union.admin.service;


import io.union.admin.dao.StatHourClickIpRepositoryCustomImpl;
import io.union.admin.dao.StatHourClickRepositoryCustomImpl;
import io.union.admin.dao.StatHourClickUvRepositoryCustomImpl;
import io.union.admin.entity.StatHourClick;
import io.union.admin.entity.StatHourClickip;
import io.union.admin.entity.StatHourClickuv;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/7/29.
 */
@Service
public class StatHourClickService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StatHourClickRepositoryCustomImpl statHourClickRepositoryCustom;
    @Autowired
    private StatHourClickIpRepositoryCustomImpl statHourClickIpRepositoryCustom;
    @Autowired
    private StatHourClickUvRepositoryCustomImpl statHourClickUvRepositoryCustom;


    public Map<String, Object> clickData(JSONObject params) throws Exception {
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

        // click
        StatHourClick click1 = statHourClickRepositoryCustom.findByCustomSearch(paramsMap1);
        StatHourClick click2 = statHourClickRepositoryCustom.findByCustomSearch(paramsMap2);
        map.put(params.get("startDate") + "点击", getParamShow(click1));
        map.put(params.get("endDate") + "点击", getParamShow(click2));
        // clickip
        StatHourClickip clickIp1 = statHourClickIpRepositoryCustom.findByCustomSearch(paramsMap1);
        StatHourClickip clickIp2 = statHourClickIpRepositoryCustom.findByCustomSearch(paramsMap2);
        map.put(params.get("startDate") + "排重ip", getParamShow(clickIp1));
        map.put(params.get("endDate") + "排重ip", getParamShow(clickIp2));
        // clickuv
        StatHourClickuv clickuv1 = statHourClickUvRepositoryCustom.findByCustomSearch(paramsMap1);
        StatHourClickuv clickuv2 = statHourClickUvRepositoryCustom.findByCustomSearch(paramsMap2);
        map.put(params.get("startDate") + "排重uv", getParamShow(clickuv1));
        map.put(params.get("endDate") + "排重uv", getParamShow(clickuv2));

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
