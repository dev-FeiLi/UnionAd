package io.union.admin.dao;

import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Shell Li on 2017/9/23.
 */
@NoRepositoryBean
public interface StatHourRepositoryCustom<T, ID extends Serializable> {
    T findByCustomSearch(Map<String, Object> params);
}
