package io.polaris.mybatis.mapper;

import java.util.Map;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityUpdateByAnyMapper<E> extends EntityMapper<E> {


	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.updateEntityByAny)
	int updateEntityByAny(@Param(BindingKeys.ENTITY) E entity
		, @Param(BindingKeys.INCLUDE_EMPTY) boolean updateIncludeEmpty
		, @Param(BindingKeys.INCLUDE_EMPTY_COLUMNS) Set<String> updateIncludeEmptyFields
		, @Param(BindingKeys.EXCLUDE_COLUMNS) Set<String> updateExcludeFields
		, @Param(BindingKeys.WHERE) E where
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean whereIncludeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> whereIncludeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> whereExcludeFields);

	default int updateEntityByAny(
		E entity, Set<String> updateIncludeEmptyFields, Set<String> updateExcludeFields,
		E where, Set<String> whereIncludeEmptyFields, Set<String> whereExcludeFields) {
		return updateEntityByAny(entity, false, updateIncludeEmptyFields, updateExcludeFields, where, false, whereIncludeEmptyFields, whereExcludeFields);
	}

	default int updateEntityByAny(
		E entity, Set<String> updateIncludeEmptyFields,
		E where, Set<String> whereIncludeEmptyFields) {
		return updateEntityByAny(entity, false, updateIncludeEmptyFields, (Set<String>) null, where, false, whereIncludeEmptyFields, (Set<String>) null);
	}

	default int updateEntityByAny(E entity, boolean updateIncludeEmpty, E where, boolean whereIncludeEmpty) {
		return updateEntityByAny(entity, updateIncludeEmpty, (Set<String>) null, (Set<String>) null, where, whereIncludeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default int updateEntityByAny(E entity, E where) {
		return updateEntityByAny(entity, false, (Set<String>) null, (Set<String>) null, where, false, (Set<String>) null, (Set<String>) null);
	}


	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.updateEntityByAny)
	int updateEntityByAnyOfMap(@Param(BindingKeys.ENTITY) E entity
		, @Param(BindingKeys.INCLUDE_EMPTY) boolean updateIncludeEmpty
		, @Param(BindingKeys.INCLUDE_EMPTY_COLUMNS) Set<String> updateIncludeEmptyFields
		, @Param(BindingKeys.EXCLUDE_COLUMNS) Set<String> updateExcludeFields
		, @Param(BindingKeys.WHERE) Object where
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean whereIncludeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> whereIncludeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> whereExcludeFields);

	default int updateEntityByAnyOfMap(
		E entity, Set<String> updateIncludeEmptyFields, Set<String> updateExcludeFields,
		Criteria where, Set<String> whereIncludeEmptyFields, Set<String> whereExcludeFields) {
		return updateEntityByAnyOfMap(entity, false, updateIncludeEmptyFields, updateExcludeFields, where, false, whereIncludeEmptyFields, whereExcludeFields);
	}

	default int updateEntityByAnyOfMap(
		E entity, Set<String> updateIncludeEmptyFields,
		Criteria where, Set<String> whereIncludeEmptyFields) {
		return updateEntityByAnyOfMap(entity, false, updateIncludeEmptyFields, (Set<String>) null, where, false, whereIncludeEmptyFields, (Set<String>) null);
	}

	default int updateEntityByAnyOfMap(E entity, boolean updateIncludeEmpty, Map<String, Object> where, boolean whereIncludeEmpty) {
		return updateEntityByAnyOfMap(entity, updateIncludeEmpty, (Set<String>) null, (Set<String>) null, where, whereIncludeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default int updateEntityByAnyOfMap(E entity, Map<String, Object> where) {
		return updateEntityByAnyOfMap(entity, false, (Set<String>) null, (Set<String>) null, where, false, (Set<String>) null, (Set<String>) null);
	}


	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.updateEntityByAny)
	int updateEntityByCriteria(@Param(BindingKeys.ENTITY) E entity
		, @Param(BindingKeys.INCLUDE_EMPTY) boolean updateIncludeEmpty
		, @Param(BindingKeys.INCLUDE_EMPTY_COLUMNS) Set<String> updateIncludeEmptyFields
		, @Param(BindingKeys.EXCLUDE_COLUMNS) Set<String> updateExcludeFields
		, @Param(BindingKeys.WHERE) Criteria where
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean whereIncludeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> whereIncludeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> whereExcludeFields);


	default int updateEntityByCriteria(
		E entity, Set<String> updateIncludeEmptyFields, Set<String> updateExcludeFields,
		Criteria where, Set<String> whereIncludeEmptyFields, Set<String> whereExcludeFields) {
		return updateEntityByCriteria(entity, false, updateIncludeEmptyFields, updateExcludeFields, where, false, whereIncludeEmptyFields, whereExcludeFields);
	}

	default int updateEntityByCriteria(
		E entity, Set<String> updateIncludeEmptyFields,
		Criteria where, Set<String> whereIncludeEmptyFields) {
		return updateEntityByCriteria(entity, false, updateIncludeEmptyFields, (Set<String>) null, where, false, whereIncludeEmptyFields, (Set<String>) null);
	}

	default int updateEntityByCriteria(E entity, boolean updateIncludeEmpty, Criteria where, boolean whereIncludeEmpty) {
		return updateEntityByCriteria(entity, updateIncludeEmpty, (Set<String>) null, (Set<String>) null, where, whereIncludeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default int updateEntityByCriteria(E entity, Criteria where) {
		return updateEntityByCriteria(entity, false, (Set<String>) null, (Set<String>) null, where, false, (Set<String>) null, (Set<String>) null);
	}


}
