package io.polaris.core.jdbc.sql.statement.expression;

import java.util.Map;

import io.polaris.core.jdbc.sql.node.SqlNode;

/**
 * @author Qt
 * @since  Aug 22, 2023
 */
@SuppressWarnings("all")
public enum LogicalExpression implements Expression {
	IS_NULL(Expressions.pattern("${ref} IS NULL")),
	NOT_NULL(Expressions.pattern("${ref} IS NOT NULL")),

	EQ(Expressions.pattern("${ref} = #{0}")),
	NE(Expressions.pattern("${ref} <> #{0}")),
	GT(Expressions.pattern("${ref} > #{0}")),
	GE(Expressions.pattern("${ref} >= #{0}")),
	LT(Expressions.pattern("${ref} < #{0}")),
	LE(Expressions.pattern("${ref} <= #{0}")),
	BETWEEN(Expressions.pattern("${ref} BETWEEN #{0} and #{1}")),
	NOT_BETWEEN(Expressions.pattern("${ref} NOT BETWEEN #{0} and #{1}")),
	IN(Expressions.pattern("${ref} IN ( #{0} )")),
	NOT_IN(Expressions.pattern("${ref} NOT IN ( #{0} )")),

	LARGE_IN(LargeInExpression.DEFAULT),
	LARGE_NOT_IN(LargeNotInExpression.DEFAULT),

	CONTAINS(LikeExpression.CONTAINS),
	STARTS_WITH(LikeExpression.STARTS_WITH),
	ENDS_WITH(LikeExpression.ENDS_WITH),
	NOT_CONTAINS(LikeExpression.NOT_CONTAINS),
	NOT_STARTS_WITH(LikeExpression.NOT_STARTS_WITH),
	NOT_ENDS_WITH(LikeExpression.NOT_ENDS_WITH),
//	CONTAINS(Expressions.pattern("${ref} LIKE '%${0}%'")),
//	STARTS_WITH(Expressions.pattern("${ref} LIKE '${0}%'")),
//	ENDS_WITH(Expressions.pattern("${ref} LIKE '%${0}'")),
//	NOT_CONTAINS(Expressions.pattern("${ref} NOT LIKE '%${0}%'")),
//	NOT_STARTS_WITH(Expressions.pattern("${ref} NOT LIKE '${0}%'")),
//	NOT_ENDS_WITH(Expressions.pattern("${ref} NOT LIKE '%${0}'")),

	LIKE(Expressions.pattern("${ref} LIKE #{0}")),
	NOT_LIKE(Expressions.pattern("${ref} NOT LIKE #{0}")),


	EXISTS(Expressions.pattern("EXISTS ( ${0} )")),
	NOT_EXISTS(Expressions.pattern("NOT EXISTS ( ${0} )")),
	BRACKET(Expressions.pattern(" ( ${0} )")),

	;

	private final Expression expression;

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
	public SqlNode toSqlNode(SqlNode baseSource, SqlNode[] extSources, Map<String, Object> bindings) {
		return expression.toSqlNode(baseSource, extSources, bindings);
	}

}
