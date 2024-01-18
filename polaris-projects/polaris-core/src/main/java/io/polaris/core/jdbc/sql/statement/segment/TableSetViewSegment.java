package io.polaris.core.jdbc.sql.statement.segment;

import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.statement.SetOpsStatement;
import io.polaris.core.string.Strings;

import java.util.List;

/**
 * @author Qt
 * @since 1.8,  Aug 23, 2023
 */
public class TableSetViewSegment<S extends TableSetViewSegment<S>> extends TableSegment<S> {

	private final SetOpsStatement<?> select;
	private final String alias;

	public TableSetViewSegment(SetOpsStatement<?> select, String alias) {
		if (Strings.isBlank(alias)) {
			throw new IllegalArgumentException("未指定子查询别名");
		}
		this.alias = alias;
		this.select = select;
	}

	@Override
	public SqlNode toSqlNode(boolean withAlias) {
		SqlNode sql = new ContainerNode();
		sql.addNode(SqlNodes.LEFT_PARENTHESIS);
		sql.addNode(select.toSqlNode());
		sql.addNode(SqlNodes.RIGHT_PARENTHESIS);
		if (withAlias) {
			sql.addNode(SqlNodes.BLANK);
			sql.addNode(new TextNode(alias));
		}
		return sql;
	}

	@Override
	public SqlNode toSqlNode() {
		return toSqlNode(true);
	}

	@Override
	public String getTableAlias() {
		return alias;
	}

	@Override
	public String getAllColumnExpression(boolean aliasWithField, boolean quotaAlias, String aliasPrefix, String aliasSuffix) {
		return getAllColumnExpression(quotaAlias);
	}

	@Override
	public String getAllColumnExpression(boolean quotaAlias) {
		List<String> selectColumns = this.select.getSelectRawColumns();
		StringBuilder sb = new StringBuilder();
		for (String selectColumn : selectColumns) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(alias).append(".").append(selectColumn);
		}
		return sb.toString();
	}

	@Override
	public List<String> getAllColumnNames() {
		return this.select.getSelectRawColumns();
	}

	@Override
	public List<String> getAllFieldNames() {
		return this.select.getSelectRawColumns();
	}

	@Override
	public String getColumnExpression(String field) {
		return this.alias + "." + getColumnName(field);
	}

	@Override
	public String getColumnName(String field) {
		if (select.hasSelectRawColumn(field)) {
			return field;
		}
		throw new IllegalStateException("找不到表对应的列信息：" + field);
	}


}
