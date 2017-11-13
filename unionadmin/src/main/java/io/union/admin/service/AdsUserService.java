package io.union.admin.service;

import io.union.admin.dao.AdsUserRepository;
import io.union.admin.entity.AdsUser;
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

/**
 * Created by chenmiaoyun on 2017/8/1.
 */
@Service
public class AdsUserService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdsUserRepository adsUserRepository;

    //根据用户的id查询改该用户的信息
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsUser findOne(Long uid) {
        try {
            return adsUserRepository.findOne(uid);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    //根据用户名查询该用户信息
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsUser findByUsername(String username) {
        try {
            return adsUserRepository.findAllByUsernameEquals(username);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public List<AdsUser> save(List<AdsUser> adsUserList) {
        try {
            return adsUserRepository.save(adsUserList);
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return null;
    }

    /**
     * 根据用户类型列表查询出所有的用户信息
     *
     * @param utype
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsUser> findAllByUtype(Integer utype) {
        try {
            return adsUserRepository.findAllByUtypeEqualsAndUstatusNotOrderByUid(utype, 8);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    /**
     * 添加用户的信息或者修改用户的信息
     *
     * @param user
     * @return
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Long saveOne(AdsUser user) {
        Long id = null;
        try {
            adsUserRepository.save(user);
            id = user.getUid();
        } catch (Exception e) {
            logger.error("save error: ", e);
        }
        return id;
    }

    /**
     * 查询全部的信息
     *
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsUser> findAll() {
        try {
            return adsUserRepository.findAll();
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;
    }

    /**
     * 分页查询出全部的信息(带条件)
     *
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<AdsUser> findAll(int utype, int ustatus, int page, int size) {
        Page<AdsUser> list = null;
        try {
            if (size == 0) size = 50;
            Sort sort = new Sort(Sort.Direction.DESC, "uid");
            Pageable pageable = new PageRequest(page, size, sort);
            list = adsUserRepository.findAllByUtypeAndUstatusNot(utype, ustatus, pageable);
        } catch (Exception e) {
            logger.error("findAll error: ", e);
        }
        return list;
    }

    /**
     * 根据用户类型为客服来 获取所有的客服
     *
     * @param utype
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsUser> getAllUserName(Integer utype) {
        logger.debug("开始查询全部客服业务");
        try {
            return adsUserRepository.findAllByUtypeEqualsAndUstatusNotOrderByUid(utype, 8);
        } catch (Exception e) {
            logger.error("find error: ", e);
        }
        return null;

    }

    /**
     * 根据用户类型和状态条件去列表查询出站长信息
     *
     * @param ustatus
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<AdsUser> findUustatus(int ustatus, int utype, int page, int size) {
        Page<AdsUser> list = null;
        try {
            if (size == 0) size = 50;
            Sort sort = new Sort(Sort.Direction.DESC, "uid");
            Pageable pageable = new PageRequest(page, size, sort);
            list = adsUserRepository.findByUstatusAndUtype(ustatus, utype, pageable);
        } catch (Exception e) {
            logger.error("findAll error: ", e);
        }
        return list;
    }

    /**
     * 根据条件查询出所有的用户信息
     *
     * @param utype
     * @param value
     * @param searchCondition
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public Page<AdsUser> findAdsUserByCondition(int utype, String value, Integer ustatus, String searchCondition, int page, int size) {
        Page<AdsUser> list = null;
        try {
            if (size == 0) size = 20;
            Sort sort = new Sort(Sort.Direction.DESC, "uid");
            Pageable pageable = new PageRequest(page, size, sort);
            switch (searchCondition) {
                case "username":
                    list = adsUserRepository.findAllByUtypeAndUsernameContainingAndUstatusNot(utype, value, ustatus, pageable);
                    break;
                case "mobile":
                    list = adsUserRepository.findAllByUtypeAndMobileContainingAndUstatusNot(utype, value, ustatus, pageable);
                    break;
                case "qq":
                    list = adsUserRepository.findAllByUtypeAndQqContainingAndUstatusNot(utype, value, ustatus, pageable);
                    break;
                case "serviceid":
                    list = adsUserRepository.findAllByUtypeAndServiceidAndUstatusNot(utype, Long.parseLong(value), ustatus, pageable);
                    break;
                case "uid":
                    list = adsUserRepository.findAllByUtypeAndUidAndUstatusNot(utype, Long.valueOf(value), ustatus, pageable);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.error("AdsUserService findUsersByCondition error", e.getMessage());
        } finally {
            return list;
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsUser> findByUsernameContaining(String username) {
        List<AdsUser> list = null;
        try {
            Sort sort = new Sort(Sort.Direction.ASC, "uid");
            list = adsUserRepository.findByUsernameContaining(username);
        } catch (Exception e) {
            logger.error("findByUsernameContaining error: ", e);
        }
        return list;
    }

    /**
     * 根据用户id和用户类型查询是否有记录数
     *
     * @param utype
     * @param uid
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsUser findByUtypeAndUid(Integer utype, Long uid, Integer ustatus) {
        AdsUser list = null;
        try {
            Sort sort = new Sort(Sort.Direction.ASC, "uid");
            list = adsUserRepository.findByUtypeAndUidAndUstatusNot(utype, uid, ustatus);
        } catch (Exception e) {
            logger.error("findByUtypeAndUid error: ", e);
        }
        return list;
    }

    public Map<Long, AdsUser> findMap() {
        try {
            Page<AdsUser> list = adsUserRepository.findByUstatusAndUtype(1, 1, new PageRequest(0, Integer.MAX_VALUE));
            Map<Long, AdsUser> map = new HashMap<>();
            if (null != list && list.getContent().size() > 0) {
                list.forEach(item -> map.put(item.getUid(), item));
            }
            return map;
        } catch (Exception e) {
            logger.error("find map error: ", e);
        }
        return null;
    }
}
