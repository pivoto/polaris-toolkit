package io.polaris.core.jdbc.sql.statement.expression;

import io.polaris.core.jdbc.sql.node.SqlNode;

/**
 * @author Qt
 * @since 1.8,  Aug 22, 2023
 */
public enum LogicalExpression implements Expression{
	IS_NULL(new PatternExpression("${ref} IS NULL")),
	NOT_NULL(new PatternExpression("${ref} IS NOT NULL")),

	EQ(new PatternExpression("${ref} = #{0}")),
	NE(new PatternExpression("${ref} <> #{0}")),
	GT(new PatternExpression("${ref} > #{0}")),
	GE(new PatternExpression("${ref} >= #{0}")),
	LT(new PatternExpression("${ref} < #{0}")),
	LE(new PatternExpression("${ref} <= #{0}")),
	BETWEEN(new PatternExpression("${ref} BETWEEN #{0} and #{1}")),
	NOT_BETWEEN(new PatternExpression("${ref} NOT BETWEEN #{0} and #{1}")),
	IN(new PatternExpression("${ref} IN ( #{0} )")),
	NOT_IN(new PatternExpression("${ref} NOT IN ( #{0} )")),

	CONTAINS(new PatternExpression("${ref} LIKE '%${0}%'")),
	STARTS_WITH(new PatternExpression("${ref} LIKE '${0}%'")),
	ENDS_WITH(new PatternExpression("${ref} LIKE '%${0}'")),
	NOT_CONTAINS(new PatternExpression("${ref} NOT LIKE '%${0}%'")),
	NOT_STARTS_WITH(new PatternExpression("${ref} NOT LIKE '${0}%'")),
	NOT_ENDS_WITH(new PatternExpression("${ref} NOT LIKE '%${0}'")),

	LIKE(new PatternExpression("${ref} LIKE #{0}")),
	NOT_LIKE(new PatternExpression("${ref} NOT LIKE #{0}")),


	EXISTS(new PatternExpression("EXISTS ( ${0} )")),
	NOT_EXISTS(new PatternExpression("NOT EXISTS ( ${0} )")),
	BRACKET(new PatternExpression(" ( ${0} )")),

	;

	private Expression expression;

	LogicalExpression(Expression expression) {
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
