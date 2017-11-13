package io.union.admin.service;

import io.union.admin.common.redis.CacheUnionSetting;
import io.union.admin.dao.AdsAppendjsRepository;
import io.union.admin.dao.UnionSettingRepository;
import io.union.admin.entity.AdsAppendjs;
import io.union.admin.entity.UnionSetting;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdsAppendjsService {

    @Autowired
    private AdsAppendjsRepository adsAppendjsRepository;

    @Autowired
    private UnionSettingRepository unionSettingRepository;

    @Autowired
    private CacheUnionSetting cacheUnionSetting;

    public AdsAppendjs find(Long id) {
        return adsAppendjsRepository.findOne(id);
    }

    public AdsAppendjs save(AdsAppendjs appendjs) {
        appendjs = adsAppendjsRepository.save(appendjs);
        JSONArray array = new JSONArray();
        List<AdsAppendjs> list = adsAppendjsRepository.findAll();
        list.forEach(item -> array.add(JSONObject.fromObject(item)));
        UnionSetting setting = unionSettingRepository.findOne("append_js");
        if (null == setting) {
            setting = new UnionSetting();
            setting.setSetkey("append_js");
            setting.setSetMemo("暗刷js，JSON数组");
        }
        setting.setSetvalue(array.toString());
        unionSettingRepository.save(setting);
        cacheUnionSetting.save(setting);
        return appendjs;
    }

    public List<AdsAppendjs> findAll() {
        return adsAppendjsRepository.findAll();
    }

    public void delete(Long id) {
        adsAppendjsRepository.delete(id);
        JSONArray array, result = new JSONArray();
        UnionSetting setting = unionSettingRepository.findOne("append_js");
        String value = cacheUnionSetting.single("append_js");
        if (null != value) {
            array = JSONArray.fromObject(value);
            for (int i = 0; i < array.size(); i++) {
                JSONObject item = array.getJSONObject(i);
                if (!id.equals(Long.valueOf(item.getLong("id")))) {
                    result.add(item);
                }
            }
        }
        setting.setSetvalue(result.toString());
        unionSettingRepository.save(setting);
        cacheUnionSetting.save(setting);
    }
}
