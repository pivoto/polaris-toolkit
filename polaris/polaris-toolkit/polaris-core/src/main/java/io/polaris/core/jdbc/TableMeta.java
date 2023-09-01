package io.polaris.core.jdbc;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Aug 20, 2023
 */
@Getter
@ToString
@EqualsAndHashCode
public class TableMeta {
	private final String schema;
	private final String catalog;
	private final String table;
	private final String alias;
	private final Map<String, ColumnMeta> columns;

	public TableMeta(String schema, String catalog, String table, String alias, Map<String, ColumnMeta> columns) {
		this.schema = schema;
		this.catalog = catalog;
		this.table = table;
		this.alias = alias;
		this.columns = columns;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private String schema;
		private String catalog;
		private String table;
		private String alias;
		private Map<String, ColumnMeta> columns;

		private Builder() {
		}

		public TableMeta build() {
			return new TableMeta(schema, catalog, table, alias, columns);
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
