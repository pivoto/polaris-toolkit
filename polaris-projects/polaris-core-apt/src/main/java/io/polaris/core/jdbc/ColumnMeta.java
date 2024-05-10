package io.polaris.core.jdbc;

import lombok.Builder;
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
public class ColumnMeta implements Cloneable{
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
	private final boolean nullable;
	private final boolean insertable;
	private final boolean updatable;
	private final String updateDefault;
	private final String insertDefault;
	private final boolean version;
	private final boolean logicDeleted;
	private final boolean createTime;
	private final boolean updateTime;

	@Builder
	public ColumnMeta(String catalog, String schema, String tableName, String fieldName, Class<?> fieldType, String columnName, String jdbcType, int jdbcTypeValue, boolean primaryKey, boolean autoIncrement, String seqName, boolean nullable, boolean insertable, boolean updatable, String updateDefault, String insertDefault, boolean version, boolean logicDeleted, boolean createTime, boolean updateTime) {
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
		this.nullable = nullable;
		this.insertable = insertable;
		this.updatable = updatable;
		this.updateDefault = updateDefault;
		this.insertDefault = insertDefault;
		this.version = version;
		this.logicDeleted = logicDeleted;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	@Override
	public ColumnMeta clone() {
		try {
			ColumnMeta clone = (ColumnMeta) super.clone();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}
