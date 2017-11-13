package io.union.admin.service;


import io.union.admin.dao.AdsZoneRepository;
import io.union.admin.entity.AdsUser;
import io.union.admin.entity.AdsZone;
import net.sf.json.JSONArray;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdsZoneService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdsZoneRepository adsZoneRepository;
    @Autowired
    private AdsUserService adsUserService;

    /**
     * 根据zoneid条件去查询该编号的广告位
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsZone findOne(Long id) {
        try {
            return adsZoneRepository.findOne(id);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    /**
     * 根据uid条件列表查询广告位
     *
     * @param uid
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsZone> findByUid(Long uid) {
        try {
            return adsZoneRepository.findAllByUidEquals(uid);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    /**
     * 添加广告位信息
     *
     * @param zone
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AdsZone saveOne(AdsZone zone) {
        try {
            return adsZoneRepository.save(zone);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    /**
     * 分页查询出全部的信息（广告位表）
     *
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public JSONObject findAll(Integer zstatus, int page, int size) {
        JSONObject result = new JSONObject();
        Page<AdsZone> list = null;
        try {
            if (size == 0) size = 20;
            Sort sort = new Sort(Sort.Direction.DESC, "zoneid");
            Pageable pageable = new PageRequest(page, size, sort);
            list = adsZoneRepository.findAllByAndZstatusNot(zstatus, pageable);
            // 准备前台显示数据
            result.put("total", list.getTotalElements());
            result.put("pageNumber", list.getSize());
            result.put("rows", prepareAdsDataRow(list.getContent()));
        } catch (Exception e) {
            logger.error("findAll with page error: ", e);
        } finally {
            return result;
        }
    }

    /**
     * 查询出所有的广告位信息
     *
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsZone> findAll() {
        try {
            return adsZoneRepository.findAll();
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    /**
     * 根据广告类型条件查询出广告位的信息
     *
     * @param viewtype
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<AdsZone> findByViewtype(Integer viewtype, Integer zstatus, int page, int size) {
        Page<AdsZone> list = null;
        try {
            if (size == 0) size = 20;
            Sort sort = new Sort(Sort.Direction.DESC, "zoneid");
            Pageable pageable = new PageRequest(page, size, sort);
            list = adsZoneRepository.findByViewtypeAndZstatusNot(viewtype, zstatus, pageable);
        } catch (Exception e) {
            logger.error("findByViewtype with page error: ", e);
        } finally {
            return list;
        }
    }

    /**
     * 模糊查询
     *
     * @param username
     * @param searchCondition
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public JSONObject findAdsZoneByCondition(String viewtype, String username, Integer zstatus, String searchCondition, int page, int size) {
        JSONObject result = new JSONObject();
        Page<AdsZone> list = null;
        try {
            if (size == 0) size = 20;
            Sort sort = new Sort(Sort.Direction.DESC, "zoneid");
            Pageable pageable = new PageRequest(page, size, sort);
            switch (searchCondition) {
                case "viewtype":
                    list = adsZoneRepository.findByViewtypeAndZstatusNot(Integer.valueOf(username), zstatus, pageable);
                    break;
                case "zonename":
                    if (viewtype.equals("0")) {
                        list = adsZoneRepository.findByZonenameContainingAndZstatusNot(username, zstatus, pageable);
                    } else {
                        list = adsZoneRepository.findByViewtypeAndZonenameContainingAndZstatusNot(Integer.valueOf(viewtype), username, zstatus, pageable);
                    }
                    break;
                case "zoneid":
                    if (viewtype.equals("0")) {
                        list = adsZoneRepository.findByZoneidAndZstatusNot(Long.valueOf(username), zstatus, pageable);
                    } else {
                        list = adsZoneRepository.findByViewtypeAndZoneidAndZstatusNot(Integer.valueOf(viewtype), Long.valueOf(username), zstatus, pageable);
                    }
                    break;
                case "uid":
                    if (viewtype.equals("0")) {
                        list = adsZoneRepository.findByUidAndZstatusNot(Long.valueOf(username), zstatus, pageable);
                    } else {
                        list = adsZoneRepository.findByViewtypeAndUidAndZstatusNot(Integer.valueOf(viewtype), Long.valueOf(username), zstatus, pageable);
                    }
                    break;
                default:
                    break;
            }
            // 准备前台显示数据
            result.put("total", list.getTotalElements());
            result.put("pageNumber", list.getSize());
            result.put("rows", prepareAdsDataRow(list.getContent()));
        } catch (Exception e) {
            logger.error("AdsZoneList error", e.getMessage());
        }
        return result;
    }

    /**
     * 根据状态去查询广告位数据
     *
     * @param zstatus
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public JSONObject findByZstatus(Integer zstatus, int page, int size) {
        JSONObject result = new JSONObject();
        Page<AdsZone> list = null;
        try {
            if (size == 0) size = 50;
            Sort sort = new Sort(Sort.Direction.ASC, "zoneid");
            Pageable pageable = new PageRequest(page, size, sort);
            list = adsZoneRepository.findByZstatus(zstatus, pageable);
            result.put("total", list.getTotalElements());
            result.put("pageNumber", list.getSize());
            result.put("rows", prepareAdsDataRow(list.getContent()));
        } catch (Exception e) {
            logger.error("findSstatus error: ", e);
        }
        return result;
    }

    /**
     * 根据计费模式去查询广告位数据
     *
     * @param type
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public JSONObject findByPaytype(String type, Integer zstatus, int page, int size) {
        JSONObject result = new JSONObject();
        Page<AdsZone> list = null;
        try {
            if (size == 0) size = 50;
            Sort sort = new Sort(Sort.Direction.ASC, "zoneid");
            int paytype = getPayTypeByParams(type);
            Pageable pageable = new PageRequest(page, size, sort);
            list = adsZoneRepository.findByPaytypeAndZstatusNot(paytype, zstatus, pageable);
            result.put("total", list.getTotalElements());
            result.put("pageNumber", list.getSize());
            result.put("rows", prepareAdsDataRow(list.getContent()));
        } catch (Exception e) {
            logger.error("findByPaytype error: ", e);
        }
        return result;
    }

    /**
     * "cpm" --> 1
     * "cpv" --> 2
     * "cpc" --> 3
     * "cpa" --> 4
     * "cps" --> 5
     *
     * @param type
     * @return
     */
    private int getPayTypeByParams(String type) {
        int payType = 0;
        switch (type) {
            case "cpm":
                payType = 1;
                break;
            case "cpv":
                payType = 2;
                break;
            case "cpc":
                payType = 3;
                break;
            case "cpa":
                payType = 4;
                break;
            case "cps":
                payType = 5;
                break;
            default:
                break;
        }
        return payType;
    }

    /**
     * 拼接出 前台datatable 需要的json对象。
     *
     * @return
     */
    private JSONArray prepareAdsDataRow(List<AdsZone> adsZoneList) {
        JSONArray dataRow = new JSONArray();
        if (adsZoneList != null && adsZoneList.size() > 0) {
            for (AdsZone adsZone : adsZoneList) {
                JSONObject data = new JSONObject();
                AdsUser adsUser = adsUserService.findOne(adsZone.getUid());
                data = JSONObject.fromObject(adsZone);
                if (adsUser != null) {
                    data.put("username", adsUser.getUsername());
                }
                dataRow.add(data);
            }
        }
        return dataRow;
    }

    public Map<Long, AdsZone> findMap() {
        try {
            Page<AdsZone> list = adsZoneRepository.findByZstatus(0, new PageRequest(0, Integer.MAX_VALUE));
            Map<Long, AdsZone> map = new HashMap<>();
            if (null != list && list.getContent().size() > 0) {
                list.forEach(item -> map.put(item.getZoneid(), item));
            }
            return map;
        } catch (Exception e) {
            logger.error("find map error: ", e);
        }
        return null;
    }
}
