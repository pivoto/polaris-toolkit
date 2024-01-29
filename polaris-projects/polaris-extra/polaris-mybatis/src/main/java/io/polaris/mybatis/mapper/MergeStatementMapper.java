package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.sql.statement.MergeStatement;
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
	int mergeBySql(@Param(BindingKeys.MERGE) MergeStatement<?> statement);


	default <E> int mergeBySql(Class<E> entityClass, E entity, boolean includeAllEmpty) {
		MergeStatement<?> st = new MergeStatement<>(entityClass, EntityStatements.DEFAULT_TABLE_ALIAS);
		st.withEntity(entity, null, null, includeAllEmpty, null);
		return mergeBySql(st);
	}

	default <E> int mergeBySql(Class<E> entityClass, E entity) {
		MergeStatement<?> st = new MergeStatement<>(entityClass, EntityStatements.DEFAULT_TABLE_ALIAS);
		st.withEntity(entity);
		return mergeBySql(st);
	}

	default <E> int mergeBySql(Class<E> entityClass, E entity, boolean includeAllEmpty, boolean updateWhenMatched, boolean insertWhenNotMatched) {
		MergeStatement<?> st = new MergeStatement<>(entityClass, EntityStatements.DEFAULT_TABLE_ALIAS);
		st.withEntity(entity, updateWhenMatched, insertWhenNotMatched, null, null, includeAllEmpty, null);
		return mergeBySql(st);
	}

	default <E> int mergeBySql(Class<E> entityClass, E entity, boolean updateWhenMatched, boolean insertWhenNotMatched) {
		MergeStatement<?> st = new MergeStatement<>(entityClass, EntityStatements.DEFAULT_TABLE_ALIAS);
		st.withEntity(entity, updateWhenMatched, insertWhenNotMatched);
		return mergeBySql(st);
	}

}
