package io.polaris.mybatis.provider;

import io.polaris.core.annotation.Published;
import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.statement.SqlNodeBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.annotation.ProviderContext;

/**
 * @author Qt
 * @since 1.8,  Sep 11, 2023
 */
@Slf4j
public class AnySqlProvider extends BaseProviderMethodResolver {

	@Published
	public static String provideSql(Object parameterObject, ProviderContext context) {
		return provideSql(parameterObject, context, (map, ctx) -> {
			Object obj = map.get(BindingKeys.SQL);
			String sql = null;
			if (obj instanceof SqlNode) {
				sql = EntityStatements.asSqlWithBindings(map, (SqlNode) obj);
			} else if (obj instanceof SqlNodeBuilder) {
				sql = EntityStatements.asSqlWithBindings(map, (SqlNodeBuilder) obj);
			} else if (obj instanceof String) {
				sql = (String) obj;
			} else {
				throw new IllegalArgumentException();
			}
			if (log.isDebugEnabled()) {
				log.debug("<sql>\n{}\n<bindings>\n{}", sql, map);
			}
			return sql;
		});
	}

}
