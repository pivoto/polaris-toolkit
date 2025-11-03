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
import io.polaris.mybatis.annotation.WithLogicDeleted;
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
public interface SelectStatementDirectMapper<R> {


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.existsBySqlDirect)
	@Options(fetchSize = 1)
	@Internal("考虑到查询性能与分页实现的兼容性，声明此此方法，实际开发中不直接调用")
	Cursor<Boolean> directExistsInnerBySql(@Param(BindingKeys.SELECT) SelectStatement<?> statement);

	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countBySqlDirect)
	int directCountBySql(@Param(BindingKeys.SELECT) SelectStatement<?> statement);


	@WithLogicDeleted(false)
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySqlDirect)
	List<R> directSelectEntityListBySql(@Param(BindingKeys.SELECT) SelectStatement<?> statement);


	@WithLogicDeleted(false)
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySqlDirect)
	List<Map<String, Object>> directSelectMapListBySql(@Param(BindingKeys.SELECT) SelectStatement<?> statement);


	@WithLogicDeleted(false)
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySqlDirect)
	R directSelectEntityBySql(@Param(BindingKeys.SELECT) SelectStatement<?> statement);


	@WithLogicDeleted(false)
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySqlDirect)
	Map<String, Object> directSelectMapBySql(@Param(BindingKeys.SELECT) SelectStatement<?> statement);

	default boolean directExistsBySql(SelectStatement<?> statement) {
		SqlExistsProvider.setQueryExistsByCount(false);
		Cursor<Boolean> cursor = null;
		try {
			cursor = directExistsInnerBySql(statement);
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

	default boolean directExistsBySql(SelectStatement<?> statement, Criteria criteria) {
		if (criteria != null) {
			statement.where(criteria);
		}
		return directExistsBySql(statement);
	}

	default int directCountBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return directCountBySql(statement);
	}

	default List<R> directSelectEntityListBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return directSelectEntityListBySql(statement);
	}

	default R directSelectEntityBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return directSelectEntityBySql(statement);
	}

	default List<Map<String, Object>> directSelectMapListBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return directSelectMapListBySql(statement);
	}

	default Map<String, Object> directSelectMapBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return directSelectMapBySql(statement);
	}


}
