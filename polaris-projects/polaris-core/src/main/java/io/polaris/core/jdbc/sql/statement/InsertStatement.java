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
import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.statement.segment.ColumnSegment;
import io.polaris.core.jdbc.sql.statement.segment.TableSegment;
import io.polaris.core.lang.Objs;
import io.polaris.core.lang.bean.Beans;

/**
 * @author Qt
 * @since 1.8,  Aug 20, 2023
 */
@SuppressWarnings("all")
@AnnotationProcessing
public class InsertStatement<S extends InsertStatement<S>> extends BaseStatement<S> {

	private TableSegment<?> table;
	private final List<ColumnSegment<?>> columns = new ArrayList<>();
	private boolean enableUpdateByDuplicateKey = false;
	private boolean enableReplace = false;

	@AnnotationProcessing
	public InsertStatement(Class<?> entityClass) {
		this.table = TableSegment.fromEntity(entityClass, null);
	}

	public static InsertStatement<?> of(Class<?> entityClass) {
		return new InsertStatement<>(entityClass);
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
			for (ColumnSegment<?> column : this.columns) {
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
			for (ColumnSegment<?> column : this.columns) {
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

	private void sqlUpdateByDuplicateKey(ContainerNode sql) {
		if (!enableUpdateByDuplicateKey || this.columns.isEmpty()) {
			return;
		}
		boolean first = true;
		for (ColumnSegment<?> column : this.columns) {
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

	public S enableReplace(boolean enabled) {
		this.enableReplace = enabled;
		return getThis();
	}

	public S enableUpdateByDuplicateKey(boolean enabled) {
		this.enableUpdateByDuplicateKey = enabled;
		return getThis();
	}

	public S withEntity(Object entity) {
		return withEntity(entity, null, null, false, null);
	}

	public S withEntity(Object entity, Predicate<String> isIncludeEmptyColumns) {
		return withEntity(entity, null, null, false, isIncludeEmptyColumns);
	}

	public S withEntity(Object entity, Predicate<String> isIncludeColumns, Predicate<String> isExcludeColumns, boolean includeAllEmpty, Predicate<String> isIncludeEmptyColumns) {
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
				if (isIncludeColumns != null && !isIncludeColumns.test(name)) {
					continue;
				}
				// 在排除列表
				if (isExcludeColumns != null && isExcludeColumns.test(name)) {
					continue;
				}

				Object val = EntityStatements.getValForInsert(entityMap, meta);
				if (meta.isVersion()) {
					val = val == null ? 1L : ((Number) val).longValue() + 1;
				}
				boolean include =
					// 需要包含空值字段
					includeAllEmpty || (isIncludeEmptyColumns != null && isIncludeEmptyColumns.test(name))
						// 或为非空值
						|| Objs.isNotEmpty(val);

				if (include) {
					this.column(name, val);
				}
			}
		}
		return getThis();
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

				Object val = EntityStatements.getValForInsert(entityMap, meta);
				if (meta.isVersion()) {
					val = val == null ? 1L : ((Number) val).longValue() + 1;
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


}
