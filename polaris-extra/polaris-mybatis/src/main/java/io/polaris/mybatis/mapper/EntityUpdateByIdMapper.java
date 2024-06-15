package io.polaris.mybatis.mapper;

import java.util.Set;

import io.polaris.core.collection.Iterables;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityUpdateByIdMapper<E> extends EntityMapper<E> {


	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.updateEntityById)
	int updateEntityById(@Param(BindingKeys.ENTITY) E entity
		, @Param(BindingKeys.INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.EXCLUDE_COLUMNS) Set<String> excludeFields);


	default int updateEntityById(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return updateEntityById(entity, false, includeEmptyFields, excludeFields);
	}


	default int updateEntityById(E entity, Set<String> includeEmptyFields) {
		return updateEntityById(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default int updateEntityById(E entity, boolean includeEmpty) {
		return updateEntityById(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default int updateEntityById(E entity) {
		return updateEntityById(entity, false, (Set<String>) null, (Set<String>) null);
	}

}
