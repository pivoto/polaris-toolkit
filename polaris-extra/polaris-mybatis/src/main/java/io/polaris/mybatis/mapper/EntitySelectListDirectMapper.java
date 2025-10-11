package io.polaris.mybatis.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.mybatis.annotation.DynamicResultMapping;
import io.polaris.mybatis.annotation.WithLogicDeleted;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntitySelectListDirectMapper<E> extends EntityMapper<E> {

	@DynamicResultMapping
	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	List<E> directSelectEntityList(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<E> directSelectEntityList(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntityList(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<E> directSelectEntityList(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectEntityList(entity, orderBy, false, includeEmptyFields, null);
	}

	default List<E> directSelectEntityList(E entity, OrderBy orderBy, boolean includeEmpty) {
		return directSelectEntityList(entity, orderBy, includeEmpty, null, null);
	}

	default List<E> directSelectEntityList(E entity, OrderBy orderBy) {
		return directSelectEntityList(entity, orderBy, false);
	}

	default List<E> directSelectEntityList(E entity) {
		return directSelectEntityList(entity, null, false);
	}


	@DynamicResultMapping
	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	List<E> directSelectEntityListByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<E> directSelectEntityListByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntityListByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<E> directSelectEntityListByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectEntityListByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	default List<E> directSelectEntityListByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeEmpty) {
		return directSelectEntityListByMap(entity, orderBy, includeEmpty, null, null);
	}

	default List<E> directSelectEntityListByMap(Map<String, Object> entity, OrderBy orderBy) {
		return directSelectEntityListByMap(entity, orderBy, false);
	}

	default List<E> directSelectEntityListByMap(Map<String, Object> entity) {
		return directSelectEntityListByMap(entity, null, false);
	}

	@DynamicResultMapping
	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	List<E> directSelectEntityListByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<E> directSelectEntityListByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntityListByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<E> directSelectEntityListByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectEntityListByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	default List<E> directSelectEntityListByCriteria(Criteria criteria, OrderBy orderBy, boolean includeEmpty) {
		return directSelectEntityListByCriteria(criteria, orderBy, includeEmpty, null, null);
	}

	default List<E> directSelectEntityListByCriteria(Criteria criteria, OrderBy orderBy) {
		return directSelectEntityListByCriteria(criteria, orderBy, false);
	}

	default List<E> directSelectEntityListByCriteria(Criteria criteria) {
		return directSelectEntityListByCriteria(criteria, null, false);
	}


	@DynamicResultMapping
	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	List<Map<String, Object>> directSelectMapList(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<Map<String, Object>> directSelectMapList(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectMapList(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<Map<String, Object>> directSelectMapList(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectMapList(entity, orderBy, false, includeEmptyFields, null);
	}

	default List<Map<String, Object>> directSelectMapList(E entity, OrderBy orderBy, boolean includeEmpty) {
		return directSelectMapList(entity, orderBy, includeEmpty, null, null);
	}

	default List<Map<String, Object>> directSelectMapList(E entity, OrderBy orderBy) {
		return directSelectMapList(entity, orderBy, false);
	}

	default List<Map<String, Object>> directSelectMapList(E entity) {
		return directSelectMapList(entity, null, false);
	}


	@DynamicResultMapping
	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	List<Map<String, Object>> directSelectMapListByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<Map<String, Object>> directSelectMapListByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectMapListByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<Map<String, Object>> directSelectMapListByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectMapListByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	default List<Map<String, Object>> directSelectMapListByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeEmpty) {
		return directSelectMapListByMap(entity, orderBy, includeEmpty, null, null);
	}

	default List<Map<String, Object>> directSelectMapListByMap(Map<String, Object> entity) {
		return directSelectMapListByMap(entity, null, false);
	}

	default List<Map<String, Object>> directSelectMapListByMap(Map<String, Object> entity, OrderBy orderBy) {
		return directSelectMapListByMap(entity, orderBy, false);
	}


	@DynamicResultMapping
	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	List<Map<String, Object>> directSelectMapListByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<Map<String, Object>> directSelectMapListByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectMapListByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<Map<String, Object>> directSelectMapListByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectMapListByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	default List<Map<String, Object>> directSelectMapListByCriteria(Criteria criteria, OrderBy orderBy, boolean includeEmpty) {
		return directSelectMapListByCriteria(criteria, orderBy, includeEmpty, null, null);
	}

	default List<Map<String, Object>> directSelectMapListByCriteria(Criteria criteria) {
		return directSelectMapListByCriteria(criteria, null, false);
	}

	default List<Map<String, Object>> directSelectMapListByCriteria(Criteria criteria, OrderBy orderBy) {
		return directSelectMapListByCriteria(criteria, orderBy, false);
	}


}
