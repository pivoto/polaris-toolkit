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
import io.polaris.mybatis.annotation.AutoResultMapping;
import io.polaris.mybatis.annotation.ExistsByCount;
import io.polaris.mybatis.annotation.SupportLogicDeleted;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.cursor.Cursor;

/**
 * SelectStatementDirectMapper 是一个 MyBatis 映射器接口，用于直接执行 SQL 查询操作。
 * 提供了多种查询方法，包括存在性检查、计数、实体列表查询、单个实体查询以及映射查询。
 *
 * @param <R> 实体类型参数
 * @author Qt
 * @since Aug 25, 2023
 */
public interface SelectStatementDirectMapper<R> {

	/**
	 * 检查是否存在满足条件的记录（内部方法，不建议直接调用）。
	 *
	 * @param statement 查询语句对象
	 * @return 返回游标，包含布尔值表示是否存在匹配记录
	 */
	@ExistsByCount(false)
	@SupportLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.existsBySqlDirect)
	@Options(fetchSize = 1)
	@Internal("考虑到查询性能与分页实现的兼容性，声明此此方法，实际开发中不直接调用")
	Cursor<Boolean> directExistsInnerBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

	/**
	 * 统计满足条件的记录数量。
	 *
	 * @param statement 查询语句对象
	 * @return 记录总数
	 */
	@SupportLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countBySqlDirect)
	int directCountBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

	/**
	 * 查询满足条件的实体列表。
	 *
	 * @param statement 查询语句对象
	 * @return 实体列表
	 */
	@SupportLogicDeleted(false)
	@AutoResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySqlDirect)
	List<R> directSelectEntityListBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

	/**
	 * 查询满足条件的映射列表。
	 *
	 * @param statement 查询语句对象
	 * @return 映射列表，键为字段名，值为字段值
	 */
	@SupportLogicDeleted(false)
	@AutoResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySqlDirect)
	List<Map<String, Object>> directSelectMapListBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

	/**
	 * 查询满足条件的单个实体。
	 *
	 * @param statement 查询语句对象
	 * @return 单个实体对象
	 */
	@SupportLogicDeleted(false)
	@AutoResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySqlDirect)
	R directSelectEntityBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

	/**
	 * 查询满足条件的单个映射。
	 *
	 * @param statement 查询语句对象
	 * @return 单个映射，键为字段名，值为字段值
	 */
	@SupportLogicDeleted(false)
	@AutoResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySqlDirect)
	Map<String, Object> directSelectMapBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

	/**
	 * 检查是否存在满足条件的记录。
	 *
	 * @param statement 查询语句对象
	 * @return 是否存在匹配记录
	 */
	default boolean directExistsBySql(SelectStatement<?> statement) {
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
			IO.close(cursor);
		}
	}

	/**
	 * 检查是否存在满足条件的记录（支持条件过滤）。
	 *
	 * @param statement 查询语句对象
	 * @param criteria 过滤条件
	 * @return 是否存在匹配记录
	 */
	default boolean directExistsBySql(SelectStatement<?> statement, Criteria criteria) {
		if (criteria != null) {
			statement.where(criteria);
		}
		return directExistsBySql(statement);
	}

	/**
	 * 统计满足条件的记录数量（支持条件过滤和排序）。
	 *
	 * @param statement 查询语句对象
	 * @param criteria 过滤条件
	 * @param orderBy 排序条件
	 * @return 记录总数
	 */
	default int directCountBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return directCountBySql(statement);
	}

	/**
	 * 查询满足条件的实体列表（支持条件过滤和排序）。
	 *
	 * @param statement 查询语句对象
	 * @param criteria 过滤条件
	 * @param orderBy 排序条件
	 * @return 实体列表
	 */
	default List<R> directSelectEntityListBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return directSelectEntityListBySql(statement);
	}

	/**
	 * 查询满足条件的单个实体（支持条件过滤和排序）。
	 *
	 * @param statement 查询语句对象
	 * @param criteria 过滤条件
	 * @param orderBy 排序条件
	 * @return 单个实体对象
	 */
	default R directSelectEntityBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return directSelectEntityBySql(statement);
	}

	/**
	 * 查询满足条件的映射列表（支持条件过滤和排序）。
	 *
	 * @param statement 查询语句对象
	 * @param criteria 过滤条件
	 * @param orderBy 排序条件
	 * @return 映射列表，键为字段名，值为字段值
	 */
	default List<Map<String, Object>> directSelectMapListBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return directSelectMapListBySql(statement);
	}

	/**
	 * 查询满足条件的单个映射（支持条件过滤和排序）。
	 *
	 * @param statement 查询语句对象
	 * @param criteria 过滤条件
	 * @param orderBy 排序条件
	 * @return 单个映射，键为字段名，值为字段值
	 */
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
