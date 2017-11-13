package io.union.wap.dao;

import io.union.wap.model.AdvDataQuery;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface UnionStatsRepositoryCustom<T, ID extends Serializable> {

    List<T> findAll(AdvDataQuery advDataQuery);
}
