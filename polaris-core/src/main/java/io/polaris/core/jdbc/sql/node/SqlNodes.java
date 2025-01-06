package io.polaris.core.jdbc.sql.node;

import io.polaris.core.consts.StdKeys;
import io.polaris.core.env.GlobalStdEnv;

/**
 * @author Qt
 * @since Aug 24, 2023
 */
public interface SqlNodes {

	ContainerNode EMPTY = ContainerNode.EMPTY;

	TextNode NULL = new TextNode("NULL");
	TextNode BLANK = new TextNode(" ");
	TextNode LF = new TextNode(GlobalStdEnv.get(StdKeys.JDBC_SQL_LINE_SEPARATOR, " "));
	TextNode SELECT = new TextNode("SELECT ");
	TextNode DELETE = new TextNode("DELETE ");
	TextNode UPDATE = new TextNode("UPDATE ");
	TextNode INSERT = new TextNode("INSERT ");
	TextNode DISTINCT = new TextNode("DISTINCT ");
	TextNode FROM = new TextNode("FROM ");
	TextNode JOIN = new TextNode("JOIN ");
	TextNode INNER_JOIN = new TextNode("INNER JOIN ");
	TextNode LEFT_JOIN = new TextNode("LEFT OUTER JOIN ");
	TextNode RIGHT_JOIN = new TextNode("RIGHT OUTER JOIN ");
	TextNode OUTER_JOIN = new TextNode("OUTER JOIN ");
	TextNode COMMA = new TextNode(", ");
	TextNode EQUALS = new TextNode(" = ");
	TextNode ON = new TextNode(" ON ");
	TextNode WHERE = new TextNode("WHERE ");
	TextNode AND = new TextNode(" AND ");
	TextNode OR = new TextNode(" OR ");
	TextNode NOT = new TextNode(" NOT ");
	TextNode EXISTS = new TextNode(" EXISTS ");
	TextNode NOT_EXISTS = new TextNode(" NOT EXISTS ");
	TextNode LIKE = new TextNode(" LIKE ");
	TextNode IN = new TextNode(" IN ");
	TextNode NOT_IN = new TextNode(" NOT IN ");
	TextNode GROUP_BY = new TextNode("GROUP BY ");
	TextNode HAVING = new TextNode("HAVING ");
	TextNode ORDER_BY = new TextNode("ORDER BY ");
	TextNode ASC = new TextNode(" ASC");
	TextNode DESC = new TextNode(" DESC");
	TextNode SET = new TextNode("SET ");
	TextNode INTO = new TextNode("INTO ");
	TextNode VALUES = new TextNode("VALUES ");
	TextNode LEFT_PARENTHESIS = new TextNode("( ");
	TextNode RIGHT_PARENTHESIS = new TextNode(" )");
	TextNode REPLACE = new TextNode("REPLACE ");
	TextNode MERGE = new TextNode("MERGE ");
	TextNode USING = new TextNode("USING ");
	TextNode WHEN_MATCHED_THEN = new TextNode("WHEN MATCHED THEN ");
	TextNode WHEN_NOT_MATCHED_THEN = new TextNode("WHEN NOT MATCHED THEN ");
	TextNode ON_DUPLICATE_KEY_UPDATE = new TextNode("ON DUPLICATE KEY UPDATE ");
	TextNode UNION = new TextNode("UNION");
	TextNode UNION_ALL = new TextNode("UNION ALL");
	TextNode INTERSECT = new TextNode("INTERSECT");
	TextNode INTERSECT_ALL = new TextNode("INTERSECT ALL");
	TextNode MINUS = new TextNode("MINUS");
	TextNode MINUS_ALL = new TextNode("MINUS ALL");
	TextNode EXCEPT = new TextNode("EXCEPT");
	TextNode EXCEPT_ALL = new TextNode("EXCEPT ALL");


	static ContainerNode group(SqlNode... sqlNodes) {
		ContainerNode container = new ContainerNode();
		container.addNodes(sqlNodes);
		return container;
	}

	static TextNode text(String text) {
		return new TextNode(text);
	}

	static VarNode dynamic(String varName, Object varValue) {
		DynamicNode varNode = new DynamicNode(varName);
		varNode.bindVarValue(varValue);
		return varNode;
	}

	static VarNode mixed(String varName, Object varValue) {
		MixedNode varNode = new MixedNode(varName);
		varNode.bindVarValue(varValue);
		return varNode;
	}


}
