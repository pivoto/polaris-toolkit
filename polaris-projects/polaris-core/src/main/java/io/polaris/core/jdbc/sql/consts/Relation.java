package io.polaris.core.jdbc.sql.consts;

import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;

/**
 * @author Qt
 * @since  Aug 11, 2023
 */
@SuppressWarnings("all")
public enum Relation {

	AND(SqlNodes.AND),
	OR(SqlNodes.OR),
	;

	final TextNode textNode;

	Relation(TextNode textNode) {
		this.textNode = textNode;
	}

	public String getSqlText() {
		return textNode.getText();
	}

	public TextNode getTextNode() {
		return textNode;
	}
}
