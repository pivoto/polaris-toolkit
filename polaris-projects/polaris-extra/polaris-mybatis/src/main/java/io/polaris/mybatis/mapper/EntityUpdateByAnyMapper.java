package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.Map;
import java.util.Set;

/**
 * @author Qt
 * @since  Aug 25, 2023
 */
public interface EntityUpdateByAnyMapper<E> extends EntityMapper<E> {


	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.updateEntityByAny)
	int updateEntityByAny(@Param(BindingKeys.ENTITY) E entity
		, @Param(BindingKeys.INCLUDE_EMPTY) boolean entityNullsInclude
		, @Param(BindingKeys.INCLUDE_EMPTY_COLUMNS) Set<String> entityNullFields
		, @Param(BindingKeys.WHERE) E where
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean whereNullsInclude
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> whereNullFields);

	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.updateEntityByAny)
	int updateEntityByAnyOfMap(@Param(BindingKeys.ENTITY) E entity
		, @Param(BindingKeys.INCLUDE_EMPTY) boolean entityNullsInclude
		, @Param(BindingKeys.INCLUDE_EMPTY_COLUMNS) Set<String> entityNullFields
		, @Param(BindingKeys.WHERE) Object where
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean whereNullsInclude
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> whereNullFields);

	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.updateEntityByAny)
	int updateEntityByCriteria(@Param(BindingKeys.ENTITY) E entity
		, @Param(BindingKeys.INCLUDE_EMPTY) boolean entityNullsInclude
		, @Param(BindingKeys.INCLUDE_EMPTY_COLUMNS) Set<String> entityNullFields
		, @Param(BindingKeys.WHERE) Criteria where
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean whereNullsInclude
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> whereNullFields);


	default int updateEntityByAny(E entity, E where) {
		return updateEntityByAny(entity, false, null, where, false, null);
	}

	default int updateEntityByAny(E entity, boolean entityNullsInclude, E where, boolean whereNullsInclude) {
		return updateEntityByAny(entity, entityNullsInclude, null, where, whereNullsInclude, null);
	}


	default int updateEntityByCriteria(E entity, Criteria where) {
		return updateEntityByCriteria(entity, false, null, where, false, null);
	}

	default int updateEntityByCriteria(E entity, boolean entityNullsInclude, Criteria where, boolean whereNullsInclude) {
		return updateEntityByCriteria(entity, entityNullsInclude, null, where, whereNullsInclude, null);
	}


	default int updateEntityByAnyOfMap(E entity, Map<String, Object> where) {
		return updateEntityByAnyOfMap(entity, false, null, where, false, null);
	}

	default int updateEntityByAnyOfMap(E entity, boolean entityNullsInclude, Map<String, Object> where, boolean whereNullsInclude) {
		return updateEntityByAnyOfMap(entity, entityNullsInclude, null, where, whereNullsInclude, null);
	}

}
