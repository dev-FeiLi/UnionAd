package io.union.admin.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Shell Li on 2017/9/29.
 */
public interface LogRepositoryCustom<T, ID extends Serializable> {
    List<T> findAll(Map<String, String> params);
}
