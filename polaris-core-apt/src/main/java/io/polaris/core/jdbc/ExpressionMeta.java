package io.polaris.core.jdbc;

import lombok.Builder;
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


	@Builder
	public ExpressionMeta(String catalog, String schema, String tableName, String fieldName, Class<?> fieldType, String expression, String jdbcType, int jdbcTypeValue, boolean selectable, String tableAliasPlaceholder) {
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

}
