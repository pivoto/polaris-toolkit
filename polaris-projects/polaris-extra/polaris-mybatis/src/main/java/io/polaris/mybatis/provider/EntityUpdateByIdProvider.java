package io.polaris.mybatis.provider;

import io.polaris.core.annotation.Published;
import io.polaris.core.jdbc.sql.SqlStatements;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Sep 11, 2023
 */

@Slf4j
public class EntityUpdateByIdProvider extends BaseProviderMethodResolver{

	@Published
	public static String provideSql(Map<String, Object> map, ProviderContext context) {
		String sql = SqlStatements.buildUpdateById(map, getEntityClass(context));
		if (log.isDebugEnabled()) {
			log.debug("Sql: {}, Vars: {}", sql, map);
		}
		return sql;
	}

}
