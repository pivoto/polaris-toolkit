package io.polaris.core.jdbc;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import io.polaris.core.annotation.AnnotationProcessing;
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
public final class ExpressionMeta implements Cloneable {
	private final String catalog;
	private final String schema;
	private final String tableName;

	private final String fieldName;
	private final Class<?> fieldType;

	private final String expression;
	private final String jdbcType;
	private final int jdbcTypeValue;
	/** 是否可查询 */
	private final boolean selectable;
	/** 表别名占位符，带`.`分隔符 */
	private final String tableAliasPlaceholder;
	private final Map<String, String> properties;
	/** 用于构造BoundSql 如`javaType=int,jdbcType=NUMERIC,typeHandler=MyTypeHandler` */
	private final String propertiesString;


	private ExpressionMeta(String catalog, String schema, String tableName, String fieldName, Class<?> fieldType, String expression, String jdbcType, int jdbcTypeValue, boolean selectable, String tableAliasPlaceholder, Map<String, String> properties) {
		this.catalog = catalog;
		this.schema = schema;
		this.tableName = tableName;
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.expression = expression;
		this.jdbcType = jdbcType;
		this.jdbcTypeValue = jdbcTypeValue;
		this.selectable = selectable;
		this.tableAliasPlaceholder = tableAliasPlaceholder;
		this.properties = properties == null ? Collections.emptyMap() : Collections.unmodifiableMap(new LinkedHashMap<>(properties));
		StringBuilder sb = new StringBuilder();
		this.properties.forEach((k, v) -> {
			sb.append(k).append("=").append(v).append(",");
		});
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		this.propertiesString = sb.toString();
	}

	@Override
	public ExpressionMeta clone() {
		try {
			return (ExpressionMeta) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}

	public String getExpressionWithoutTableAlias() {
		if (tableAliasPlaceholder == null || tableAliasPlaceholder.isEmpty()) {
			return expression;
		}
		return expression.replace(tableAliasPlaceholder, "");
	}

	public String getExpressionWithTableAlias(String alias) {
		if (tableAliasPlaceholder == null || tableAliasPlaceholder.isEmpty()) {
			return expression;
		}
		if (alias == null || alias.isEmpty()) {
			alias = tableName;
		}
		return expression.replace(tableAliasPlaceholder, alias + ".");
	}

	public String getExpressionWithTableName() {
		if (tableAliasPlaceholder == null || tableAliasPlaceholder.isEmpty()) {
			return expression;
		}
		return expression.replace(tableAliasPlaceholder, tableName + ".");
	}

	@AnnotationProcessing
	public static Builder builder() {
		return new Builder();
	}


	@AnnotationProcessing
	public static class Builder {
		private String catalog;
		private String schema;
		private String tableName;
		private String fieldName;
		private Class<?> fieldType;
		private String expression;
		private String jdbcType;
		private int jdbcTypeValue;
		private boolean selectable;
		private String tableAliasPlaceholder;
		private Map<String, String> properties = new LinkedHashMap<>();

		Builder() {
		}

		public Builder catalog(final String catalog) {
			this.catalog = catalog;
			return this;
		}

		public Builder schema(final String schema) {
			this.schema = schema;
			return this;
		}

		public Builder tableName(final String tableName) {
			this.tableName = tableName;
			return this;
		}

		public Builder fieldName(final String fieldName) {
			this.fieldName = fieldName;
			return this;
		}

		public Builder fieldType(final Class<?> fieldType) {
			this.fieldType = fieldType;
			return this;
		}

		public Builder expression(final String expression) {
			this.expression = expression;
			return this;
		}

		public Builder jdbcType(final String jdbcType) {
			this.jdbcType = jdbcType;
			return this;
		}

		public Builder jdbcTypeValue(final int jdbcTypeValue) {
			this.jdbcTypeValue = jdbcTypeValue;
			return this;
		}

		public Builder selectable(final boolean selectable) {
			this.selectable = selectable;
			return this;
		}

		public Builder tableAliasPlaceholder(final String tableAliasPlaceholder) {
			this.tableAliasPlaceholder = tableAliasPlaceholder;
			return this;
		}

		public Builder properties(final Map<String, String> properties) {
			this.properties = properties;
			return this;
		}

		public Builder properties(final String key, final String value) {
			this.properties = properties == null ? new LinkedHashMap<>() : properties;
			this.properties.put(key, value);
			return this;
		}

		public ExpressionMeta build() {
			return new ExpressionMeta(this.catalog, this.schema, this.tableName, this.fieldName, this.fieldType, this.expression, this.jdbcType, this.jdbcTypeValue, this.selectable, this.tableAliasPlaceholder, this.properties);
		}

	}
}
