package io.polaris.mybatis.mapper;

import java.util.Map;
import java.util.Set;

import io.polaris.core.collection.Iterables;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityDeleteByAnyMapper<E> extends EntityMapper<E> {


	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.deleteEntityByAny)
	int deleteEntityByAny(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default int deleteEntityByAny(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return deleteEntityByAny(entity, false, includeEmptyFields, excludeFields);
	}

	default int deleteEntityByAny(E entity, Set<String> includeEmptyFields) {
		return deleteEntityByAny(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default int deleteEntityByAny(E entity, boolean includeEmpty) {
		return deleteEntityByAny(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default int deleteEntityByAny(E entity) {
		return deleteEntityByAny(entity, false, (Set<String>) null, (Set<String>) null);
	}


	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.deleteEntityByAny)
	int deleteEntityByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default int deleteEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return deleteEntityByMap(entity, false, includeEmptyFields, excludeFields);
	}

	default int deleteEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return deleteEntityByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default int deleteEntityByMap(Map<String, Object> entity, boolean includeEmpty) {
		return deleteEntityByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default int deleteEntityByMap(Map<String, Object> entity) {
		return deleteEntityByMap(entity, false, (Set<String>) null, (Set<String>) null);
	}

	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.deleteEntityByAny)
	int deleteEntityByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);


	default int deleteEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return deleteEntityByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	default int deleteEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return deleteEntityByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	default int deleteEntityByCriteria(Criteria criteria, boolean includeEmpty) {
		return deleteEntityByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default int deleteEntityByCriteria(Criteria criteria) {
		return deleteEntityByCriteria(criteria, false, (Set<String>) null, (Set<String>) null);
	}


}
