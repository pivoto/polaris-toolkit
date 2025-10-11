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

	/** 数据库字段名称。 默认采用驼峰命名转换为下划线命名 */
	private final String columnName;
	/** java.sql.Types 的 SQL 类型 */
	private final String jdbcType;
	/** java.sql.Types 的 SQL 类型 */
	private final int jdbcTypeValue;

	/** 是否主键 */
	private final boolean primaryKey;
	/** 是否自增主键 */
	private final boolean autoIncrement;
	/**
	 * 产生主键值的数据库序列名，数据新增时使用。
	 * 默认赋值表达式为`seq_name.nextval`
	 */
	private final String seqName;
	/**
	 * 产生主键值的数据库SQL表达式，数据新增时使用。
	 * 区别于常规自增或序列值的使用，完全使用自定义语句，
	 * 如`seq_name.nextval`、`uuid()`、`sys_guid()`等
	 */
	private final String idSql;

	/** 是否可为空 */
	private final boolean nullable;
	/** 是否可新增列 */
	private final boolean insertable;
	/** 是否可修改列 */
	private final boolean updatable;
	/** 字段 update set 默认值 */
	private final String updateDefault;
	/** insert的时候默认值 */
	private final String insertDefault;
	/** 字段 update set 默认SQL */
	private final String updateDefaultSql;
	/** insert的时候默认值SQL */
	private final String insertDefaultSql;
	/** 版本锁字段标识 */
	private final boolean version;
	/** 标识逻辑删除字段 */
	private final boolean logicDeleted;
	/** 标识创建时间字段 */
	private final boolean createTime;
	/** 标识修改时间字段 */
	private final boolean updateTime;

	/**
	 * 默认排序方向
	 * <uL>
	 * <li>0：不排序</li>
	 * <li>1：正序</li>
	 * <li>-1：逆序</li>
	 * </uL>
	 */
	private final int sortDirection;
	/** 默认排序位置 */
	private final int sortPosition;

	private final Map<String, String> props;
	/** 用于构造BoundSql 如`javaType=int,jdbcType=NUMERIC,typeHandler=MyTypeHandler` */
	private final String propString;

	private ColumnMeta(String catalog, String schema, String tableName, String fieldName, Class<?> fieldType, String columnName, String jdbcType, int jdbcTypeValue, boolean primaryKey, boolean autoIncrement, String seqName, String idSql, boolean nullable, boolean insertable, boolean updatable, String updateDefault, String insertDefault, String updateDefaultSql, String insertDefaultSql, boolean version, boolean logicDeleted, boolean createTime, boolean updateTime, int sortPosition, int sortDirection, Map<String, String> props) {
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
		this.sortPosition = sortPosition;
		this.sortDirection = sortDirection;
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
		private int sortDirection;
		private int sortPosition;
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

		public Builder sortPosition(final int sortPosition) {
			this.sortPosition = sortPosition;
			return this;
		}

		public Builder sortDirection(final int sortDirection) {
			this.sortDirection = sortDirection;
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
			return new ColumnMeta(this.catalog, this.schema, this.tableName, this.fieldName, this.fieldType, this.columnName, this.jdbcType, this.jdbcTypeValue, this.primaryKey, this.autoIncrement, this.seqName, this.idSql, this.nullable, this.insertable, this.updatable, this.updateDefault, this.insertDefault, this.updateDefaultSql, this.insertDefaultSql, this.version, this.logicDeleted, this.createTime, this.updateTime, sortPosition, sortDirection, this.props);
		}

	}
}
