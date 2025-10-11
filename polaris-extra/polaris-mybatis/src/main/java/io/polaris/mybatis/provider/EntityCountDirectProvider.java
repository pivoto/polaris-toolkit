package io.polaris.mybatis.provider;

import io.polaris.core.annotation.Published;
import io.polaris.core.jdbc.sql.SqlStatements;
import io.polaris.mybatis.consts.MappingKeys;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.annotation.ProviderContext;

/**
 * @author Qt
 * @since  Sep 11, 2023
 */
@Slf4j
public class EntityCountDirectProvider extends BaseProviderMethodResolver {

	@Published
	public static String provideSql(Object parameterObject, ProviderContext context) {
		return provideSql(parameterObject, context, (map, ctx) -> {
			String sql = SqlStatements.buildCount(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(context), false);
			if (log.isDebugEnabled()) {
				log.debug("<sql>\n{}\n<bindings>\n{}", sql, map);
			}
			return sql;
		});
	}

}
