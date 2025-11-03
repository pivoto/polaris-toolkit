package io.polaris.mybatis.mapper;

import java.util.Map;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.mybatis.annotation.DynamicResultMapping;
import io.polaris.mybatis.annotation.WithLogicDeleted;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.cursor.Cursor;

/**
 * @author Qt
 * @since  Aug 25, 2023
 */
public interface SelectStatementCursorDefaultMapper<R> {


	@WithLogicDeleted(true)
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySql)
	@Options(fetchSize = 1000)
	Cursor<R> selectEntityCursorBySql(@Param(BindingKeys.SELECT) SelectStatement<?> statement);


	@WithLogicDeleted(true)
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySql)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursorBySql(@Param(BindingKeys.SELECT) SelectStatement<?> statement);


	default Cursor<R> selectEntityCursorBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return selectEntityCursorBySql(statement);
	}

	default Cursor<Map<String, Object>> selectMapCursorBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return selectMapCursorBySql(statement);
	}


}
