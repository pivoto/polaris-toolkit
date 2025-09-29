package io.polaris.core.jdbc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.concurrent.NotThreadSafe;

import io.polaris.core.string.Strings;
import io.polaris.core.tuple.ValueRef;

/**
 * @author Qt
 * @since Aug 13, 2024
 */
public class TableMetaMutation {
	private final Class<?> entityClass;
	private final String newTableName;
	private final Map<String, ValueRef<String>> newColumnNames;
	private final Map<String, ColumnMeta> newColumnMetas;
	private final Map<String, ValueRef<String>> newExpressionNames;
	private final Map<String, ExpressionMeta> newExpressionMetas;
	private final boolean mutable;

	private TableMetaMutation(Class<?> entityClass, String newTableName
		, Map<String, ValueRef<String>> newColumnNames
		, Map<String, ColumnMeta> newColumnMetas
		, Map<String, ValueRef<String>> newExpressionNames
		, Map<String, ExpressionMeta> newExpressionMetas) {
		if (entityClass == null) {
			throw new IllegalArgumentException("entityClass cannot be null");
		}
		this.entityClass = entityClass;
		this.newTableName = newTableName;
		this.newColumnNames = newColumnNames;
		this.newColumnMetas = newColumnMetas;
		this.newExpressionNames = newExpressionNames;
		this.newExpressionMetas = newExpressionMetas;
		this.mutable = Strings.isNotBlank(newTableName)
			|| (newColumnNames != null && !newColumnNames.isEmpty())
			|| (newColumnMetas != null && !newColumnMetas.isEmpty())
			|| (newExpressionNames != null && !newExpressionNames.isEmpty())
			|| (newExpressionMetas != null && !newExpressionMetas.isEmpty())
		;
	}

	public static TableMetaMutation origin(Class<?> entityClass) {
		return new TableMetaMutation(entityClass, null, null, null, null, null);
	}

