package io.union.admin.service;

import io.union.admin.dao.AdsClassRepository;
import io.union.admin.entity.AdsClass;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdsClassService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdsClassRepository adsClassRepository;

    /**
     * 根据id条件查询网站分类的信息
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsClass findOne(Integer id) {
        try {
            return adsClassRepository.findOne(id);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    /**
     * 列表查询出所有的网站分类信息
     *
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsClass> findAll() {
        try {
            return adsClassRepository.findAll();
        } catch (Exception e) {
            logger.error("find all error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<AdsClass> findAll(int page, int size) {
        Page<AdsClass> list = null;
        if (size == 0) size = 20;
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        list = adsClassRepository.findAll(pageable);

        return list;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Map<String, Object> saveAdsClass(JSONObject model) {
        Map<String, Object> map = new HashMap<>();
        AdsClass adsClass = null;
        if (!StringUtils.hasText(model.getString("classname"))) {
            map.put("error", "分类名称不能为空");
            return map;
        } else if (model.getString("classname").length() > 10) {
            map.put("error", "分类名称字段太长");
            return map;
        }
        if (!StringUtils.hasText(model.getString("cstatus"))) {
            map.put("error", "请选择状态");
            return map;
        }
        if (StringUtils.hasText(model.getString("id"))) {
            adsClass = (AdsClass) JSONObject.toBean(model, AdsClass.class);
        } else {
            adsClass = new AdsClass();
            adsClass.setClassname(model.getString("classname"));
            adsClass.setCstatus(model.getString("cstatus"));
        }
        try {
            adsClassRepository.save(adsClass);
        } catch (Exception e) {
            logger.error("saveAdsClass", e);
            map.put("error", e.getMessage());
        }
        return map;
    }

}
