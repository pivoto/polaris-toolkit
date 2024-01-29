package io.polaris.core.jdbc.sql.consts;

import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.DynamicNode;
import io.polaris.core.jdbc.sql.node.MixedNode;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.string.Strings;

/**
 * 常规的查询谓语
 *
 * @author Qt
 * @since 1.8,  Aug 11, 2023
 */
@SuppressWarnings("all")
public enum Operator {
	EQ("="), NE("<>"),
	LT("<"), GT(">"),
	LE("<="), GE(">="),
	LIKE("like"),
	NOT_LIKE("not like"),
	IN("in", "(", ")"),
	NOT_IN("not in", "(", ")"),
	NULL("is null", false),
	NOT_NULL("is not null", false);

	private String sqlText;
	private boolean hasValue = true;
	private String openToken = SymbolConsts.EMPTY;
	private String closeToken = SymbolConsts.EMPTY;

	Operator(String sqlText) {
		this.sqlText = sqlText;
	}

	Operator(String sqlText, boolean hasValue) {
		this.sqlText = sqlText;
		this.hasValue = hasValue;
	}

	Operator(String sqlText, String openToken, String closeToken) {
		this.sqlText = sqlText;
		this.openToken = openToken;
		this.closeToken = closeToken;
	}

	public ContainerNode toSqlNode(String column, String varName, Object varValue, String varReplacement) {
		ContainerNode sql = new ContainerNode();
		sql.addNode(new TextNode(column + " " + sqlText + " "));
		if (hasValue) {
			if (Strings.isNotBlank(openToken)) {
				sql.addNode(new TextNode(openToken));
			}
			if (Strings.isNotBlank(varReplacement)) {
				MixedNode node = new MixedNode(varName);
				node.bindVarValue(varReplacement);
				sql.addNode(node);
			} else {
				DynamicNode node = new DynamicNode(varName);
				node.bindVarValue(varValue);
				sql.addNode(node);
			}
			if (Strings.isNotBlank(closeToken)) {
				sql.addNode(new TextNode(closeToken));
			}
		}

		return sql;
	}

	public String getSqlText() {
		return sqlText;
	}

	public boolean hasValue() {
		return hasValue;
	}

	public String getOpenToken() {
		return openToken;
	}

	public String getCloseToken() {
		return closeToken;
	}
}
