package io.polaris.mybatis.enhance.mapper;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.polaris.core.annotation.Internal;
import io.polaris.core.io.IO;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.mybatis.annotation.AutoMapper;
import io.polaris.mybatis.annotation.AutoResultMapping;
import io.polaris.mybatis.annotation.SupportLogicDeleted;
import io.polaris.mybatis.consts.AutoMapperType;
import io.polaris.mybatis.mapper.EntityMapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;

/**
 * @author Qt
 * @since Apr 17, 2026
 */
public interface EntitySelectListAutoMapper<E> extends EntityMapper<E> {

	/**
	 * 根据实体对象查询列表
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param supportLogicDeleted 是否处理逻辑删除规则
	 * @return 查询结果列表
	 */
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectEntity)
	List<E> selectEntityList(@Param(BindingKeys.WHERE) Object entity,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields,
		@Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据实体对象查询列表（重载方法）
	 * 提供多种参数组合的便捷调用方式
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果列表
	 */
	default List<E> selectEntityList(Object entity, OrderBy orderBy, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityList(entity, orderBy, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据实体对象查询列表（重载方法）
	 * 默认不包含所有空值字段
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果列表
	 */
	default List<E> selectEntityList(Object entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityList(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体对象查询列表（重载方法）
	 * 仅指定包含的空值字段，排除字段为空
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询结果列表
	 */
	default List<E> selectEntityList(Object entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityList(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体对象查询列表（重载方法）
	 * 指定是否包含所有空值字段，其他参数为空
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询结果列表
	 */
	default List<E> selectEntityList(Object entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectEntityList(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体对象查询列表（重载方法）
	 * 仅指定排序条件，其他参数使用默认值
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @return 查询结果列表
	 */
	default List<E> selectEntityList(Object entity, OrderBy orderBy) {
		return selectEntityList(entity, orderBy, false);
	}

	/**
	 * 根据实体对象查询列表（重载方法）
	 * 仅指定实体对象，使用默认排序和过滤条件
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria对象
	 * @return 查询结果列表
	 */
	default List<E> selectEntityList(Object entity) {
		return selectEntityList(entity, null, false);
	}


	/**
	 * 根据实体对象查询实体列表
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 实体列表
	 */
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectEntityDirect)
	List<E> directSelectEntityList(@Param(BindingKeys.WHERE) Object entity,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体对象查询实体列表（重载方法）
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 实体列表
	 */
	default List<E> directSelectEntityList(Object entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntityList(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体对象查询实体列表（重载方法）
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 实体列表
	 */
	default List<E> directSelectEntityList(Object entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectEntityList(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体对象查询实体列表（重载方法）
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 实体列表
	 */
	default List<E> directSelectEntityList(Object entity, OrderBy orderBy, boolean includeAllEmpty) {
		return directSelectEntityList(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体对象查询实体列表（重载方法）
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @return 实体列表
	 */
	default List<E> directSelectEntityList(Object entity, OrderBy orderBy) {
		return directSelectEntityList(entity, orderBy, false);
	}

	/**
	 * 根据实体对象查询实体列表（重载方法）
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @return 实体列表
	 */
	default List<E> directSelectEntityList(Object entity) {
		return directSelectEntityList(entity, null, false);
	}

	// endregion


	/**
	 * 根据实体对象查询列表，排除逻辑删除记录
	 *
	 * @param entity 实体对象作为查询条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @param excludeFields 指定排除的字段集合
	 * @return 实体对象列表
	 */
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectEntityExceptLogicDeleted)
	List<E> selectEntityListExceptLogicDeleted(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体对象查询列表（指定包含和排除字段）
	 *
	 * @param entity 实体对象作为查询条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @param excludeFields 指定排除的字段集合
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeleted(Object entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityListExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体对象查询列表（指定包含字段）
	 *
	 * @param entity 实体对象作为查询条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeleted(Object entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityListExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体对象查询列表（指定是否包含所有空值）
	 *
	 * @param entity 实体对象作为查询条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeleted(Object entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectEntityListExceptLogicDeleted(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体对象查询列表（仅排序）
	 *
	 * @param entity 实体对象作为查询条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeleted(Object entity, OrderBy orderBy) {
		return selectEntityListExceptLogicDeleted(entity, orderBy, false);
	}

	/**
	 * 根据实体对象查询列表（无排序）
	 *
	 * @param entity 实体对象作为查询条件，可以是Bean或Map或Criteria对象
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeleted(Object entity) {
		return selectEntityListExceptLogicDeleted(entity, null, false);
	}

}
