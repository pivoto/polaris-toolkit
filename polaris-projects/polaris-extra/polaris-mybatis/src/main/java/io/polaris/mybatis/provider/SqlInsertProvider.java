package io.polaris.mybatis.provider;

import io.polaris.core.annotation.Published;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.sql.statement.InsertStatement;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Sep 11, 2023
 */
@Slf4j
public class SqlInsertProvider extends BaseProviderMethodResolver{

	@Published
	public static String provideSql(Map<String, Object> map, ProviderContext context) {
		InsertStatement<?> st = (InsertStatement<?>) map.get(BindingKeys.INSERT);
		String sql = EntityStatements.asSqlWithBindings(map, st);
		if (log.isDebugEnabled()) {
			log.debug("Sql: {}, Vars: {}", sql, map);
		}
		return sql;
	}

}
