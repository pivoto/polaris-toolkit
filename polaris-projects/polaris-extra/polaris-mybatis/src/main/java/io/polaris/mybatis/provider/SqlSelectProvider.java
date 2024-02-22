package io.polaris.mybatis.provider;

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
public class SqlSelectProvider extends BaseProviderMethodResolver {

	@Published
	public static String provideSql(Object parameterObject, ProviderContext context) {
		return provideSql(parameterObject, context, (map, ctx) -> {
			SelectStatement<?> st = (SelectStatement<?>) map.get(BindingKeys.SELECT);
			if (st == null) {
				st = (SelectStatement<?>) map.get(BindingKeys.SQL);
			}
			String sql = EntityStatements.asSqlWithBindings(map, st);
			if (log.isDebugEnabled()) {
				log.debug("[Sql]{}", sql);
				log.debug("[Var]{}", map);
			}
			return sql;
		});
	}

}
