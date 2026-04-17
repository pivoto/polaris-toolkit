package io.polaris.mybatis.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.mybatis.annotation.DynamicResultMapping;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * 实体查询接口，提供排除逻辑删除记录的查询功能
 * <p>
 * 该接口提供了多种查询方式：
 * <br>- 按实体对象查询
 * <br>- 按Map参数查询
 * <br>- 按Criteria条件查询
 * <p>
 * 每种查询方式都支持以下参数：
 * <br>- entity/criteria/map: 查询条件
 * <br>- orderBy: 排序条件
 * <br>- includeAllEmpty: 是否包含所有空值字段
 * <br>- includeEmptyFields: 指定包含的空值字段集合
 * <br>- excludeFields: 指定排除的字段集合
 * <p>
 * 同时提供了多个便捷的默认方法，简化常用查询场景的调用
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntitySelectListExceptLogicDeletedMapper<E> extends EntityMapper<E> {

	/**
	 * 根据实体对象查询列表，排除逻辑删除记录
	 *
	 * @param entity 实体对象作为查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @param excludeFields 指定排除的字段集合
	 * @return 实体对象列表
	 */
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	List<E> selectEntityListExceptLogicDeleted(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体对象查询列表（指定包含和排除字段）
	 *
	 * @param entity 实体对象作为查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @param excludeFields 指定排除的字段集合
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeleted(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityListExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体对象查询列表（指定包含字段）
	 *
	 * @param entity 实体对象作为查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeleted(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityListExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体对象查询列表（指定是否包含所有空值）
	 *
	 * @param entity 实体对象作为查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeleted(E entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectEntityListExceptLogicDeleted(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体对象查询列表（仅排序）
	 *
	 * @param entity 实体对象作为查询条件
	 * @param orderBy 排序条件
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeleted(E entity, OrderBy orderBy) {
		return selectEntityListExceptLogicDeleted(entity, orderBy, false);
	}

	/**
	 * 根据实体对象查询列表（无排序）
	 *
	 * @param entity 实体对象作为查询条件
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeleted(E entity) {
		return selectEntityListExceptLogicDeleted(entity, null, false);
	}

	/**
	 * 根据Map参数查询实体列表，排除逻辑删除记录
	 *
	 * @param entity Map参数作为查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @param excludeFields 指定排除的字段集合
	 * @return 实体对象列表
	 */
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	List<E> selectEntityListExceptLogicDeletedByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Map参数查询列表（指定包含和排除字段）
	 *
	 * @param entity Map参数作为查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @param excludeFields 指定排除的字段集合
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityListExceptLogicDeletedByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map参数查询列表（指定包含字段）
	 *
	 * @param entity Map参数作为查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityListExceptLogicDeletedByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Map参数查询列表（指定是否包含所有空值）
	 *
	 * @param entity Map参数作为查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectEntityListExceptLogicDeletedByMap(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Map参数查询列表（仅排序）
	 *
	 * @param entity Map参数作为查询条件
	 * @param orderBy 排序条件
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectEntityListExceptLogicDeletedByMap(entity, orderBy, false);
	}

	/**
	 * 根据Map参数查询列表（无排序）
	 *
	 * @param entity Map参数作为查询条件
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeletedByMap(Map<String, Object> entity) {
		return selectEntityListExceptLogicDeletedByMap(entity, null, false);
	}

	/**
	 * 根据Criteria条件查询实体列表，排除逻辑删除记录
	 *
	 * @param criteria Criteria条件对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @param excludeFields 指定排除的字段集合
	 * @return 实体对象列表
	 */
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	List<E> selectEntityListExceptLogicDeletedByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Criteria条件查询列表（指定包含和排除字段）
	 *
	 * @param criteria Criteria条件对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @param excludeFields 指定排除的字段集合
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityListExceptLogicDeletedByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Criteria条件查询列表（指定包含字段）
	 *
	 * @param criteria Criteria条件对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityListExceptLogicDeletedByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Criteria条件查询列表（指定是否包含所有空值）
	 *
	 * @param criteria Criteria条件对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, boolean includeAllEmpty) {
		return selectEntityListExceptLogicDeletedByCriteria(criteria, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Criteria条件查询列表（仅排序）
	 *
	 * @param criteria Criteria条件对象
	 * @param orderBy 排序条件
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectEntityListExceptLogicDeletedByCriteria(criteria, orderBy, false);
	}

	/**
	 * 根据Criteria条件查询列表（无排序）
	 *
	 * @param criteria Criteria条件对象
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeletedByCriteria(Criteria criteria) {
		return selectEntityListExceptLogicDeletedByCriteria(criteria, null, false);
	}

	/**
	 * 根据实体对象查询Map列表，排除逻辑删除记录
	 *
	 * @param entity 实体对象作为查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @param excludeFields 指定排除的字段集合
	 * @return Map对象列表
	 */
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	List<Map<String, Object>> selectMapListExceptLogicDeleted(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体对象查询Map列表（指定包含和排除字段）
	 *
	 * @param entity 实体对象作为查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @param excludeFields 指定排除的字段集合
	 * @return Map对象列表
	 */
	default List<Map<String, Object>> selectMapListExceptLogicDeleted(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapListExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体对象查询Map列表（指定包含字段）
	 *
	 * @param entity 实体对象作为查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @return Map对象列表
	 */
	default List<Map<String, Object>> selectMapListExceptLogicDeleted(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapListExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体对象查询Map列表（指定是否包含所有空值）
	 *
	 * @param entity 实体对象作为查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return Map对象列表
	 */
	default List<Map<String, Object>> selectMapListExceptLogicDeleted(E entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectMapListExceptLogicDeleted(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体对象查询Map列表（仅排序）
	 *
	 * @param entity 实体对象作为查询条件
	 * @param orderBy 排序条件
	 * @return Map对象列表
	 */
	default List<Map<String, Object>> selectMapListExceptLogicDeleted(E entity, OrderBy orderBy) {
		return selectMapListExceptLogicDeleted(entity, orderBy, false);
	}

	/**
	 * 根据实体对象查询Map列表（无排序）
	 *
	 * @param entity 实体对象作为查询条件
	 * @return Map对象列表
	 */
	default List<Map<String, Object>> selectMapListExceptLogicDeleted(E entity) {
		return selectMapListExceptLogicDeleted(entity, null, false);
	}

	/**
	 * 根据Map参数查询Map列表，排除逻辑删除记录
	 *
	 * @param entity Map参数作为查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @param excludeFields 指定排除的字段集合
	 * @return Map对象列表
	 */
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	List<Map<String, Object>> selectMapListExceptLogicDeletedByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Map参数查询Map列表（指定包含和排除字段）
	 *
	 * @param entity Map参数作为查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @param excludeFields 指定排除的字段集合
	 * @return Map对象列表
	 */
	default List<Map<String, Object>> selectMapListExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapListExceptLogicDeletedByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map参数查询Map列表（指定包含字段）
	 *
	 * @param entity Map参数作为查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @return Map对象列表
	 */
	default List<Map<String, Object>> selectMapListExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapListExceptLogicDeletedByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Map参数查询Map列表（指定是否包含所有空值）
	 *
	 * @param entity Map参数作为查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return Map对象列表
	 */
	default List<Map<String, Object>> selectMapListExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectMapListExceptLogicDeletedByMap(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Map参数查询Map列表（无排序）
	 *
	 * @param entity Map参数作为查询条件
	 * @return Map对象列表
	 */
	default List<Map<String, Object>> selectMapListExceptLogicDeletedByMap(Map<String, Object> entity) {
		return selectMapListExceptLogicDeletedByMap(entity, null, false);
	}

	/**
	 * 根据Map参数查询Map列表（仅排序）
	 *
	 * @param entity Map参数作为查询条件
	 * @param orderBy 排序条件
	 * @return Map对象列表
	 */
	default List<Map<String, Object>> selectMapListExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectMapListExceptLogicDeletedByMap(entity, orderBy, false);
	}

	/**
	 * 根据Criteria条件查询Map列表，排除逻辑删除记录
	 *
	 * @param criteria Criteria条件对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @param excludeFields 指定排除的字段集合
	 * @return Map对象列表
	 */
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	List<Map<String, Object>> selectMapListExceptLogicDeletedByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Criteria条件查询Map列表（指定包含和排除字段）
	 *
	 * @param criteria Criteria条件对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @param excludeFields 指定排除的字段集合
	 * @return Map对象列表
	 */
	default List<Map<String, Object>> selectMapListExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapListExceptLogicDeletedByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Criteria条件查询Map列表（指定包含字段）
	 *
	 * @param criteria Criteria条件对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @return Map对象列表
	 */
	default List<Map<String, Object>> selectMapListExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapListExceptLogicDeletedByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Criteria条件查询Map列表（指定是否包含所有空值）
	 *
	 * @param criteria Criteria条件对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return Map对象列表
	 */
	default List<Map<String, Object>> selectMapListExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, boolean includeAllEmpty) {
		return selectMapListExceptLogicDeletedByCriteria(criteria, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Criteria条件查询Map列表（无排序）
	 *
	 * @param criteria Criteria条件对象
	 * @return Map对象列表
	 */
	default List<Map<String, Object>> selectMapListExceptLogicDeletedByCriteria(Criteria criteria) {
		return selectMapListExceptLogicDeletedByCriteria(criteria, null, false);
	}

	/**
	 * 根据Criteria条件查询Map列表（仅排序）
	 *
	 * @param criteria Criteria条件对象
	 * @param orderBy 排序条件
	 * @return Map对象列表
	 */
	default List<Map<String, Object>> selectMapListExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectMapListExceptLogicDeletedByCriteria(criteria, orderBy, false);
	}

}
