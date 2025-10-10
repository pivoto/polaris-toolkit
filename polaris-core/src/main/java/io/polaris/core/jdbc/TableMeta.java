package io.polaris.core.jdbc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.polaris.core.jdbc.sql.VarRef;
import io.polaris.core.lang.Copyable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Qt
 * @since Aug 20, 2023
 */
@Getter
@ToString
@EqualsAndHashCode
public final class TableMeta implements Cloneable, Copyable<TableMeta> {
	private final Class<?> entityClass;
	private final String schema;
	private final String catalog;
	private final String table;
	private final String alias;
	private final Map<String, ColumnMeta> columns;
	private final Map<String, ColumnMeta> pkColumns;
	private final Map<String, ExpressionMeta> expressions;

	public TableMeta(Class<?> entityClass, String schema, String catalog, String table, String alias, Map<String, ColumnMeta> columns, Map<String, ExpressionMeta> expressions) {
		this.entityClass = entityClass;
		this.schema = schema;
		this.catalog = catalog;
		this.table = table;
		this.alias = alias == null ? "" : alias;
		{
			String canonicalName = columns.getClass().getName();
			if (canonicalName.equals("java.util.Collections$UnmodifiableMap")) {
				this.columns = columns;
			} else {
				this.columns = Collections.unmodifiableMap(columns);
			}
		}
		{
			String canonicalName = expressions.getClass().getName();
			if (canonicalName.equals("java.util.Collections$UnmodifiableMap")) {
				this.expressions = expressions;
			} else {
				this.expressions = Collections.unmodifiableMap(expressions);
			}
		}
		Map<String, ColumnMeta> pkColumns = new HashMap<>();
		columns.forEach((key, value) -> {
			if (value.isPrimaryKey()) {
				pkColumns.put(key, value);
			}
		});
		this.pkColumns = Collections.unmodifiableMap(pkColumns);
	}

	public ColumnMeta getColumn(String fieldName) {
		return columns.get(fieldName);
	}

	public ExpressionMeta getExpression(String fieldName) {
		return expressions.get(fieldName);
	}

	public <V> VarRef<V> wrapColumn(String fieldName, V value) {
		ColumnMeta meta = columns.get(fieldName);
		if (meta != null) {
			return meta.wrap(value);
		}
		return VarRef.of(value);
	}

	public <V> VarRef<V> wrapExpression(String fieldName, V value) {
		ExpressionMeta meta = expressions.get(fieldName);
		if (meta != null) {
			return meta.wrap(value);
		}
		return VarRef.of(value);
	}

	@SuppressWarnings("MethodDoesntCallSuperMethod")
	@Override
	public TableMeta clone() {
		return copy();
	}

	@Override
	public TableMeta copy() {
		Map<String, ColumnMeta> cloneColumns = new HashMap<>();
		Map<String, ExpressionMeta> cloneExpressions = new HashMap<>();
		// clone columns
		columns.forEach((key, value) -> {
			cloneColumns.put(key, value.clone());
		});
		expressions.forEach((key, value) -> {
			cloneExpressions.put(key, value.clone());
		});
		return TableMeta.builder()
			.entityClass(entityClass)
			.schema(schema)
			.catalog(catalog)
			.table(table)
			.alias(alias)
			.columns(Collections.unmodifiableMap(cloneColumns))
			.expressions(Collections.unmodifiableMap(cloneExpressions))
			.build();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private Class<?> entityClass;
		private String schema;
		private String catalog;
		private String table;
		private String alias;
		private Map<String, ColumnMeta> columns;
		private Map<String, ExpressionMeta> expressions;

		private Builder() {
		}

		public TableMeta build() {
			return new TableMeta(entityClass, schema, catalog, table, alias, columns, expressions);
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

		public Builder expressions(Map<String, ExpressionMeta> expressions) {
			this.expressions = expressions;
			return this;
		}

	}
}
