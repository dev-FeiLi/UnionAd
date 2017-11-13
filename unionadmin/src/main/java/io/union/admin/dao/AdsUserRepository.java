package io.union.admin.dao;

import io.union.admin.entity.AdsUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/8/1.
 */
@Repository
public interface AdsUserRepository extends JpaRepository<AdsUser, Long>, JpaSpecificationExecutor<AdsUser> {

    //根据用户名称查找该用户
    AdsUser findAllByUsernameEquals(String username);

    //根据用户类型的字段列表查询用户信息
    List<AdsUser> findAllByUtypeEqualsAndUstatusNotOrderByUid(Integer utype, Integer ustatus);

    //带条件的分页查询
    Page<AdsUser> findByUtype(Integer utype, Pageable pageable);

    //查询出状态不带8的用户信息
    Page<AdsUser> findAllByUtypeAndUstatusNot(Integer utype, Integer ustatus, Pageable pageable);

    /**
     * 根据站长状态和用户类型分页查询用户信息
     *
     * @param ustatus
     * @param utype
     * @param pageable
     * @return
     */
    Page<AdsUser> findByUstatusAndUtype(Integer ustatus, Integer utype, Pageable pageable);

    /**
     * 根据用户类型和用户名称模糊查询用户的信息
     *
     * @param utype
     * @param username
     * @param pageable
     * @return
     */
    Page<AdsUser> findAllByUtypeAndUsernameContainingAndUstatusNot(Integer utype, String username, Integer ustatus, Pageable pageable);

    /**
     * 根据用户类型和qq模糊查询用户信息
     *
     * @param utype
     * @param qq
     * @param pageable
     * @return
     */
    Page<AdsUser> findAllByUtypeAndQqContainingAndUstatusNot(Integer utype, String qq, Integer ustatus, Pageable pageable);

    /**
     * 根据用户类型和专属客服ID查询
     *
     * @param utype
     * @param serviceid
     * @param ustatus
     * @param pageable
     * @return
     */
    Page<AdsUser> findAllByUtypeAndServiceidAndUstatusNot(Integer utype, Long serviceid, Integer ustatus, Pageable pageable);

    Page<AdsUser> findAllByUtypeAndUidAndUstatusNot(Integer utype, Long uid, Integer ustatus, Pageable pageable);

    /**
     * 根据用户类型和手机号模糊查询用户的信息
     *
     * @param utype
     * @param mobile
     * @param pageable
     * @return
     */
    Page<AdsUser> findAllByUtypeAndMobileContainingAndUstatusNot(Integer utype, String mobile, Integer ustatus, Pageable pageable);

    /**
     * 根据用户名模糊查询出所有用户信息
     *
     * @param username
     * @return
     */
    List<AdsUser> findByUsernameContaining(String username);

    /**
     * 根据用户id和用户类型查询是否有记录数
     *
     * @param utype
     * @param uid
     * @return
     */
    AdsUser findByUtypeAndUidAndUstatusNot(Integer utype, Long uid, Integer ustatus);
}