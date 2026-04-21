package io.polaris.mybatis.enhance.mapper;

import java.lang.reflect.Type;
import java.util.Set;

import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.statement.ColumnPredicate;
import io.polaris.core.jdbc.sql.statement.MergeStatement;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;
import io.polaris.mybatis.annotation.AutoMapper;
import io.polaris.mybatis.annotation.AutoUseGeneratedKeys;
import io.polaris.mybatis.consts.AutoMapperType;
import io.polaris.mybatis.mapper.EntityMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Qt
 * @since Apr 17, 2026
 */
public interface EntityInsertAutoMapper<E> extends EntityMapper<E> {

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

}
