package io.polaris.mybatis.provider;

import java.util.Map;

import io.polaris.core.annotation.Published;
import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.statement.MergeStatement;
import io.polaris.core.jdbc.sql.statement.UpdateStatement;
import io.polaris.mybatis.scripting.ProviderSqlSourceDriver;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.annotation.ProviderContext;

/**
 * @author Qt
 * @since 1.8,  Sep 11, 2023
 */
@Slf4j
public class SqlUpdateProvider extends BaseProviderMethodResolver {

	@Published
	public static String provideSql(Object parameterObject, ProviderContext context) {
		Map<String, Object> map = ProviderSqlSourceDriver.toParameterBindings(context.getMapperMethod(), parameterObject);
		UpdateStatement<?> st = (UpdateStatement<?>) map.get(BindingKeys.UPDATE);
		if (st == null) {
			st = (UpdateStatement<?>) map.get(BindingKeys.SQL);
		}
		String sql = EntityStatements.asSqlWithBindings(map, st);
		if (log.isDebugEnabled()) {
			log.debug("<sql>\n{}\n<bindings>\n{}", sql, map);
		}
		return sql;
	}

}
