package io.polaris.core.jdbc.sql.statement.segment;

import java.util.Collections;
import java.util.List;

import io.polaris.core.annotation.Experimental;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since Aug 20, 2023
 */
@Experimental
public class TableRawSegment<S extends TableRawSegment<S>> extends TableSegment<S> {
	private final String rawTable;
	private final String alias;

	public TableRawSegment(String rawTable, String alias) {
		this.rawTable = rawTable;
		this.alias = alias;
	}

	@Override
	public SqlNode toSqlNode(boolean withAlias) {
		if (!withAlias) {
			return new TextNode(rawTable);
		}
		String tableAlias = getTableAlias();
		if (Strings.isBlank(tableAlias)) {
			return new TextNode(rawTable);
		}
		return new TextNode(rawTable + " " + tableAlias);
	}

	@Override
	public SqlNode toSqlNode() {
		return toSqlNode(true);
	}

	@Override
	public String getTableAlias() {
		return this.alias;
	}

	@Override
	public String getAllColumnExpression(boolean withTableAlias, boolean aliasWithField, boolean quotaAlias, String aliasPrefix, String aliasSuffix) {
		String alias = getTableAlias();
		StringBuilder sb = new StringBuilder();
		if (Strings.isNotBlank(alias) && withTableAlias) {
			sb.append(alias).append(".");
		}
		sb.append("*");
		return sb.toString();
	}

	@Override
	public String getColumnExpression(String field, boolean withTableAlias) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> getAllColumnNames() {
		return Collections.emptyList();
	}

	@Override
	public List<String> getAllFieldNames() {
		return Collections.emptyList();
	}

	@Override
	public String getColumnName(String field) {
		throw new UnsupportedOperationException();
	}

}
