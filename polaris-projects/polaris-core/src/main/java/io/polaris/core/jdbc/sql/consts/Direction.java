package io.polaris.core.jdbc.sql.consts;

import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since 1.8,  Aug 29, 2023
 */
@SuppressWarnings("all")
public enum Direction {
	ASC(SqlNodes.ASC),
	DESC(SqlNodes.DESC),
	;

	private final TextNode textNode;

	Direction(TextNode textNode) {
		this.textNode = textNode;
	}

	public String getSqlText() {
		return textNode.getText();
	}

	public TextNode getTextNode() {
		return textNode;
	}

	public static Direction parse(String value) {
		if (Strings.isBlank(value)) {
			return ASC;
		}
		if (value.trim().equalsIgnoreCase(DESC.name())) {
			return DESC;
		}
		return ASC;
	}
}
