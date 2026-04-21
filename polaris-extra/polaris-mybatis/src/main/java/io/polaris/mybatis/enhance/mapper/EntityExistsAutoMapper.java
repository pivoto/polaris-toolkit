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
public interface EntityExistsAutoMapper<E> extends EntityMapper<E> {

	/**
	 * 判断实体是否存在（根据ID判断）
	 *
	 * @param entity 实体对象，包含主键信息，可以是Bean或Map等对象
	 * @param supportLogicDeleted 是否处理逻辑删除规则
	 * @return 存在返回true，否则返回false
	 */
	@AutoMapper(type = AutoMapperType.existsEntityById)
	boolean existsById(@Param(BindingKeys.ENTITY) Object entity
		, @Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 判断实体是否存在（根据ID判断，默认不包含逻辑删除数据）
	 *
	 * @param entity 实体对象，包含主键信息，可以是Bean或Map等对象
	 * @return 存在返回true，否则返回false
	 */
	default boolean existsById(Object entity) {
		return existsById(entity, null);
	}

	/**
	 * 内部方法：根据任意条件判断实体是否存在
	 *
	 * @param entity 查询条件对象，可以是Bean或Map等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields 排除的字段集合
	 * @param supportLogicDeleted 是否处理逻辑删除规则
	 * @return Cursor游标对象
	 */
	@AutoMapper(type = AutoMapperType.select1)
	@Options(fetchSize = 1)
	@Internal("考虑到查询性能与分页实现的兼容性，声明此方法，实际开发中不直接调用")
	Cursor<Boolean> _cursorExists(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 判断实体是否存在（根据实体条件判断）
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields 排除的字段集合
	 * @param supportLogicDeleted 是否处理逻辑删除规则
	 * @return 存在返回true，否则返回false
	 */
	default boolean exists(Object entity, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields, Boolean supportLogicDeleted) {
		Cursor<Boolean> cursor = null;
		try {
			cursor = _cursorExists(entity, includeAllEmpty, includeEmptyFields, excludeFields, supportLogicDeleted);
			Iterator<Boolean> iter = cursor.iterator();
			if (iter.hasNext()) {
				Boolean next = iter.next();
				return Boolean.TRUE.equals(next);
			}
			return false;
		} finally {
			IO.close(cursor);
		}
	}

	/**
	 * 判断实体是否存在（根据实体条件判断，完整参数版本）
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields 排除的字段集合
	 * @return 存在返回true，否则返回false
	 */
	default boolean exists(Object entity, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return exists(entity, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 判断实体是否存在（根据实体条件判断，指定包含和排除字段）
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map等对象
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields 排除的字段集合
	 * @return 存在返回true，否则返回false
	 */
	default boolean exists(Object entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return exists(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 判断实体是否存在（根据实体条件判断，仅指定包含字段）
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map等对象
	 * @param includeEmptyFields 包含的空值字段集合
	 * @return 存在返回true，否则返回false
	 */
	default boolean exists(Object entity, Set<String> includeEmptyFields) {
		return exists(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 判断实体是否存在（根据实体条件判断，指定是否包含所有空值）
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 存在返回true，否则返回false
	 */
	default boolean exists(Object entity, boolean includeAllEmpty) {
		return exists(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 判断实体是否存在（根据实体条件判断，基础版本）
	 *
	 * @param entity 实体对象，作为查询条件
	 * @return 存在返回true，否则返回false
	 */
	default boolean exists(Object entity) {
		return exists(entity, false);
	}

	/**
	 * 内部方法，根据任意条件判断实体是否存在，返回Cursor结果。
	 *
	 * @param entity 查询条件对象, 可以是Bean或Map或Criteria对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return Cursor结果，包含布尔值
	 */
	@SupportLogicDeleted(false)
	@AutoMapper(type = AutoMapperType.select1Direct)
	@Options(fetchSize = 1)
	@Internal("考虑到查询性能与分页实现的兼容性，声明此此方法，实际开发中不直接调用")
	Cursor<Boolean> _cursorDirectExists(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体条件判断实体是否存在。
	 *
	 * @param entity 实体对象，作为查询条件, 可以是Bean或Map或Criteria对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean directExists(Object entity, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		Cursor<Boolean> cursor = null;
		try {
			cursor = _cursorDirectExists(entity, includeAllEmpty, includeEmptyFields, excludeFields);
			Iterator<Boolean> iter = cursor.iterator();
			if (iter.hasNext()) {
				Boolean next = iter.next();
				return Boolean.TRUE.equals(next);
			}
			return false;
		} finally {
			IO.close(cursor);
		}
	}

	/**
	 * 根据实体条件判断实体是否存在，默认不包含所有空值字段。
	 *
	 * @param entity 实体对象，作为查询条件, 可以是Bean或Map或Criteria对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean directExists(Object entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directExists(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件判断实体是否存在，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param entity 实体对象，作为查询条件, 可以是Bean或Map或Criteria对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean directExists(Object entity, Set<String> includeEmptyFields) {
		return directExists(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件判断实体是否存在，可指定是否包含所有空值字段。
	 *
	 * @param entity 实体对象，作为查询条件, 可以是Bean或Map或Criteria对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 若存在返回true，否则返回false
	 */
	default boolean directExists(Object entity, boolean includeAllEmpty) {
		return directExists(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件判断实体是否存在，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param entity 实体对象，作为查询条件, 可以是Bean或Map或Criteria对象
	 * @return 若存在返回true，否则返回false
	 */
	default boolean directExists(Object entity) {
		return directExists(entity, false);
	}

	/**
	 * 根据实体ID判断是否存在，排除逻辑删除的数据。
	 *
	 * @param entity 实体对象，包含ID信息，可以是Bean或Map对象
	 * @return 若存在返回true，否则返回false
	 */
	@AutoMapper(type = AutoMapperType.existsEntityByIdExceptLogicDeleted)
	boolean existsByIdExceptLogicDeleted(@Param(BindingKeys.ENTITY) Object entity);

	/**
	 * 内部方法，用于根据任意条件判断是否存在，排除逻辑删除的数据。
	 * 此方法仅供内部使用，实际开发中不应直接调用。
	 *
	 * @param entity 查询条件对象，可以是Bean或Map或Criteria对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return Cursor对象，包含查询结果
	 */
	@AutoMapper(type = AutoMapperType.select1ExceptLogicDeleted)
	@Options(fetchSize = 1)
	@Internal("考虑到查询性能与分页实现的兼容性，声明此此方法，实际开发中不直接调用")
	Cursor<Boolean> _cursorExistsExceptLogicDeleted(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体条件判断是否存在，排除逻辑删除的数据。
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean existsExceptLogicDeleted(Object entity, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		Cursor<Boolean> cursor = null;
		try {
			cursor = _cursorExistsExceptLogicDeleted(entity, includeAllEmpty, includeEmptyFields, excludeFields);
			Iterator<Boolean> iter = cursor.iterator();
			if (iter.hasNext()) {
				Boolean next = iter.next();
				return Boolean.TRUE.equals(next);
			}
			return false;
		} finally {
			IO.close(cursor);
		}
	}

	/**
	 * 根据实体条件判断是否存在，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean existsExceptLogicDeleted(Object entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return existsExceptLogicDeleted(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件判断是否存在，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean existsExceptLogicDeleted(Object entity, Set<String> includeEmptyFields) {
		return existsExceptLogicDeleted(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件判断是否存在，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 若存在返回true，否则返回false
	 */
	default boolean existsExceptLogicDeleted(Object entity, boolean includeAllEmpty) {
		return existsExceptLogicDeleted(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件判断是否存在，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @return 若存在返回true，否则返回false
	 */
	default boolean existsExceptLogicDeleted(Object entity) {
		return existsExceptLogicDeleted(entity, false);
	}

}
