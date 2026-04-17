package io.polaris.mybatis.enhance.mapper;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.polaris.core.annotation.Internal;
import io.polaris.core.io.IO;
import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.core.jdbc.sql.statement.ColumnPredicate;
import io.polaris.core.jdbc.sql.statement.DeleteStatement;
import io.polaris.core.jdbc.sql.statement.InsertStatement;
import io.polaris.core.jdbc.sql.statement.MergeStatement;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.core.jdbc.sql.statement.UpdateStatement;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;
import io.polaris.mybatis.annotation.AutoMapper;
import io.polaris.mybatis.annotation.AutoResultMapping;
import io.polaris.mybatis.annotation.AutoUseGeneratedKeys;
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
public interface EnhanceAutoMapper<E> extends EntityMapper<E> {

	// region sql

	/**
	 * 根据提供的 InsertStatement 对象执行插入操作。
	 *
	 * @param statement 包含插入语句信息的对象
	 * @return 插入操作影响的行数
	 */
	@AutoMapper(type = AutoMapperType.insertBySql)
	int insertBySql(@Param(BindingKeys.SQL) InsertStatement<?> statement);

	/**
	 * 根据提供的UpdateStatement执行更新操作。
	 *
	 * @param statement 包含更新SQL语句和参数的UpdateStatement对象
	 * @return 受影响的行数
	 */
	@AutoMapper(type = AutoMapperType.updateBySql)
	int updateBySql(@Param(BindingKeys.SQL) UpdateStatement<?> statement);

	/**
	 * 根据提供的删除语句执行删除操作。
	 *
	 * @param statement 包含删除条件和表信息的DeleteStatement对象
	 * @return 受影响的行数
	 */
	@AutoMapper(type = AutoMapperType.deleteBySql)
	int deleteBySql(@Param(BindingKeys.SQL) DeleteStatement<?> statement);

	/**
	 * 检查是否存在满足条件的记录（内部方法，不建议直接调用）
	 *
	 * @param statement SQL查询语句对象
	 * @return 游标结果，包含布尔值表示是否存在匹配记录
	 */
	@SupportLogicDeleted(true) // 是否处理逻辑删除
	@AutoMapper(type = AutoMapperType.select1BySql)
	@Options(fetchSize = 1)
	@Internal("考虑到查询性能与分页实现的兼容性，声明此此方法，实际开发中不直接调用")
	Cursor<Boolean> _cursorExistsBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

	/**
	 * 检查是否存在满足条件的记录
	 *
	 * @param statement SQL查询语句对象
	 * @return 是否存在匹配记录
	 */
	default boolean existsBySql(SelectStatement<?> statement) {
		Cursor<Boolean> cursor = null;
		try {
			cursor = _cursorExistsBySql(statement);
			if (cursor == null) {
				return false;
			}
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
	 * 统计满足条件的记录数量
	 *
	 * @param statement SQL查询语句对象
	 * @return 记录总数
	 */
	@SupportLogicDeleted(true) // 是否处理逻辑删除
	@AutoMapper(type = AutoMapperType.countBySql)
	int countBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

	/**
	 * 查询实体列表
	 *
	 * @param statement SQL查询语句对象
	 * @return 实体对象列表
	 */
	@SupportLogicDeleted(true) // 是否处理逻辑删除
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectBySql)
	List<E> selectEntityListBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

	/**
	 * 查询单个实体对象
	 *
	 * @param statement SQL查询语句对象
	 * @return 实体对象
	 */
	@SupportLogicDeleted(true) // 是否处理逻辑删除
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectBySql)
	E selectEntityBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

