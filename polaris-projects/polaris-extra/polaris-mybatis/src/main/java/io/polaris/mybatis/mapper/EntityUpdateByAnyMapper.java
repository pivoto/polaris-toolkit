package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.mybatis.consts.EntityMapperKeys;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.Map;
import java.util.Set;

/**
 * @author Qt
 * @since 1.8,  Aug 25, 2023
 */
public interface EntityUpdateByAnyMapper<E> extends EntityMapper<E> {


	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.updateEntityByAny)
	int updateEntityByAny(@Param(EntityMapperKeys.ENTITY) E entity
		, @Param(EntityMapperKeys.ENTITY_NULLS_INCLUDE) boolean entityNullsInclude
		, @Param(EntityMapperKeys.ENTITY_NULLS_KEYS) Set<String> entityNullFields
		, @Param(EntityMapperKeys.WHERE) E where
		, @Param(EntityMapperKeys.WHERE_NULLS_INCLUDE) boolean whereNullsInclude
		, @Param(EntityMapperKeys.WHERE_NULLS_KEYS) Set<String> whereNullFields);

	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.updateEntityByAny)
	int updateEntityByAnyOfMap(@Param(EntityMapperKeys.ENTITY) E entity
		, @Param(EntityMapperKeys.ENTITY_NULLS_INCLUDE) boolean entityNullsInclude
		, @Param(EntityMapperKeys.ENTITY_NULLS_KEYS) Set<String> entityNullFields
		, @Param(EntityMapperKeys.WHERE) Object where
		, @Param(EntityMapperKeys.WHERE_NULLS_INCLUDE) boolean whereNullsInclude
		, @Param(EntityMapperKeys.WHERE_NULLS_KEYS) Set<String> whereNullFields);

	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.updateEntityByAny)
	int updateEntityByCriteria(@Param(EntityMapperKeys.ENTITY) E entity
		, @Param(EntityMapperKeys.ENTITY_NULLS_INCLUDE) boolean entityNullsInclude
		, @Param(EntityMapperKeys.ENTITY_NULLS_KEYS) Set<String> entityNullFields
		, @Param(EntityMapperKeys.WHERE) Criteria where
		, @Param(EntityMapperKeys.WHERE_NULLS_INCLUDE) boolean whereNullsInclude
		, @Param(EntityMapperKeys.WHERE_NULLS_KEYS) Set<String> whereNullFields);


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
