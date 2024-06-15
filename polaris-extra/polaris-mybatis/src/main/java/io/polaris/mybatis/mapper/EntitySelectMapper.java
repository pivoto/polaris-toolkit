package io.polaris.mybatis.mapper;

import java.util.Map;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntitySelectMapper<E> extends EntityMapper<E> {


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityById)
	E selectEntityById(@Param(BindingKeys.ENTITY) E entity);


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	E selectEntity(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default E selectEntity(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntity(entity, false, includeEmptyFields, excludeFields);
	}

	default E selectEntity(E entity, Set<String> includeEmptyFields) {
		return selectEntity(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default E selectEntity(E entity, boolean includeEmpty) {
		return selectEntity(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default E selectEntity(E entity) {
		return selectEntity(entity, false);
	}


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	E selectEntityByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default E selectEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityByMap(entity, false, includeEmptyFields, excludeFields);
	}

	default E selectEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return selectEntityByMap(entity, false, includeEmptyFields, null);
	}

	default E selectEntityByMap(Map<String, Object> entity, boolean includeEmpty) {
		return selectEntityByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default E selectEntityByMap(Map<String, Object> entity) {
		return selectEntityByMap(entity, false);
	}


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	E selectEntityByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default E selectEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	default E selectEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return selectEntityByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	default E selectEntityByCriteria(Criteria criteria, boolean includeEmpty) {
		return selectEntityByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default E selectEntityByCriteria(Criteria entity) {
		return selectEntityByCriteria(entity, false);
	}


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	Map<String, Object> selectMap(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Map<String, Object> selectMap(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMap(entity, false, includeEmptyFields, excludeFields);
	}

	default Map<String, Object> selectMap(E entity, Set<String> includeEmptyFields) {
		return selectMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default Map<String, Object> selectMap(E entity, boolean includeEmpty) {
		return selectMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default Map<String, Object> selectMap(E entity) {
		return selectMap(entity, false);
	}

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	Map<String, Object> selectMapByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Map<String, Object> selectMapByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapByMap(entity, false, includeEmptyFields, excludeFields);
	}

	default Map<String, Object> selectMapByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return selectMapByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default Map<String, Object> selectMapByMap(Map<String, Object> entity, boolean includeEmpty) {
		return selectMapByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default Map<String, Object> selectMapByMap(Map<String, Object> entity) {
		return selectMapByMap(entity, false);
	}


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	Map<String, Object> selectMapByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Map<String, Object> selectMapByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	default Map<String, Object> selectMapByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return selectMapByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	default Map<String, Object> selectMapByCriteria(Criteria criteria, boolean includeEmpty) {
		return selectMapByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default Map<String, Object> selectMapByCriteria(Criteria entity) {
		return selectMapByCriteria(entity, false);
	}
}
