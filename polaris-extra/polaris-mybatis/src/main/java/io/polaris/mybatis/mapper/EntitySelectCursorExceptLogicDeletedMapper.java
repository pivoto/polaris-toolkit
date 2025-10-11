package io.polaris.mybatis.mapper;

import java.util.Map;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.mybatis.annotation.DynamicResultMapping;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.cursor.Cursor;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntitySelectCursorExceptLogicDeletedMapper<E> extends EntityMapper<E> {

	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursorExceptLogicDeleted(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Cursor<E> selectEntityCursorExceptLogicDeleted(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursorExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<E> selectEntityCursorExceptLogicDeleted(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityCursorExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<E> selectEntityCursorExceptLogicDeleted(E entity, OrderBy orderBy, boolean includeEmpty) {
		return selectEntityCursorExceptLogicDeleted(entity, orderBy, includeEmpty, null, null);
	}

	default Cursor<E> selectEntityCursorExceptLogicDeleted(E entity, OrderBy orderBy) {
		return selectEntityCursorExceptLogicDeleted(entity, orderBy, false);
	}

	default Cursor<E> selectEntityCursorExceptLogicDeleted(E entity) {
		return selectEntityCursorExceptLogicDeleted(entity, null, false);
	}


	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursorExceptLogicDeletedByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Cursor<E> selectEntityCursorExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursorExceptLogicDeletedByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<E> selectEntityCursorExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityCursorExceptLogicDeletedByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<E> selectEntityCursorExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeEmpty) {
		return selectEntityCursorExceptLogicDeletedByMap(entity, orderBy, includeEmpty, null, null);
	}

	default Cursor<E> selectEntityCursorExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectEntityCursorExceptLogicDeletedByMap(entity, orderBy, false);
	}

	default Cursor<E> selectEntityCursorExceptLogicDeletedByMap(Map<String, Object> entity) {
		return selectEntityCursorExceptLogicDeletedByMap(entity, null, false);
	}


	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursorExceptLogicDeletedByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Cursor<E> selectEntityCursorExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursorExceptLogicDeletedByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<E> selectEntityCursorExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityCursorExceptLogicDeletedByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<E> selectEntityCursorExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, boolean includeEmpty) {
		return selectEntityCursorExceptLogicDeletedByCriteria(criteria, orderBy, includeEmpty, null, null);
	}

	default Cursor<E> selectEntityCursorExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectEntityCursorExceptLogicDeletedByCriteria(criteria, orderBy, false);
	}

	default Cursor<E> selectEntityCursorExceptLogicDeletedByCriteria(Criteria criteria) {
		return selectEntityCursorExceptLogicDeletedByCriteria(criteria, null, false);
	}

	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursorExceptLogicDeleted(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeleted(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursorExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeleted(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapCursorExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeleted(E entity, OrderBy orderBy, boolean includeEmpty) {
		return selectMapCursorExceptLogicDeleted(entity, orderBy, includeEmpty, null, null);
	}

	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeleted(E entity, OrderBy orderBy) {
		return selectMapCursorExceptLogicDeleted(entity, orderBy, false);
	}

	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeleted(E entity) {
		return selectMapCursorExceptLogicDeleted(entity, null, false);
	}


	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursorExceptLogicDeletedByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapCursorExceptLogicDeletedByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeEmpty) {
		return selectMapCursorExceptLogicDeletedByMap(entity, orderBy, includeEmpty, null, null);
	}

	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByMap(Map<String, Object> entity) {
		return selectMapCursorExceptLogicDeletedByMap(entity, null, false);
	}

	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectMapCursorExceptLogicDeletedByMap(entity, orderBy, false);
	}


	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursorExceptLogicDeletedByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapCursorExceptLogicDeletedByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, boolean includeEmpty) {
		return selectMapCursorExceptLogicDeletedByCriteria(criteria, orderBy, includeEmpty, null, null);
	}

	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByCriteria(Criteria criteria) {
		return selectMapCursorExceptLogicDeletedByCriteria(criteria, null, false);
	}

	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectMapCursorExceptLogicDeletedByCriteria(criteria, orderBy, false);
	}

}
