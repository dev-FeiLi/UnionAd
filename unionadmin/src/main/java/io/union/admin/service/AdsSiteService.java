package io.union.admin.service;

import io.union.admin.common.redis.CacheAdsSite;
import io.union.admin.dao.AdsClassRepository;
import io.union.admin.dao.AdsSiteRepository;
import io.union.admin.dao.AdsUserRepository;
import io.union.admin.entity.AdsClass;
import io.union.admin.entity.AdsSite;
import io.union.admin.entity.AdsUser;
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

import java.util.List;

@Service
public class AdsSiteService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdsSiteRepository adsSiteRepository;
    @Autowired
    private AdsUserRepository adsUserRepository;
    @Autowired
    private AdsClassRepository adsClassRepository;
    @Autowired
    private AdsClassService adsClassService;
    @Autowired
    private AdsUserService adsUserService;
    @Autowired
    private CacheAdsSite cacheAdsSite;

    /**
     * 根据id条件查询出站长域名表中的信息
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsSite findOne(Long id) {
        try {
            return adsSiteRepository.findOne(id);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    /**
     * 根据id添加列表查询出所有站长域名表
     *
     * @param uid
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsSite> findByUid(Long uid) {
        try {
            return adsSiteRepository.findAllByUidEquals(uid);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    /**
     * 根据网址查询是否含有该网址的信息
     *
     * @param siteurl
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsSite findBySiteurl(String siteurl) {
        try {
            return adsSiteRepository.findBySiteurl(siteurl);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    /**
     * 添加数据
     *
     * @param site
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public long saveOne(AdsSite site) {
        long siteid = 0;
        try {
            if (adsSiteRepository.save(site) != null) {
                cacheAdsSite.save(site);
            }
            siteid = site.getSiteid();
        } catch (Exception e) {
            logger.error("save AdsPlan error: ", e);
        }
        return siteid;
    }

    /**
     * 查询全部的信息（站长域名表）
     *
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsSite> findAll() {
        try {
            return adsSiteRepository.findAll();
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    /**
     * 分页查询出全部的信息
     *
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<AdsSite> findAll(int page, int size) {
        Page<AdsSite> list = null;
        try {
            if (size == 0) size = 20;
            Sort sort = new Sort(Sort.Direction.DESC, "siteid");
            Pageable pageable = new PageRequest(page, size, sort);
            list = adsSiteRepository.findAll(pageable);
        } catch (Exception e) {
            logger.error("findAll with page error: ", e);
        } finally {
            return list;
        }
    }

    /**
     * 根据id条件分页查询出网站信息
     *
     * @param id
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<AdsSite> findAll(Integer id, Integer sstatus, int page, int size) {
        Page<AdsSite> list = null;
        try {
            if (size == 0) size = 20;
            Sort sort = new Sort(Sort.Direction.DESC, "siteid");
            Pageable pageable = new PageRequest(page, size, sort);
            list = adsSiteRepository.findBySitetypeAndSstatusNot(id, sstatus, pageable);
        } catch (Exception e) {
            logger.error("findAll with page error: ", e);
        } finally {
            return list;
        }
    }

    /**
     * 根据uid模糊查询出所有相关的信息
     *
     * @param id
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<AdsSite> findByUidContaining(Long id, int page, int size) {
        Page<AdsSite> list = null;
        try {
            if (size == 0) size = 20;
            Sort sort = new Sort(Sort.Direction.DESC, "siteid");
            Pageable pageable = new PageRequest(page, size, sort);
            list = adsSiteRepository.findByUid(id, pageable);
        } catch (Exception e) {
            logger.error("findByUidContaining with page error: ", e);
        } finally {
            return list;
        }
    }

    /**
     * 根据状态查询出包含该状态的信息
     *
     * @param sstatus
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public JSONObject findSstatus(Integer sstatus, int page, int size) {
        JSONObject result = new JSONObject();
        Page<AdsSite> list = null;
        try {
            if (size == 0) size = 50;
            Sort sort = new Sort(Sort.Direction.ASC, "siteid");
            Pageable pageable = new PageRequest(page, size, sort);
            list = adsSiteRepository.findBySstatus(sstatus, pageable);
            // 准备前台显示数据
            result.put("total", list.getTotalElements());
            result.put("pageNumber", list.getSize());
            result.put("rows", prepareAdsDataRow(list.getContent()));
        } catch (Exception e) {
            logger.error("findSstatus error: ", e);
        } finally {
            return result;
        }
    }

    /**
     * 初始化站长域名列表数据
     *
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public JSONObject getAllAdsSiteByPageSize(int page, int size, Integer sstatus) {
        JSONObject result = new JSONObject();
        Page<AdsSite> list = null;
        try {
            // 获得站长域名列表
            if (size == 0) size = 20;
            Sort sort = new Sort(Sort.Direction.DESC, "siteid");
            Pageable pageable = new PageRequest(page, size, sort);
            list = adsSiteRepository.findAllBySstatusNot(sstatus, pageable);
            // 准备前台显示数据
            result.put("total", list.getTotalElements());
            result.put("pageNumber", list.getSize());
            result.put("rows", prepareAdsDataRow(list.getContent()));
        } catch (Exception e) {
            logger.error("getAllAdByPageSize with page error: ", e);
        } finally {
            return result;
        }
    }

    /**
     * 根据条件查询出网站信息
     *
     * @param username
     * @param searchCondition
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public JSONObject findAdsSiteByCondition(String sitetype, String username, Integer sstatus, String searchCondition, int page, int size) {
        JSONObject result = new JSONObject();
        Page<AdsSite> list = null;
        try {
            if (size == 0) size = 20;
            Sort sort = new Sort(Sort.Direction.DESC, "siteid");
            Pageable pageable = new PageRequest(page, size, sort);
            switch (searchCondition) {
                case "siteurl":
                    if (sitetype.equals("0")) {
                        list = adsSiteRepository.findBySiteurlContainingAndSstatusNot(username, Integer.valueOf(sstatus), pageable);
                        result.put("total", list.getTotalElements());
                        result.put("pageNumber", list.getSize());
                        result.put("rows", prepareAdsDataRow(list.getContent()));
                    } else {
                        list = adsSiteRepository.findBySitetypeAndSiteurlContainingAndSstatusNot(Integer.valueOf(sitetype), username, Integer.valueOf(sstatus), pageable);
                        result.put("total", list.getTotalElements());
                        result.put("pageNumber", list.getSize());
                        result.put("rows", prepareAdsDataRow(list.getContent()));
                    }
                    break;
                case "username":
                    List<AdsUser> adsUserList = adsUserRepository.findByUsernameContaining(username);
                    if (adsUserList.size() > 0) {
                        for (AdsUser adsUser : adsUserList) {
                            if (sitetype.equals("0")) {
                                list = adsSiteRepository.findByUidAndSstatusNot(adsUser.getUid(), Integer.valueOf(sstatus), pageable);
                                result.put("total", list.getTotalElements());
                                result.put("pageNumber", list.getSize());
                                result.put("rows", prepareAdsDataRow(list.getContent()));
                            } else {
                                list = adsSiteRepository.findBySitetypeAndUidAndSstatusNot(Integer.valueOf(sitetype), adsUser.getUid(), Integer.valueOf(sstatus), pageable);
                                result.put("total", list.getTotalElements());
                                result.put("pageNumber", list.getSize());
                                result.put("rows", prepareAdsDataRow(list.getContent()));
                            }
                        }
                    } else {
                        result.put("total", 0);
                        result.put("pageNumber", 0);
                        result.put("rows", prepareAdsDataRow(null));
                    }
                    break;
                case "sitename":
                    if (sitetype.equals("0")) {
                        list = adsSiteRepository.findBySitenameContainingAndSstatusNot(username, Integer.valueOf(sstatus), pageable);
                        result.put("total", list.getTotalElements());
                        result.put("pageNumber", list.getSize());
                        result.put("rows", prepareAdsDataRow(list.getContent()));
                    } else {
                        list = adsSiteRepository.findBySitetypeAndSitenameContainingAndSstatusNot(Integer.valueOf(sitetype), username, Integer.valueOf(sstatus), pageable);
                        result.put("total", list.getTotalElements());
                        result.put("pageNumber", list.getSize());
                        result.put("rows", prepareAdsDataRow(list.getContent()));
                    }
                    break;
                case "sitetype":
                    AdsClass adsClass = adsClassRepository.findOne(Integer.valueOf(username));
                    if (adsClass != null) {
                        list = adsSiteRepository.findBySitetypeAndSstatusNot(adsClass.getId(), Integer.valueOf(sstatus), pageable);
                        result.put("total", list.getTotalElements());
                        result.put("pageNumber", list.getSize());
                        result.put("rows", prepareAdsDataRow(list.getContent()));
                    }
                    break;
                case "uid":
                    if (sitetype.equals("0")) {
                        list = adsSiteRepository.findByUidAndSstatusNot(Long.valueOf(username), Integer.valueOf(sstatus), pageable);
                        result.put("total", list.getTotalElements());
                        result.put("pageNumber", list.getSize());
                        result.put("rows", prepareAdsDataRow(list.getContent()));
                    } else {
                        list = adsSiteRepository.findBySitetypeAndUidAndSstatusNot(Integer.valueOf(sitetype), Long.valueOf(username), Integer.valueOf(sstatus), pageable);
                        result.put("total", list.getTotalElements());
                        result.put("pageNumber", list.getSize());
                        result.put("rows", prepareAdsDataRow(list.getContent()));
                    }
                default:
                    break;
            }
        } catch (Exception e) {
            logger.error("findAdsSiteByCondition error", e.getMessage());
        } finally {
            return result;
        }
    }

    /**
     * 拼接出 前台datatable 需要的json对象。
     *
     * @param adsSiteList
     * @return
     */
    private JSONArray prepareAdsDataRow(List<AdsSite> adsSiteList) {
        JSONArray dataRow = new JSONArray();
        if (adsSiteList != null && adsSiteList.size() > 0) {
            for (AdsSite adsSite : adsSiteList) {
                JSONObject data = new JSONObject();
                AdsClass adsClass = adsClassService.findOne(adsSite.getSitetype());
                AdsUser adsUser = adsUserService.findOne(adsSite.getUid());
                data = JSONObject.fromObject(adsSite);
                if (adsClass != null) {
                    data.put("classname", adsClass.getClassname());
                }
                if (adsUser != null) {
                    data.put("username", adsUser.getUsername());
                }
                dataRow.add(data);
            }
        }
        return dataRow;
    }
}
