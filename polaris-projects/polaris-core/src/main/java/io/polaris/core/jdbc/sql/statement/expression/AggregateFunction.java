package io.polaris.core.jdbc.sql.statement.expression;

import io.polaris.core.jdbc.sql.node.SqlNode;

import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Aug 23, 2023
 */
public enum AggregateFunction implements Expression {

	COUNT(Expressions.pattern("COUNT(${ref})")),
	SUM(Expressions.pattern("SUM(${ref})")),
	MAX(Expressions.pattern("MAX(${ref})")),
	MIN(Expressions.pattern("MIN(${ref})")),
	AVG(Expressions.pattern("AVG(${ref})")),

	;


	private Expression expression;

	AggregateFunction(Expression expression) {
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
