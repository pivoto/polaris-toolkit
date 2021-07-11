package io.awesome.dbv.model;

import io.awesome.dbv.annotation.ColumnName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Qt
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class PrimaryKey {

	/**
	 * catalog 目录名, 通常为数据库名称
	 */
	@ToString.Exclude
	@ColumnName("TABLE_CAT")
	private String tableCatalog;
	/**
	 * schema 模式名, 通常为用户名
	 */
	@ToString.Exclude
	@ColumnName("TABLE_SCHEM")
	private String tableSchema;
	/**
	 * 表名
	 */
	@ToString.Exclude
	@ColumnName("TABLE_NAME")
	private String tableName;
	/**
	 * 列名
	 */
	@ColumnName("COLUMN_NAME")
	private String columnName;
	/**
	 * 主键序号
	 */
	@ColumnName("KEY_SEQ")
	private String keySeq;
	/**
	 * 主键名称
	 */
	@ColumnName("PK_NAME")
	private String pkName;

}
