package io.polaris.core.jdbc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Qt
 * @since  Aug 20, 2023
 */
@Getter
@ToString
@EqualsAndHashCode
public class TableMeta implements Cloneable {
	private final Class<?> entityClass;
	private final String schema;
	private final String catalog;
	private final String table;
	private final String alias;
	private final Map<String, ColumnMeta> columns;
	private final Map<String, ColumnMeta> pkColumns;

	public TableMeta(Class<?> entityClass, String schema, String catalog, String table, String alias, Map<String, ColumnMeta> columns) {
		this.entityClass = entityClass;
		this.schema = schema;
		this.catalog = catalog;
		this.table = table;
		this.alias = alias == null ? "" : alias;
		this.columns = columns;
		Map<String, ColumnMeta> pkColumns = new HashMap<>();
		columns.forEach((key, value) -> {
			if (value.isPrimaryKey()) {
				pkColumns.put(key, value);
			}
		});
		this.pkColumns = Collections.unmodifiableMap(pkColumns);
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public TableMeta clone() {
		Map<String, ColumnMeta> cloneColumns = new HashMap<>();
		// clone columns
		columns.forEach((key, value) -> {
			cloneColumns.put(key, value.clone());
		});
		TableMeta clone = TableMeta.builder()
			.entityClass(entityClass)
			.schema(schema)
			.catalog(catalog)
			.table(table)
			.alias(alias)
			.columns(Collections.unmodifiableMap(cloneColumns))
			.build();
		return clone;
	}

	public static final class Builder {
		private Class<?> entityClass;
		private String schema;
		private String catalog;
		private String table;
		private String alias;
		private Map<String, ColumnMeta> columns;

		private Builder() {
		}

		public TableMeta build() {
			return new TableMeta(entityClass, schema, catalog, table, alias, columns);
		}

		public Builder entityClass(Class<?> entityClass) {
			this.entityClass = entityClass;
			return this;
		}

		public Builder schema(String schema) {
			this.schema = schema;
			return this;
		}


		public Builder catalog(String catalog) {
			this.catalog = catalog;
			return this;
		}

		public Builder table(String table) {
			this.table = table;
			return this;
		}

		public Builder alias(String alias) {
			this.alias = alias;
			return this;
		}

		public Builder columns(Map<String, ColumnMeta> columns) {
			this.columns = columns;
			return this;
		}

	}
}
