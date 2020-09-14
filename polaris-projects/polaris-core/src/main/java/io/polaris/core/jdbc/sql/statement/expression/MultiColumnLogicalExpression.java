package io.polaris.core.jdbc.sql.statement.expression;

/**
 * @author Qt
 * @since 1.8,  Aug 22, 2023
 */
public enum MultiColumnLogicalExpression {
	EQ(Expressions.pattern("${ref} = ${ref1}")),
	NE(Expressions.pattern("${ref} <> ${ref1}")),
	GT(Expressions.pattern("${ref} > ${ref1}")),
	GE(Expressions.pattern("${ref} >= ${ref1}")),
	LT(Expressions.pattern("${ref} < ${ref1}")),
	LE(Expressions.pattern("${ref} <= ${ref1}")),
	BETWEEN(Expressions.pattern("${ref} BETWEEN ${ref1} and ${ref2}")),
	NOT_BETWEEN(Expressions.pattern("${ref} NOT BETWEEN ${ref1} and ${ref2}")),

	;

	private Expression expression;

	MultiColumnLogicalExpression(Expression expression) {
		this.expression = expression;
	}


	public Expression getExpression() {
		return expression;
	}
}
