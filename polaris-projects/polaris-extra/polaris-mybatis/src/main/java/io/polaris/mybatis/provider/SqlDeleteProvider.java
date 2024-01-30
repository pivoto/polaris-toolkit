package io.polaris.mybatis.provider;

import java.util.Map;

import io.polaris.core.annotation.Published;
import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.statement.DeleteStatement;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.annotation.ProviderContext;

/**
 * @author Qt
 * @since 1.8,  Sep 11, 2023
 */
@Slf4j
public class SqlDeleteProvider extends BaseProviderMethodResolver {

	@Published
	public static String provideSql(Map<String, Object> map, ProviderContext context) {
		DeleteStatement<?> st = (DeleteStatement<?>) map.get(BindingKeys.DELETE);
		String sql = EntityStatements.asSqlWithBindings(map, st);
		if (log.isDebugEnabled()) {
			log.debug("<sql>\n{}\n<bindings>\n{}", sql, map);
		}
		return sql;
	}

}
