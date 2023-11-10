package io.polaris.core.jdbc.sql.statement.expression;

import io.polaris.core.jdbc.sql.node.SqlNodes;

/**
 * @author Qt
 * @since 1.8,  Nov 07, 2023
 */
public class LargeNotInExpression extends LargeInOrNotExpression {
	public static final LargeNotInExpression DEFAULT = new LargeNotInExpression();

	public LargeNotInExpression() {
		super(1000, SqlNodes.NOT_IN, SqlNodes.AND);
	}

	public LargeNotInExpression(int limit) {
		super(limit, SqlNodes.NOT_IN, SqlNodes.AND);
	}
}
