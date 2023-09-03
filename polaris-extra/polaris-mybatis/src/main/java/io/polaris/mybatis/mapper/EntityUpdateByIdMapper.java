package io.polaris.mybatis.mapper;

import io.polaris.mybatis.consts.EntityMapperKeys;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.Set;

/**
 * @author Qt
 * @since 1.8,  Aug 25, 2023
 */
public interface EntityUpdateByIdMapper<E> extends EntityMapper<E> {


	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.updateEntityById)
	int updateEntityById(@Param(EntityMapperKeys.ENTITY) E entity
		, @Param(EntityMapperKeys.ENTITY_NULLS_INCLUDE) boolean entityNullsInclude
		, @Param(EntityMapperKeys.ENTITY_NULLS_KEYS) Set<String> entityNullFields);


	default int updateEntityById(E entity) {
		return updateEntityById(entity, false, null);
	}

	default int updateEntityById(E entity, boolean entityNullsInclude) {
		return updateEntityById(entity, entityNullsInclude, null);
	}

	default int updateEntityById(E entity, Set<String> entityNullFields) {
		return updateEntityById(entity, false, entityNullFields);
	}

}
