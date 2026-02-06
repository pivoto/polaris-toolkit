package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.statement.ColumnPredicate;
import io.polaris.core.jdbc.sql.statement.MergeStatement;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * MergeStatementMapper接口提供了基于MergeStatement的数据库合并操作方法。
 * <p>
 * 该接口定义了多种mergeBySql方法的重载形式，支持不同的参数组合来执行SQL MERGE操作。
 * MERGE操作通常用于根据条件匹配更新现有记录或插入新记录。
 *
 * @author Qt
 * @since Aug 25, 2023
 */
public interface MergeStatementMapper {

	/**
	 * 根据提供的MergeStatement执行SQL MERGE操作。
	 *
	 * @param statement 包含MERGE操作详细信息的MergeStatement对象
	 * @return 受影响的行数
	 */
	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.mergeBySql)
	int mergeBySql(@Param(BindingKeys.MERGE) MergeStatement<?> statement);

	/**
	 * 根据实体类和实体对象执行SQL MERGE操作。
	 * 使用默认的列谓词策略。
	 *
	 * @param <T>         实体类型
	 * @param entityClass 实体类的Class对象
	 * @param entity      实体对象实例
	 * @return 受影响的行数
	 */
	default <T> int mergeBySql(Class<T> entityClass, T entity) {
		MergeStatement<?> st = new MergeStatement<>(entityClass, EntityStatements.DEFAULT_TABLE_ALIAS);
		st.withEntity(entity);
		return mergeBySql(st);
	}

	/**
	 * 根据实体类和实体对象执行SQL MERGE操作，支持自定义各种选项。
	 *
	 * @param <T>                  实体类型
	 * @param entityClass          实体类的Class对象
	 * @param entity               实体对象实例
	 * @param includeAllEmpty      是否包含所有空值字段
	 * @param updateWhenMatched    匹配时是否执行更新操作
	 * @param insertWhenNotMatched 不匹配时是否执行插入操作
	 * @return 受影响的行数
	 */
	default <T> int mergeBySql(Class<T> entityClass, T entity, boolean includeAllEmpty, boolean updateWhenMatched, boolean insertWhenNotMatched) {
		MergeStatement<?> st = new MergeStatement<>(entityClass, EntityStatements.DEFAULT_TABLE_ALIAS);
		st.withEntity(entity, updateWhenMatched, insertWhenNotMatched, includeAllEmpty ? ColumnPredicate.ALL : ColumnPredicate.DEFAULT);
		return mergeBySql(st);
	}

	/**
	 * 根据实体类和实体对象执行SQL MERGE操作，支持指定更新和插入行为。
	 *
	 * @param <T>                  实体类型
	 * @param entityClass          实体类的Class对象
	 * @param entity               实体对象实例
	 * @param updateWhenMatched    匹配时是否执行更新操作
	 * @param insertWhenNotMatched 不匹配时是否执行插入操作
	 * @return 受影响的行数
	 */
	default <T> int mergeBySql(Class<T> entityClass, T entity, boolean updateWhenMatched, boolean insertWhenNotMatched) {
		MergeStatement<?> st = new MergeStatement<>(entityClass, EntityStatements.DEFAULT_TABLE_ALIAS);
		st.withEntity(entity, updateWhenMatched, insertWhenNotMatched);
		return mergeBySql(st);
	}

}
