package io.union.admin.dao;

import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Shell Li on 2017/8/24.
 */
@NoRepositoryBean
public interface AdvpayRepositoryCustom<T, ID extends Serializable> {

    List<T> findAll(Map<String, Object> advpayParams);
}
