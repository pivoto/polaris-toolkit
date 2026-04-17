package io.polaris.mybatis.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.mybatis.annotation.DynamicResultMapping;
import io.polaris.mybatis.annotation.SupportLogicDeleted;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * 实体查询列表默认映射器接口
 * 提供基于实体、Map和Criteria的多种查询方式
 * 支持逻辑删除、排序、字段过滤等功能
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntitySelectListDefaultMapper<E> extends EntityMapper<E> {

	/**
	 * 根据实体对象查询列表
	 *
	 * @param entity 实体对象，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param supportLogicDeleted 是否处理逻辑删除规则
	 * @return 查询结果列表
	 */
	@DynamicResultMapping
	@SupportLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	List<E> selectEntityList(@Param(BindingKeys.WHERE) E entity,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields,
		@Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据实体对象查询列表（重载方法）
	 * 提供多种参数组合的便捷调用方式
	 *
	 * @param entity 实体对象，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果列表
	 */
	default List<E> selectEntityList(E entity, OrderBy orderBy, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityList(entity, orderBy, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据实体对象查询列表（重载方法）
	 * 默认不包含所有空值字段
	 *
	 * @param entity 实体对象，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果列表
	 */
	default List<E> selectEntityList(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityList(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体对象查询列表（重载方法）
	 * 仅指定包含的空值字段，排除字段为空
	 *
	 * @param entity 实体对象，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询结果列表
	 */
	default List<E> selectEntityList(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityList(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体对象查询列表（重载方法）
	 * 指定是否包含所有空值字段，其他参数为空
	 *
	 * @param entity 实体对象，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询结果列表
	 */
	default List<E> selectEntityList(E entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectEntityList(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体对象查询列表（重载方法）
	 * 仅指定排序条件，其他参数使用默认值
	 *
	 * @param entity 实体对象，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @return 查询结果列表
	 */
	default List<E> selectEntityList(E entity, OrderBy orderBy) {
		return selectEntityList(entity, orderBy, false);
	}

	/**
	 * 根据实体对象查询列表（重载方法）
	 * 仅指定实体对象，使用默认排序和过滤条件
	 *
	 * @param entity 实体对象，用于构建WHERE条件
	 * @return 查询结果列表
	 */
	default List<E> selectEntityList(E entity) {
		return selectEntityList(entity, null, false);
	}

	/**
	 * 根据Map参数查询实体列表
	 *
	 * @param entity Map参数，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param supportLogicDeleted 是否处理逻辑删除规则
	 * @return 查询结果列表
	 */
	@DynamicResultMapping
	@SupportLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	List<E> selectEntityListByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields,
		@Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据Map参数查询实体列表（重载方法）
	 * 提供多种参数组合的便捷调用方式
	 *
	 * @param entity Map参数，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果列表
	 */
	default List<E> selectEntityListByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityListByMap(entity, orderBy, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据Map参数查询实体列表（重载方法）
	 * 默认不包含所有空值字段
	 *
	 * @param entity Map参数，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果列表
	 */
	default List<E> selectEntityListByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityListByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map参数查询实体列表（重载方法）
	 * 仅指定包含的空值字段，排除字段为空
	 *
	 * @param entity Map参数，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询结果列表
	 */
	default List<E> selectEntityListByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityListByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Map参数查询实体列表（重载方法）
	 * 指定是否包含所有空值字段，其他参数为空
	 *
	 * @param entity Map参数，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询结果列表
	 */
	default List<E> selectEntityListByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectEntityListByMap(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Map参数查询实体列表（重载方法）
	 * 仅指定排序条件，其他参数使用默认值
	 *
	 * @param entity Map参数，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @return 查询结果列表
	 */
	default List<E> selectEntityListByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectEntityListByMap(entity, orderBy, false);
	}

	/**
	 * 根据Map参数查询实体列表（重载方法）
	 * 仅指定Map参数，使用默认排序和过滤条件
	 *
	 * @param entity Map参数，用于构建WHERE条件
	 * @return 查询结果列表
	 */
	default List<E> selectEntityListByMap(Map<String, Object> entity) {
		return selectEntityListByMap(entity, null, false);
	}

	/**
	 * 根据Criteria条件查询实体列表
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param supportLogicDeleted 是否处理逻辑删除规则
	 * @return 查询结果列表
	 */
	@DynamicResultMapping
	@SupportLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	List<E> selectEntityListByCriteria(@Param(BindingKeys.WHERE) Criteria criteria,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields,
		@Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据Criteria条件查询实体列表（重载方法）
	 * 提供多种参数组合的便捷调用方式
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果列表
	 */
	default List<E> selectEntityListByCriteria(Criteria criteria, OrderBy orderBy, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityListByCriteria(criteria, orderBy, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据Criteria条件查询实体列表（重载方法）
	 * 默认不包含所有空值字段
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果列表
	 */
	default List<E> selectEntityListByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityListByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Criteria条件查询实体列表（重载方法）
	 * 仅指定包含的空值字段，排除字段为空
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询结果列表
	 */
	default List<E> selectEntityListByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityListByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Criteria条件查询实体列表（重载方法）
	 * 指定是否包含所有空值字段，其他参数为空
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询结果列表
	 */
	default List<E> selectEntityListByCriteria(Criteria criteria, OrderBy orderBy, boolean includeAllEmpty) {
		return selectEntityListByCriteria(criteria, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Criteria条件查询实体列表（重载方法）
	 * 仅指定排序条件，其他参数使用默认值
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @return 查询结果列表
	 */
	default List<E> selectEntityListByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectEntityListByCriteria(criteria, orderBy, false);
	}

	/**
	 * 根据Criteria条件查询实体列表（重载方法）
	 * 仅指定查询条件，使用默认排序和过滤条件
	 *
	 * @param criteria 查询条件对象
	 * @return 查询结果列表
	 */
	default List<E> selectEntityListByCriteria(Criteria criteria) {
		return selectEntityListByCriteria(criteria, null, false);
	}

	/**
	 * 根据实体对象查询Map结果列表
	 *
	 * @param entity 实体对象，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param supportLogicDeleted 是否处理逻辑删除规则
	 * @return 查询结果Map列表
	 */
	@DynamicResultMapping
	@SupportLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	List<Map<String, Object>> selectMapList(@Param(BindingKeys.WHERE) E entity,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields,
		@Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据实体对象查询Map结果列表（重载方法）
	 * 提供多种参数组合的便捷调用方式
	 *
	 * @param entity 实体对象，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果Map列表
	 */
	default List<Map<String, Object>> selectMapList(E entity, OrderBy orderBy, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapList(entity, orderBy, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据实体对象查询Map结果列表（重载方法）
	 * 默认不包含所有段
	 *
	 * @param entity 实体对象，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果Map列表
	 */
	default List<Map<String, Object>> selectMapList(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapList(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体对象查询Map结果列表（重载方法）
	 * 仅指定包含的空值字段，排除字段为空
	 *
	 * @param entity 实体对象，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询结果Map列表
	 */
	default List<Map<String, Object>> selectMapList(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapList(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体对象查询Map结果列表（重载方法）
	 * 指定是否包含所有空值字段，其他参数为空
	 *
	 * @param entity 实体对象，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询结果Map列表
	 */
	default List<Map<String, Object>> selectMapList(E entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectMapList(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体对象查询Map结果列表（重载方法）
	 * 仅指定排序条件，其他参数使用默认值
	 *
	 * @param entity 实体对象，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @return 查询结果Map列表
	 */
	default List<Map<String, Object>> selectMapList(E entity, OrderBy orderBy) {
		return selectMapList(entity, orderBy, false);
	}

	/**
	 * 根据实体对象查询Map结果列表（重载方法）
	 * 仅指定实体对象，使用默认排序和过滤条件
	 *
	 * @param entity 实体对象，用于构建WHERE条件
	 * @return 查询结果Map列表
	 */
	default List<Map<String, Object>> selectMapList(E entity) {
		return selectMapList(entity, null, false);
	}

	/**
	 * 根据Map参数查询Map结果列表
	 *
	 * @param entity Map参数，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param supportLogicDeleted 是否处理逻辑删除规则
	 * @return 查询结果Map列表
	 */
	@DynamicResultMapping
	@SupportLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	List<Map<String, Object>> selectMapListByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields,
		@Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据Map参数查询Map结果列表（重载方法）
	 * 提供多种参数组合的便捷调用方式
	 *
	 * @param entity Map参数，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果Map列表
	 */
	default List<Map<String, Object>> selectMapListByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapListByMap(entity, orderBy, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据Map参数查询Map结果列表（重载方法）
	 * 默认不包含所有空值字段
	 *
	 * @param entity Map参数，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果Map列表
	 */
	default List<Map<String, Object>> selectMapListByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapListByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map参数查询Map结果列表（重载方法）
	 * 仅指定包含的空值字段，排除字段为空
	 *
	 * @param entity Map参数，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询结果Map列表
	 */
	default List<Map<String, Object>> selectMapListByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapListByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Map参数查询Map结果列表（重载方法）
	 * 指定是否包含所有空值字段，其他参数为空
	 *
	 * @param entity Map参数，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询结果Map列表
	 */
	default List<Map<String, Object>> selectMapListByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectMapListByMap(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Map参数查询Map结果列表（重载方法）
	 * 仅指定Map参数，使用默认排序和过滤条件
	 *
	 * @param entity Map参数，用于构建WHERE条件
	 * @return 查询结果Map列表
	 */
	default List<Map<String, Object>> selectMapListByMap(Map<String, Object> entity) {
		return selectMapListByMap(entity, null, false);
	}

	/**
	 * 根据Map参数查询Map结果列表（重载方法）
	 * 仅指定排序条件，其他参数使用默认值
	 *
	 * @param entity Map参数，用于构建WHERE条件
	 * @param orderBy 排序条件
	 * @return 查询结果Map列表
	 */
	default List<Map<String, Object>> selectMapListByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectMapListByMap(entity, orderBy, false);
	}

	/**
	 * 根据Criteria条件查询Map结果列表
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param supportLogicDeleted 是否处理逻辑删除规则
	 * @return 查询结果Map列表
	 */
	@DynamicResultMapping
	@SupportLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	List<Map<String, Object>> selectMapListByCriteria(@Param(BindingKeys.WHERE) Criteria criteria,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields,
		@Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据Criteria条件查询Map结果列表（重载方法）
	 * 提供多种参数组合的便捷调用方式
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果Map列表
	 */
	default List<Map<String, Object>> selectMapListByCriteria(Criteria criteria, OrderBy orderBy, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapListByCriteria(criteria, orderBy, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据Criteria条件查询Map结果列表（重载方法）
	 * 默认不包含所有空值字段
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果Map列表
	 */
	default List<Map<String, Object>> selectMapListByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapListByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Criteria条件查询Map结果列表（重载方法）
	 * 仅指定包含的空值字段，排除字段为空
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询结果Map列表
	 */
	default List<Map<String, Object>> selectMapListByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapListByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Criteria条件查询Map结果列表（重载方法）
	 * 指定是否包含所有空值字段，其他参数为空
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询结果Map列表
	 */
	default List<Map<String, Object>> selectMapListByCriteria(Criteria criteria, OrderBy orderBy, boolean includeAllEmpty) {
		return selectMapListByCriteria(criteria, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Criteria条件查询Map结果列表（重载方法）
	 * 仅指定查询条件，使用默认排序和过滤条件
	 *
	 * @param criteria 查询条件对象
	 * @return 查询结果Map列表
	 */
	default List<Map<String, Object>> selectMapListByCriteria(Criteria criteria) {
		return selectMapListByCriteria(criteria, null, false);
	}

	/**
	 * 根据Criteria条件查询Map结果列表（重载方法）
	 * 仅指定排序条件，其他参数使用默认值
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @return 查询结果Map列表
	 */
	default List<Map<String, Object>> selectMapListByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectMapListByCriteria(criteria, orderBy, false);
	}

}
