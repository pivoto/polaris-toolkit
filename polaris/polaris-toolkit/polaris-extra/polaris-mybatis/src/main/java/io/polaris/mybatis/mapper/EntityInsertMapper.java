package io.polaris.mybatis.mapper;

import io.polaris.mybatis.consts.EntityMapperKeys;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;

import java.util.Map;
import java.util.Set;

/**
 * @author Qt
 * @since 1.8,  Aug 25, 2023
 */
public interface EntityInsertMapper<E> extends EntityMapper<E> {

	@InsertProvider(type = MapperProviders.class, method = MapperProviderKeys.insertEntity)
	int insertEntity(@Param(EntityMapperKeys.ENTITY) E entity
		, @Param(EntityMapperKeys.ENTITY_NULLS_INCLUDE) boolean entityNullsInclude
		, @Param(EntityMapperKeys.ENTITY_NULLS_KEYS) Set<String> entityNullsFields);

	@InsertProvider(type = MapperProviders.class, method = MapperProviderKeys.insertEntity)
	int insertEntityByMap(@Param(EntityMapperKeys.ENTITY) Map<String, Object> entity
		, @Param(EntityMapperKeys.ENTITY_NULLS_INCLUDE) boolean entityNullsInclude
		, @Param(EntityMapperKeys.ENTITY_NULLS_KEYS) Set<String> entityNullsFields);


	default int insertEntity(E entity) {
		return insertEntity(entity, false, null);
	}

	default int insertEntity(E entity, boolean entityNullsInclude) {
		return insertEntity(entity, entityNullsInclude, null);
	}

	default int insertEntity(E entity, Set<String> entityNullsFields) {
		return insertEntity(entity, false, entityNullsFields);
	}


	default int insertEntityByMap(Map<String, Object> entity) {
		return insertEntityByMap(entity, false, null);
	}

	default int insertEntityByMap(Map<String, Object> entity, boolean entityNullsInclude) {
		return insertEntityByMap(entity, entityNullsInclude, null);
	}

	default int insertEntityByMap(Map<String, Object> entity, Set<String> entityNullsFields) {
		return insertEntityByMap(entity, false, entityNullsFields);
	}


}
