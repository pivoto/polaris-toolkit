package io.polaris.core.jdbc.sql.node;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

class ContainerNodeTest {

	@Test
	void test01() {
		ContainerNode container = new ContainerNode(
			SqlNodes.group(SqlNodes.RIGHT_PARENTHESIS, SqlNodes.AND, SqlNodes.LEFT_PARENTHESIS),
			SqlNodes.group(SqlNodes.WHERE, SqlNodes.LEFT_PARENTHESIS),
			SqlNodes.RIGHT_PARENTHESIS);
		{
			ContainerNode node1 = new ContainerNode(SqlNodes.OR);
			node1.addNode(SqlNodes.group(new TextNode(" a = "), new DynamicNode("v1")));
			node1.addNode(new TextNode(" a = 3"));
			node1.bindSubsetVarValue("a", "xx");
			container.addNode(node1);
		}
		{
			ContainerNode node1 = new ContainerNode(SqlNodes.OR);
			node1.addNode(new TextNode(" a = 1"));
			node1.addNode(new TextNode(" a = 2"));
			node1.addNode(new TextNode(" a = 3"));
			container.addNodes(node1);
		}
		Object[] args1 = new Object[]{container.asPreparedSql()};
		Consoles.println(args1);
		Object[] args = new Object[]{container.asBoundSql()};
		Consoles.println(args);
	}
}
