package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.mybatis.consts.EntityMapperKeys;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Aug 25, 2023
 */
public interface SelectStatementMapper<R> {


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countBySql)
	int countBySql(@Param(EntityMapperKeys.SELECT) SelectStatement<?> statement);


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySql)
	List<R> selectListBySql(@Param(EntityMapperKeys.SELECT) SelectStatement<?> statement);


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySql)
	List<Map<String, Object>> selectMapListBySql(@Param(EntityMapperKeys.SELECT) SelectStatement<?> statement);


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySql)
	R selectEntityBySql(@Param(EntityMapperKeys.SELECT) SelectStatement<?> statement);


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySql)
	Map<String, Object> selectMapBySql(@Param(EntityMapperKeys.SELECT) SelectStatement<?> statement);



	default int countBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return countBySql(statement);
	}

	default List<R> selectListBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return selectListBySql(statement);
	}

	default R selectEntityBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return selectEntityBySql(statement);
	}

	default List<Map<String, Object>> selectMapListBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return selectMapListBySql(statement);
	}

	default Map<String, Object> selectMapBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return selectMapBySql(statement);
	}


}
