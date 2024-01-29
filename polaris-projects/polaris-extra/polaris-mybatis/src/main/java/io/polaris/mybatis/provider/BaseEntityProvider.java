package io.polaris.mybatis.provider;

import java.lang.reflect.Method;
import java.util.Map;

import io.polaris.core.annotation.Published;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.sql.annotation.EntityDelete;
import io.polaris.core.jdbc.sql.annotation.EntityInsert;
import io.polaris.core.jdbc.sql.annotation.EntityMerge;
import io.polaris.core.jdbc.sql.annotation.EntitySelect;
import io.polaris.core.jdbc.sql.annotation.EntityUpdate;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.core.jdbc.sql.statement.SqlNodeBuilder;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.builder.annotation.ProviderContext;

/**
 * @author Qt
 * @since 1.8,  Jan 28, 2024
 */
public class BaseEntityProvider extends BaseProviderMethodResolver {

	@Published
	public static String provideSql(Map<String, Object> map, ProviderContext context) {
		Method mapperMethod = context.getMapperMethod();

		boolean isSelect = mapperMethod.isAnnotationPresent(SelectProvider.class);
		boolean isUpdate = mapperMethod.isAnnotationPresent(UpdateProvider.class);
		boolean isInsert = mapperMethod.isAnnotationPresent(InsertProvider.class);
		boolean isDelete = mapperMethod.isAnnotationPresent(DeleteProvider.class);

		if (isSelect) {
			EntitySelect entitySelect = mapperMethod.getAnnotation(EntitySelect.class);
			if (entitySelect != null) {
				SelectStatement<?> st = EntityStatements.buildSelect(map, entitySelect);
				if (entitySelect.count()){
					return EntityStatements.getSqlWithBindings(map, st.toCountSqlNode());
				}
				return EntityStatements.getSqlWithBindings(map, st.toSqlNode());
			}

		} else if (isInsert) {
			EntityInsert entityInsert = mapperMethod.getAnnotation(EntityInsert.class);
			if (entityInsert != null) {
				return EntityStatements.getSqlWithBindings(map,
					EntityStatements.buildInsert(map, entityInsert));
			}

		} else if (isDelete) {
			EntityDelete entityDelete = mapperMethod.getAnnotation(EntityDelete.class);
			if (entityDelete != null) {
				return EntityStatements.getSqlWithBindings(map,
					EntityStatements.buildDelete(map, entityDelete));
			}

		} else if (isUpdate) {
			EntityUpdate entityUpdate = mapperMethod.getAnnotation(EntityUpdate.class);
			if (entityUpdate != null) {
				return EntityStatements.getSqlWithBindings(map,
					EntityStatements.buildUpdate(map, entityUpdate));
			}
		}
		EntityMerge entityMerge = mapperMethod.getAnnotation(EntityMerge.class);
		if (entityMerge != null) {
			return EntityStatements.getSqlWithBindings(map,
				EntityStatements.buildMerge(map, entityMerge));
		}

		Object sql = map.get(BindingKeys.SQL);
		if (sql instanceof SqlNode) {
			return EntityStatements.getSqlWithBindings(map, (SqlNode) sql);
		}
		if (sql instanceof SqlNodeBuilder) {
			return EntityStatements.getSqlWithBindings(map, (SqlNodeBuilder) sql);
		}
		throw new IllegalArgumentException();
	}

}
