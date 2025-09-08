package io.polaris.mybatis.mapper;

import java.util.Map;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.mybatis.annotation.WithLogicDeleted;
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
public interface EntitySelectCursorDirectMapper<E> extends EntityMapper<E> {

	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	@Options(fetchSize = 1000)
	Cursor<E> directSelectEntityCursor(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Cursor<E> directSelectEntityCursor(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntityCursor(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<E> directSelectEntityCursor(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectEntityCursor(entity, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<E> directSelectEntityCursor(E entity, OrderBy orderBy, boolean includeEmpty) {
		return directSelectEntityCursor(entity, orderBy, includeEmpty, null, null);
	}

	default Cursor<E> directSelectEntityCursor(E entity, OrderBy orderBy) {
		return directSelectEntityCursor(entity, orderBy, false);
	}

	default Cursor<E> directSelectEntityCursor(E entity) {
		return directSelectEntityCursor(entity, null, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	@Options(fetchSize = 1000)
	Cursor<E> directSelectEntityCursorByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Cursor<E> directSelectEntityCursorByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntityCursorByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<E> directSelectEntityCursorByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectEntityCursorByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<E> directSelectEntityCursorByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeEmpty) {
		return directSelectEntityCursorByMap(entity, orderBy, includeEmpty, null, null);
	}

	default Cursor<E> directSelectEntityCursorByMap(Map<String, Object> entity, OrderBy orderBy) {
		return directSelectEntityCursorByMap(entity, orderBy, false);
	}

	default Cursor<E> directSelectEntityCursorByMap(Map<String, Object> entity) {
		return directSelectEntityCursorByMap(entity, null, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	@Options(fetchSize = 1000)
	Cursor<E> directSelectEntityCursorByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Cursor<E> directSelectEntityCursorByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntityCursorByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<E> directSelectEntityCursorByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectEntityCursorByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<E> directSelectEntityCursorByCriteria(Criteria criteria, OrderBy orderBy, boolean includeEmpty) {
		return directSelectEntityCursorByCriteria(criteria, orderBy, includeEmpty, null, null);
	}

	default Cursor<E> directSelectEntityCursorByCriteria(Criteria criteria, OrderBy orderBy) {
		return directSelectEntityCursorByCriteria(criteria, orderBy, false);
	}

	default Cursor<E> directSelectEntityCursorByCriteria(Criteria criteria) {
		return directSelectEntityCursorByCriteria(criteria, null, false);
	}

	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> directSelectMapCursor(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Cursor<Map<String, Object>> directSelectMapCursor(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectMapCursor(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<Map<String, Object>> directSelectMapCursor(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectMapCursor(entity, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<Map<String, Object>> directSelectMapCursor(E entity, OrderBy orderBy, boolean includeEmpty) {
		return directSelectMapCursor(entity, orderBy, includeEmpty, null, null);
	}

	default Cursor<Map<String, Object>> directSelectMapCursor(E entity, OrderBy orderBy) {
		return directSelectMapCursor(entity, orderBy, false);
	}

	default Cursor<Map<String, Object>> directSelectMapCursor(E entity) {
		return directSelectMapCursor(entity, null, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> directSelectMapCursorByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Cursor<Map<String, Object>> directSelectMapCursorByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectMapCursorByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<Map<String, Object>> directSelectMapCursorByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectMapCursorByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<Map<String, Object>> directSelectMapCursorByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeEmpty) {
		return directSelectMapCursorByMap(entity, orderBy, includeEmpty, null, null);
	}

	default Cursor<Map<String, Object>> directSelectMapCursorByMap(Map<String, Object> entity) {
		return directSelectMapCursorByMap(entity, null, false);
	}

	default Cursor<Map<String, Object>> directSelectMapCursorByMap(Map<String, Object> entity, OrderBy orderBy) {
		return directSelectMapCursorByMap(entity, orderBy, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> directSelectMapCursorByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Cursor<Map<String, Object>> directSelectMapCursorByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectMapCursorByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<Map<String, Object>> directSelectMapCursorByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectMapCursorByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<Map<String, Object>> directSelectMapCursorByCriteria(Criteria criteria, OrderBy orderBy, boolean includeEmpty) {
		return directSelectMapCursorByCriteria(criteria, orderBy, includeEmpty, null, null);
	}

	default Cursor<Map<String, Object>> directSelectMapCursorByCriteria(Criteria criteria) {
		return directSelectMapCursorByCriteria(criteria, null, false);
	}

	default Cursor<Map<String, Object>> directSelectMapCursorByCriteria(Criteria criteria, OrderBy orderBy) {
		return directSelectMapCursorByCriteria(criteria, orderBy, false);
	}

}
