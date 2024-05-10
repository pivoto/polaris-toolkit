package io.polaris.core.jdbc.sql.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
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
import io.polaris.core.jdbc.sql.statement.segment.ColumnSegment;
import io.polaris.core.jdbc.sql.statement.segment.TableAccessible;
import io.polaris.core.jdbc.sql.statement.segment.TableSegment;
import io.polaris.core.lang.Objs;
import io.polaris.core.lang.bean.Beans;

/**
 * @author Qt
 * @since  Aug 20, 2023
 */
@SuppressWarnings("all")
@AnnotationProcessing
public class InsertStatement<S extends InsertStatement<S>> extends BaseStatement<S> implements TableAccessible {

	private TableSegment<?> table;
	private final List<ColumnSegment<S, ?>> columns = new ArrayList<>();
	private boolean enableUpdateByDuplicateKey = false;
	private boolean enableReplace = false;

	@AnnotationProcessing
	public InsertStatement(Class<?> entityClass) {
		this.table = TableSegment.fromEntity(entityClass, null);
	}

	@AnnotationProcessing
	public InsertStatement(Class<?> entityClass, String alias) {
		this.table = TableSegment.fromEntity(entityClass, alias);
	}

	public static InsertStatement<?> of(Class<?> entityClass) {
		return new InsertStatement<>(entityClass);
	}

	public static InsertStatement<?> of(Class<?> entityClass, String alias) {
		return new InsertStatement<>(entityClass, alias);
	}

	@Override
	public SqlNode toSqlNode() {
		ContainerNode sql = new ContainerNode();
		sqlInsert(sql);
		sqlColumn(sql);
		sqlValue(sql);
		sqlUpdateByDuplicateKey(sql);
		return sql;
	}


	private void sqlInsert(ContainerNode sql) {
		if (table != null) {
			if (!sql.isEmpty()) {
				sql.addNode(SqlNodes.LF);
			}
			if (enableReplace) {
				sql.addNode(SqlNodes.REPLACE);
			} else {
				sql.addNode(SqlNodes.INSERT);
			}
			sql.addNode(SqlNodes.INTO);
			sql.addNode(this.table.toSqlNode(false));
		}
	}

	private void sqlColumn(ContainerNode sql) {
		if (!this.columns.isEmpty()) {
			boolean first = true;
			for (ColumnSegment<S, ?> column : this.columns) {
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
	}

	private void sqlValue(ContainerNode sql) {
		if (!this.columns.isEmpty()) {
			boolean first = true;
			for (ColumnSegment<S, ?> column : this.columns) {
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
				sql.addNode(column.toValueSqlNode());
			}
			if (!first) {
				sql.addNode(SqlNodes.RIGHT_PARENTHESIS);
			}
		}
	}

	private void sqlUpdateByDuplicateKey(ContainerNode sql) {
		if (!enableUpdateByDuplicateKey || this.columns.isEmpty()) {
			return;
		}
		boolean first = true;
		for (ColumnSegment<S, ?> column : this.columns) {
			if (first) {
				first = false;
				if (!sql.isEmpty()) {
					sql.addNode(SqlNodes.LF);
				}
				sql.addNode(SqlNodes.ON_DUPLICATE_KEY_UPDATE);
				sql.addNode(SqlNodes.LF);
			} else {
				sql.addNode(SqlNodes.LF);
				sql.addNode(SqlNodes.COMMA);
			}
			String columnName = column.getColumnName();
			sql.addNode(new TextNode(columnName + " = "));
			sql.addNode(column.toValueSqlNode());
		}
	}

	public S enableReplace(boolean enabled) {
		this.enableReplace = enabled;
		return getThis();
	}

	public S enableUpdateByDuplicateKey(boolean enabled) {
		this.enableUpdateByDuplicateKey = enabled;
		return getThis();
	}

	public S withEntity(Object entity) {
		return withEntity(entity, ColumnPredicate.DEFAULT);
	}

	public S withEntity(Object entity, Predicate<String> isIncludeEmptyColumns) {
		return withEntity(entity, ConfigurableColumnPredicate.of(isIncludeEmptyColumns));
	}

	public S withEntity(Object entity, Predicate<String> isIncludeColumns, Predicate<String> isExcludeColumns,
		Predicate<String> isIncludeEmptyColumns, boolean includeAllEmpty) {
		return withEntity(entity, ConfigurableColumnPredicate.of(
			isIncludeColumns, isExcludeColumns, isIncludeEmptyColumns, includeAllEmpty));
	}

	public S withEntity(Object entity, ColumnPredicate columnPredicate) {
		TableMeta tableMeta = table.getTableMeta();
		if (tableMeta != null) {
			Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, tableMeta.getEntityClass());

			for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
				String name = entry.getKey();
				ColumnMeta meta = entry.getValue();
				boolean insertable = meta.isInsertable() || meta.isCreateTime() || meta.isUpdateTime();
				if (!insertable) {
					continue;
				}
				// 不在包含列表
				if (!columnPredicate.isIncludedColumn(name)) {
					continue;
				}

				Object val1 = entityMap.get(meta.getFieldName());
				Object val = BindingValues.getValueForInsert(meta, val1);
				if (meta.isVersion()) {
					val = val == null ? 1L : ((Number) val).longValue();
				}
				// 需要包含空值字段,或为非空值
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
