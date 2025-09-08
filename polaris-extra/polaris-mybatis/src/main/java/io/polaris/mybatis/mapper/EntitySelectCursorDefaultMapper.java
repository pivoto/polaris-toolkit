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
public interface EntitySelectCursorDefaultMapper<E> extends EntityMapper<E> {

	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursor(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	default Cursor<E> selectEntityCursor(E entity, OrderBy orderBy, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursor(entity, orderBy, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	default Cursor<E> selectEntityCursor(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursor(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<E> selectEntityCursor(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityCursor(entity, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<E> selectEntityCursor(E entity, OrderBy orderBy, boolean includeEmpty) {
		return selectEntityCursor(entity, orderBy, includeEmpty, null, null);
	}

	default Cursor<E> selectEntityCursor(E entity, OrderBy orderBy) {
		return selectEntityCursor(entity, orderBy, false);
	}

	default Cursor<E> selectEntityCursor(E entity) {
		return selectEntityCursor(entity, null, false);
	}


	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursorByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	default Cursor<E> selectEntityCursorByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursorByMap(entity, orderBy, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	default Cursor<E> selectEntityCursorByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursorByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<E> selectEntityCursorByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityCursorByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<E> selectEntityCursorByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeEmpty) {
		return selectEntityCursorByMap(entity, orderBy, includeEmpty, null, null);
	}

	default Cursor<E> selectEntityCursorByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectEntityCursorByMap(entity, orderBy, false);
	}

	default Cursor<E> selectEntityCursorByMap(Map<String, Object> entity) {
		return selectEntityCursorByMap(entity, null, false);
	}


	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursorByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	default Cursor<E> selectEntityCursorByCriteria(Criteria criteria, OrderBy orderBy, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursorByCriteria(criteria, orderBy, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	default Cursor<E> selectEntityCursorByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursorByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<E> selectEntityCursorByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityCursorByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<E> selectEntityCursorByCriteria(Criteria criteria, OrderBy orderBy, boolean includeEmpty) {
		return selectEntityCursorByCriteria(criteria, orderBy, includeEmpty, null, null);
	}

	default Cursor<E> selectEntityCursorByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectEntityCursorByCriteria(criteria, orderBy, false);
	}

	default Cursor<E> selectEntityCursorByCriteria(Criteria criteria) {
		return selectEntityCursorByCriteria(criteria, null, false);
	}

	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursor(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	default Cursor<Map<String, Object>> selectMapCursor(E entity, OrderBy orderBy, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursor(entity, orderBy, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	default Cursor<Map<String, Object>> selectMapCursor(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursor(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<Map<String, Object>> selectMapCursor(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapCursor(entity, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<Map<String, Object>> selectMapCursor(E entity, OrderBy orderBy, boolean includeEmpty) {
		return selectMapCursor(entity, orderBy, includeEmpty, null, null);
	}

	default Cursor<Map<String, Object>> selectMapCursor(E entity, OrderBy orderBy) {
		return selectMapCursor(entity, orderBy, false);
	}

	default Cursor<Map<String, Object>> selectMapCursor(E entity) {
		return selectMapCursor(entity, null, false);
	}


	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursorByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	default Cursor<Map<String, Object>> selectMapCursorByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursorByMap(entity, orderBy, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	default Cursor<Map<String, Object>> selectMapCursorByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursorByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<Map<String, Object>> selectMapCursorByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapCursorByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<Map<String, Object>> selectMapCursorByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeEmpty) {
		return selectMapCursorByMap(entity, orderBy, includeEmpty, null, null);
	}

	default Cursor<Map<String, Object>> selectMapCursorByMap(Map<String, Object> entity) {
		return selectMapCursorByMap(entity, null, false);
	}

	default Cursor<Map<String, Object>> selectMapCursorByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectMapCursorByMap(entity, orderBy, false);
	}


	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursorByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	default Cursor<Map<String, Object>> selectMapCursorByCriteria(Criteria criteria, OrderBy orderBy, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursorByCriteria(criteria, orderBy, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	default Cursor<Map<String, Object>> selectMapCursorByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursorByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<Map<String, Object>> selectMapCursorByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapCursorByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<Map<String, Object>> selectMapCursorByCriteria(Criteria criteria, OrderBy orderBy, boolean includeEmpty) {
		return selectMapCursorByCriteria(criteria, orderBy, includeEmpty, null, null);
	}

	default Cursor<Map<String, Object>> selectMapCursorByCriteria(Criteria criteria) {
		return selectMapCursorByCriteria(criteria, null, false);
	}

	default Cursor<Map<String, Object>> selectMapCursorByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectMapCursorByCriteria(criteria, orderBy, false);
	}
}
