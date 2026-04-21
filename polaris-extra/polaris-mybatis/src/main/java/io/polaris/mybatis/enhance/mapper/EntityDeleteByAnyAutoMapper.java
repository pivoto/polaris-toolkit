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
public interface EntityDeleteByAnyAutoMapper<E> extends EntityMapper<E> {

	/**
	 * 根据实体条件删除记录
	 *
	 * @param entity 实体对象，用于构建删除条件，可以是Bean或Map或Criteria等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param supportLogicDeleted 是否使用逻辑删除，null表示使用默认策略
	 * @return 删除的记录数量
	 */
	@AutoMapper(type = AutoMapperType.deleteEntityByAny)
	int deleteEntityByAny(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);


	/**
	 * 根据实体条件删除记录
	 *
	 * @param entity 实体对象，用于构建删除条件，可以是Bean或Map或Criteria等对象
	 * @return 删除的记录数量
	 */
	default int deleteEntityByAny(Object entity, Boolean supportLogicDeleted) {
		return deleteEntityByAny(entity, false, (Set<String>) null, (Set<String>) null, supportLogicDeleted);
	}


	/**
	 * 根据实体条件删除记录
	 *
	 * @param entity 实体对象，用于构建删除条件，可以是Bean或Map或Criteria等对象
	 * @return 删除的记录数量
	 */
	default int deleteEntityByAny(Object entity) {
		return deleteEntityByAny(entity, false, (Set<String>) null, (Set<String>) null, true);
	}


	/**
	 * 根据实体条件直接删除记录，不考虑逻辑删除
	 *
	 * @param entity 实体对象，用于构建删除条件，可以是Bean或Map或Criteria等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 删除的记录数量
	 */
	@AutoMapper(type = AutoMapperType.directDeleteEntityByAny)
	int directDeleteEntityByAny(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体条件直接删除记录，不考虑逻辑删除
	 *
	 * @param entity 实体对象，用于构建删除条件，可以是Bean或Map或Criteria等对象
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByAny(Object entity) {
		return directDeleteEntityByAny(entity, false, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件进行逻辑删除操作
	 *
	 * @param entity 实体对象，用于构建删除条件，可以是Bean或Map或Criteria等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 受影响的行数
	 */
	@AutoMapper(type = AutoMapperType.logicDeleteEntityByAny)
	int logicDeleteEntityByAny(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体条件进行逻辑删除操作
	 *
	 * @param entity 实体对象，用于构建删除条件
	 * @return 受影响的行数
	 */
	default int logicDeleteEntityByAny(Object entity) {
		return logicDeleteEntityByAny(entity, false, (Set<String>) null, (Set<String>) null);
	}

}
