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
public interface EntityCountMapper<E> extends EntityMapper<E> {

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntity)
	int countEntity(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default int countEntity(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntity(entity, false, includeEmptyFields, excludeFields);
	}

	default int countEntity(E entity, Set<String> includeEmptyFields) {
		return countEntity(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default int countEntity(E entity, boolean includeEmpty) {
		return countEntity(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default int countEntity(E entity) {
		return countEntity(entity, false);
	}



	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntity)
	int countEntityByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);


	default int countEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntityByMap(entity, false, includeEmptyFields, excludeFields);
	}

	default int countEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return countEntityByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default int countEntityByMap(Map<String, Object> entity, boolean includeEmpty) {
		return countEntityByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default int countEntityByMap(Map<String, Object> entity) {
		return countEntityByMap(entity, false);
	}

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntity)
	int countEntityByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);


	default int countEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntityByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	default int countEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return countEntityByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	default int countEntityByCriteria(Criteria criteria, boolean includeEmpty) {
		return countEntityByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default int countEntityByCriteria(Criteria criteria) {
		return countEntityByCriteria(criteria, false);
	}


}
