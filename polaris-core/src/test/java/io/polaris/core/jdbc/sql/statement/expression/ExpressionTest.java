package io.polaris.core.jdbc.sql.statement.expression;

import io.polaris.core.TestConsole;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import org.junit.jupiter.api.Test;

class ExpressionTest {

	@Test
	void test01() {
		Expression expr = Expressions.function("coalesce", true, false, false);
		TestConsole.println(expr.toSqlNode(SqlNodes.text("col"), new Object[]{1}).asBoundSql());
		TestConsole.println(expr.toSqlNode(SqlNodes.text("col"), new Object[]{1, 2}).asBoundSql());
		TestConsole.println(expr.toSqlNode(SqlNodes.text("col"), new Object[]{1, 2, 3, 4}).asBoundSql());
	}

	@Test
	void testNode() {
		ContainerNode sql = new ContainerNode();
		for (int i = 0; i < 10; i++) {
			sql.addNode(new TextNode(" " + i + " "));
		}
		{
			ContainerNode sub = new ContainerNode();
			for (int i = 0; i < 10; i++) {
				sql.addNode(new TextNode(" " + i + " "));
			}
			sql.addNode(sub);
		}
		for (int i = 0; i < 10; i++) {
			sql.addNode(new TextNode(" " + i + " "));
		}

		TestConsole.println(sql.asBoundSql().toString());
		sql.visitSubsetWritable(op -> {
			if (op.getSqlNode().isTextNode()) {
				if (Integer.parseInt(op.getSqlNode().getText().trim()) % 3 == 0) {
					op.delete();
				}
			}
		});
		TestConsole.println(sql.asBoundSql().toString());
	}

}

