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
	Cursor<E> selectEntityCursorDirect(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Cursor<E> selectEntityCursorDirect(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursorDirect(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<E> selectEntityCursorDirect(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityCursorDirect(entity, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<E> selectEntityCursorDirect(E entity, OrderBy orderBy, boolean includeEmpty) {
		return selectEntityCursorDirect(entity, orderBy, includeEmpty, null, null);
	}

	default Cursor<E> selectEntityCursorDirect(E entity, OrderBy orderBy) {
		return selectEntityCursorDirect(entity, orderBy, false);
	}

	default Cursor<E> selectEntityCursorDirect(E entity) {
		return selectEntityCursorDirect(entity, null, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursorDirectByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Cursor<E> selectEntityCursorDirectByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursorDirectByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<E> selectEntityCursorDirectByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityCursorDirectByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<E> selectEntityCursorDirectByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeEmpty) {
		return selectEntityCursorDirectByMap(entity, orderBy, includeEmpty, null, null);
	}

	default Cursor<E> selectEntityCursorDirectByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectEntityCursorDirectByMap(entity, orderBy, false);
	}

	default Cursor<E> selectEntityCursorDirectByMap(Map<String, Object> entity) {
		return selectEntityCursorDirectByMap(entity, null, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursorDirectByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Cursor<E> selectEntityCursorDirectByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursorDirectByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<E> selectEntityCursorDirectByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityCursorDirectByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<E> selectEntityCursorDirectByCriteria(Criteria criteria, OrderBy orderBy, boolean includeEmpty) {
		return selectEntityCursorDirectByCriteria(criteria, orderBy, includeEmpty, null, null);
	}

	default Cursor<E> selectEntityCursorDirectByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectEntityCursorDirectByCriteria(criteria, orderBy, false);
	}

	default Cursor<E> selectEntityCursorDirectByCriteria(Criteria criteria) {
		return selectEntityCursorDirectByCriteria(criteria, null, false);
	}

	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursorDirect(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Cursor<Map<String, Object>> selectMapCursorDirect(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursorDirect(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<Map<String, Object>> selectMapCursorDirect(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapCursorDirect(entity, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<Map<String, Object>> selectMapCursorDirect(E entity, OrderBy orderBy, boolean includeEmpty) {
		return selectMapCursorDirect(entity, orderBy, includeEmpty, null, null);
	}

	default Cursor<Map<String, Object>> selectMapCursorDirect(E entity, OrderBy orderBy) {
		return selectMapCursorDirect(entity, orderBy, false);
	}

	default Cursor<Map<String, Object>> selectMapCursorDirect(E entity) {
		return selectMapCursorDirect(entity, null, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursorDirectByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Cursor<Map<String, Object>> selectMapCursorDirectByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursorDirectByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<Map<String, Object>> selectMapCursorDirectByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapCursorDirectByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<Map<String, Object>> selectMapCursorDirectByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeEmpty) {
		return selectMapCursorDirectByMap(entity, orderBy, includeEmpty, null, null);
	}

	default Cursor<Map<String, Object>> selectMapCursorDirectByMap(Map<String, Object> entity) {
		return selectMapCursorDirectByMap(entity, null, false);
	}

	default Cursor<Map<String, Object>> selectMapCursorDirectByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectMapCursorDirectByMap(entity, orderBy, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursorDirectByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Cursor<Map<String, Object>> selectMapCursorDirectByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursorDirectByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	default Cursor<Map<String, Object>> selectMapCursorDirectByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapCursorDirectByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	default Cursor<Map<String, Object>> selectMapCursorDirectByCriteria(Criteria criteria, OrderBy orderBy, boolean includeEmpty) {
		return selectMapCursorDirectByCriteria(criteria, orderBy, includeEmpty, null, null);
	}

	default Cursor<Map<String, Object>> selectMapCursorDirectByCriteria(Criteria criteria) {
		return selectMapCursorDirectByCriteria(criteria, null, false);
	}

	default Cursor<Map<String, Object>> selectMapCursorDirectByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectMapCursorDirectByCriteria(criteria, orderBy, false);
	}

}
