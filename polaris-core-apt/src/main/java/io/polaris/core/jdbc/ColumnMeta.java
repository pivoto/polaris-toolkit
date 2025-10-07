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
public final class ColumnMeta implements Cloneable {
	private final String catalog;
	private final String schema;
	private final String tableName;

	private final String fieldName;
	private final Class<?> fieldType;

	private final String columnName;
	private final String jdbcType;
	private final int jdbcTypeValue;

	private final boolean primaryKey;
	private final boolean autoIncrement;
	private final String seqName;
	private final String idSql;

	private final boolean nullable;
	private final boolean insertable;
	private final boolean updatable;
	private final String updateDefault;
	private final String insertDefault;
	private final String updateDefaultSql;
	private final String insertDefaultSql;
	private final boolean version;
	private final boolean logicDeleted;
	private final boolean createTime;
	private final boolean updateTime;
	private final Map<String, String> properties;
	/** 用于构造BoundSql 如`javaType=int,jdbcType=NUMERIC,typeHandler=MyTypeHandler` */
	private final String propertiesString;

	private ColumnMeta(String catalog, String schema, String tableName, String fieldName, Class<?> fieldType, String columnName, String jdbcType, int jdbcTypeValue, boolean primaryKey, boolean autoIncrement, String seqName, String idSql, boolean nullable, boolean insertable, boolean updatable, String updateDefault, String insertDefault, String updateDefaultSql, String insertDefaultSql, boolean version, boolean logicDeleted, boolean createTime, boolean updateTime, Map<String, String> properties) {
		this.catalog = catalog;
		this.schema = schema;
		this.tableName = tableName;
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.columnName = columnName;
		this.jdbcType = jdbcType;
		this.jdbcTypeValue = jdbcTypeValue;
		this.primaryKey = primaryKey;
		this.autoIncrement = autoIncrement;
		this.seqName = seqName;
		this.idSql = idSql;
		this.nullable = nullable;
		this.insertable = insertable;
		this.updatable = updatable;
		this.updateDefault = updateDefault;
		this.insertDefault = insertDefault;
		this.updateDefaultSql = updateDefaultSql;
		this.insertDefaultSql = insertDefaultSql;
		this.version = version;
		this.logicDeleted = logicDeleted;
		this.createTime = createTime;
		this.updateTime = updateTime;
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
	public ColumnMeta clone() {
		try {
			return (ColumnMeta) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
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
		private String columnName;
		private String jdbcType;
		private int jdbcTypeValue;
		private boolean primaryKey;
		private boolean autoIncrement;
		private String seqName;
		private String idSql;
		private boolean nullable;
		private boolean insertable;
		private boolean updatable;
		private String updateDefault;
		private String insertDefault;
		private String updateDefaultSql;
		private String insertDefaultSql;
		private boolean version;
		private boolean logicDeleted;
		private boolean createTime;
		private boolean updateTime;
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

		public Builder columnName(final String columnName) {
			this.columnName = columnName;
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

		public Builder primaryKey(final boolean primaryKey) {
			this.primaryKey = primaryKey;
			return this;
		}

		public Builder autoIncrement(final boolean autoIncrement) {
			this.autoIncrement = autoIncrement;
			return this;
		}

		public Builder seqName(final String seqName) {
			this.seqName = seqName;
			return this;
		}

		public Builder idSql(final String idSql) {
			this.idSql = idSql;
			return this;
		}

		public Builder nullable(final boolean nullable) {
			this.nullable = nullable;
			return this;
		}

		public Builder insertable(final boolean insertable) {
			this.insertable = insertable;
			return this;
		}

		public Builder updatable(final boolean updatable) {
			this.updatable = updatable;
			return this;
		}

		public Builder updateDefault(final String updateDefault) {
			this.updateDefault = updateDefault;
			return this;
		}

		public Builder insertDefault(final String insertDefault) {
			this.insertDefault = insertDefault;
			return this;
		}

		public Builder updateDefaultSql(final String updateDefaultSql) {
			this.updateDefaultSql = updateDefaultSql;
			return this;
		}

		public Builder insertDefaultSql(final String insertDefaultSql) {
			this.insertDefaultSql = insertDefaultSql;
			return this;
		}

		public Builder version(final boolean version) {
			this.version = version;
			return this;
		}

		public Builder logicDeleted(final boolean logicDeleted) {
			this.logicDeleted = logicDeleted;
			return this;
		}

		public Builder createTime(final boolean createTime) {
			this.createTime = createTime;
			return this;
		}

		public Builder updateTime(final boolean updateTime) {
			this.updateTime = updateTime;
			return this;
		}

		public Builder updateTime(final Map<String, String> properties) {
			this.properties = properties;
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

		public ColumnMeta build() {
			return new ColumnMeta(this.catalog, this.schema, this.tableName, this.fieldName, this.fieldType, this.columnName, this.jdbcType, this.jdbcTypeValue, this.primaryKey, this.autoIncrement, this.seqName, this.idSql, this.nullable, this.insertable, this.updatable, this.updateDefault, this.insertDefault, this.updateDefaultSql, this.insertDefaultSql, this.version, this.logicDeleted, this.createTime, this.updateTime, this.properties);
		}

	}
}
