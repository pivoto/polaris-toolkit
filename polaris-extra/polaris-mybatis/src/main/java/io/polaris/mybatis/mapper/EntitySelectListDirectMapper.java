package io.polaris.mybatis.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
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

	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	List<E> selectEntityListDirect(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<E> selectEntityListDirect(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityListDirect(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<E> selectEntityListDirect(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityListDirect(entity, orderBy, false, includeEmptyFields, null);
	}

	default List<E> selectEntityListDirect(E entity, OrderBy orderBy, boolean includeEmpty) {
		return selectEntityListDirect(entity, orderBy, includeEmpty, null, null);
	}

	default List<E> selectEntityListDirect(E entity, OrderBy orderBy) {
		return selectEntityListDirect(entity, orderBy, false);
	}

	default List<E> selectEntityListDirect(E entity) {
		return selectEntityListDirect(entity, null, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	List<E> selectEntityListDirectByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<E> selectEntityListDirectByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityListDirectByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<E> selectEntityListDirectByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityListDirectByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	default List<E> selectEntityListDirectByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeEmpty) {
		return selectEntityListDirectByMap(entity, orderBy, includeEmpty, null, null);
	}

	default List<E> selectEntityListDirectByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectEntityListDirectByMap(entity, orderBy, false);
	}

	default List<E> selectEntityListDirectByMap(Map<String, Object> entity) {
		return selectEntityListDirectByMap(entity, null, false);
	}

	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	List<E> selectEntityListDirectByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<E> selectEntityListDirectByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityListDirectByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<E> selectEntityListDirectByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityListDirectByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	default List<E> selectEntityListDirectByCriteria(Criteria criteria, OrderBy orderBy, boolean includeEmpty) {
		return selectEntityListDirectByCriteria(criteria, orderBy, includeEmpty, null, null);
	}

	default List<E> selectEntityListDirectByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectEntityListDirectByCriteria(criteria, orderBy, false);
	}

	default List<E> selectEntityListDirectByCriteria(Criteria criteria) {
		return selectEntityListDirectByCriteria(criteria, null, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	List<Map<String, Object>> selectMapListDirect(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<Map<String, Object>> selectMapListDirect(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapListDirect(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<Map<String, Object>> selectMapListDirect(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapListDirect(entity, orderBy, false, includeEmptyFields, null);
	}

	default List<Map<String, Object>> selectMapListDirect(E entity, OrderBy orderBy, boolean includeEmpty) {
		return selectMapListDirect(entity, orderBy, includeEmpty, null, null);
	}

	default List<Map<String, Object>> selectMapListDirect(E entity, OrderBy orderBy) {
		return selectMapListDirect(entity, orderBy, false);
	}

	default List<Map<String, Object>> selectMapListDirect(E entity) {
		return selectMapListDirect(entity, null, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	List<Map<String, Object>> selectMapListDirectByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<Map<String, Object>> selectMapListDirectByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapListDirectByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<Map<String, Object>> selectMapListDirectByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapListDirectByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	default List<Map<String, Object>> selectMapListDirectByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeEmpty) {
		return selectMapListDirectByMap(entity, orderBy, includeEmpty, null, null);
	}

	default List<Map<String, Object>> selectMapListDirectByMap(Map<String, Object> entity) {
		return selectMapListDirectByMap(entity, null, false);
	}

	default List<Map<String, Object>> selectMapListDirectByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectMapListDirectByMap(entity, orderBy, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	List<Map<String, Object>> selectMapListDirectByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<Map<String, Object>> selectMapListDirectByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapListDirectByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<Map<String, Object>> selectMapListDirectByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapListDirectByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	default List<Map<String, Object>> selectMapListDirectByCriteria(Criteria criteria, OrderBy orderBy, boolean includeEmpty) {
		return selectMapListDirectByCriteria(criteria, orderBy, includeEmpty, null, null);
	}

	default List<Map<String, Object>> selectMapListDirectByCriteria(Criteria criteria) {
		return selectMapListDirectByCriteria(criteria, null, false);
	}

	default List<Map<String, Object>> selectMapListDirectByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectMapListDirectByCriteria(criteria, orderBy, false);
	}


}
