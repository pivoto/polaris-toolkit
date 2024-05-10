package io.polaris.mybatis.provider;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

import io.polaris.core.annotation.Published;
import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.sql.node.SqlNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.builder.annotation.ProviderContext;

/**
 * @author Qt
 * @since  Jan 28, 2024
 */
@Slf4j
public class AnyEntityProvider extends BaseProviderMethodResolver {

	@Published
	public static String provideSql(Object parameterObject, ProviderContext context) {
		return provideSql(parameterObject, context, (map, ctx)->{
			String sql = doProvideSql(map, context.getMapperMethod());
			if (log.isDebugEnabled()) {
				log.debug("<sql>\n{}\n<bindings>\n{}", sql, map);
			}
			return sql;
		});
	}

	@SuppressWarnings("all")
	private static String doProvideSql(Map<String, Object> bindings, Method method) {
		boolean isSelect = method.isAnnotationPresent(SelectProvider.class);
		Function<Map<String, Object>, SqlNode> function;
		if (isSelect) {
			function = EntityStatements.buildSqlSelectFunction(method);
		} else {
			function = EntityStatements.buildSqlUpdateFunction(method);
		}
		SqlNode sqlNode = function.apply(bindings);
		return EntityStatements.asSqlWithBindings(bindings, sqlNode);
	}

}
