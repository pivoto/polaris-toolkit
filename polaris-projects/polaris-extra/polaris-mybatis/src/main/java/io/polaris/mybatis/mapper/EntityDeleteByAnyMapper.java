package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;

import java.util.Map;
import java.util.Set;

/**
 * @author Qt
 * @since 1.8,  Aug 25, 2023
 */
public interface EntityDeleteByAnyMapper<E> extends EntityMapper<E> {


	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.deleteEntityByAny)
	int deleteEntityByAny(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeWhereNulls
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> whereNullsFields);

	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.deleteEntityByAny)
	int deleteEntityByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeWhereNulls
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> whereNullsFields);

	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.deleteEntityByAny)
	int deleteEntityByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeWhereNulls
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> whereNullsFields);


	default int deleteEntityByAny(E entity) {
		return deleteEntityByAny(entity, false, null);
	}


	default int deleteEntityByAny(E entity, boolean includeWhereNulls) {
		return deleteEntityByAny(entity, includeWhereNulls, null);
	}

	default int deleteEntityByAny(E entity, Set<String> whereNullsFields) {
		return deleteEntityByAny(entity, false, whereNullsFields);
	}


	default int deleteEntityByMap(Map<String, Object> entity) {
		return deleteEntityByMap(entity, false, null);
	}

	default int deleteEntityByMap(Map<String, Object> entity, boolean includeWhereNulls) {
		return deleteEntityByMap(entity, includeWhereNulls, null);
	}

	default int deleteEntityByMap(Map<String, Object> entity, Set<String> whereNullsFields) {
		return deleteEntityByMap(entity, false, whereNullsFields);
	}

	default int deleteEntityByCriteria(Criteria criteria) {
		return deleteEntityByCriteria(criteria, false, null);
	}

	default int deleteEntityByCriteria(Criteria criteria, boolean includeWhereNulls) {
		return deleteEntityByCriteria(criteria, includeWhereNulls, null);
	}

	default int deleteEntityByCriteria(Criteria criteria, Set<String> whereNullsFields) {
		return deleteEntityByCriteria(criteria, false, whereNullsFields);
	}


}
