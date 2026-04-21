package io.polaris.mybatis.enhance.mapper;

import java.util.List;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.OrderBy;
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
public interface EntitySelectCursorAutoMapper<E> extends EntityMapper<E> {


	/**
	 * 根据实体条件查询游标数据，考虑逻辑删除标记
	 *
	 * @param entity 实体对象，用于构建查询条件，可以是Bean或Map或Criteria等对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param supportLogicDeleted 是否处理逻辑删除规则
	 * @return 查询结果游标
	 */
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectEntity)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursor(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据实体条件查询游标数据
	 *
	 * @param entity 实体对象，用于构建查询条件，可以是Bean或Map或Criteria等对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursor(Object entity, OrderBy orderBy, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursor(entity, orderBy, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据实体条件查询游标数据
	 *
	 * @param entity 实体对象，用于构建查询条件，可以是Bean或Map或Criteria等对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursor(Object entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursor(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件查询游标数据
	 *
	 * @param entity 实体对象，用于构建查询条件，可以是Bean或Map或Criteria等对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursor(Object entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityCursor(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体条件查询游标数据
	 *
	 * @param entity 实体对象，用于构建查询条件，可以是Bean或Map或Criteria等对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursor(Object entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectEntityCursor(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体条件查询游标数据
	 *
	 * @param entity 实体对象，用于构建查询条件，可以是Bean或Map或Criteria等对象
	 * @param orderBy 排序条件
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursor(Object entity, OrderBy orderBy) {
		return selectEntityCursor(entity, orderBy, false);
	}

	/**
	 * 根据实体条件查询游标数据
	 *
	 * @param entity 实体对象，用于构建查询条件，可以是Bean或Map或Criteria等对象
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursor(Object entity) {
		return selectEntityCursor(entity, null, false);
	}


	/**
	 * 根据实体对象查询游标数据
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria等对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 游标结果集
	 */
	@SupportLogicDeleted(false)
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectEntityDirect)
	@Options(fetchSize = 1000)
	Cursor<E> directSelectEntityCursor(@Param(BindingKeys.WHERE) Object entity,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体对象查询游标数据（重载方法）
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria等对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields 排除字段集合
	 * @return 游标结果集
	 */
	default Cursor<E> directSelectEntityCursor(Object entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntityCursor(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体对象查询游标数据（重载方法）
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria等对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @return 游标结果集
	 */
	default Cursor<E> directSelectEntityCursor(Object entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectEntityCursor(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体对象查询游标数据（重载方法）
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria等对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 游标结果集
	 */
	default Cursor<E> directSelectEntityCursor(Object entity, OrderBy orderBy, boolean includeAllEmpty) {
		return directSelectEntityCursor(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体对象查询游标数据（重载方法）
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria等对象
	 * @param orderBy 排序条件
	 * @return 游标结果集
	 */
	default Cursor<E> directSelectEntityCursor(Object entity, OrderBy orderBy) {
		return directSelectEntityCursor(entity, orderBy, false);
	}

	/**
	 * 根据实体对象查询游标数据（重载方法）
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria等对象
	 * @return 游标结果集
	 */
	default Cursor<E> directSelectEntityCursor(Object entity) {
		return directSelectEntityCursor(entity, null, false);
	}

	/**
	 * 根据实体对象查询游标数据，排除逻辑删除记录
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria等对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields 排除的字段集合
	 * @return 游标结果
	 */
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectEntityExceptLogicDeleted)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursorExceptLogicDeleted(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体对象查询游标数据（指定包含空值字段和排除字段）
	 *
	 * @param entity 实体对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields 排除的字段集合
	 * @return 游标结果
	 */
	default Cursor<E> selectEntityCursorExceptLogicDeleted(Object entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursorExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体对象查询游标数据（指定包含空值字段）
	 *
	 * @param entity 实体对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @return 游标结果
	 */
	default Cursor<E> selectEntityCursorExceptLogicDeleted(Object entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityCursorExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体对象查询游标数据（指定是否包含空值）
	 *
	 * @param entity 实体对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 游标结果
	 */
	default Cursor<E> selectEntityCursorExceptLogicDeleted(Object entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectEntityCursorExceptLogicDeleted(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体对象查询游标数据（仅指定排序条件）
	 *
	 * @param entity 实体对象
	 * @param orderBy 排序条件
	 * @return 游标结果
	 */
	default Cursor<E> selectEntityCursorExceptLogicDeleted(Object entity, OrderBy orderBy) {
		return selectEntityCursorExceptLogicDeleted(entity, orderBy, false);
	}

	/**
	 * 根据实体对象查询游标数据（仅实体条件）
	 *
	 * @param entity 实体对象
	 * @return 游标结果
	 */
	default Cursor<E> selectEntityCursorExceptLogicDeleted(Object entity) {
		return selectEntityCursorExceptLogicDeleted(entity, null, false);
	}

}
