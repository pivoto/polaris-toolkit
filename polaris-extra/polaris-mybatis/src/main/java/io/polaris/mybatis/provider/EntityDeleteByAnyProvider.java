package io.polaris.mybatis.provider;

import java.util.Map;

import io.polaris.core.annotation.Published;
import io.polaris.core.jdbc.sql.SqlStatements;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.annotation.ProviderContext;

/**
 * @author Qt
 * @since  Sep 11, 2023
 */
@Slf4j
public class EntityDeleteByAnyProvider extends BaseProviderMethodResolver {

	@Published
	public static String provideSql(Object parameterObject, ProviderContext context) {
		return provideSql(parameterObject, context, (map, ctx)-> {
			String sql = SqlStatements.buildDeleteByAny(map, getEntityClass(context));
			if (log.isDebugEnabled()) {
				log.debug("<sql>\n{}\n<bindings>\n{}", sql, map);
			}
			return sql;
		});
	}

}
