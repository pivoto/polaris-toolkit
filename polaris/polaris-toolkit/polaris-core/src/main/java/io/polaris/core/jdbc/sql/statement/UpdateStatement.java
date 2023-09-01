package io.polaris.core.jdbc.sql.statement;

import io.polaris.core.annotation.AnnotationProcessing;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.Queries;
import io.polaris.core.jdbc.sql.statement.segment.AndSegment;
import io.polaris.core.jdbc.sql.statement.segment.ColumnSegment;
import io.polaris.core.jdbc.sql.statement.segment.TableEntitySegment;
import io.polaris.core.jdbc.sql.statement.segment.TableSegment;
import io.polaris.core.lang.Objs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8,  Aug 20, 2023
 */
@AnnotationProcessing
public class UpdateStatement<S extends UpdateStatement<S>> extends BaseStatement<S> {

	private TableSegment<?> table;
	private final List<ColumnSegment<?>> columns = new ArrayList<>();
	private AndSegment<S, ?> where;
	private final Function<String, String> columnDiscovery;
	private final List<Criteria> criteriaList = new ArrayList<>();

	@AnnotationProcessing
	public UpdateStatement(Class<?> entityClass) {
		this(entityClass, null);
	}

	@AnnotationProcessing
	public UpdateStatement(Class<?> entityClass, String alias) {
		this.table = buildTable(entityClass, alias);
		this.columnDiscovery = columnDiscovery();
	}

	private Function<String, String> columnDiscovery() {
		return field -> {
			String col = null;
			try {
				if (this.table != null) {
					col = this.table.getColumnExpression(field);
				}
			} catch (Exception e) {// 未找到对应的列，忽略此条件字段
			}
			return col;
		};
	}

	protected TableSegment<?> buildTable(Class<?> entityClass, String alias) {
		return new TableEntitySegment<>(entityClass, alias);
	}

	@AnnotationProcessing
	protected AndSegment<S, ?> buildWhere() {
		return new AndSegment<>(getThis(), this.table);
	}


	@Override
	public SqlNode toSqlNode() {
		ContainerNode sql = new ContainerNode();
		sqlUpdate(sql);
		// 暂不支持复杂Update语句（多表join、嵌套）
		sqlSet(sql);
		sqlWhere(sql);
		return sql;
	}

	private void sqlUpdate(ContainerNode sql) {
		if (this.table != null) {
			if (!sql.isEmpty()) {
				sql.addNode(SqlNodes.LF);
			}
			sql.addNode(SqlNodes.UPDATE);
			sql.addNode(this.table.toSqlNode());
		}
	}

	private void sqlSet(ContainerNode sql) {
		if (!this.columns.isEmpty()) {
			boolean first = true;
			for (ColumnSegment<?> column : this.columns) {
				if (!sql.isEmpty()) {
					sql.addNode(SqlNodes.LF);
				}
				if (first) {
					sql.addNode(SqlNodes.SET);
					first = false;
				} else {
					sql.addNode(SqlNodes.COMMA);
				}
				String columnName = column.getColumnName();
				sql.addNode(new TextNode(columnName + " = "));
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
	}

	private void sqlWhere(ContainerNode sql) {
		boolean first = true;
		if (this.where != null) {
			SqlNode sqlNode = this.where.toSqlNode();
			if (!sqlNode.isSkipped()) {
				if (!sql.isEmpty()) {
					sql.addNode(SqlNodes.LF);
				}
				first = false;
				sql.addNode(SqlNodes.WHERE);
				sql.addNode(sqlNode);
			}
		}
		if (!this.criteriaList.isEmpty()) {
			for (Criteria criteria : criteriaList) {
				SqlNode sqlNode = Queries.parse(criteria, false, columnDiscovery);
				if (sqlNode.isSkipped()) {
					continue;
				}
				if (!sql.isEmpty()) {
					sql.addNode(SqlNodes.LF);
				}
				if (first) {
					sql.addNode(SqlNodes.WHERE);
					first = false;
				} else {
					sql.addNode(SqlNodes.AND);
				}
				sql.addNode(SqlNodes.LEFT_PARENTHESIS);
				sql.addNode(sqlNode);
				sql.addNode(SqlNodes.RIGHT_PARENTHESIS);
			}
		}
	}

	@AnnotationProcessing
	public S column(String field, Object value) {
		ColumnSegment<?> column = new ColumnSegment<>(this.table);
		column.column(field).value(value);
		this.columns.add(column);
		return getThis();
	}

	@AnnotationProcessing
	public S column(String field, Object value, BiPredicate<String, Object> predicate) {
		if (predicate.test(field, value)) {
			ColumnSegment<?> column = new ColumnSegment<>(this.table);
			column.column(field).value(value);
			this.columns.add(column);
		}
		return getThis();
	}

	@AnnotationProcessing
	public S column(String field, Object value, Supplier<Boolean> predicate) {
		if (Boolean.TRUE.equals(predicate.get())) {
			ColumnSegment<?> column = new ColumnSegment<>(this.table);
			column.column(field).value(value);
			this.columns.add(column);
		}
		return getThis();
	}

	public S columnRaw(String rawColumn, Object value) {
		ColumnSegment<?> column = new ColumnSegment<>(this.table);
		column.rawColumn(rawColumn).value(value);
		this.columns.add(column);
		return getThis();
	}

	public S columnRaw(String rawColumn, Object value, BiPredicate<String, Object> predicate) {
		if (predicate.test(rawColumn, value)) {
			ColumnSegment<?> column = new ColumnSegment<>(this.table);
			column.rawColumn(rawColumn).value(value);
			this.columns.add(column);
		}
		return getThis();
	}

	public S columnRaw(String rawColumn, Object value, Supplier<Boolean> predicate) {
		if (Boolean.TRUE.equals(predicate.get())) {
			ColumnSegment<?> column = new ColumnSegment<>(this.table);
			column.rawColumn(rawColumn).value(value);
			this.columns.add(column);
		}
		return getThis();
	}

	public S where(Criteria criteria) {
		if (criteria != null) {
			criteriaList.add(criteria);
		}
		return getThis();
	}

	@AnnotationProcessing
	@SuppressWarnings("unchecked")
	public <W extends AndSegment<S, W>> W where() {
		return (W) (where = Objs.defaultIfNull(where, this::buildWhere));
	}


	@AnnotationProcessing
	public TableSegment<?> getTable() {
		return table;
	}
}
