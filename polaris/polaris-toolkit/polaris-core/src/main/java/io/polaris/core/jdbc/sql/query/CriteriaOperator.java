package io.polaris.core.jdbc.sql.query;

import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.PreparedVarNode;
import io.polaris.core.jdbc.sql.node.ReplacedVarNode;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.string.Strings;

/**
 * 常规的查询谓语
 *
 * @author Qt
 * @since 1.8,  Aug 11, 2023
 */
@SuppressWarnings("all")
public enum CriteriaOperator {
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

	CriteriaOperator(String sqlText) {
		this.sqlText = sqlText;
	}

	CriteriaOperator(String sqlText, boolean hasValue) {
		this.sqlText = sqlText;
		this.hasValue = hasValue;
	}

	CriteriaOperator(String sqlText, String openToken, String closeToken) {
		this.sqlText = sqlText;
		this.openToken = openToken;
		this.closeToken = closeToken;
	}

	public ContainerNode toSqlNode(String fieldName, String varName, Object varValue, String varReplacement) {
		ContainerNode sql = new ContainerNode();
		sql.addNode(new TextNode(fieldName + " " + sqlText + " "));
		if (hasValue) {
			if (Strings.isNotBlank(openToken)) {
				sql.addNode(new TextNode(openToken));
			}
			if (Strings.isNotBlank(varReplacement)) {
				ReplacedVarNode node = new ReplacedVarNode(varName);
				node.bindVarValue(varReplacement);
				sql.addNode(node);
			} else {
				PreparedVarNode node = new PreparedVarNode(varName);
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
