package io.polaris.core.jdbc.sql.statement.expression;

import java.util.Map;

import io.polaris.core.jdbc.sql.node.SqlNode;

/**
 * @author Qt
 * @since Aug 23, 2023
 */
public enum FunctionExpression implements Expression {

	COALESCE_FIELD1(Expressions.pattern("COALESCE(${ref},${ref1})")),
	COALESCE_FIELD2(Expressions.pattern("COALESCE(${ref},${ref1},${ref2})")),
	COALESCE_FIELD3(Expressions.pattern("COALESCE(${ref},${ref1},${ref2},${ref3})")),
	COALESCE_VALUE1(Expressions.pattern("COALESCE(${ref},${0})")),
	COALESCE_VALUE2(Expressions.pattern("COALESCE(${ref},${0},${1})")),
	COALESCE_VALUE3(Expressions.pattern("COALESCE(${ref},${0},${1},${2})")),
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
