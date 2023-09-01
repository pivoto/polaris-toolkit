package io.polaris.core.jdbc.sql.statement;

import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.statement.segment.*;
import io.polaris.core.lang.Objs;
import io.polaris.core.string.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Oracle Merge Into
 *
 * @author Qt
 * @since 1.8,  Aug 30, 2023
 */
public class MergeStatement<S extends MergeStatement<S>> extends BaseStatement<S> implements TableAccessible {
	private final TableSegment<?> table;
	private TableSegment<?> using;
	private AndSegment<S, ?> on;
	private final List<ColumnSegment<?>> insertColumns = new ArrayList<>();
	private final List<ColumnSegment<?>> updateColumns = new ArrayList<>();
	private boolean updateWhenMatched;
	private boolean insertWhenNotMatched;

	public MergeStatement(Class<?> entityClass, String alias) {
		if (Strings.isBlank(alias)) {
			throw new IllegalArgumentException("未指定别名");
		}
		this.table = buildTable(entityClass, alias);
	}

	protected TableSegment<?> buildTable(Class<?> entityClass, String alias) {
		return new TableEntitySegment<>(entityClass, alias);
	}

	protected TableSegment<?> buildView(SelectStatement<?> select, String alias) {
		return new TableViewSegment<>(select, alias);
	}

	@SuppressWarnings("unchecked")
	protected <T extends AndSegment<S, T>> T buildWhere() {
		return (T) new AndSegment<>(getThis(), this.table);
	}

	@Override
	public SqlNode toSqlNode() {
		ContainerNode sql = new ContainerNode();
		sqlMerge(sql);
		sqlUsing(sql);
		sqlOn(sql);
		sqlUpdate(sql);
		sqlInsert(sql);
		return sql;
	}

	private void sqlMerge(ContainerNode sql) {
		if (!sql.isEmpty()) {
			sql.addNode(SqlNodes.LF);
		}
		sql.addNodes(SqlNodes.MERGE, SqlNodes.INTO, this.table.toSqlNode());
	}

	private void sqlUsing(ContainerNode sql) {
		if (!sql.isEmpty()) {
			sql.addNode(SqlNodes.LF);
		}
		sql.addNodes(SqlNodes.USING, this.using.toSqlNode());
	}

	private void sqlOn(ContainerNode sql) {
		if (!sql.isEmpty()) {
			sql.addNode(SqlNodes.LF);
		}
		sql.addNodes(SqlNodes.ON, this.on.toSqlNode());
	}

	private void sqlUpdate(ContainerNode sql) {
		if (!updateWhenMatched) {
			return;
		}
		if (!sql.isEmpty()) {
			sql.addNode(SqlNodes.LF);
		}
		sql.addNode(SqlNodes.WHEN_MATCHED_THEN);
		sql.addNode(SqlNodes.UPDATE);
		sql.addNode(SqlNodes.LF);
		boolean first = true;
		for (ColumnSegment<?> column : this.updateColumns) {
			if (first) {
				sql.addNode(SqlNodes.SET);
				first = false;
			} else {
				sql.addNode(SqlNodes.LF);
				sql.addNode(SqlNodes.COMMA);
			}
			String columnName = column.getColumnName();
			sql.addNode(new TextNode(this.table.getTableAlias() + "." + columnName + " = "));
			Object columnValue = column.getColumnValue();
			if (columnValue instanceof SqlNode) {
				sql.addNode((SqlNode) columnValue);
			} else {
				if (columnValue == null) {
					sql.addNode(SqlNodes.mixed(columnName, null));
				} else {
					sql.addNode(SqlNodes.dynamic(columnName, columnValue));
				}
			}
		}
	}

