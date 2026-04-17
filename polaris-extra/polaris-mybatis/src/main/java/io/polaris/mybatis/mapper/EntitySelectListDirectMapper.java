package io.polaris.mybatis.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.mybatis.annotation.AutoResultMapping;
import io.polaris.mybatis.annotation.SupportLogicDeleted;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * 实体查询接口，提供多种方式直接查询实体列表
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntitySelectListDirectMapper<E> extends EntityMapper<E> {

	/**
	 * 根据实体对象查询实体列表
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 实体列表
	 */
	@AutoResultMapping
	@SupportLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	List<E> directSelectEntityList(@Param(BindingKeys.WHERE) E entity,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体对象查询实体列表（重载方法）
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 实体列表
	 */
	default List<E> directSelectEntityList(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntityList(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体对象查询实体列表（重载方法）
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 实体列表
	 */
	default List<E> directSelectEntityList(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectEntityList(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体对象查询实体列表（重载方法）
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 实体列表
	 */
	default List<E> directSelectEntityList(E entity, OrderBy orderBy, boolean includeAllEmpty) {
		return directSelectEntityList(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体对象查询实体列表（重载方法）
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param orderBy 排序条件
	 * @return 实体列表
	 */
	default List<E> directSelectEntityList(E entity, OrderBy orderBy) {
		return directSelectEntityList(entity, orderBy, false);
	}

	/**
	 * 根据实体对象查询实体列表（重载方法）
	 *
	 * @param entity 实体对象，作为查询条件
	 * @return 实体列表
	 */
	default List<E> directSelectEntityList(E entity) {
		return directSelectEntityList(entity, null, false);
	}

	/**
	 * 根据Map查询实体列表
	 *
	 * @param entity Map对象，作为查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 实体列表
	 */
	@AutoResultMapping
	@SupportLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	List<E> directSelectEntityListByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Map查询实体列表（重载方法）
	 *
	 * @param entity Map对象，作为查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 实体列表
	 */
	default List<E> directSelectEntityListByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntityListByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map查询实体列表（重载方法）
	 *
	 * @param entity Map对象，作为查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 实体列表
	 */
	default List<E> directSelectEntityListByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectEntityListByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Map查询实体列表（重载方法）
	 *
	 * @param entity Map对象，作为查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 实体列表
	 */
	default List<E> directSelectEntityListByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeAllEmpty) {
		return directSelectEntityListByMap(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Map查询实体列表（重载方法）
	 *
	 * @param entity Map对象，作为查询条件
	 * @param orderBy 排序条件
	 * @return 实体列表
	 */
	default List<E> directSelectEntityListByMap(Map<String, Object> entity, OrderBy orderBy) {
		return directSelectEntityListByMap(entity, orderBy, false);
	}

	/**
	 * 根据Map查询实体列表（重载方法）
	 *
	 * @param entity Map对象，作为查询条件
	 * @return 实体列表
	 */
	default List<E> directSelectEntityListByMap(Map<String, Object> entity) {
		return directSelectEntityListByMap(entity, null, false);
	}

	/**
	 * 根据Criteria查询实体列表
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 实体列表
	 */
	@AutoResultMapping
	@SupportLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	List<E> directSelectEntityListByCriteria(@Param(BindingKeys.WHERE) Criteria criteria,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Criteria查询实体列表（重载方法）
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 实体列表
	 */
	default List<E> directSelectEntityListByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntityListByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Criteria查询实体列表（重载方法）
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 实体列表
	 */
	default List<E> directSelectEntityListByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectEntityListByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Criteria查询实体列表（重载方法）
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 实体列表
	 */
	default List<E> directSelectEntityListByCriteria(Criteria criteria, OrderBy orderBy, boolean includeAllEmpty) {
		return directSelectEntityListByCriteria(criteria, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Criteria查询实体列表（重载方法）
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @return 实体列表
	 */
	default List<E> directSelectEntityListByCriteria(Criteria criteria, OrderBy orderBy) {
		return directSelectEntityListByCriteria(criteria, orderBy, false);
	}

	/**
	 * 根据Criteria查询实体列表（重载方法）
	 *
	 * @param criteria 查询条件对象
	 * @return 实体列表
	 */
	default List<E> directSelectEntityListByCriteria(Criteria criteria) {
		return directSelectEntityListByCriteria(criteria, null, false);
	}

	/**
	 * 根据实体对象查询Map列表
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return Map列表
	 */
	@AutoResultMapping
	@SupportLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	List<Map<String, Object>> directSelectMapList(@Param(BindingKeys.WHERE) E entity,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体对象查询Map列表（重载方法）
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return Map列表
	 */
	default List<Map<String, Object>> directSelectMapList(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectMapList(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体对象查询Map列表（重载方法）
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return Map列表
	 */
	default List<Map<String, Object>> directSelectMapList(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectMapList(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体对象查询Map列表（重载方法）
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return Map列表
	 */
	default List<Map<String, Object>> directSelectMapList(E entity, OrderBy orderBy, boolean includeAllEmpty) {
		return directSelectMapList(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体对象查询Map列表（重载方法）
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param orderBy 排序条件
	 * @return Map列表
	 */
	default List<Map<String, Object>> directSelectMapList(E entity, OrderBy orderBy) {
		return directSelectMapList(entity, orderBy, false);
	}

	/**
	 * 根据实体对象查询Map列表（重载方法）
	 *
	 * @param entity 实体对象，作为查询条件
	 * @return Map列表
	 */
	default List<Map<String, Object>> directSelectMapList(E entity) {
		return directSelectMapList(entity, null, false);
	}

	/**
	 * 根据Map查询Map列表
	 *
	 * @param entity Map对象，作为查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return Map列表
	 */
	@AutoResultMapping
	@SupportLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	List<Map<String, Object>> directSelectMapListByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Map查询Map列表（重载方法）
	 *
	 * @param entity Map对象，作为查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return Map列表
	 */
	default List<Map<String, Object>> directSelectMapListByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectMapListByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map查询Map列表（重载方法）
	 *
	 * @param entity Map对象，作为查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return Map列表
	 */
	default List<Map<String, Object>> directSelectMapListByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectMapListByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Map查询Map列表（重载方法）
	 *
	 * @param entity Map对象，作为查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return Map列表
	 */
	default List<Map<String, Object>> directSelectMapListByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeAllEmpty) {
		return directSelectMapListByMap(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Map查询Map列表（重载方法）
	 *
	 * @param entity Map对象，作为查询条件
	 * @return Map列表
	 */
	default List<Map<String, Object>> directSelectMapListByMap(Map<String, Object> entity) {
		return directSelectMapListByMap(entity, null, false);
	}

	/**
	 * 根据Map查询Map列表（重载方法）
	 *
	 * @param entity Map对象，作为查询条件
	 * @param orderBy 排序条件
	 * @return Map列表
	 */
	default List<Map<String, Object>> directSelectMapListByMap(Map<String, Object> entity, OrderBy orderBy) {
		return directSelectMapListByMap(entity, orderBy, false);
	}

	/**
	 * 根据Criteria查询Map列表
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return Map列表
	 */
	@AutoResultMapping
	@SupportLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	List<Map<String, Object>> directSelectMapListByCriteria(@Param(BindingKeys.WHERE) Criteria criteria,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Criteria查询Map列表（重载方法）
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return Map列表
	 */
	default List<Map<String, Object>> directSelectMapListByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectMapListByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Criteria查询Map列表（重载方法）
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return Map列表
	 */
	default List<Map<String, Object>> directSelectMapListByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectMapListByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Criteria查询Map列表（重载方法）
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return Map列表
	 */
	default List<Map<String, Object>> directSelectMapListByCriteria(Criteria criteria, OrderBy orderBy, boolean includeAllEmpty) {
		return directSelectMapListByCriteria(criteria, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Criteria查询Map列表（重载方法）
	 *
	 * @param criteria 查询条件对象
	 * @return Map列表
	 */
	default List<Map<String, Object>> directSelectMapListByCriteria(Criteria criteria) {
		return directSelectMapListByCriteria(criteria, null, false);
	}

	/**
	 * 根据Criteria查询Map列表（重载方法）
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy 排序条件
	 * @return Map列表
	 */
	default List<Map<String, Object>> directSelectMapListByCriteria(Criteria criteria, OrderBy orderBy) {
		return directSelectMapListByCriteria(criteria, orderBy, false);
	}

}
