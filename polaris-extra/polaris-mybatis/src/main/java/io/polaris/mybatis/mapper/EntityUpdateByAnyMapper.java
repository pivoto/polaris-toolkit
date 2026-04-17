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
 * 实体更新映射器接口，提供基于任意条件更新实体的方法。
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityUpdateByAnyMapper<E> extends EntityMapper<E> {

	/**
	 * 根据任意实体条件更新实体。
	 *
	 * @param entity 要更新的实体对象
	 * @param updateIncludeAllEmpty 是否包含所有空值字段进行更新
	 * @param updateIncludeEmptyFields 包含空值的字段集合
	 * @param updateExcludeFields 排除更新的字段集合
	 * @param where 作为更新条件的实体对象
	 * @param whereIncludeAllEmpty 是否包含所有空值字段作为条件
	 * @param whereIncludeAllEmptyFields 包含空值的条件字段集合
	 * @param whereExcludeFields 排除作为条件的字段集合
	 * @return 更新记录数
	 */
	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.updateEntityByAny)
	int updateEntityByAny(@Param(BindingKeys.ENTITY) E entity
		, @Param(BindingKeys.INCLUDE_ALL_EMPTY) boolean updateIncludeAllEmpty
		, @Param(BindingKeys.INCLUDE_EMPTY_COLUMNS) Set<String> updateIncludeEmptyFields
		, @Param(BindingKeys.EXCLUDE_COLUMNS) Set<String> updateExcludeFields
		, @Param(BindingKeys.WHERE) E where
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean whereIncludeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> whereIncludeAllEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> whereExcludeFields);

	/**
	 * 根据指定字段更新实体，默认不包含空值字段。
	 *
	 * @param entity 要更新的实体对象
	 * @param updateIncludeEmptyFields 包含空值的更新字段集合
	 * @param updateExcludeFields 排除更新的字段集合
	 * @param where 作为更新条件的实体对象
	 * @param whereIncludeAllEmptyFields 包含空值的条件字段集合
	 * @param whereExcludeFields 排除作为条件的字段集合
	 * @return 更新记录数
	 */
	default int updateEntityByAny(
		E entity, Set<String> updateIncludeEmptyFields, Set<String> updateExcludeFields,
		E where, Set<String> whereIncludeAllEmptyFields, Set<String> whereExcludeFields) {
		return updateEntityByAny(entity, false, updateIncludeEmptyFields, updateExcludeFields, where, false, whereIncludeAllEmptyFields, whereExcludeFields);
	}

	/**
	 * 根据指定字段更新实体，默认不包含空值字段且无排除字段。
	 *
	 * @param entity 要更新的实体对象
	 * @param updateIncludeEmptyFields 包含空值的更新字段集合
	 * @param where 作为更新条件的实体对象
	 * @param whereIncludeAllEmptyFields 包含空值的条件字段集合
	 * @return 更新记录数
	 */
	default int updateEntityByAny(
		E entity, Set<String> updateIncludeEmptyFields,
		E where, Set<String> whereIncludeAllEmptyFields) {
		return updateEntityByAny(entity, false, updateIncludeEmptyFields, (Set<String>) null, where, false, whereIncludeAllEmptyFields, (Set<String>) null);
	}

	/**
	 * 更新实体，可选择是否包含空值字段。
	 *
	 * @param entity 要更新的实体对象
	 * @param updateIncludeAllEmpty 是否包含所有空值字段进行更新
	 * @param where 作为更新条件的实体对象
	 * @param whereIncludeAllEmpty 是否包含所有空值字段作为条件
	 * @return 更新记录数
	 */
	default int updateEntityByAny(E entity, boolean updateIncludeAllEmpty, E where, boolean whereIncludeAllEmpty) {
		return updateEntityByAny(entity, updateIncludeAllEmpty, (Set<String>) null, (Set<String>) null, where, whereIncludeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 更新实体，默认不包含空值字段。
	 *
	 * @param entity 要更新的实体对象
	 * @param where 作为更新条件的实体对象
	 * @return 更新记录数
	 */
	default int updateEntityByAny(E entity, E where) {
		return updateEntityByAny(entity, false, (Set<String>) null, (Set<String>) null, where, false, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据任意Map条件更新实体。
	 *
	 * @param entity 要更新的实体对象
	 * @param updateIncludeAllEmpty 是否包含所有空值字段进行更新
	 * @param updateIncludeEmptyFields 包含空值的字段集合
	 * @param updateExcludeFields 排除更新的字段集合
	 * @param where 作为更新条件的Map对象
	 * @param whereIncludeAllEmpty 是否包含所有空值字段作为条件
	 * @param whereIncludeAllEmptyFields 包含空值的条件字段集合
	 * @param whereExcludeFields 排除作为条件的字段集合
	 * @return 更新记录数
	 */
	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.updateEntityByAny)
	int updateEntityByAnyOfMap(@Param(BindingKeys.ENTITY) E entity
		, @Param(BindingKeys.INCLUDE_ALL_EMPTY) boolean updateIncludeAllEmpty
		, @Param(BindingKeys.INCLUDE_EMPTY_COLUMNS) Set<String> updateIncludeEmptyFields
		, @Param(BindingKeys.EXCLUDE_COLUMNS) Set<String> updateExcludeFields
		, @Param(BindingKeys.WHERE) Object where
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean whereIncludeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> whereIncludeAllEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> whereExcludeFields);

	/**
	 * 根据指定字段更新实体，默认不包含空值字段。
	 *
	 * @param entity 要更新的实体对象
	 * @param updateIncludeEmptyFields 包含空值的更新字段集合
	 * @param updateExcludeFields 排除更新的字段集合
	 * @param where 作为更新条件的Criteria对象
	 * @param whereIncludeAllEmptyFields 包含空值的条件字段集合
	 * @param whereExcludeFields 排除作为条件的字段集合
	 * @return 更新记录数
	 */
	default int updateEntityByAnyOfMap(
		E entity, Set<String> updateIncludeEmptyFields, Set<String> updateExcludeFields,
		Criteria where, Set<String> whereIncludeAllEmptyFields, Set<String> whereExcludeFields) {
		return updateEntityByAnyOfMap(entity, false, updateIncludeEmptyFields, updateExcludeFields, where, false, whereIncludeAllEmptyFields, whereExcludeFields);
	}

	/**
	 * 根据指定字段更新实体，默认不包含空值字段且无排除字段。
	 *
	 * @param entity 要更新的实体对象
	 * @param updateIncludeEmptyFields 包含空值的更新字段集合
	 * @param where 作为更新条件的Criteria对象
	 * @param whereIncludeAllEmptyFields 包含空值的条件字段集合
	 * @return 更新记录数
	 */
	default int updateEntityByAnyOfMap(
		E entity, Set<String> updateIncludeEmptyFields,
		Criteria where, Set<String> whereIncludeAllEmptyFields) {
		return updateEntityByAnyOfMap(entity, false, updateIncludeEmptyFields, (Set<String>) null, where, false, whereIncludeAllEmptyFields, (Set<String>) null);
	}

	/**
	 * 更新实体，可选择是否包含空值字段。
	 *
	 * @param entity 要更新的实体对象
	 * @param updateIncludeAllEmpty 是否包含所有空值字段进行更新
	 * @param where 作为更新条件的Map对象
	 * @param whereIncludeAllEmpty 是否包含所有空值字段作为条件
	 * @return 更新记录数
	 */
	default int updateEntityByAnyOfMap(E entity, boolean updateIncludeAllEmpty, Map<String, Object> where, boolean whereIncludeAllEmpty) {
		return updateEntityByAnyOfMap(entity, updateIncludeAllEmpty, (Set<String>) null, (Set<String>) null, where, whereIncludeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 更新实体，默认不包含空值字段。
	 *
	 * @param entity 要更新的实体对象
	 * @param where 作为更新条件的Map对象
	 * @return 更新记录数
	 */
	default int updateEntityByAnyOfMap(E entity, Map<String, Object> where) {
		return updateEntityByAnyOfMap(entity, false, (Set<String>) null, (Set<String>) null, where, false, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Criteria条件更新实体。
	 *
	 * @param entity 要更新的实体对象
	 * @param updateIncludeAllEmpty 是否包含所有空值字段进行更新
	 * @param updateIncludeEmptyFields 包含空值的字段集合
	 * @param updateExcludeFields 排除更新的字段集合
	 * @param where 作为更新条件的Criteria对象
	 * @param whereIncludeAllEmpty 是否包含所有空值字段作为条件
	 * @param whereIncludeAllEmptyFields 包含空值的条件字段集合
	 * @param whereExcludeFields 排除作为条件的字段集合
	 * @return 更新记录数
	 */
	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.updateEntityByAny)
	int updateEntityByCriteria(@Param(BindingKeys.ENTITY) E entity
		, @Param(BindingKeys.INCLUDE_ALL_EMPTY) boolean updateIncludeAllEmpty
		, @Param(BindingKeys.INCLUDE_EMPTY_COLUMNS) Set<String> updateIncludeEmptyFields
		, @Param(BindingKeys.EXCLUDE_COLUMNS) Set<String> updateExcludeFields
		, @Param(BindingKeys.WHERE) Criteria where
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean whereIncludeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> whereIncludeAllEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> whereExcludeFields);

	/**
	 * 根据指定字段更新实体，默认不包含空值字段。
	 *
	 * @param entity 要更新的实体对象
	 * @param updateIncludeEmptyFields 包含空值的更新字段集合
	 * @param updateExcludeFields 排除更新的字段集合
	 * @param where 作为更新条件的Criteria对象
	 * @param whereIncludeAllEmptyFields 包含空值的条件字段集合
	 * @param whereExcludeFields 排除作为条件的字段集合
	 * @return 更新记录数
	 */
	default int updateEntityByCriteria(
		E entity, Set<String> updateIncludeEmptyFields, Set<String> updateExcludeFields,
		Criteria where, Set<String> whereIncludeAllEmptyFields, Set<String> whereExcludeFields) {
		return updateEntityByCriteria(entity, false, updateIncludeEmptyFields, updateExcludeFields, where, false, whereIncludeAllEmptyFields, whereExcludeFields);
	}

	/**
	 * 根据指定字段更新实体，默认不包含空值字段且无排除字段。
	 *
	 * @param entity 要更新的实体对象
	 * @param updateIncludeEmptyFields 包含空值的更新字段集合
	 * @param where 作为更新条件的Criteria对象
	 * @param whereIncludeAllEmptyFields 包含空值的条件字段集合
	 * @return 更新记录数
	 */
	default int updateEntityByCriteria(
		E entity, Set<String> updateIncludeEmptyFields,
		Criteria where, Set<String> whereIncludeAllEmptyFields) {
		return updateEntityByCriteria(entity, false, updateIncludeEmptyFields, (Set<String>) null, where, false, whereIncludeAllEmptyFields, (Set<String>) null);
	}

	/**
	 * 更新实体，可选择是否包含空值字段。
	 *
	 * @param entity 要更新的实体对象
	 * @param updateIncludeAllEmpty 是否包含所有空值字段进行更新
	 * @param where 作为更新条件的Criteria对象
	 * @param whereIncludeAllEmpty 是否包含所有空值字段作为条件
	 * @return 更新记录数
	 */
	default int updateEntityByCriteria(E entity, boolean updateIncludeAllEmpty, Criteria where, boolean whereIncludeAllEmpty) {
		return updateEntityByCriteria(entity, updateIncludeAllEmpty, (Set<String>) null, (Set<String>) null, where, whereIncludeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 更新实体，默认不包含空值字段。
	 *
	 * @param entity 要更新的实体对象
	 * @param where 作为更新条件的Criteria对象
	 * @return 更新记录数
	 */
	default int updateEntityByCriteria(E entity, Criteria where) {
		return updateEntityByCriteria(entity, false, (Set<String>) null, (Set<String>) null, where, false, (Set<String>) null, (Set<String>) null);
	}

}
