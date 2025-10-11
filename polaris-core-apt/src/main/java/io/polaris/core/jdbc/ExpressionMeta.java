package io.polaris.core.jdbc;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

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
public final class ExpressionMeta implements Cloneable, Copyable<ExpressionMeta> {
	private final String catalog;
	private final String schema;
	private final String tableName;

	private final String fieldName;
	private final Class<?> fieldType;

	/** 数据库字段表达式，注意表别名的占位符使用，以防止在多表关联等场景下字段混淆，如： coalesce($T.col_name, '1') */
	private final String expression;
	/** java.sql.Types 的 SQL 类型 */
	private final String jdbcType;
	/** java.sql.Types 的 SQL 类型 */
	private final int jdbcTypeValue;
	/** 是否可查询 */
	private final boolean selectable;
	/** 表别名占位符，带`.`分隔符 */
	private final String tableAliasPlaceholder;

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

	/** 扩展属性 */
	private final Map<String, String> props;
	/** 用于构造BoundSql 如`javaType=int,jdbcType=NUMERIC,typeHandler=MyTypeHandler` */
	private final String propString;


	private ExpressionMeta(String catalog, String schema, String tableName, String fieldName, Class<?> fieldType, String expression, String jdbcType, int jdbcTypeValue, boolean selectable, String tableAliasPlaceholder, int sortDirection, int sortPosition, Map<String, String> props) {
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
		this.sortDirection = sortDirection;
		this.sortPosition = sortPosition;
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

	public Map<String, String> getProps() {
		return props;
	}

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
	public ExpressionMeta clone() {
		try {
			return (ExpressionMeta) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}

	@Override
	public ExpressionMeta copy() {
		return clone();
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

		public ExpressionMeta build() {
			return new ExpressionMeta(this.catalog, this.schema, this.tableName, this.fieldName, this.fieldType, this.expression, this.jdbcType, this.jdbcTypeValue, this.selectable, this.tableAliasPlaceholder, sortDirection, sortPosition, this.props);
		}

	}
}
