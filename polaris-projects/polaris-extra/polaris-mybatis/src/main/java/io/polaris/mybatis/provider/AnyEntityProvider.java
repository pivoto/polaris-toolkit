package io.polaris.mybatis.provider;

import java.lang.reflect.Method;
import java.util.Map;

import io.polaris.core.annotation.Published;
import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.sql.annotation.EntityDelete;
import io.polaris.core.jdbc.sql.annotation.EntityInsert;
import io.polaris.core.jdbc.sql.annotation.EntityMerge;
import io.polaris.core.jdbc.sql.annotation.EntitySelect;
import io.polaris.core.jdbc.sql.annotation.EntityUpdate;
import io.polaris.core.jdbc.sql.annotation.SqlDelete;
import io.polaris.core.jdbc.sql.annotation.SqlInsert;
import io.polaris.core.jdbc.sql.annotation.SqlSelect;
import io.polaris.core.jdbc.sql.annotation.SqlSelectSet;
import io.polaris.core.jdbc.sql.annotation.SqlUpdate;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.core.jdbc.sql.statement.SetOpsStatement;
import io.polaris.core.jdbc.sql.statement.SqlNodeBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.builder.annotation.ProviderContext;

/**
 * @author Qt
 * @since 1.8,  Jan 28, 2024
 */
@Slf4j
public class AnyEntityProvider extends BaseProviderMethodResolver {

	@Published
	public static String provideSql(Map<String, Object> map, ProviderContext context) {
		String sql = doProvideSql(map, context);
		if (log.isDebugEnabled()) {
			log.debug("<sql>\n{}\n<bindings>\n{}", sql, map);
		}
		return sql;
	}

	@SuppressWarnings("all")
	private static String doProvideSql(Map<String, Object> bindings, ProviderContext context) {
		Method mapperMethod = context.getMapperMethod();

		boolean isSelect = mapperMethod.isAnnotationPresent(SelectProvider.class);
		////boolean isUpdate = mapperMethod.isAnnotationPresent(UpdateProvider.class);
		////boolean isInsert = mapperMethod.isAnnotationPresent(InsertProvider.class);
		////boolean isDelete = mapperMethod.isAnnotationPresent(DeleteProvider.class);

		if (isSelect) {
			{
				EntitySelect entitySelect = mapperMethod.getAnnotation(EntitySelect.class);
				if (entitySelect != null) {
					SelectStatement<?> st = EntityStatements.buildSelect(bindings, entitySelect);
					if (entitySelect.count()) {
						return EntityStatements.asSqlWithBindings(bindings, st.toCountSqlNode());
					}
					return EntityStatements.asSqlWithBindings(bindings, st.toSqlNode());
				}
			}
			{
				SqlSelect sqlSelect = mapperMethod.getAnnotation(SqlSelect.class);
				if (sqlSelect != null) {
					SelectStatement<?> st = EntityStatements.buildSelect(bindings, sqlSelect);
					if (sqlSelect.count()) {
						return EntityStatements.asSqlWithBindings(bindings, st.toCountSqlNode());
					}
					return EntityStatements.asSqlWithBindings(bindings, st.toSqlNode());
				}
			}
			{
				SqlSelectSet sqlSelect = mapperMethod.getAnnotation(SqlSelectSet.class);
				if (sqlSelect != null) {
					SetOpsStatement<?> st = EntityStatements.buildSelectSet(bindings, sqlSelect);
					if (sqlSelect.count()) {
						return EntityStatements.asSqlWithBindings(bindings, st.toCountSqlNode());
					}
					return EntityStatements.asSqlWithBindings(bindings, st.toSqlNode());
				}
			}
		}

		{
			EntityInsert entityInsert = mapperMethod.getAnnotation(EntityInsert.class);
			if (entityInsert != null) {
				return EntityStatements.asSqlWithBindings(bindings,
					EntityStatements.buildInsert(bindings, entityInsert));
			}
		}
		{
			EntityDelete entityDelete = mapperMethod.getAnnotation(EntityDelete.class);
			if (entityDelete != null) {
				return EntityStatements.asSqlWithBindings(bindings,
					EntityStatements.buildDelete(bindings, entityDelete));
			}
		}
		{
			EntityUpdate entityUpdate = mapperMethod.getAnnotation(EntityUpdate.class);
			if (entityUpdate != null) {
				return EntityStatements.asSqlWithBindings(bindings,
					EntityStatements.buildUpdate(bindings, entityUpdate));
			}
		}
		{
			EntityMerge entityMerge = mapperMethod.getAnnotation(EntityMerge.class);
			if (entityMerge != null) {
				return EntityStatements.asSqlWithBindings(bindings,
					EntityStatements.buildMerge(bindings, entityMerge));
			}
		}
		{
			SqlInsert sqlInsert = mapperMethod.getAnnotation(SqlInsert.class);
			if (sqlInsert != null) {
				return EntityStatements.asSqlWithBindings(bindings,
					EntityStatements.buildInsert(bindings, sqlInsert));
			}
		}
		{
			SqlDelete sqlDelete = mapperMethod.getAnnotation(SqlDelete.class);
			if (sqlDelete != null) {
				return EntityStatements.asSqlWithBindings(bindings,
					EntityStatements.buildDelete(bindings, sqlDelete));
			}
		}
		{
			SqlUpdate sqlUpdate = mapperMethod.getAnnotation(SqlUpdate.class);
			if (sqlUpdate != null) {
				return EntityStatements.asSqlWithBindings(bindings,
					EntityStatements.buildUpdate(bindings, sqlUpdate));
			}
		}

		// 找不到实体Sql注解，从入参获取直接SQL
		Object sql = bindings.get(BindingKeys.SQL);
		if (sql instanceof SqlNode) {
			return EntityStatements.asSqlWithBindings(bindings, (SqlNode) sql);
		}
		if (sql instanceof SqlNodeBuilder) {
			return EntityStatements.asSqlWithBindings(bindings, (SqlNodeBuilder) sql);
		}
		throw new IllegalArgumentException();
	}

}
