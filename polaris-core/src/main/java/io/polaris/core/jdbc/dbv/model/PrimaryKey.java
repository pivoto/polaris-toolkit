package io.polaris.core.jdbc.dbv.model;

import io.polaris.core.jdbc.dbv.annotation.DbvColumn;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
	@DbvColumn("TABLE_CAT")
	private String tableCatalog;
	/**
	 * schema 模式名, 通常为用户名
	 */
	@ToString.Exclude
	@DbvColumn("TABLE_SCHEM")
	private String tableSchema;
	/**
	 * 表名
	 */
	@ToString.Exclude
	@DbvColumn("TABLE_NAME")
	private String tableName;
	/**
	 * 列名
	 */
	@DbvColumn("COLUMN_NAME")
	private String columnName;
	/**
	 * 主键序号
	 */
	@DbvColumn("KEY_SEQ")
	private String keySeq;
	/**
	 * 主键名称
	 */
	@DbvColumn("PK_NAME")
	private String pkName;

}
