package io.polaris.mybatis.provider;

import io.polaris.core.annotation.Published;
import io.polaris.core.jdbc.sql.BindingValues;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.mybatis.consts.MappingKeys;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.annotation.ProviderContext;

/**
 * @author Qt
 * @since  Sep 11, 2023
 */
@Slf4j
public class SqlCountProvider extends BaseProviderMethodResolver {

	@Published
	public static String provideSql(Object parameterObject, ProviderContext context) {
		return provideSql(parameterObject, context, (map, ctx) -> {
			SelectStatement<?> st = (SelectStatement<?>) map.get(BindingKeys.SELECT);
			if (st == null) {
				st = (SelectStatement<?>) map.get(BindingKeys.SQL);
			}
			String sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st::toCountSqlNode);
			if (log.isDebugEnabled()) {
				log.debug("<sql>\n{}\n<bindings>\n{}", sql, map);
			}
			return sql;
		});
	}

}
