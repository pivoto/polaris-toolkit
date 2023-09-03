package io.polaris.core.jdbc.dbv.model;

import io.polaris.core.jdbc.dbv.annotation.ColumnName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * DatabaseMetaData.getColumns
 * ResultSet getColumns(String catalog,
 *                      String schemaPattern,
 *                      String tableNamePattern,
 *                      String columnNamePattern)
 *                      throws SQLException获取可在指定类别中使用的表列的描述。
 * 仅返回与类别、模式、表和列名称标准匹配的列描述。它们根据 TABLE_CAT、TABLE_SCHEM、TABLE_NAME 和 ORDINAL_POSITION 进行排序。
 *
 * 每个列描述都有以下列：
 *
 * TABLE_CAT String => 表类别（可为 null）
 * TABLE_SCHEM String => 表模式（可为 null）
 * TABLE_NAME String => 表名称
 * COLUMN_NAME String => 列名称
 * DATA_TYPE int => 来自 java.sql.Types 的 SQL 类型
 * TYPE_NAME String => 数据源依赖的类型名称，对于 UDT，该类型名称是完全限定的
 * COLUMN_SIZE int => 列的大小。
 * BUFFER_LENGTH 未被使用。
 * DECIMAL_DIGITS int => 小数部分的位数。对于 DECIMAL_DIGITS 不适用的数据类型，则返回 Null。
 * NUM_PREC_RADIX int => 基数（通常为 10 或 2）
 * NULLABLE int => 是否允许使用 NULL。
 * columnNoNulls - 可能不允许使用 NULL 值
 * columnNullable - 明确允许使用 NULL 值
 * columnNullableUnknown - 不知道是否可使用 null
 * REMARKS String => 描述列的注释（可为 null）
 * COLUMN_DEF String => 该列的默认值，当值在单引号内时应被解释为一个字符串（可为 null）
 * SQL_DATA_TYPE int => 未使用
 * SQL_DATETIME_SUB int => 未使用
 * CHAR_OCTET_LENGTH int => 对于 char 类型，该长度是列中的最大字节数
 * ORDINAL_POSITION int => 表中的列的索引（从 1 开始）
 * IS_NULLABLE String => ISO 规则用于确定列是否包括 null。
 *              YES --- 如果参数可以包括 NULL
 *              NO --- 如果参数不可以包括 NULL
 * 空字符串 --- 如果不知道参数是否可以包括 null
 * SCOPE_CATLOG String => 表的类别，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为 null）
 * SCOPE_SCHEMA String => 表的模式，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为 null）
 * SCOPE_TABLE String => 表名称，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为 null）
 * SOURCE_DATA_TYPE short => 不同类型或用户生成 Ref 类型、来自 java.sql.Types 的 SQL 类型的源类型（如果 DATA_TYPE 不是 DISTINCT 或用户生成的 REF，则为 null）
 * IS_AUTOINCREMENT String => 指示此列是否自动增加
 *                  YES --- 如果该列自动增加
 *                  NO --- 如果该列不自动增加
 *                  空字符串 --- 如果不能确定该列是否是自动增加参数
 * COLUMN_SIZE 列表示给定列的指定列大小。对于数值数据，这是最大精度。对于字符数据，这是字符长度。对于日期时间数据类型，这是 String 表示形式的字符长度（假定允许的最大小数秒组件的精度）。对于二进制数据，这是字节长度。对于 ROWID 数据类型，这是字节长度。对于列大小不适用的数据类型，则返回 Null。
 *
 *
 * 参数：
 * catalog - 类别名称；它必须与存储在数据库中的类别名称匹配；该参数为 "" 表示获取没有类别的那些描述；为 null 则表示该类别名称不应该用于缩小搜索范围
 * schemaPattern - 模式名称的模式；它必须与存储在数据库中的模式名称匹配；该参数为 "" 表示获取没有模式的那些描述；为 null 则表示该模式名称不应该用于缩小搜索范围
 * tableNamePattern - 表名称模式；它必须与存储在数据库中的表名称匹配
 * columnNamePattern - 列名称模式；它必须与存储在数据库中的列名称匹配
 * 返回：
 * ResultSet - 每一行都是一个列描述
 *
 * </pre>
 *
 * @author Qt
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Column {
	/**
	 * 表类别（可能为 null）
	 */
	@ToString.Exclude
	@ColumnName("TABLE_CAT")
	private String catalog;
	/**
	 * 表模式（可能为 null）
	 */
	@ToString.Exclude
	@ColumnName("TABLE_SCHEM")
	private String schema;
	/**
	 * 表名称
	 */
	@ColumnName("TABLE_NAME")
	private String tableName;
	/**
	 * 列名称
	 */
	@ColumnName("COLUMN_NAME")
	private String columnName;
	/**
	 * java.sql.Types 的 SQL 类型
	 */
	@ColumnName("DATA_TYPE")
	private int dataType;
	/**
	 * 数据源依赖的类型名称，对于 UDT，该类型名称是完全限定的
	 */
	@ColumnName("TYPE_NAME")
	private String typeName;
	/**
	 * 列的大小
	 */
	@ColumnName("COLUMN_SIZE")
	private int columnSize;
	/**
	 * 小数部分的位数。对于 DECIMAL_DIGITS 不适用的数据类型，则返回 Null
	 */
	@ColumnName("DECIMAL_DIGITS")
	private int decimalDigits;
	/**
	 * 基数（通常为 10 或 2）
	 */
	@ColumnName("NUM_PREC_RADIX")
	private int numPrecRadix;
	/**
	 * 是否允许使用 NULL
	 * <li>DatabaseMetaData.columnNoNulls - 可能不允许NULL值</li>
	 * <li>DatabaseMetaData.columnNullable - 绝对允许NULL值</li>
	 * <li>DatabaseMetaData.columnNullableUnknown - 可空性未知</li>
	 */
	@ColumnName("NULLABLE")
	private int nullable;
	/**
	 * 描述列的注释（可为 null）
	 */
	@ColumnName("REMARKS")
	private String remarks;
	/**
	 * 该列的默认值，当值在单引号内时应被解释为一个字符串（可为 null）
	 */
	@ColumnName("COLUMN_DEF")
	private String columnDef;
	/**
	 * ISO规则用于确定列是否包括 null
	 * <li>YES ---可以包含NULL </li>
	 * <li>NO---不能包含NULL </li>
	 * <li>空字符串---列的可空性是未知的 </li>
	 */
	@ColumnName("IS_NULLABLE")
	private String isNullable;
	/**
	 * 是否自增:YES/NO
	 * <li>YES ---是 </li>
	 * <li>NO --- 否 </li>
	 * <li>空字符串 --- 不确定</li>
	 */
	@ColumnName("IS_AUTOINCREMENT")
	private String isAutoincrement;
	/**
	 * 是否是生成的列 :YES/NO
	 * <li>YES ---是 </li>
	 * <li>NO --- 否 </li>
	 * <li>空字符串 --- 不确定</li>
	 */
	@ColumnName("IS_GENERATEDCOLUMN")
	private String isGeneratedcolumn;

	/**
	 * 自定义属性, 是否主键列:YES/NO
	 */
	@ToString.Exclude
	private String isPrimaryKey;
	/**
	 * 自定义属性, 是否可为空:YES/NO
	 */
	@ToString.Exclude
	private String isNotNullable;
	/**
	 * 自定义属性, 是否主键列
	 */
	private boolean primaryKey = false;
	/**
	 * 自定义属性, 是否可为空
	 */
	private boolean notNull = false;

	/**
	 * 自定义属性, 是否生成的虚拟列
	 */
	private boolean generated = false;
	/**
	 * 自定义属性, 是否自增列
	 */
	private boolean autoincrement = false;
	/**
	 * 自定义属性, 列类型, 包括列值大小与数值精度, 如 varchar(32), number(24,6)
	 */
	private String columnType;

}
