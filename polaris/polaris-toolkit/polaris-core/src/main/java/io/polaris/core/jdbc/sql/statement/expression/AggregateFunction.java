package io.polaris.core.jdbc.sql.statement.expression;

import io.polaris.core.jdbc.sql.node.SqlNode;

/**
 * @author Qt
 * @since 1.8,  Aug 23, 2023
 */
public enum AggregateFunction implements Expression {

	COUNT(new PatternExpression("COUNT(${ref})")),
	SUM(new PatternExpression("SUM(${ref})")),
	MAX(new PatternExpression("MAX(${ref})")),
	MIN(new PatternExpression("MIN(${ref})")),
	AVG(new PatternExpression("AVG(${ref})")),

	;


	private Expression expression;

	AggregateFunction(Expression expression) {
		this.expression = expression;
	}


	public Expression getExpression() {
		return expression;
	}

	@Override
	public SqlNode toSqlNode(SqlNode baseSource, SqlNode[] extSources, Object[] bindings) {
		return expression.toSqlNode(baseSource, extSources, bindings);
	}

	@Override
	public SqlNode toSqlNode(String source) {
		return expression.toSqlNode(source);
	}

	@Override
	public SqlNode toSqlNode(String source, Object[] bindings) {
		return expression.toSqlNode(source, bindings);
	}

	@Override
	public SqlNode toSqlNode(SqlNode source) {
		return expression.toSqlNode(source);
	}

	@Override
	public SqlNode toSqlNode(SqlNode source, Object[] bindings) {
		return expression.toSqlNode(source, bindings);
	}
}
