package io.polaris.mybatis.mapper;

import java.util.Map;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.mybatis.annotation.DynamicResultMapping;
import io.polaris.mybatis.annotation.SupportLogicDeleted;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.cursor.Cursor;

/**
 * SelectStatementCursorDirectMapper 是一个 MyBatis 映射器接口，用于直接执行 SQL 查询并返回游标结果。
 * 该接口支持实体对象和 Map 类型的结果映射，并提供了灵活的查询条件和排序功能。
 *
 * @param <R> 实体对象的类型参数
 * @author Qt
 * @since Aug 25, 2023
 */
public interface SelectStatementCursorDirectMapper<R> {

	/**
	 * 根据提供的 SQL 语句直接查询实体对象，并返回游标结果。
	 * 该方法支持逻辑删除过滤和动态结果映射。
	 *
	 * @param statement 包含查询条件的 SelectStatement 对象
	 * @return 返回实体对象的游标结果
	 */
	@SupportLogicDeleted(false)
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySqlDirect)
	@Options(fetchSize = 1000)
	Cursor<R> directSelectEntityCursorBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

	/**
	 * 根据提供的 SQL 语句直接查询数据，并返回 Map 类型的游标结果。
	 * 该方法支持逻辑删除过滤和动态结果映射。
	 *
	 * @param statement 包含查询条件的 SelectStatement 对象
	 * @return 返回 Map 类型的游标结果，键为列名，值为对应的值
	 */
	@SupportLogicDeleted(false)
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySqlDirect)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> directSelectMapCursorBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

	/**
	 * 根据提供的查询条件和排序规则，查询实体对象并返回游标结果。
	 * 该方法允许动态添加查询条件和排序规则。
	 *
	 * @param statement 包含基础查询条件的 SelectStatement 对象
	 * @param criteria 查询条件对象，可为空
	 * @param orderBy 排序规则对象，可为空
	 * @return 返回实体对象的游标结果
	 */
	default Cursor<R> directSelectEntityCursorBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return directSelectEntityCursorBySql(statement);
	}

	/**
	 * 根据提供的查询条件和排序规则，查询数据并返回 Map 类型的游标结果。
	 * 该方法允许动态添加查询条件和排序规则。
	 *
	 * @param statement 包含基础查询条件的 SelectStatement 对象
	 * @param criteria 查询条件对象，可为空
	 * @param orderBy 排序规则对象，可为空
	 * @return 返回 Map 类型的游标结果，键为列名，值为对应的值
	 */
	default Cursor<Map<String, Object>> directSelectMapCursorBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return directSelectMapCursorBySql(statement);
	}

}
