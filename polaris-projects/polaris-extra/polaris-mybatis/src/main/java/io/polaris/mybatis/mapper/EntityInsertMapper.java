package io.polaris.mybatis.mapper;

import java.util.Map;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityInsertMapper<E> extends EntityMapper<E> {

	@InsertProvider(type = MapperProviders.class, method = MapperProviderKeys.insertEntity)
	int insertEntity(@Param(BindingKeys.ENTITY) E entity
		, @Param(BindingKeys.INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.EXCLUDE_COLUMNS) Set<String> excludeFields);


	default int insertEntity(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return insertEntity(entity, false, includeEmptyFields, excludeFields);
	}

	default int insertEntity(E entity, Set<String> includeEmptyFields) {
		return insertEntity(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default int insertEntity(E entity, boolean includeEmpty) {
		return insertEntity(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default int insertEntity(E entity) {
		return insertEntity(entity, false, (Set<String>) null, (Set<String>) null);
	}


	@InsertProvider(type = MapperProviders.class, method = MapperProviderKeys.insertEntity)
	int insertEntityByMap(@Param(BindingKeys.ENTITY) Map<String, Object> entity
		, @Param(BindingKeys.INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.EXCLUDE_COLUMNS) Set<String> excludeFields);


	default int insertEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return insertEntityByMap(entity, false, includeEmptyFields, excludeFields);
	}

	default int insertEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return insertEntityByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default int insertEntityByMap(Map<String, Object> entity, boolean includeEmpty) {
		return insertEntityByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default int insertEntityByMap(Map<String, Object> entity) {
		return insertEntityByMap(entity, false, (Set<String>) null, (Set<String>) null);
	}


}
