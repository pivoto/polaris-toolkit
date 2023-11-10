package io.polaris.core.jdbc.sql.statement.expression;

import io.polaris.core.jdbc.sql.node.SqlNodes;

/**
 * @author Qt
 * @since 1.8,  Nov 07, 2023
 */
public class LargeInExpression extends LargeInOrNotExpression {
	public static final LargeInExpression DEFAULT = new LargeInExpression();

	public LargeInExpression() {
		super(1000, SqlNodes.IN, SqlNodes.OR);
	}

	public LargeInExpression(int limit) {
		super(limit, SqlNodes.IN, SqlNodes.OR);
	}

}
