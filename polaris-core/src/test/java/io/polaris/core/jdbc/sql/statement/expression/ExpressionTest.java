package io.polaris.core.jdbc.sql.statement.expression;

import io.polaris.core.jdbc.sql.node.SqlNodes;
import org.junit.jupiter.api.Test;

class ExpressionTest {

	@Test
	void test01() {
		Expression expr = Expressions.function("coalesce", true, false, false);
		System.out.println(expr.toSqlNode(SqlNodes.text("col"), new Object[]{1}).asBoundSql());
		System.out.println(expr.toSqlNode(SqlNodes.text("col"), new Object[]{1, 2}).asBoundSql());
		System.out.println(expr.toSqlNode(SqlNodes.text("col"), new Object[]{1, 2, 3, 4}).asBoundSql());
	}
}

