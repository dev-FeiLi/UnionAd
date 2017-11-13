package io.union.admin.dao;

import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/28.
 */
@NoRepositoryBean
public interface UnionStatsRepositoryCustom<T, ID extends Serializable> {

    T findByAddTime(Date addTime);
}
