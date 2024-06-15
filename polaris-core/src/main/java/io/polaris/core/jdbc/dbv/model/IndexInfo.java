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
public class IndexInfo {
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
	@DbvColumn("TABLE_NAME")
	private String tableName;

	/**
	 * 索引值是否可以不唯一
	 * <p>
	 * TYPE为tableIndexStatistic时为false
	 */
	@DbvColumn("NON_UNIQUE")
	private boolean nonUnique;
	/**
	 * 索引目录/类别（可为 null）
	 * <p>
	 * TYPE 为 tableIndexStatistic 时索引类别为 null
	 */
	@ToString.Exclude
	@DbvColumn("INDEX_QUALIFIER")
	private String indexQualifier;
	/**
	 * 索引名称
	 * <p>
	 * TYPE 为 tableIndexStatistic 时索引名称为 null
	 */
	@DbvColumn("INDEX_NAME")
	private String indexName;
	/**
	 * <pre>
	 * 索引类型：
	 * tableIndexStatistic - 此标识与表的索引描述一起返回的表统计信息
	 * tableIndexClustered - 此为集群索引
	 * tableIndexHashed - 此为散列索引
	 * tableIndexOther - 此为某种其他样式的索引
	 * </pre>
	 */
	@DbvColumn("TYPE")
	private short type;
	/**
	 * 索引中的列序列号
	 * <p>
	 * TYPE 为 tableIndexStatistic 时该序列号为零
	 */
	@DbvColumn("ORDINAL_POSITION")
	private short ordinalPosition;
	/**
	 * 列名称
	 * <p>
	 * TYPE 为 tableIndexStatistic 时列名称为 null
	 */
	@DbvColumn("COLUMN_NAME")
	private String columnName;
	/**
	 * 列排序序列  "A" => 升序，"D" => 降序，如果排序序列不受支持，可能为 null
	 * <p>
	 * TYPE 为 tableIndexStatistic时排序序列为 null
	 */
	@DbvColumn("ASC_OR_DESC")
	private String ascOrDesc;
	/**
	 * TYPE 为 tableIndexStatistic 时，它是表中的行数
	 * <p>
	 * 否则，它是索引中唯一值的数量。
	 */
	@ToString.Exclude
	@DbvColumn("CARDINALITY")
	private int cardinality;
	/**
	 * TYPE 为 tableIndexStatisic 时，它是用于表的页数，否则它是用于当前索引的页数。
	 */
	@ToString.Exclude
	@DbvColumn("PAGES")
	private int pages;

	/**
	 * 自定义属性, 是否唯一, YES/NO
	 */
	private String isUnique;

}
