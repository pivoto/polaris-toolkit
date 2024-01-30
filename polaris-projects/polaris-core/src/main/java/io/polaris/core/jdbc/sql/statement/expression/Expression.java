package io.polaris.core.jdbc.sql.statement.expression;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.TextNode;

/**
 * @author Qt
 * @since 1.8,  Aug 23, 2023
 */
@FunctionalInterface
public interface Expression {

	/**
	 * @param baseSource 原引用表达式或字段
	 * @param extSources 扩展引用表达式或字段
	 * @param bindings   绑定参数
	 * @return
	 */
	SqlNode toSqlNode(SqlNode baseSource, SqlNode[] extSources, Map<String, Object> bindings);

	/**
	 * @param baseSource 原引用表达式或字段
	 * @param extSources 扩展引用表达式或字段
	 * @param bindings   绑定参数
	 * @return
	 */
	default SqlNode toSqlNode(SqlNode baseSource, SqlNode[] extSources, Object[] bindings) {
		// 默认将绑定数组转为map
		if (bindings != null && bindings.length > 0) {
			Map<String, Object> map = new HashMap<>();
			for (int i = 0; i < bindings.length; i++) {
				map.put(String.valueOf(i), bindings[i]);
			}
			return toSqlNode(baseSource, extSources, map);
		}
		return toSqlNode(baseSource, extSources, Collections.emptyMap());
	}

	default SqlNode toSqlNode(String source) {
		return toSqlNode(new TextNode(source), null, Collections.emptyMap());
	}

	default SqlNode toSqlNode(String source, Object[] bindings) {
		return toSqlNode(new TextNode(source), null, bindings);
	}

	default SqlNode toSqlNode(String source, Map<String, Object> bindings) {
		return toSqlNode(new TextNode(source), null, bindings);
	}

	default SqlNode toSqlNode(SqlNode source) {
		return toSqlNode(source, null, Collections.emptyMap());
	}

	default SqlNode toSqlNode(SqlNode source, Object[] bindings) {
		return toSqlNode(source, null, bindings);
	}

	default SqlNode toSqlNode(SqlNode source, Map<String, Object> bindings) {
		return toSqlNode(source, null, bindings);
	}


}
