package io.polaris.core.jdbc.sql.statement.segment;

import io.polaris.core.reflect.GetterFunction;
import io.polaris.core.reflect.Reflects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Qt
 * @since 1.8,  Aug 23, 2023
 */
@EqualsAndHashCode
@ToString
@Getter
public class TableField {
	private final String fieldName;
	private final Integer tableIndex;
	private final String tableAlias;

	private TableField(Integer tableIndex, String fieldName) {
		this.fieldName = fieldName;
		this.tableIndex = tableIndex;
		this.tableAlias = null;
	}

	private TableField(String tableAlias, String fieldName) {
		this.fieldName = fieldName;
		this.tableIndex = null;
		this.tableAlias = tableAlias;
	}

	public static TableField of(Integer tableIndex, String fieldName) {
		return new TableField(tableIndex, fieldName);
	}

	public static TableField of(String tableAlias, String fieldName) {
		return new TableField(tableAlias, fieldName);
	}

	public static <T, R> TableField of(Integer tableIndex, GetterFunction<T, R> getter) {
		return new TableField(tableIndex, Reflects.getPropertyName(getter));
	}

	public static <T, R> TableField of(String tableAlias, GetterFunction<T, R> getter) {
		return new TableField(tableAlias, Reflects.getPropertyName(getter));
	}
}
