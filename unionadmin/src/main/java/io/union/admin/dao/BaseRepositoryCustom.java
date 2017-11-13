package io.union.admin.dao;

import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Created by Shell Li on 2017/11/1.
 */
@NoRepositoryBean
public interface BaseRepositoryCustom<T, ID extends Serializable> {
}