	private void sqlInsert(ContainerNode sql) {
		if (!insertWhenNotMatched) {
			return;
		}
		if (!sql.isEmpty()) {
			sql.addNode(SqlNodes.LF);
		}
		sql.addNode(SqlNodes.WHEN_NOT_MATCHED_THEN);
		sql.addNode(SqlNodes.INSERT);
		sql.addNode(SqlNodes.LF);
		{
			boolean first = true;
			for (ColumnSegment<?> column : this.insertColumns) {
				if (first) {
					if (!sql.isEmpty()) {
						sql.addNode(SqlNodes.LF);
					}
					sql.addNode(SqlNodes.LEFT_PARENTHESIS);
					first = false;
				} else {
					sql.addNode(SqlNodes.COMMA);
				}
				sql.addNode(new TextNode(column.getColumnName()));
			}
			if (!first) {
				sql.addNode(SqlNodes.RIGHT_PARENTHESIS);
			}
		}
		{
			boolean first = true;
			for (ColumnSegment<?> column : this.insertColumns) {
				if (first) {
					if (!sql.isEmpty()) {
						sql.addNode(SqlNodes.LF);
					}
					sql.addNode(SqlNodes.VALUES);
					sql.addNode(SqlNodes.LEFT_PARENTHESIS);
					first = false;
				} else {
					sql.addNode(SqlNodes.COMMA);
				}
				Object columnValue = column.getColumnValue();
				if (columnValue instanceof SqlNode) {
					sql.addNode((SqlNode) columnValue);
				} else {
					if (columnValue == null) {
						sql.addNode(SqlNodes.mixed(column.getColumnName(), null));
					} else {
						sql.addNode(SqlNodes.dynamic(column.getColumnName(), columnValue));
					}
				}
			}
			if (!first) {
				sql.addNode(SqlNodes.RIGHT_PARENTHESIS);
			}
		}
	}

	public S using(SelectStatement<?> select, String alias) {
		this.using = buildView(select, alias);
		return getThis();
	}

	public S using(Class<?> entityClass, String alias) {
		this.using = buildTable(entityClass, alias);
		return getThis();
	}

	@SuppressWarnings("unchecked")
	public <T extends AndSegment<S, T>> T on() {
		return (T) (on = Objs.defaultIfNull(on, this::buildWhere));
	}

	public S updateWhenMatched() {
		this.updateWhenMatched = true;
		return getThis();
	}

	public S insertWhenNotMatched() {
		this.insertWhenNotMatched = true;
		return getThis();
	}

	public S insertWith(String field, String usingField) {
		ColumnSegment<?> column = new ColumnSegment<>(this.table);
		column.column(field).value(SqlNodes.text(this.using.getColumnExpression(usingField)));
		this.insertColumns.add(column);
		return getThis();
	}

	public S insert(String field, Object value) {
		ColumnSegment<?> column = new ColumnSegment<>(this.table);
		column.column(field).value(value);
		this.insertColumns.add(column);
		return getThis();
	}

	public S insertRawWith(String rawColumn, String usingColumn) {
		ColumnSegment<?> column = new ColumnSegment<>(this.table);
		column.rawColumn(rawColumn).value(SqlNodes.text(usingColumn));
		this.insertColumns.add(column);
		return getThis();
	}

	public S insertRaw(String rawColumn, Object value) {
		ColumnSegment<?> column = new ColumnSegment<>(this.table);
		column.rawColumn(rawColumn).value(value);
		this.insertColumns.add(column);
		return getThis();
	}

	public S updateWith(String field, String usingField) {
		ColumnSegment<?> column = new ColumnSegment<>(this.table);
		column.column(field).value(SqlNodes.text(this.using.getColumnExpression(usingField)));
		this.updateColumns.add(column);
		return getThis();
	}

	public S update(String field, Object value) {
		ColumnSegment<?> column = new ColumnSegment<>(this.table);
		column.column(field).value(value);
		this.updateColumns.add(column);
		return getThis();
	}

	public S updateRawWith(String rawColumn, String usingColumn) {
		ColumnSegment<?> column = new ColumnSegment<>(this.table);
		column.rawColumn(rawColumn).value(SqlNodes.text(usingColumn));
		this.updateColumns.add(column);
		return getThis();
	}

	public S updateRaw(String rawColumn, Object value) {
		ColumnSegment<?> column = new ColumnSegment<>(this.table);
		column.rawColumn(rawColumn).value(value);
		this.updateColumns.add(column);
		return getThis();
	}


	@Override
	public TableSegment<?> getTable(String tableAlias) {
		if (Objs.equals(this.table.getTableAlias(), tableAlias)) {
			return this.table;
		}
		if (Objs.equals(this.using.getTableAlias(), tableAlias)) {
			return this.using;
		}
		throw new IllegalStateException("找不到对应的表");
	}


	@Override
	public TableSegment<?> getTable(int tableIndex) {
		if (tableIndex == 0) {
			return this.table;
		}
		int size = 2;
		while (tableIndex < 0) {
			tableIndex += size;
		}
		if (tableIndex > 0 && tableIndex < size) {
			return this.using;
		}
		throw new IllegalStateException("找不到对应的表");
	}
}