	public static Builder builder(Class<?> entityClass) {
		return new Builder(entityClass);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TableMetaMutation)) return false;
		TableMetaMutation that = (TableMetaMutation) o;
		return mutable == that.mutable
			&& Objects.equals(entityClass, that.entityClass)
			&& Objects.equals(newTableName, that.newTableName)
			&& Objects.equals(newColumnNames, that.newColumnNames)
			&& Objects.equals(newColumnMetas, that.newColumnMetas)
			&& Objects.equals(newExpressionNames, that.newExpressionNames)
			&& Objects.equals(newExpressionMetas, that.newExpressionMetas)
			;
	}

	@Override
	public int hashCode() {
		return Objects.hash(entityClass, newTableName, newColumnNames, newColumnMetas, newExpressionNames, newExpressionMetas, mutable);
	}

	public TableMeta apply(TableMeta origin) {
		if (!mutable) {
			return origin;
		}
		String tableName = Strings.isBlank(newTableName) ? origin.getTable() : newTableName;
		Map<String, ColumnMeta> columns = origin.getColumns();
		Map<String, ExpressionMeta> expressions = origin.getExpressions();

		if ((newColumnMetas != null && !newColumnMetas.isEmpty())
			|| (newColumnNames != null && !newColumnNames.isEmpty())
		) {
			Map<String, ColumnMeta> newColumns = new HashMap<>(columns);

			if (newColumnNames != null && !newColumnNames.isEmpty()) {
				columns.forEach((key, value) -> {
					ValueRef<String> ref = newColumnNames.get(key);
					if (ref != null && Strings.isBlank(ref.get())) {
						// 删除字段
						newColumns.remove(key);
						return;
					}
					String columnName = ref == null ? value.getColumnName() : ref.get();
					ColumnMeta columnMeta = ColumnMeta.builder()
						.tableName(tableName)
						.schema(value.getSchema())
						.catalog(value.getCatalog())
						.fieldName(value.getFieldName())
						.fieldType(value.getFieldType())
						.columnName(columnName)
						.jdbcType(value.getJdbcType())
						.jdbcTypeValue(value.getJdbcTypeValue())
						.updateDefault(value.getUpdateDefault())
						.insertDefault(value.getInsertDefault())
						.nullable(value.isNullable())
						.insertable(value.isInsertable())
						.updatable(value.isUpdatable())
						.version(value.isVersion())
						.logicDeleted(value.isLogicDeleted())
						.createTime(value.isCreateTime())
						.updateTime(value.isUpdateTime())
						.primaryKey(value.isPrimaryKey())
						.autoIncrement(value.isAutoIncrement())
						.seqName(value.getSeqName())
						.idSql(value.getIdSql())
						.build();
					newColumns.put(key, columnMeta);
				});
			}

			if (newColumnMetas != null && !newColumnMetas.isEmpty()) {
				newColumns.putAll(newColumnMetas);
			}

			columns = Collections.unmodifiableMap(newColumns);
		}
		if ((newExpressionMetas != null && !newExpressionMetas.isEmpty())
			|| (newExpressionNames != null && !newExpressionNames.isEmpty())
		) {
			Map<String, ExpressionMeta> newExpressions = new HashMap<>(expressions);

			if (newExpressionNames != null && !newExpressionNames.isEmpty()) {
				expressions.forEach((key, value) -> {
					ValueRef<String> ref = newExpressionNames.get(key);
					if (ref != null && Strings.isBlank(ref.get())) {
						// 删除字段
						newExpressions.remove(key);
						return;
					}
					String expression = ref == null ? value.getExpression() : ref.get();
					ExpressionMeta columnMeta = ExpressionMeta.builder()
						.tableName(tableName)
						.schema(value.getSchema())
						.catalog(value.getCatalog())
						.fieldName(value.getFieldName())
						.fieldType(value.getFieldType())
						.expression(expression)
						.jdbcType(value.getJdbcType())
						.jdbcTypeValue(value.getJdbcTypeValue())
						.tableAliasPlaceholder(value.getTableAliasPlaceholder())
						.selectable(value.isSelectable())
						.build();
					newExpressions.put(key, columnMeta);
				});
			}

			if (newExpressionMetas != null && !newExpressionMetas.isEmpty()) {
				newExpressions.putAll(newExpressionMetas);
			}
			expressions = Collections.unmodifiableMap(newExpressions);
		}
		return TableMeta.builder().entityClass(origin.getEntityClass())
			.table(tableName).alias(origin.getAlias())
			.columns(columns)
			.expressions(expressions)
			.schema(origin.getSchema()).catalog(origin.getCatalog()).build();
	}


	public boolean mutable() {
		return mutable;
	}

	public Class<?> entityClass() {
		return entityClass;
	}


	@NotThreadSafe
	public static final class Builder {

		private final Class<?> entityClass;
		private String newTableName;
		private Map<String, ValueRef<String>> newColumnNames;
		private Map<String, ColumnMeta> newColumnMetas;
		private Map<String, ValueRef<String>> newExpressionNames;
		private Map<String, ExpressionMeta> newExpressionMetas;

		public Builder(Class<?> entityClass) {
			this.entityClass = entityClass;
		}

		public Builder renameTable(String newTableName) {
			this.newTableName = newTableName;
			return this;
		}

		public Builder renameColumn(String fieldName, String columnName) {
			if (newColumnNames == null) {
				newColumnNames = new HashMap<>();
			}
			newColumnNames.put(fieldName, ValueRef.of(columnName));
			return this;
		}

		public Builder deleteColumn(String fieldName) {
			return renameColumn(fieldName, null);
		}

		public Builder addColumn(ColumnMeta columnMeta) {
			if (newColumnMetas == null) {
				newColumnMetas = new HashMap<>();
			}
			newColumnMetas.put(columnMeta.getFieldName(), columnMeta);
			return this;
		}

		public Builder renameExpression(String fieldName, String expression) {
			if (newExpressionNames == null) {
				newExpressionNames = new HashMap<>();
			}
			newExpressionNames.put(fieldName, ValueRef.of(expression));
			return this;
		}

		public Builder deleteExpression(String fieldName) {
			return renameExpression(fieldName, null);
		}

		public Builder addExpression(ExpressionMeta expressionMeta) {
			if (newExpressionMetas == null) {
				newExpressionMetas = new HashMap<>();
			}
			newExpressionMetas.put(expressionMeta.getFieldName(), expressionMeta);
			return this;
		}

		public TableMetaMutation build() {
			return new TableMetaMutation(entityClass, newTableName
				, newColumnNames == null ? Collections.emptyMap() : Collections.unmodifiableMap(newColumnNames)
				, newColumnMetas == null ? Collections.emptyMap() : Collections.unmodifiableMap(newColumnMetas)
				, newExpressionNames == null ? Collections.emptyMap() : Collections.unmodifiableMap(newExpressionNames)
				, newExpressionMetas == null ? Collections.emptyMap() : Collections.unmodifiableMap(newExpressionMetas)
			);
		}


	}

}
