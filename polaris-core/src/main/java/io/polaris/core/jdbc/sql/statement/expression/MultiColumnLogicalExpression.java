package io.polaris.core.jdbc.sql.statement.expression;

/**
 * @author Qt
 * @since 1.8,  Aug 22, 2023
 */
public enum MultiColumnLogicalExpression {
	EQ(new PatternExpression("${ref} = ${ref1}")),
	NE(new PatternExpression("${ref} <> ${ref1}")),
	GT(new PatternExpression("${ref} > ${ref1}")),
	GE(new PatternExpression("${ref} >= ${ref1}")),
	LT(new PatternExpression("${ref} < ${ref1}")),
	LE(new PatternExpression("${ref} <= ${ref1}")),
	BETWEEN(new PatternExpression("${ref} BETWEEN ${ref1} and ${ref2}")),
	NOT_BETWEEN(new PatternExpression("${ref} NOT BETWEEN ${ref1} and ${ref2}")),

	;

	private Expression expression;

	MultiColumnLogicalExpression(Expression expression) {
		this.expression = expression;
	}


	public Expression getExpression() {
		return expression;
	}
}
