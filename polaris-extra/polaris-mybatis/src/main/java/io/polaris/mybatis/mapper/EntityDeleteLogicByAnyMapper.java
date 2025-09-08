package io.polaris.mybatis.mapper;

import java.util.Map;
import java.util.Set;

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
public interface EntityDeleteLogicByAnyMapper<E> extends EntityMapper<E> {


	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.deleteEntityLogicByAny)
	int logicDeleteEntityByAny(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default int logicDeleteEntityByAny(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return logicDeleteEntityByAny(entity, false, includeEmptyFields, excludeFields);
	}

	default int logicDeleteEntityByAny(E entity, Set<String> includeEmptyFields) {
		return logicDeleteEntityByAny(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default int logicDeleteEntityByAny(E entity, boolean includeEmpty) {
		return logicDeleteEntityByAny(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default int logicDeleteEntityByAny(E entity) {
		return logicDeleteEntityByAny(entity, false, (Set<String>) null, (Set<String>) null);
	}


	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.deleteEntityLogicByAny)
	int logicDeleteEntityByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default int logicDeleteEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return logicDeleteEntityByMap(entity, false, includeEmptyFields, excludeFields);
	}

	default int logicDeleteEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return logicDeleteEntityByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default int logicDeleteEntityByMap(Map<String, Object> entity, boolean includeEmpty) {
		return logicDeleteEntityByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default int logicDeleteEntityByMap(Map<String, Object> entity) {
		return logicDeleteEntityByMap(entity, false, (Set<String>) null, (Set<String>) null);
	}

	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.deleteEntityLogicByAny)
	int logicDeleteEntityByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);


	default int logicDeleteEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return logicDeleteEntityByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	default int logicDeleteEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return logicDeleteEntityByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	default int logicDeleteEntityByCriteria(Criteria criteria, boolean includeEmpty) {
		return logicDeleteEntityByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default int logicDeleteEntityByCriteria(Criteria criteria) {
		return logicDeleteEntityByCriteria(criteria, false, (Set<String>) null, (Set<String>) null);
	}

}
