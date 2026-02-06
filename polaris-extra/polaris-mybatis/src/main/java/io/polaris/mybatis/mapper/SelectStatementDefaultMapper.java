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
 * 通用SQL查询映射器接口，提供基于SelectStatement的数据库查询操作。
 *
 * @param <R> 实体类型参数
 * @author Qt
 * @since Aug 25, 2023
 */
public interface SelectStatementDefaultMapper<R> {

	/**
	 * 检查是否存在满足条件的记录（内部方法，不建议直接调用）
	 *
	 * @param statement SQL查询语句对象
	 * @return 游标结果，包含布尔值表示是否存在匹配记录
	 */
	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.existsBySql)
	@Options(fetchSize = 1)
	@Internal("考虑到查询性能与分页实现的兼容性，声明此此方法，实际开发中不直接调用")
	Cursor<Boolean> existsInnerBySql(@Param(BindingKeys.SELECT) SelectStatement<?> statement);

	/**
	 * 统计满足条件的记录数量
	 *
	 * @param statement SQL查询语句对象
	 * @return 记录总数
	 */
	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countBySql)
	int countBySql(@Param(BindingKeys.SELECT) SelectStatement<?> statement);

	/**
	 * 查询实体列表
	 *
	 * @param statement SQL查询语句对象
	 * @return 实体对象列表
	 */
	@WithLogicDeleted(true)
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySql)
	List<R> selectEntityListBySql(@Param(BindingKeys.SELECT) SelectStatement<?> statement);

	/**
	 * 查询Map列表
	 *
	 * @param statement SQL查询语句对象
	 * @return Map对象列表，键为列名，值为对应值
	 */
	@WithLogicDeleted(true)
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySql)
	List<Map<String, Object>> selectMapListBySql(@Param(BindingKeys.SELECT) SelectStatement<?> statement);

	/**
	 * 查询单个实体对象
	 *
	 * @param statement SQL查询语句对象
	 * @return 实体对象
	 */
	@WithLogicDeleted(true)
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySql)
	R selectEntityBySql(@Param(BindingKeys.SELECT) SelectStatement<?> statement);

	/**
	 * 查询单个Map对象
	 *
	 * @param statement SQL查询语句对象
	 * @return Map对象，键为列名，值为对应值
	 */
	@WithLogicDeleted(true)
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectBySql)
	Map<String, Object> selectMapBySql(@Param(BindingKeys.SELECT) SelectStatement<?> statement);

	/**
	 * 检查是否存在满足条件的记录
	 *
	 * @param statement SQL查询语句对象
	 * @return 是否存在匹配记录
	 */
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

	/**
	 * 检查是否存在满足条件的记录（支持动态条件）
	 *
	 * @param statement SQL查询语句对象
	 * @param criteria  查询条件对象
	 * @return 是否存在匹配记录
	 */
	default boolean existsBySql(SelectStatement<?> statement, Criteria criteria) {
		if (criteria != null) {
			statement.where(criteria);
		}
		return existsBySql(statement);
	}

	/**
	 * 统计满足条件的记录数量（支持动态条件和排序）
	 *
	 * @param statement SQL查询语句对象
	 * @param criteria  查询条件对象
	 * @param orderBy   排序条件对象
	 * @return 记录总数
	 */
	default int countBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return countBySql(statement);
	}

	/**
	 * 查询实体列表（支持动态条件和排序）
	 *
	 * @param statement SQL查询语句对象
	 * @param criteria  查询条件对象
	 * @param orderBy   排序条件对象
	 * @return 实体对象列表
	 */
	default List<R> selectEntityListBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return selectEntityListBySql(statement);
	}

	/**
	 * 查询单个实体对象（支持动态条件和排序）
	 *
	 * @param statement SQL查询语句对象
	 * @param criteria  查询条件对象
	 * @param orderBy   排序条件对象
	 * @return 实体对象
	 */
	default R selectEntityBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return selectEntityBySql(statement);
	}

	/**
	 * 查询Map列表（支持动态条件和排序）
	 *
	 * @param statement SQL查询语句对象
	 * @param criteria  查询条件对象
	 * @param orderBy   排序条件对象
	 * @return Map对象列表
	 */
	default List<Map<String, Object>> selectMapListBySql(SelectStatement<?> statement, Criteria criteria, OrderBy orderBy) {
		if (criteria != null) {
			statement.where(criteria);
		}
		if (orderBy != null) {
			statement.orderBy(orderBy);
		}
		return selectMapListBySql(statement);
	}

	/**
	 * 查询单个Map对象（支持动态条件和排序）
	 *
	 * @param statement SQL查询语句对象
	 * @param criteria  查询条件对象
	 * @param orderBy   排序条件对象
	 * @return Map对象
	 */
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
