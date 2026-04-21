package io.polaris.mybatis.enhance.mapper;

import java.util.Iterator;
import java.util.Set;

import io.polaris.core.annotation.Internal;
import io.polaris.core.io.IO;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.mybatis.annotation.AutoMapper;
import io.polaris.mybatis.annotation.AutoResultMapping;
import io.polaris.mybatis.annotation.SupportLogicDeleted;
import io.polaris.mybatis.consts.AutoMapperType;
import io.polaris.mybatis.mapper.EntityMapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;

/**
 * @author Qt
 * @since Apr 17, 2026
 */
public interface EntitySelectOneAutoMapper<E> extends EntityMapper<E> {

	/**
	 * 根据实体ID查询单条记录
	 *
	 * @param entity 实体对象，包含主键信息，可以是Bean或Map等对象
	 * @param supportLogicDeleted 是否处理逻辑删除规则
	 * @return 查询到的实体对象，未找到返回null
	 */
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectEntityById)
	E selectEntityById(@Param(BindingKeys.ENTITY) Object entity
		, @Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据实体ID查询单条记录（默认不包含逻辑删除数据）
	 *
	 * @param entity 实体对象，包含主键信息
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntityById(Object entity) {
		return selectEntityById(entity, null);
	}

	/**
	 * 根据实体条件查询单条记录
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields 排除的字段集合
	 * @param supportLogicDeleted 是否处理逻辑删除规则
	 * @return 查询到的实体对象，未找到返回null
	 */
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectEntity)
	E selectEntity(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据实体条件查询单条记录（完整参数版本）
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields 排除的字段集合
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntity(Object entity, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntity(entity, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据实体条件查询单条记录（指定包含和排除字段）
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria等对象
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields 排除的字段集合
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntity(Object entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntity(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件查询单条记录（仅指定包含字段）
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria等对象
	 * @param includeEmptyFields 包含的空值字段集合
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntity(Object entity, Set<String> includeEmptyFields) {
		return selectEntity(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件查询单条记录（指定是否包含所有空值）
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntity(Object entity, boolean includeAllEmpty) {
		return selectEntity(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null, null);
	}

	/**
	 * 根据实体条件查询单条记录（基础版本）
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria等对象
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntity(Object entity) {
		return selectEntity(entity, false);
	}


	/**
	 * 根据实体ID直接查询单个实体。
	 *
	 * @param entity 实体对象，包含ID信息, 可以是Bean或Map等对象
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	@AutoResultMapping
	@SupportLogicDeleted(false)
	@AutoMapper(type = AutoMapperType.selectEntityByIdDirect)
	E directSelectEntityById(@Param(BindingKeys.ENTITY) Object entity);

	/**
	 * 根据实体条件直接查询单个实体。
	 *
	 * @param entity 实体对象，作为查询条件, 可以是Bean或Map或Criteria对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	@AutoResultMapping
	@SupportLogicDeleted(false)
	@AutoMapper(type = AutoMapperType.selectEntityDirect)
	E directSelectEntity(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体条件直接查询单个实体，默认不包含所有空值字段。
	 *
	 * @param entity 实体对象，作为查询条件, 可以是Bean或Map或Criteria对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E directSelectEntity(Object entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntity(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件直接查询单个实体，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param entity 实体对象，作为查询条件, 可以是Bean或Map或Criteria对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E directSelectEntity(Object entity, Set<String> includeEmptyFields) {
		return directSelectEntity(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件直接查询单个实体，可指定是否包含所有空值字段。
	 *
	 * @param entity 实体对象，作为查询条件, 可以是Bean或Map或Criteria对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E directSelectEntity(Object entity, boolean includeAllEmpty) {
		return directSelectEntity(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件直接查询单个实体，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param entity 实体对象，作为查询条件, 可以是Bean或Map或Criteria对象
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E directSelectEntity(Object entity) {
		return directSelectEntity(entity, false);
	}


	/**
	 * 根据实体ID查询单个实体，排除逻辑删除的数据。
	 *
	 * @param entity 实体对象，包含ID信息，可以是Bean或Map对象
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectEntityByIdExceptLogicDeleted)
	E selectEntityByIdExceptLogicDeleted(@Param(BindingKeys.ENTITY) Object entity);

	/**
	 * 根据实体条件查询单个实体，排除逻辑删除的数据。
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectEntityExceptLogicDeleted)
	E selectEntityExceptLogicDeleted(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体条件查询单个实体，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E selectEntityExceptLogicDeleted(Object entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityExceptLogicDeleted(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件查询单个实体，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E selectEntityExceptLogicDeleted(Object entity, Set<String> includeEmptyFields) {
		return selectEntityExceptLogicDeleted(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件查询单个实体，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E selectEntityExceptLogicDeleted(Object entity, boolean includeAllEmpty) {
		return selectEntityExceptLogicDeleted(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件查询单个实体，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E selectEntityExceptLogicDeleted(Object entity) {
		return selectEntityExceptLogicDeleted(entity, false);
	}

}