	/**
	 * 查询Map列表
	 *
	 * @param statement SQL查询语句对象
	 * @return Map对象列表，键为列名，值为对应值
	 */
	@SupportLogicDeleted(true) // 是否处理逻辑删除
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectBySql)
	List<Map<String, Object>> selectMapListBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

	/**
	 * 查询单个Map对象
	 *
	 * @param statement SQL查询语句对象
	 * @return Map对象，键为列名，值为对应值
	 */
	@SupportLogicDeleted(true) // 是否处理逻辑删除
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectBySql)
	Map<String, Object> selectMapBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

	/**
	 * 根据SQL语句查询实体对象的游标结果。
	 *
	 * @param statement SQL查询语句对象
	 * @return 实体对象的游标结果
	 */
	@SupportLogicDeleted(true) // 是否处理逻辑删除
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectBySql)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursorBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);


	/**
	 * 根据SQL语句查询Map结构的游标结果。
	 *
	 * @param statement SQL查询语句对象
	 * @return Map结构的游标结果，键为列名，值为对应的数据
	 */
	@SupportLogicDeleted(true) // 是否处理逻辑删除
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectBySql)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursorBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);


	// endregion sql


	// region merge

	/**
	 * 根据提供的MergeStatement执行SQL MERGE操作。
	 *
	 * @param statement 包含MERGE操作详细信息的MergeStatement对象
	 * @return 受影响的行数
	 */
	@AutoMapper(type = AutoMapperType.mergeBySql)
	int mergeBySql(@Param(BindingKeys.SQL) MergeStatement<?> statement);

	/**
	 * 根据实体类和实体对象执行SQL MERGE操作。
	 * 使用默认的列谓词策略。
	 *
	 * @param entity 实体对象实例，可以是Bean或Map等对象
	 * @return 受影响的行数
	 */
	default int mergeBySql(Object entity) {
		Type actualType = JavaType.of(getClass()).getActualType(EntityMapper.class, 0);
		Class<?> entityClass = Types.getClass(actualType);
		if (entityClass == Object.class || entityClass == void.class) {
			throw new IllegalStateException("未知实体类型！");
		}
		MergeStatement<?> st = new MergeStatement<>(entityClass, EntityStatements.DEFAULT_TABLE_ALIAS);
		st.withEntity(entity);
		return mergeBySql(st);
	}

	/**
	 * 根据实体类和实体对象执行SQL MERGE操作，支持自定义各种选项。
	 *
	 * @param entity 实体对象实例，可以是Bean或Map等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param updateWhenMatched 匹配时是否执行更新操作
	 * @param insertWhenNotMatched 不匹配时是否执行插入操作
	 * @return 受影响的行数
	 */
	default int mergeBySql(Object entity, boolean includeAllEmpty, boolean updateWhenMatched, boolean insertWhenNotMatched) {
		Type actualType = JavaType.of(getClass()).getActualType(EntityMapper.class, 0);
		Class<?> entityClass = Types.getClass(actualType);
		if (entityClass == Object.class || entityClass == void.class) {
			throw new IllegalStateException("未知实体类型！");
		}
		MergeStatement<?> st = new MergeStatement<>(entityClass, EntityStatements.DEFAULT_TABLE_ALIAS);
		st.withEntity(entity, updateWhenMatched, insertWhenNotMatched, includeAllEmpty ? ColumnPredicate.ALL : ColumnPredicate.DEFAULT);
		return mergeBySql(st);
	}

	/**
	 * 根据实体类和实体对象执行SQL MERGE操作，支持指定更新和插入行为。
	 *
	 * @param entity 实体对象实例，可以是Bean或Map等对象
	 * @param updateWhenMatched 匹配时是否执行更新操作
	 * @param insertWhenNotMatched 不匹配时是否执行插入操作
	 * @return 受影响的行数
	 */
	default int mergeBySql(Object entity, boolean updateWhenMatched, boolean insertWhenNotMatched) {
		Type actualType = JavaType.of(getClass()).getActualType(EntityMapper.class, 0);
		Class<?> entityClass = Types.getClass(actualType);
		if (entityClass == Object.class || entityClass == void.class) {
			throw new IllegalStateException("未知实体类型！");
		}
		MergeStatement<?> st = new MergeStatement<>(entityClass, EntityStatements.DEFAULT_TABLE_ALIAS);
		st.withEntity(entity, updateWhenMatched, insertWhenNotMatched);
		return mergeBySql(st);
	}


	// endregion merge


	// region insert

	/**
	 * 插入实体对象到数据库
	 *
	 * @param entity 实体对象，包含要插入的数据，可以是Bean或Map等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 受影响的行数
	 */
	@AutoUseGeneratedKeys(value = BindingKeys.ENTITY)
	@AutoMapper(type = AutoMapperType.insertEntity)
	int insertEntity(@Param(BindingKeys.ENTITY) Object entity
		, @Param(BindingKeys.INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.EXCLUDE_COLUMNS) Set<String> excludeFields);


	/**
	 * 插入实体对象到数据库
	 *
	 * @param entity 实体对象，包含要插入的数据，可以是Bean或Map等对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 受影响的行数
	 */
	default int insertEntity(Object entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return insertEntity(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 插入实体对象到数据库
	 *
	 * @param entity 实体对象，包含要插入的数据，可以是Bean或Map等对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 受影响的行数
	 */
	default int insertEntity(Object entity, Set<String> includeEmptyFields) {
		return insertEntity(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 插入实体对象到数据库
	 *
	 * @param entity 实体对象，包含要插入的数据，可以是Bean或Map等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 受影响的行数
	 */
	default int insertEntity(Object entity, boolean includeAllEmpty) {
		return insertEntity(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 插入实体对象到数据库
	 *
	 * @param entity 实体对象，包含要插入的数据，可以是Bean或Map等对象
	 * @return 受影响的行数
	 */
	default int insertEntity(Object entity) {
		return insertEntity(entity, false, (Set<String>) null, (Set<String>) null);
	}

	// endregion


	// region deleteById

	/**
	 * 根据实体ID删除记录，考虑逻辑删除标记
	 *
	 * @param entity 实体对象，包含要删除记录的ID信息，可以是Bean或Map等对象
	 * @param supportLogicDeleted 是否使用逻辑删除，null表示使用默认策略
	 * @return 受影响的行数
	 */
	@AutoMapper(type = AutoMapperType.deleteEntityById)
	int deleteEntityById(@Param(BindingKeys.ENTITY) Object entity
		, @Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 直接根据实体ID物理删除记录，不考虑逻辑删除标记
	 *
	 * @param entity 实体对象，包含要删除记录的ID信息，可以是Bean或Map等对象
	 * @return 受影响的行数
	 */
	@AutoMapper(type = AutoMapperType.directDeleteEntityById)
	int directDeleteEntityById(@Param(BindingKeys.ENTITY) Object entity);

	/**
	 * 根据实体ID进行逻辑删除操作
	 *
	 * @param entity 实体对象，包含要逻辑删除记录的ID信息，可以是Bean或Map等对象
	 * @return 受影响的行数
	 */
	@AutoMapper(type = AutoMapperType.logicDeleteEntityById)
	int logicDeleteEntityById(@Param(BindingKeys.ENTITY) Object entity);

	// endregion


	// region deleteByAny


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
	default int deleteEntityByAny(Object entity, @Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted) {
		return deleteEntityByAny(entity, false, (Set<String>) null, (Set<String>) null, supportLogicDeleted);
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

	// endregion


	// region updateById

	/**
	 * 根据实体ID更新实体数据。
	 *
	 * @param entity 实体对象，可以是Bean或Map等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 更新影响的行数
	 */
	@AutoMapper(type = AutoMapperType.updateEntityById)
	int updateEntityById(@Param(BindingKeys.ENTITY) Object entity,
		@Param(BindingKeys.INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体ID更新实体数据，不包含所有空值字段。
	 *
	 * @param entity 实体对象，可以是Bean或Map等对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 更新影响的行数
	 */
	default int updateEntityById(Object entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return updateEntityById(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体ID更新实体数据，不包含所有空值字段，不排除任何字段。
	 *
	 * @param entity 实体对象，可以是Bean或Map等对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 更新影响的行数
	 */
	default int updateEntityById(Object entity, Set<String> includeEmptyFields) {
		return updateEntityById(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体ID更新实体数据，可选择是否包含所有空值字段。
	 *
	 * @param entity 实体对象，可以是Bean或Map等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 更新影响的行数
	 */
	default int updateEntityById(Object entity, boolean includeAllEmpty) {
		return updateEntityById(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体ID更新实体数据，不包含所有空值字段，不排除任何字段。
	 *
	 * @param entity 实体对象，可以是Bean或Map等对象
	 * @return 更新影响的行数
	 */
	default int updateEntityById(Object entity) {
		return updateEntityById(entity, false, (Set<String>) null, (Set<String>) null);
	}

	// endregion


	// region updateByAny

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

	// endregion


	// region selectOne

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

	// endregion


	// region directSelectOne

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
	 * 根据实体ID判断实体是否存在。
	 *
	 * @param entity 实体对象，包含ID信息, 可以是Bean或Map对象
	 * @return 若存在返回true，否则返回false
	 */
	@SupportLogicDeleted(false)
	@AutoMapper(type = AutoMapperType.existsEntityByIdDirect)
	boolean directExistsById(@Param(BindingKeys.ENTITY) Object entity);

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

	// endregion


	// region selectOneExceptLogicDeleted

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

	// endregion


	// region selectList

	/**
	 * 根据实体对象查询列表
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param supportLogicDeleted 是否处理逻辑删除规则
	 * @return 查询结果列表
	 */
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectEntity)
	List<E> selectEntityList(@Param(BindingKeys.WHERE) Object entity,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields,
		@Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据实体对象查询列表（重载方法）
	 * 提供多种参数组合的便捷调用方式
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果列表
	 */
	default List<E> selectEntityList(Object entity, OrderBy orderBy, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityList(entity, orderBy, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据实体对象查询列表（重载方法）
	 * 默认不包含所有空值字段
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果列表
	 */
	default List<E> selectEntityList(Object entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityList(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体对象查询列表（重载方法）
	 * 仅指定包含的空值字段，排除字段为空
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询结果列表
	 */
	default List<E> selectEntityList(Object entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityList(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体对象查询列表（重载方法）
	 * 指定是否包含所有空值字段，其他参数为空
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询结果列表
	 */
	default List<E> selectEntityList(Object entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectEntityList(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体对象查询列表（重载方法）
	 * 仅指定排序条件，其他参数使用默认值
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @return 查询结果列表
	 */
	default List<E> selectEntityList(Object entity, OrderBy orderBy) {
		return selectEntityList(entity, orderBy, false);
	}

	/**
	 * 根据实体对象查询列表（重载方法）
	 * 仅指定实体对象，使用默认排序和过滤条件
	 *
	 * @param entity 实体对象，用于构建WHERE条件，可以是Bean或Map或Criteria对象
	 * @return 查询结果列表
	 */
	default List<E> selectEntityList(Object entity) {
		return selectEntityList(entity, null, false);
	}


	// endregion


	// region directSelectList

	/**
	 * 根据实体对象查询实体列表
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 实体列表
	 */
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectEntityDirect)
	List<E> directSelectEntityList(@Param(BindingKeys.WHERE) Object entity,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体对象查询实体列表（重载方法）
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 实体列表
	 */
	default List<E> directSelectEntityList(Object entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntityList(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体对象查询实体列表（重载方法）
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 实体列表
	 */
	default List<E> directSelectEntityList(Object entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectEntityList(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体对象查询实体列表（重载方法）
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 实体列表
	 */
	default List<E> directSelectEntityList(Object entity, OrderBy orderBy, boolean includeAllEmpty) {
		return directSelectEntityList(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体对象查询实体列表（重载方法）
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @return 实体列表
	 */
	default List<E> directSelectEntityList(Object entity, OrderBy orderBy) {
		return directSelectEntityList(entity, orderBy, false);
	}

	/**
	 * 根据实体对象查询实体列表（重载方法）
	 *
	 * @param entity 实体对象，作为查询条件，可以是Bean或Map或Criteria对象
	 * @return 实体列表
	 */
	default List<E> directSelectEntityList(Object entity) {
		return directSelectEntityList(entity, null, false);
	}

	// endregion


	// region selectListExceptLogicDeleted

	/**
	 * 根据实体对象查询列表，排除逻辑删除记录
	 *
	 * @param entity 实体对象作为查询条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @param excludeFields 指定排除的字段集合
	 * @return 实体对象列表
	 */
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectEntityExceptLogicDeleted)
	List<E> selectEntityListExceptLogicDeleted(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体对象查询列表（指定包含和排除字段）
	 *
	 * @param entity 实体对象作为查询条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @param excludeFields 指定排除的字段集合
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeleted(Object entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityListExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体对象查询列表（指定包含字段）
	 *
	 * @param entity 实体对象作为查询条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 指定包含的空值字段集合
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeleted(Object entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityListExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体对象查询列表（指定是否包含所有空值）
	 *
	 * @param entity 实体对象作为查询条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeleted(Object entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectEntityListExceptLogicDeleted(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体对象查询列表（仅排序）
	 *
	 * @param entity 实体对象作为查询条件，可以是Bean或Map或Criteria对象
	 * @param orderBy 排序条件
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeleted(Object entity, OrderBy orderBy) {
		return selectEntityListExceptLogicDeleted(entity, orderBy, false);
	}

	/**
	 * 根据实体对象查询列表（无排序）
	 *
	 * @param entity 实体对象作为查询条件，可以是Bean或Map或Criteria对象
	 * @return 实体对象列表
	 */
	default List<E> selectEntityListExceptLogicDeleted(Object entity) {
		return selectEntityListExceptLogicDeleted(entity, null, false);
	}

	// endregion

}
