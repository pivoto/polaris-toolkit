package io.polaris.mybatis.mapper;

import java.util.Map;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.mybatis.annotation.AutoResultMapping;
import io.polaris.mybatis.annotation.SupportLogicDeleted;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.cursor.Cursor;

/**
 * 通用的游标查询映射器接口，提供基于SQL语句的游标查询功能。
 *
 * @param <R> 实体类型参数
 * @author Qt
 * @since Aug 25, 2023
 */
public interface SelectStatementCursorDefaultMapper<R> {

	/**
	 * 根据SQL语句查询实体对象的游标结果。
	 *
	 * @param statement SQL查询语句对象
	 * @return 实体对象的游标结果
	 */
	@SupportLogicDeleted(true)
	@AutoResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySql)
	@Options(fetchSize = 1000)
	Cursor<R> selectEntityCursorBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

	/**
	 * 根据SQL语句查询Map结构的游标结果。
	 *
	 * @param statement SQL查询语句对象
	 * @return Map结构的游标结果，键为列名，值为对应的数据
	 */
	@SupportLogicDeleted(true)
	@AutoResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySql)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursorBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

	/**
	 * 根据条件和排序规则查询实体对象的游标结果。
	 *
	 * @param statement SQL查询语句对象
	 * @param criteria 查询条件对象
	 * @param orderBy 排序规则对象
	 * @return 实体对象的游标结果
	 */
	default Cursor<R> selectEntityCursorBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return selectEntityCursorBySql(statement);
	}

	/**
	 * 根据条件和排序规则查询Map结构的游标结果。
	 *
	 * @param statement SQL查询语句对象
	 * @param criteria 查询条件对象
	 * @param orderBy 排序规则对象
	 * @return Map结构的游标结果，键为列名，值为对应的数据
	 */
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
