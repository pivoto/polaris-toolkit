package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.statement.MergeStatement;
import io.polaris.core.jdbc.sql.statement.Statements;
import io.polaris.mybatis.consts.EntityMapperKeys;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * @author Qt
 * @since 1.8,  Aug 25, 2023
 */
public interface MergeStatementMapper {


	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.mergeBySql)
	int mergeBySql(@Param(EntityMapperKeys.MERGE) MergeStatement<?> statement);


	default <E> int mergeBySql(Class<E> entityClass, E entity, boolean entityNullsInclude) {
		MergeStatement<?> st = new MergeStatement<>(entityClass, Statements.DEFAULT_TABLE_ALIAS);
		st.withEntity(entity, entityNullsInclude ? name -> true : Statements.DEFAULT_PREDICATE_EXCLUDE_NULLS);
		return mergeBySql(st);
	}

	default <E> int mergeBySql(Class<E> entityClass, E entity) {
		MergeStatement<?> st = new MergeStatement<>(entityClass, Statements.DEFAULT_TABLE_ALIAS);
		st.withEntity(entity, Statements.DEFAULT_PREDICATE_EXCLUDE_NULLS);
		return mergeBySql(st);
	}


	@SuppressWarnings("unchecked")
	default <E> int mergeBySql(E entity) {
		return mergeBySql((Class<E>) entity.getClass(), entity);
	}

	@SuppressWarnings("unchecked")
	default <E> int mergeBySql(E entity, boolean entityNullsInclude) {
		return mergeBySql((Class<E>) entity.getClass(), entity, entityNullsInclude);
	}

}
