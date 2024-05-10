package io.polaris.core.jdbc.sql.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.polaris.core.annotation.AnnotationProcessing;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.sql.BindingValues;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.Queries;
import io.polaris.core.jdbc.sql.statement.segment.AndSegment;
import io.polaris.core.jdbc.sql.statement.segment.ColumnSegment;
import io.polaris.core.jdbc.sql.statement.segment.TableAccessible;
import io.polaris.core.jdbc.sql.statement.segment.TableSegment;
import io.polaris.core.lang.Objs;
import io.polaris.core.lang.bean.Beans;

/**
 * @author Qt
 * @since  Aug 20, 2023
 */
@SuppressWarnings("unused")
@AnnotationProcessing
public class UpdateStatement<S extends UpdateStatement<S>> extends BaseStatement<S> implements TableAccessible {

	private final TableSegment<?> table;
	private final List<ColumnSegment<S, ?>> columns = new ArrayList<>();
	private AndSegment<S, ?> where;
	private final Function<String, String> columnDiscovery;
	private final List<Criteria> criteriaList = new ArrayList<>();

	@AnnotationProcessing
	public UpdateStatement(Class<?> entityClass) {
		this(entityClass, null);
	}

	@AnnotationProcessing
	public UpdateStatement(Class<?> entityClass, String alias) {
		this.table = TableSegment.fromEntity(entityClass, alias);
		this.columnDiscovery = columnDiscovery();
	}

	public static UpdateStatement<?> of(Class<?> entityClass, String alias) {
		return new UpdateStatement<>(entityClass, alias);
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
			for (ColumnSegment<S, ?> column : this.columns) {
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
				sql.addNode(column.toValueSqlNode());
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

	public S withEntity(Object entity) {
		return withEntity(entity, ColumnPredicate.DEFAULT);
	}

	public S withEntity(Object entity, Predicate<String> isIncludeEmptyColumns) {
		return withEntity(entity, ConfigurableColumnPredicate.of(isIncludeEmptyColumns));
	}

	public S withEntity(Object entity, String[] includeEmptyColumns) {
		return withEntity(entity, ConfigurableColumnPredicate.of(includeEmptyColumns));
	}

	public S withEntity(Object entity, Predicate<String> isIncludeColumns, Predicate<String> isExcludeColumns
		, Predicate<String> isIncludeEmptyColumns, boolean includeAllEmpty) {
		return withEntity(entity, ConfigurableColumnPredicate.of(
			isIncludeColumns, isExcludeColumns, isIncludeEmptyColumns, includeAllEmpty));
	}

	public S withEntity(Object entity, String[] includeColumns, String[] excludeColumns
		, String[] includeEmptyColumns, boolean includeAllEmpty) {
		return withEntity(entity, ConfigurableColumnPredicate.of(
			includeColumns, excludeColumns, includeEmptyColumns, includeAllEmpty));
	}

	public S withEntity(Object entity, ColumnPredicate columnPredicate) {
		TableMeta tableMeta = this.table.getTableMeta();
		if (tableMeta != null) {
			@SuppressWarnings("unchecked")
			Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, tableMeta.getEntityClass());

			for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
				String name = entry.getKey();
				ColumnMeta meta = entry.getValue();
				boolean updatable = meta.isUpdatable() || meta.isVersion() || meta.isUpdateTime();
				if (!updatable) {
					continue;
				}
				if (meta.isPrimaryKey()) {
					// 不更新主键值
					continue;
				}
				// 不在包含列表
				if (!columnPredicate.isIncludedColumn(name)) {
					continue;
				}
				Object val1 = entityMap.get(meta.getFieldName());
				Object val = BindingValues.getValueForUpdate(meta, val1);
				if (meta.isVersion()) {
					val = Objs.isEmpty(val) ? 1L : ((Number) val).longValue() + 1;
				}

				// 需要包含空值字段或非空值
				boolean include = columnPredicate.isIncludedEmptyColumn(name) || Objs.isNotEmpty(val);
				if (include) {
					this.column(name, val);
				}
			}
		}
		return getThis();
	}

	private ColumnSegment<S, ?> buildColumnSegment() {
		return new ColumnSegment<>(getThis(), this.table);
	}

	@SuppressWarnings("unchecked")
	@AnnotationProcessing
	public <C extends ColumnSegment<S, C>> C column(String field) {
		ColumnSegment<S, ?> column = buildColumnSegment();
		column.column(field);
		this.columns.add(column);
		return (C) column;
	}

	@AnnotationProcessing
	public S column(String field, Object value) {
		ColumnSegment<S, ?> column = buildColumnSegment();
		column.column(field).value(value);
		this.columns.add(column);
		return getThis();
	}

	@AnnotationProcessing
	public S column(String field, Object value, BiPredicate<String, Object> predicate) {
		if (predicate.test(field, value)) {
			ColumnSegment<S, ?> column = buildColumnSegment();
			column.column(field).value(value);
			this.columns.add(column);
		}
		return getThis();
	}

	@AnnotationProcessing
	public S column(String field, Object value, Supplier<Boolean> predicate) {
		if (Boolean.TRUE.equals(predicate.get())) {
			ColumnSegment<S, ?> column = buildColumnSegment();
			column.column(field).value(value);
			this.columns.add(column);
		}
		return getThis();
	}

	public S columnRaw(String rawColumn, Object value) {
		ColumnSegment<S, ?> column = buildColumnSegment();
		column.rawColumn(rawColumn).value(value);
		this.columns.add(column);
		return getThis();
	}


	public S columnRaw(String rawColumn, Object value, BiPredicate<String, Object> predicate) {
		if (predicate.test(rawColumn, value)) {
			ColumnSegment<S, ?> column = buildColumnSegment();
			column.rawColumn(rawColumn).value(value);
			this.columns.add(column);
		}
		return getThis();
	}

	public S columnRaw(String rawColumn, Object value, Supplier<Boolean> predicate) {
		if (Boolean.TRUE.equals(predicate.get())) {
			ColumnSegment<S, ?> column = buildColumnSegment();
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


	@Override
	public TableSegment<?> getTable(int tableIndex) {
		// 不支持负数定位
		if (tableIndex < 0) {
			throw new IllegalArgumentException("tableIndex: " + tableIndex);
		}
		if (tableIndex == 0) {
			return this.table;
		}
		throw new IllegalArgumentException("no such table! tableIndex: " + tableIndex);
	}

	@Override
	public TableSegment<?> getTable(String tableAlias) {
		if (Objs.equals(this.table.getTableAlias(), tableAlias)) {
			return this.table;
		}
		throw new IllegalArgumentException("no such table! tableAlias: " + tableAlias);
	}
}
