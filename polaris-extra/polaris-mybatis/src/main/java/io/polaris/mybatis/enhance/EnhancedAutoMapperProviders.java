package io.polaris.mybatis.enhance;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

import io.polaris.core.jdbc.sql.BindingValues;
import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.statement.SqlNodeBuilder;
import io.polaris.mybatis.consts.MappingKeys;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Qt
 * @since Mar 30, 2026
 */
@Slf4j
public class EnhancedAutoMapperProviders {

	public static void log(String sql, Map<String, Object> map) {
		if (log.isDebugEnabled()) {
			log.debug("SQL-TEXT: {}", sql);
			log.debug("SQL-ARGS: {}", map);
		}
	}

	public static String selectAny(Map<String, Object> map, ProviderContext ctx) {
		Method mapperMethod = ctx.getMapperMethod();
		Function<Map<String, Object>, SqlNode> function;
		function = EntityStatements.buildSqlSelectFunction(mapperMethod);
		SqlNode sqlNode = function.apply(map);
		String sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, sqlNode);
		log(sql, map);
		return sql;
	}


	public static String updateAny(Map<String, Object> map, ProviderContext ctx) {
		Method mapperMethod = ctx.getMapperMethod();
		Function<Map<String, Object>, SqlNode> function;
		function = EntityStatements.buildSqlUpdateFunction(mapperMethod);
		SqlNode sqlNode = function.apply(map);
		String sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, sqlNode);
		log(sql, map);
		return sql;
	}

	public static String executeAnySql(Map<String, Object> map, ProviderContext ctx) {
		Object obj = map.get(BindingKeys.SQL);
		String sql = null;
		if (obj instanceof SqlNode) {
			sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, (SqlNode) obj);
		} else if (obj instanceof SqlNodeBuilder) {
			sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, (SqlNodeBuilder) obj);
		} else if (obj instanceof String) {
			sql = (String) obj;
		} else {
			throw new IllegalArgumentException();
		}
		log(sql, map);
		return sql;
	}


}
