package io.polaris.mybatis.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntitySelectListMapper<E> extends EntityMapper<E> {

	// region normal

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	List<E> selectEntityList(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<E> selectEntityList(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityList(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<E> selectEntityList(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityList(entity, orderBy, false, includeEmptyFields, null);
	}

	default List<E> selectEntityList(E entity, OrderBy orderBy, boolean includeEmpty) {
		return selectEntityList(entity, orderBy, includeEmpty, null, null);
	}

	default List<E> selectEntityList(E entity, OrderBy orderBy) {
		return selectEntityList(entity, orderBy, false);
	}

	default List<E> selectEntityList(E entity) {
		return selectEntityList(entity, null, false);
	}


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	List<E> selectEntityListByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<E> selectEntityListByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityListByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<E> selectEntityListByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityListByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	default List<E> selectEntityListByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeEmpty) {
		return selectEntityListByMap(entity, orderBy, includeEmpty, null, null);
	}

	default List<E> selectEntityListByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectEntityListByMap(entity, orderBy, false);
	}

	default List<E> selectEntityListByMap(Map<String, Object> entity) {
		return selectEntityListByMap(entity, null, false);
	}

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	List<E> selectEntityListByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<E> selectEntityListByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityListByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<E> selectEntityListByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityListByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	default List<E> selectEntityListByCriteria(Criteria criteria, OrderBy orderBy, boolean includeEmpty) {
		return selectEntityListByCriteria(criteria, orderBy, includeEmpty, null, null);
	}

	default List<E> selectEntityListByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectEntityListByCriteria(criteria, orderBy, false);
	}

	default List<E> selectEntityListByCriteria(Criteria criteria) {
		return selectEntityListByCriteria(criteria, null, false);
	}

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	List<Map<String, Object>> selectMapList(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<Map<String, Object>> selectMapList(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapList(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<Map<String, Object>> selectMapList(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapList(entity, orderBy, false, includeEmptyFields, null);
	}

	default List<Map<String, Object>> selectMapList(E entity, OrderBy orderBy, boolean includeEmpty) {
		return selectMapList(entity, orderBy, includeEmpty, null, null);
	}

	default List<Map<String, Object>> selectMapList(E entity, OrderBy orderBy) {
		return selectMapList(entity, orderBy, false);
	}

	default List<Map<String, Object>> selectMapList(E entity) {
		return selectMapList(entity, null, false);
	}


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	List<Map<String, Object>> selectMapListByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<Map<String, Object>> selectMapListByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapListByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<Map<String, Object>> selectMapListByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapListByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	default List<Map<String, Object>> selectMapListByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeEmpty) {
		return selectMapListByMap(entity, orderBy, includeEmpty, null, null);
	}

	default List<Map<String, Object>> selectMapListByMap(Map<String, Object> entity) {
		return selectMapListByMap(entity, null, false);
	}

	default List<Map<String, Object>> selectMapListByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectMapListByMap(entity, orderBy, false);
	}


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	List<Map<String, Object>> selectMapListByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<Map<String, Object>> selectMapListByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapListByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<Map<String, Object>> selectMapListByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapListByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	default List<Map<String, Object>> selectMapListByCriteria(Criteria criteria, OrderBy orderBy, boolean includeEmpty) {
		return selectMapListByCriteria(criteria, orderBy, includeEmpty, null, null);
	}

	default List<Map<String, Object>> selectMapListByCriteria(Criteria criteria) {
		return selectMapListByCriteria(criteria, null, false);
	}

	default List<Map<String, Object>> selectMapListByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectMapListByCriteria(criteria, orderBy, false);
	}


	// end normal

	// region except logic deleted

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	List<E> selectEntityListExceptLogicDeleted(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<E> selectEntityListExceptLogicDeleted(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityListExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<E> selectEntityListExceptLogicDeleted(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityListExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, null);
	}

	default List<E> selectEntityListExceptLogicDeleted(E entity, OrderBy orderBy, boolean includeEmpty) {
		return selectEntityListExceptLogicDeleted(entity, orderBy, includeEmpty, null, null);
	}

	default List<E> selectEntityListExceptLogicDeleted(E entity, OrderBy orderBy) {
		return selectEntityListExceptLogicDeleted(entity, orderBy, false);
	}

	default List<E> selectEntityListExceptLogicDeleted(E entity) {
		return selectEntityListExceptLogicDeleted(entity, null, false);
	}


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	List<E> selectEntityListExceptLogicDeletedByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<E> selectEntityListExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityListExceptLogicDeletedByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<E> selectEntityListExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityListExceptLogicDeletedByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	default List<E> selectEntityListExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeEmpty) {
		return selectEntityListExceptLogicDeletedByMap(entity, orderBy, includeEmpty, null, null);
	}

	default List<E> selectEntityListExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectEntityListExceptLogicDeletedByMap(entity, orderBy, false);
	}

	default List<E> selectEntityListExceptLogicDeletedByMap(Map<String, Object> entity) {
		return selectEntityListExceptLogicDeletedByMap(entity, null, false);
	}

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	List<E> selectEntityListExceptLogicDeletedByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<E> selectEntityListExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityListExceptLogicDeletedByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<E> selectEntityListExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityListExceptLogicDeletedByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	default List<E> selectEntityListExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, boolean includeEmpty) {
		return selectEntityListExceptLogicDeletedByCriteria(criteria, orderBy, includeEmpty, null, null);
	}

	default List<E> selectEntityListExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectEntityListExceptLogicDeletedByCriteria(criteria, orderBy, false);
	}

	default List<E> selectEntityListExceptLogicDeletedByCriteria(Criteria criteria) {
		return selectEntityListExceptLogicDeletedByCriteria(criteria, null, false);
	}

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	List<Map<String, Object>> selectMapListExceptLogicDeleted(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<Map<String, Object>> selectMapListExceptLogicDeleted(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapListExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<Map<String, Object>> selectMapListExceptLogicDeleted(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapListExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, null);
	}

	default List<Map<String, Object>> selectMapListExceptLogicDeleted(E entity, OrderBy orderBy, boolean includeEmpty) {
		return selectMapListExceptLogicDeleted(entity, orderBy, includeEmpty, null, null);
	}

	default List<Map<String, Object>> selectMapListExceptLogicDeleted(E entity, OrderBy orderBy) {
		return selectMapListExceptLogicDeleted(entity, orderBy, false);
	}

	default List<Map<String, Object>> selectMapListExceptLogicDeleted(E entity) {
		return selectMapListExceptLogicDeleted(entity, null, false);
	}


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	List<Map<String, Object>> selectMapListExceptLogicDeletedByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<Map<String, Object>> selectMapListExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapListExceptLogicDeletedByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<Map<String, Object>> selectMapListExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapListExceptLogicDeletedByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	default List<Map<String, Object>> selectMapListExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeEmpty) {
		return selectMapListExceptLogicDeletedByMap(entity, orderBy, includeEmpty, null, null);
	}

	default List<Map<String, Object>> selectMapListExceptLogicDeletedByMap(Map<String, Object> entity) {
		return selectMapListExceptLogicDeletedByMap(entity, null, false);
	}

	default List<Map<String, Object>> selectMapListExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectMapListExceptLogicDeletedByMap(entity, orderBy, false);
	}


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	List<Map<String, Object>> selectMapListExceptLogicDeletedByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default List<Map<String, Object>> selectMapListExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapListExceptLogicDeletedByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	default List<Map<String, Object>> selectMapListExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapListExceptLogicDeletedByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	default List<Map<String, Object>> selectMapListExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, boolean includeEmpty) {
		return selectMapListExceptLogicDeletedByCriteria(criteria, orderBy, includeEmpty, null, null);
	}

	default List<Map<String, Object>> selectMapListExceptLogicDeletedByCriteria(Criteria criteria) {
		return selectMapListExceptLogicDeletedByCriteria(criteria, null, false);
	}

	default List<Map<String, Object>> selectMapListExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectMapListExceptLogicDeletedByCriteria(criteria, orderBy, false);
	}

	// endregion
}
