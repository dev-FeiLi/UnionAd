package io.union.js.dao;

import io.union.js.entity.UnionSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017/7/21.
 */
@Repository
public interface UnionSettingRepository extends JpaRepository<UnionSetting, String> {
}
