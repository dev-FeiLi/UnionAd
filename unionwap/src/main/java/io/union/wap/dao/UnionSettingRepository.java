package io.union.wap.dao;

import io.union.wap.entity.UnionSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/8/2.
 */
@Repository
public interface UnionSettingRepository extends JpaRepository<UnionSetting, String> {
}
