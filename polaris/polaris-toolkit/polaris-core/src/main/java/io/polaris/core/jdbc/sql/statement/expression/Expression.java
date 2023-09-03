package io.polaris.core.jdbc.sql.statement.expression;

import io.polaris.core.consts.StdConsts;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.TextNode;

/**
 * @author Qt
 * @since 1.8,  Aug 23, 2023
 */
@FunctionalInterface
public interface Expression {
	SqlNode toSqlNode(SqlNode baseSource, SqlNode[] extSources, Object[] bindings);

	default SqlNode toSqlNode(String source) {
		return toSqlNode(new TextNode(source), StdConsts.EMPTY_ARRAY);
	}

	default SqlNode toSqlNode(String source, Object[] bindings) {
		return toSqlNode(new TextNode(source), bindings);
	}


	default SqlNode toSqlNode(SqlNode source) {
		return toSqlNode(source, StdConsts.EMPTY_ARRAY);
	}

	default SqlNode toSqlNode(SqlNode source, Object[] bindings) {
		return toSqlNode(source, null, bindings);
	}


}
