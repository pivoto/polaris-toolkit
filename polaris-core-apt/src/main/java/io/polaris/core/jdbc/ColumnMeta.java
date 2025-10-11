package io.polaris.core.jdbc;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.annotation.AnnotationProcessing;
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
public final class ColumnMeta implements Cloneable, Copyable<ColumnMeta> {
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
	private final Map<String, String> props;
	/** 用于构造BoundSql 如`javaType=int,jdbcType=NUMERIC,typeHandler=MyTypeHandler` */
	private final String propString;

	private ColumnMeta(String catalog, String schema, String tableName, String fieldName, Class<?> fieldType, String columnName, String jdbcType, int jdbcTypeValue, boolean primaryKey, boolean autoIncrement, String seqName, String idSql, boolean nullable, boolean insertable, boolean updatable, String updateDefault, String insertDefault, String updateDefaultSql, String insertDefaultSql, boolean version, boolean logicDeleted, boolean createTime, boolean updateTime, Map<String, String> props) {
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
		this.props = props == null ? Collections.emptyMap() : Collections.unmodifiableMap(new LinkedHashMap<>(props));
		if (this.props.isEmpty()) {
			this.propString = "";
		} else {
			StringBuilder sb = new StringBuilder();
			this.props.forEach((k, v) -> sb.append(k).append("=").append(v).append(","));
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			this.propString = sb.toString();
		}
	}

	public boolean hasProperties(Predicate<String> filter) {
		return props.keySet().stream().anyMatch(filter);
	}

	public String getProp(String key) {
		return props.get(key);
	}

	@Nullable
	public Map<String, String> getPropsIfNotEmpty() {
		return props.isEmpty() ? null : props;
	}

	@Nullable
	public Map<String, String> getPropsIfNotEmpty(Predicate<String> filter) {
		Map<String, String> props = getProps(filter);
		return props.isEmpty() ? null : props;
	}

	@Nonnull
	public Map<String, String> getProps() {
		return props;
	}

	@Nonnull
	public Map<String, String> getProps(Predicate<String> filter) {
		if (filter == null) {
			return props;
		}
		Map<String, String> rs = new LinkedHashMap<>();
		props.forEach((k, v) -> {
			if (filter.test(k)) {
				rs.put(k, v);
			}
		});
		return rs;
	}

	public String getPropString() {
		return propString;
	}


	public String getPropString(Predicate<String> filter) {
		if (filter == null) {
			return propString;
		}
		StringBuilder sb = new StringBuilder();
		props.forEach((k, v) -> {
			if (filter.test(k)) {
				sb.append(k).append("=").append(v).append(",");
			}
		});
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public <V> VarRef<V> wrap(V value) {
		if (value instanceof VarRef) {
			Map<String, String> props = ((VarRef<?>) value).getProps();
			if (!props.isEmpty()) {
				return (VarRef<V>) value;
			}
		}
		return VarRef.of(value, props);
	}

	@Override
	public ColumnMeta clone() {
		try {
			return (ColumnMeta) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}

	@Override
	public ColumnMeta copy() {
		return clone();
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
		private Map<String, String> props = new LinkedHashMap<>();

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
			this.props = properties;
			return this;
		}

		public Builder props(final Map<String, String> properties) {
			this.props = properties;
			return this;
		}


		public Builder prop(final String key, final String value) {
			this.props = props == null ? new LinkedHashMap<>() : props;
			this.props.put(key, value);
			return this;
		}

		public ColumnMeta build() {
			return new ColumnMeta(this.catalog, this.schema, this.tableName, this.fieldName, this.fieldType, this.columnName, this.jdbcType, this.jdbcTypeValue, this.primaryKey, this.autoIncrement, this.seqName, this.idSql, this.nullable, this.insertable, this.updatable, this.updateDefault, this.insertDefault, this.updateDefaultSql, this.insertDefaultSql, this.version, this.logicDeleted, this.createTime, this.updateTime, this.props);
		}

	}
}
