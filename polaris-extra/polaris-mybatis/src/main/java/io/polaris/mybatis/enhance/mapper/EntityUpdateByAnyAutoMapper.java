package io.polaris.mybatis.enhance.mapper;

import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.mybatis.annotation.AutoMapper;
import io.polaris.mybatis.consts.AutoMapperType;
import io.polaris.mybatis.mapper.EntityMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Qt
 * @since Apr 17, 2026
 */
public interface EntityUpdateByAnyAutoMapper<E> extends EntityMapper<E> {

	/**
	 * 根据任意实体条件更新实体。
	 *
	 * @param entity 要更新的实体对象，可以是Bean或Map等对象
	 * @param updateIncludeAllEmpty 是否包含所有空值字段进行更新
	 * @param updateIncludeEmptyFields 包含空值的字段集合
	 * @param updateExcludeFields 排除更新的字段集合
	 * @param where 作为更新条件的实体对象，可以是Bean或Map或Criteria等对象
	 * @param whereIncludeAllEmpty 是否包含所有空值字段作为条件
	 * @param whereIncludeAllEmptyFields 包含空值的条件字段集合
	 * @param whereExcludeFields 排除作为条件的字段集合
	 * @return 更新记录数
	 */
	@AutoMapper(type = AutoMapperType.updateEntityByAny)
	int updateEntityByAny(@Param(BindingKeys.ENTITY) Object entity
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
	 * @param entity 要更新的实体对象，可以是Bean或Map等对象
	 * @param updateIncludeEmptyFields 包含空值的更新字段集合
	 * @param updateExcludeFields 排除更新的字段集合
	 * @param where 作为更新条件的实体对象，可以是Bean或Map或Criteria等对象
	 * @param whereIncludeAllEmptyFields 包含空值的条件字段集合
	 * @param whereExcludeFields 排除作为条件的字段集合
	 * @return 更新记录数
	 */
	default int updateEntityByAny(
		Object entity, Set<String> updateIncludeEmptyFields, Set<String> updateExcludeFields,
		Object where, Set<String> whereIncludeAllEmptyFields, Set<String> whereExcludeFields) {
		return updateEntityByAny(entity, false, updateIncludeEmptyFields, updateExcludeFields, where, false, whereIncludeAllEmptyFields, whereExcludeFields);
	}

	/**
	 * 根据指定字段更新实体，默认不包含空值字段且无排除字段。
	 *
	 * @param entity 要更新的实体对象，可以是Bean或Map等对象
	 * @param updateIncludeEmptyFields 包含空值的更新字段集合
	 * @param where 作为更新条件的实体对象，可以是Bean或Map或Criteria等对象
	 * @param whereIncludeAllEmptyFields 包含空值的条件字段集合
	 * @return 更新记录数
	 */
	default int updateEntityByAny(
		Object entity, Set<String> updateIncludeEmptyFields,
		Object where, Set<String> whereIncludeAllEmptyFields) {
		return updateEntityByAny(entity, false, updateIncludeEmptyFields, (Set<String>) null, where, false, whereIncludeAllEmptyFields, (Set<String>) null);
	}

	/**
	 * 更新实体，可选择是否包含空值字段。
	 *
	 * @param entity 要更新的实体对象，可以是Bean或Map等对象
	 * @param updateIncludeAllEmpty 是否包含所有空值字段进行更新
	 * @param where 作为更新条件的实体对象，可以是Bean或Map或Criteria等对象
	 * @param whereIncludeAllEmpty 是否包含所有空值字段作为条件
	 * @return 更新记录数
	 */
	default int updateEntityByAny(Object entity, boolean updateIncludeAllEmpty, Object where, boolean whereIncludeAllEmpty) {
		return updateEntityByAny(entity, updateIncludeAllEmpty, (Set<String>) null, (Set<String>) null, where, whereIncludeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 更新实体，默认不包含空值字段。
	 *
	 * @param entity 要更新的实体对象，可以是Bean或Map等对象
	 * @param where 作为更新条件的实体对象，可以是Bean或Map或Criteria等对象
	 * @return 更新记录数
	 */
	default int updateEntityByAny(Object entity, Object where) {
		return updateEntityByAny(entity, false, (Set<String>) null, (Set<String>) null, where, false, (Set<String>) null, (Set<String>) null);
	}

}
