package io.polaris.mybatis.enhance.mapper;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.polaris.core.annotation.Internal;
import io.polaris.core.io.IO;
import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
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
public interface EntityMergeAutoMapper<E> extends EntityMapper<E> {



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

}
