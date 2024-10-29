package io.polaris.core.jdbc.sql.statement.segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.ExpressionMeta;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.TableMetaKit;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since Aug 20, 2023
 */
public class TableEntitySegment<S extends TableEntitySegment<S>> extends TableSegment<S> {
	private final TableMeta tableMeta;
	private final Class<?> entityClass;
	private final String alias;

	public TableEntitySegment(Class<?> entityClass, String alias) {
		this.entityClass = entityClass;
		this.tableMeta = entityClass == null ? null : TableMetaKit.instance().get(entityClass);
		// this.alias = alias;
		this.alias = Strings.coalesce(alias, this.tableMeta.getAlias());
	}

	@Override
	public SqlNode toSqlNode(boolean withAlias) {
		if (!withAlias) {
			return new TextNode(this.tableMeta.getTable());
		}
		String tableAlias = getTableAlias();
		if (Strings.isBlank(tableAlias)) {
			return new TextNode(this.tableMeta.getTable());
		}
		return new TextNode(this.tableMeta.getTable() + " " + tableAlias);
	}

	@Override
	public SqlNode toSqlNode() {
		return toSqlNode(true);
	}


	@Override
	public TableMeta getTableMeta() {
		return tableMeta;
	}

	@Override
	public String getTableAlias() {
		return this.alias;
	}

	@Override
	public String getAllColumnExpression(boolean quotaAlias) {
		return getAllColumnExpression(true, quotaAlias);
	}

	@Override
	public String getAllColumnExpression(boolean withTableAlias, boolean quotaAlias) {
		return getAllColumnExpression(withTableAlias, false, quotaAlias, "", "");
	}

	@Override
	public String getAllColumnExpression(boolean aliasWithField, boolean quotaAlias, String aliasPrefix, String aliasSuffix) {
		return getAllColumnExpression(true, aliasWithField, quotaAlias, "", "");
	}

	@Override
	public String getAllColumnExpression(boolean withTableAlias, boolean aliasWithField, boolean quotaAlias, String aliasPrefix, String aliasSuffix) {
		String alias = getTableAlias();
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, ColumnMeta> entry : this.tableMeta.getColumns().entrySet()) {
			String field = entry.getKey();
			ColumnMeta columnMeta = entry.getValue();
			String columnName = columnMeta.getColumnName();
			if (sb.length() > 0) {
				sb.append(", ");
			}
			if (Strings.isNotBlank(alias) && withTableAlias) {
				sb.append(alias).append(".");
			}
			sb.append(columnName);
			if (aliasWithField) {
				String fieldAlias = SelectSegment.toAlias(field, aliasPrefix, aliasSuffix);
				if (quotaAlias && !fieldAlias.startsWith("\"")) {
					sb.append(" ").append("\"").append(fieldAlias).append("\"");
				} else {
					sb.append(" ").append(fieldAlias);
				}
			}
		}
		for (Map.Entry<String, ExpressionMeta> entry : this.tableMeta.getExpressions().entrySet()) {
			String field = entry.getKey();
			ExpressionMeta expressionMeta = entry.getValue();
			if (!expressionMeta.isSelectable()) {
				continue;
			}

			if (sb.length() > 0) {
				sb.append(", ");
			}
			if (Strings.isNotBlank(alias) && withTableAlias) {
				sb.append(expressionMeta.getExpressionWithTableAlias(alias));
			} else {
				// 无别名时，使用表名，防止子查询中字段来源不明确
				sb.append(expressionMeta.getExpressionWithTableName());
			}
			if (aliasWithField) {
				String fieldAlias = SelectSegment.toAlias(field, aliasPrefix, aliasSuffix);
				if (quotaAlias && !fieldAlias.startsWith("\"")) {
					sb.append(" ").append("\"").append(fieldAlias).append("\"");
				} else {
					sb.append(" ").append(fieldAlias);
				}
			}
		}
		return sb.toString();
	}

	@Override
	public String getColumnExpression(String field) {
		return getColumnExpression(field, true);
	}

	@Override
	public String getColumnExpression(String field, boolean withTableAlias) {
		String alias = getTableAlias();

		ColumnMeta columnMeta = this.tableMeta.getColumns().get(field);
		ExpressionMeta expressionMeta = this.tableMeta.getExpressions().get(field);
		if (columnMeta == null && expressionMeta == null) {
			throw new IllegalArgumentException("找不到表对应的列信息：" + field);
		}
		if (columnMeta != null) {
			if (Strings.isNotBlank(alias) && withTableAlias) {
				return alias + "." + columnMeta.getColumnName();
			} else {
				return columnMeta.getColumnName();
			}
		} else {
			if (Strings.isNotBlank(alias) && withTableAlias) {
				return expressionMeta.getExpressionWithTableAlias(alias);
			} else {
				// 无别名时，使用表名，防止子查询中字段来源不明确
				return expressionMeta.getExpressionWithTableName();
			}
		}
	}

	@Override
	public List<String> getAllColumnNames() {
		// 不包括表达式字段
		Map<String, ColumnMeta> columns = this.tableMeta.getColumns();
		return columns.values().stream().map(ColumnMeta::getColumnName).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllFieldNames() {
		// 不包括表达式字段
		Map<String, ColumnMeta> columns = this.tableMeta.getColumns();
		return new ArrayList<>(columns.keySet());
	}

	@Override
	public String getColumnName(String field) {
		// 不包括表达式字段
		ColumnMeta columnMeta = this.tableMeta.getColumns().get(field);
		if (columnMeta == null) {
			throw new IllegalArgumentException("找不到表对应的列信息：" + field);
		}
		return columnMeta.getColumnName();
	}

}
