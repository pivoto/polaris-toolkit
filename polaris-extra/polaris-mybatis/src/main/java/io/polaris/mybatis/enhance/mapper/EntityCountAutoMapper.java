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
public interface EntityCountAutoMapper<E> extends EntityMapper<E> {


	/**
	 * 根据实体条件统计记录数量
	 *
	 * @param entity 实体对象，用于构建查询条件，可以是Bean或Map或Criteria等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param supportLogicDeleted 是否支持逻辑删除字段，默认为true
	 * @return 符合条件的记录数量
	 */
	@AutoMapper(type = AutoMapperType.countEntity)
	int countEntity(@Param(BindingKeys.WHERE) Object entity,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields,
		@Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据实体条件统计记录数量（重载方法）
	 *
	 * @param entity 实体对象，用于构建查询条件，可以是Bean或Map或Criteria等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntity(Object entity, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntity(entity, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据实体条件统计记录数量（重载方法）
	 *
	 * @param entity 实体对象，用于构建查询条件，可以是Bean或Map或Criteria等对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntity(Object entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntity(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件统计记录数量（重载方法）
	 *
	 * @param entity 实体对象，用于构建查询条件，可以是Bean或Map或Criteria等对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntity(Object entity, Set<String> includeEmptyFields) {
		return countEntity(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件统计记录数量（重载方法）
	 *
	 * @param entity 实体对象，用于构建查询条件，可以是Bean或Map或Criteria等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 符合条件的记录数量
	 */
	default int countEntity(Object entity, boolean includeAllEmpty) {
		return countEntity(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件统计记录数量（重载方法）
	 *
	 * @param entity 实体对象，用于构建查询条件，可以是Bean或Map或Criteria等对象
	 * @return 符合条件的记录数量
	 */
	default int countEntity(Object entity) {
		return countEntity(entity, false);
	}

	/**
	 * 根据实体条件统计记录数量，不考虑逻辑删除标记
	 *
	 * @param entity 实体对象，用于构建查询条件，可以是Bean或Map或Criteria等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	@AutoMapper(type = AutoMapperType.countEntityDirect)
	int directCountEntity(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体条件统计记录数量，不考虑逻辑删除标记
	 *
	 * @param entity 实体对象，用于构建查询条件，可以是Bean或Map或Criteria等对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	default int directCountEntity(Object entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directCountEntity(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件统计记录数量，不考虑逻辑删除标记
	 *
	 * @param entity 实体对象，用于构建查询条件，可以是Bean或Map或Criteria等对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 符合条件的记录数量
	 */
	default int directCountEntity(Object entity, Set<String> includeEmptyFields) {
		return directCountEntity(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件统计记录数量，不考虑逻辑删除标记
	 *
	 * @param entity 实体对象，用于构建查询条件，可以是Bean或Map或Criteria等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 符合条件的记录数量
	 */
	default int directCountEntity(Object entity, boolean includeAllEmpty) {
		return directCountEntity(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件统计记录数量，不考虑逻辑删除标记
	 *
	 * @param entity 实体对象，用于构建查询条件，可以是Bean或Map或Criteria等对象
	 * @return 符合条件的记录数量
	 */
	default int directCountEntity(Object entity) {
		return directCountEntity(entity, false);
	}


	/**
	 * 统计非逻辑删除的实体记录数量
	 *
	 * @param entity 实体对象，用于构建查询条件，可以是Bean或Map或Criteria等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 符合条件的非逻辑删除记录数量
	 */
	@AutoMapper(type = AutoMapperType.countEntityExceptLogicDeleted)
	int countEntityExceptLogicDeleted(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 统计非逻辑删除的实体记录数量
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 符合条件的非逻辑删除记录数量
	 */
	default int countEntityExceptLogicDeleted(Object entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntityExceptLogicDeleted(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 统计非逻辑删除的实体记录数量
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 符合条件的非逻辑删除记录数量
	 */
	default int countEntityExceptLogicDeleted(Object entity, Set<String> includeEmptyFields) {
		return countEntityExceptLogicDeleted(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 统计非逻辑删除的实体记录数量
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 符合条件的非逻辑删除记录数量
	 */
	default int countEntityExceptLogicDeleted(Object entity, boolean includeAllEmpty) {
		return countEntityExceptLogicDeleted(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 统计非逻辑删除的实体记录数量
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @return 符合条件的非逻辑删除记录数量
	 */
	default int countEntityExceptLogicDeleted(Object entity) {
		return countEntityExceptLogicDeleted(entity, false);
	}


}
