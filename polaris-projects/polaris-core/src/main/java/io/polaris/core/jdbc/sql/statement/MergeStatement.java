package io.polaris.core.jdbc.sql.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.sql.BindingValues;
import io.polaris.core.jdbc.sql.node.ContainerNode;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.jdbc.sql.node.TextNode;
import io.polaris.core.jdbc.sql.statement.segment.AndSegment;
import io.polaris.core.jdbc.sql.statement.segment.ColumnSegment;
import io.polaris.core.jdbc.sql.statement.segment.TableAccessible;
import io.polaris.core.jdbc.sql.statement.segment.TableField;
import io.polaris.core.jdbc.sql.statement.segment.TableSegment;
import io.polaris.core.jdbc.table.DualEntity;
import io.polaris.core.lang.Objs;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.string.Strings;

/**
 * Oracle Merge Into
 *
 * @author Qt
 * @since  Aug 30, 2023
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class MergeStatement<S extends MergeStatement<S>> extends BaseStatement<S> implements TableAccessible {
	private final TableSegment<?> table;
	private TableSegment<?> using;
	private AndSegment<S, ?> on;
	private final List<ColumnSegment<S,?>> insertColumns = new ArrayList<>();
	private final List<ColumnSegment<S,?>> updateColumns = new ArrayList<>();
	private boolean updateWhenMatched;
	private boolean insertWhenNotMatched;

	public MergeStatement(Class<?> entityClass, String alias) {
		if (Strings.isBlank(alias)) {
			throw new IllegalArgumentException("未指定别名");
		}
		this.table = TableSegment.fromEntity(entityClass, alias);
	}

	public static MergeStatement<?> of(Class<?> entityClass, String alias) {
		return new MergeStatement<>(entityClass, alias);
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
		sql.addNodes(SqlNodes.ON, SqlNodes.LEFT_PARENTHESIS, this.on.toSqlNode(), SqlNodes.RIGHT_PARENTHESIS);
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
		boolean first = true;
		for (ColumnSegment<S,?> column : this.updateColumns) {
			if (first) {
				sql.addNode(SqlNodes.LF);
				sql.addNode(SqlNodes.SET);
				first = false;
			} else {
				sql.addNode(SqlNodes.LF);
				sql.addNode(SqlNodes.COMMA);
			}
			String columnName = column.getColumnName();
			sql.addNode(new TextNode(this.table.getTableAlias() + "." + columnName + " = "));
			sql.addNode(column.toValueSqlNode());
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
		{
			boolean first = true;
			for (ColumnSegment<S,?> column : this.insertColumns) {
				if (first) {
					sql.addNode(SqlNodes.LF);
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
			for (ColumnSegment<S,?> column : this.insertColumns) {
				if (first) {
					sql.addNode(SqlNodes.LF);
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

	public S withEntity(Object entity) {
		return withEntity(entity, ColumnPredicate.DEFAULT);
	}

	public S withEntity(Object entity, boolean updateWhenMatched, boolean insertWhenNotMatched) {
		return withEntity(entity, updateWhenMatched, insertWhenNotMatched, ColumnPredicate.DEFAULT);
	}

	public S withEntity(Object entity, ColumnPredicate columnPredicate) {
		return withEntity(entity, true, true, columnPredicate);
	}

	public S withEntity(Object entity, Predicate<String> isIncludeEmptyColumns) {
		return withEntity(entity, true, true,
			ConfigurableColumnPredicate.of( isIncludeEmptyColumns));
	}

	public S withEntity(Object entity, Predicate<String> isIncludeColumns, Predicate<String> isExcludeColumns,
		Predicate<String> isIncludeEmptyColumns, boolean includeAllEmpty) {
		return withEntity(entity, true, true,
			ConfigurableColumnPredicate.of(isIncludeColumns, isExcludeColumns, isIncludeEmptyColumns, includeAllEmpty));
	}

	public S withEntity(Object entity, boolean updateWhenMatched, boolean insertWhenNotMatched, Predicate<String> isIncludeEmptyColumns) {
		return withEntity(entity, updateWhenMatched, insertWhenNotMatched,
			ConfigurableColumnPredicate.of( isIncludeEmptyColumns));
	}

	public S withEntity(Object entity, boolean updateWhenMatched, boolean insertWhenNotMatched,
		Predicate<String> isIncludeColumns, Predicate<String> isExcludeColumns,
		Predicate<String> isIncludeEmptyColumns, boolean includeAllEmpty) {
		return withEntity(entity, updateWhenMatched, insertWhenNotMatched,
			ConfigurableColumnPredicate.of(isIncludeColumns, isExcludeColumns, isIncludeEmptyColumns, includeAllEmpty));
	}

	public S withEntity(Object entity, boolean updateWhenMatched, boolean insertWhenNotMatched, ColumnPredicate columnPredicate) {
		TableMeta tableMeta = table.getTableMeta();
		if (tableMeta != null) {
			if (tableMeta.getColumns().values().stream().noneMatch(ColumnMeta::isPrimaryKey)) {
				throw new IllegalStateException("未配置主键列：" + tableMeta.getEntityClass().getName());
			}

			@SuppressWarnings("unchecked")
			Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, tableMeta.getEntityClass());

			SelectStatement<?> using = new SelectStatement<>(DualEntity.class);
			for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
				String name = entry.getKey();
				ColumnMeta meta = entry.getValue();
				if (meta.isPrimaryKey()) {
					Object val1 = entityMap.get(meta.getFieldName());
					Object val = BindingValues.getValueForInsert(meta, val1);
					using.select().column(DualEntity.Fields.dummy).value(val, name);
				}
			}
			String tableAlias = table.getTableAlias();
			String usingAlias = "S".equalsIgnoreCase(tableAlias) ? "O" : "S";
			this.using(using, usingAlias);

			for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
				String name = entry.getKey();
				ColumnMeta meta = entry.getValue();
				if (meta.isPrimaryKey()) {
					this.on().column(name).eq(TableField.of(usingAlias, name));
				}
			}
			this.updateWhenMatched(updateWhenMatched);
			if (updateWhenMatched) {
				for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
					String name = entry.getKey();
					ColumnMeta meta = entry.getValue();
					boolean updatable = meta.isUpdatable() || meta.isVersion() || meta.isUpdateTime();
					if (!updatable || meta.isPrimaryKey()) {
						continue;
					}
					// 不在包含列表
					if (!columnPredicate.isIncludedColumn(name)) {
						continue;
					}
					Object val1 = entityMap.get(meta.getFieldName());
					Object val = BindingValues.getValueForUpdate(meta, val1);
					if (meta.isVersion()) {
						val = val == null ? 1L : ((Number) val).longValue() + 1;
					}
					// 需要包含空值字段 或为非空值
					boolean include = columnPredicate.isIncludedEmptyColumn(name) || Objs.isNotEmpty(val);
					if (include) {
						this.update(name, val);
					}
				}
			}
			this.insertWhenNotMatched(insertWhenNotMatched);
			if (insertWhenNotMatched) {
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
					// 需要包含空值字段 或为非空值
					boolean include = columnPredicate.isIncludedEmptyColumn(name) || Objs.isNotEmpty(val);
					if (include) {
						this.insert(name, val);
					}
				}
			}
		}
		return getThis();
	}

	public S using(SelectStatement<?> select, String alias) {
		this.using = TableSegment.fromSelect(select, alias);
		return getThis();
	}

	public S using(Class<?> entityClass, String alias) {
		this.using = TableSegment.fromEntity(entityClass, alias);
		return getThis();
	}

	@SuppressWarnings("unchecked")
	public <T extends AndSegment<S, T>> T on() {
		return (T) (on = Objs.defaultIfNull(on, this::buildWhere));
	}

	public S updateWhenMatched() {
		return this.updateWhenMatched(true);
	}

	public S insertWhenNotMatched() {
		return this.insertWhenNotMatched(true);
	}

	public S updateWhenMatched(boolean enabled) {
		this.updateWhenMatched = enabled;
		return getThis();
	}

	public S insertWhenNotMatched(boolean enabled) {
		this.insertWhenNotMatched = enabled;
		return getThis();
	}

	private ColumnSegment<S, ?> buildColumnSegment() {
		return new ColumnSegment<>(getThis(), this.table);
	}

	public S insertWith(String field, String usingField) {
		ColumnSegment<S,?> column = buildColumnSegment();
		column.column(field).value(SqlNodes.text(this.using.getColumnExpression(usingField)));
		this.insertColumns.add(column);
		return getThis();
	}


	public S insert(String field, Object value) {
		ColumnSegment<S,?> column = buildColumnSegment();
		column.column(field).value(value);
		this.insertColumns.add(column);
		return getThis();
	}

	public S insertRawWith(String rawColumn, String usingColumn) {
		ColumnSegment<S,?> column = buildColumnSegment();
		column.rawColumn(rawColumn).value(SqlNodes.text(usingColumn));
		this.insertColumns.add(column);
		return getThis();
	}

	public S insertRaw(String rawColumn, Object value) {
		ColumnSegment<S,?> column = buildColumnSegment();
		column.rawColumn(rawColumn).value(value);
		this.insertColumns.add(column);
		return getThis();
	}

	public S updateWith(String field, String usingField) {
		ColumnSegment<S,?> column = buildColumnSegment();
		column.column(field).value(SqlNodes.text(this.using.getColumnExpression(usingField)));
		this.updateColumns.add(column);
		return getThis();
	}

	public S update(String field, Object value) {
		ColumnSegment<S,?> column = buildColumnSegment();
		column.column(field).value(value);
		this.updateColumns.add(column);
		return getThis();
	}

	public S updateRawWith(String rawColumn, String usingColumn) {
		ColumnSegment<S,?> column = buildColumnSegment();
		column.rawColumn(rawColumn).value(SqlNodes.text(usingColumn));
		this.updateColumns.add(column);
		return getThis();
	}

	public S updateRaw(String rawColumn, Object value) {
		ColumnSegment<S,?> column = buildColumnSegment();
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
		throw new IllegalArgumentException("no such table! tableAlias: " + tableAlias);
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
		if (tableIndex == 1) {
			return this.using;
		}
		throw new IllegalArgumentException("no such table! tableIndex: " + tableIndex);
	}
}
