package io.polaris.core.jdbc.sql.statement.expression;

import java.util.Map;

import io.polaris.core.jdbc.sql.node.SqlNode;

/**
 * @author Qt
 * @since  Aug 23, 2023
 */
public enum FunctionExpression implements Expression {

	COALESCE(Expressions.pattern("COALESCE(${ref1},${ref})")),
	;


	private final Expression expression;

	FunctionExpression(Expression expression) {
		this.expression = expression;
	}


	public Expression getExpression() {
		return expression;
	}

	@Override
	public SqlNode toSqlNode(SqlNode baseSource, SqlNode[] extSources, Map<String, Object> bindings) {
		return expression.toSqlNode(baseSource, extSources, bindings);
	}

	@Override
	public SqlNode toSqlNode(SqlNode baseSource, SqlNode[] extSources, Object[] bindings) {
		return expression.toSqlNode(baseSource, extSources, bindings);
	}

}
