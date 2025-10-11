package io.polaris.mybatis.mapper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.polaris.core.annotation.Internal;
import io.polaris.core.io.IO;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.mybatis.annotation.DynamicResultMapping;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.EntityExistsByAnyProvider;
import io.polaris.mybatis.provider.MapperProviders;
import io.polaris.mybatis.provider.SqlExistsProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.cursor.Cursor;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface SelectStatementMapper<R> {


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.existsBySql)
	@Options(fetchSize = 1)
	@Internal("考虑到查询性能与分页实现的兼容性，声明此此方法，实际开发中不直接调用")
	Cursor<Boolean> existsInnerBySql(@Param(BindingKeys.SELECT) SelectStatement<?> statement);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countBySql)
	int countBySql(@Param(BindingKeys.SELECT) SelectStatement<?> statement);


	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySql)
	List<R> selectEntityListBySql(@Param(BindingKeys.SELECT) SelectStatement<?> statement);


	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySql)
	List<Map<String, Object>> selectMapListBySql(@Param(BindingKeys.SELECT) SelectStatement<?> statement);


	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySql)
	R selectEntityBySql(@Param(BindingKeys.SELECT) SelectStatement<?> statement);


	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySql)
	Map<String, Object> selectMapBySql(@Param(BindingKeys.SELECT) SelectStatement<?> statement);

	default boolean existsBySql(SelectStatement<?> statement) {
		SqlExistsProvider.setQueryExistsByCount(false);
		Cursor<Boolean> cursor = null;
		try {
			cursor = existsInnerBySql(statement);
			Iterator<Boolean> iter = cursor.iterator();
			if (iter.hasNext()) {
				Boolean next = iter.next();
				return Boolean.TRUE.equals(next);
			}
			return false;
		} finally {
			EntityExistsByAnyProvider.clearQueryExistsByCount();
			IO.close(cursor);
		}
	}

	default boolean existsBySql(SelectStatement<?> statement, Criteria criteria) {
		if (criteria != null) {
			statement.where(criteria);
		}
		return existsBySql(statement);
	}

	default int countBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return countBySql(statement);
	}

	default List<R> selectEntityListBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return selectEntityListBySql(statement);
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
