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
	private final boolean mutable;

	private TableMetaMutation(Class<?> entityClass, String newTableName, Map<String, ValueRef<String>> newColumnNames) {
		this.entityClass = entityClass;
		this.newTableName = newTableName;
		this.newColumnNames = newColumnNames;
		this.mutable = Strings.isNotBlank(newTableName)
			|| (newColumnNames != null && !newColumnNames.isEmpty());
	}

	public static TableMetaMutation origin(Class<?> entityClass) {
		return new TableMetaMutation(entityClass, null, null);
	}

	public static Builder builder(Class<?> entityClass) {
		return new Builder(entityClass);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TableMetaMutation)) return false;
		TableMetaMutation that = (TableMetaMutation) o;
		return mutable == that.mutable && Objects.equals(entityClass, that.entityClass) && Objects.equals(newTableName, that.newTableName) && Objects.equals(newColumnNames, that.newColumnNames);
	}

	@Override
	public int hashCode() {
		return Objects.hash(entityClass, newTableName, newColumnNames, mutable);
	}

	public TableMeta apply(TableMeta origin) {
		if (!mutable) {
			return origin;
		}
		String tableName = Strings.isBlank(newTableName) ? origin.getTable() : newTableName;
		Map<String, ColumnMeta> columns = origin.getColumns();
		if (newColumnNames != null && !newColumnNames.isEmpty()) {
			Map<String, ColumnMeta> newColumns = new HashMap<>(columns);
			columns.forEach((key, value) -> {
				ValueRef<String> ref = newColumnNames.get(key);
				if (ref != null && Strings.isBlank(ref.get())) {
					// 删除字段
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
					.build();
				newColumns.put(key, columnMeta);
			});
			columns = Collections.unmodifiableMap(newColumns);
		}
		return TableMeta.builder().entityClass(origin.getEntityClass())
			.table(tableName).alias(origin.getAlias())
			.columns(columns)
			.schema(origin.getSchema()).catalog(origin.getCatalog()).build();
	}


	public boolean mutable() {
		return mutable;
	}

	public Class<?> entityClass() {
		return entityClass;
	}

	public String newTableName() {
		return newTableName;
	}

	public Map<String, ValueRef<String>> newColumnNames() {
		return newColumnNames;
	}

	@NotThreadSafe
	public static final class Builder {

		private final Class<?> entityClass;
		private String newTableName;
		private Map<String, ValueRef<String>> newColumnNames;

		public Builder(Class<?> entityClass) {
			this.entityClass = entityClass;
		}

		public Builder renameTable(String newTableName) {
			this.newTableName = newTableName;
			return this;
		}

		public Builder renameColumn(String columnName, String newColumnName) {
			if (newColumnNames == null) {
				newColumnNames = new HashMap<>();
			}
			newColumnNames.put(columnName, ValueRef.of(newColumnName));
			return this;
		}

		public Builder deleteColumn(String columnName) {
			return renameColumn(columnName, null);
		}

		public TableMetaMutation build() {
			return new TableMetaMutation(entityClass, newTableName,
				newColumnNames == null ? Collections.emptyMap() : Collections.unmodifiableMap(newColumnNames));
		}


	}

}
