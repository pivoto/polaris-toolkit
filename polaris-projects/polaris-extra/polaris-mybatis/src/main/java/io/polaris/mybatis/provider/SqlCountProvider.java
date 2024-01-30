package io.polaris.mybatis.provider;

import java.util.Map;

import io.polaris.core.annotation.Published;
import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.annotation.ProviderContext;

/**
 * @author Qt
 * @since 1.8,  Sep 11, 2023
 */
@Slf4j
public class SqlCountProvider extends BaseProviderMethodResolver {

	@Published
	public static String provideSql(Map<String, Object> map, ProviderContext context) {
		SelectStatement<?> st = (SelectStatement<?>) map.get(BindingKeys.SELECT);
		String sql = EntityStatements.asSqlWithBindings(map, st::toCountSqlNode);
		if (log.isDebugEnabled()) {
			log.debug("Sql: {}, Vars: {}", sql, map);
		}
		return sql;
	}

}
