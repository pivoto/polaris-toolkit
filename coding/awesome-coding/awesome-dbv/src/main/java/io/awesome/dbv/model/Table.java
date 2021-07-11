package io.awesome.dbv.model;

import io.awesome.dbv.annotation.ColumnName;
import io.awesome.dbv.toolkit.MapKit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.DatabaseMetaData;
import java.sql.Types;
import java.util.*;

/**
 * @author Qt
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Table {
	/**
	 * catalog 目录名, 通常为数据库名称
	 */
	@ColumnName("TABLE_CAT")
	private String tableCatalog;
	/**
	 * schema 模式名, 通常为用户名
	 */
	@ColumnName("TABLE_SCHEM")
	private String tableSchema;
	/**
	 * 表名
	 */
	@ColumnName("TABLE_NAME")
	private String tableName;
	/**
	 * 表类型
	 * <p>
	 * 典型的类型是“TABLE”，“VIEW”，“SYSTEM TABLE”，“GLOBAL TEMPORARY”，“LOCAL TEMPORARY”，“ALIAS”，“SYNONYM”
	 */
	@ColumnName("TABLE_TYPE")
	private String tableType;
	/**
	 * 表的备注/注释
	 */
	@ColumnName("REMARKS")
	private String remarks;
	/**
	 * 主键列表
	 */
	@ToString.Exclude
	private List<PrimaryKey> primaryKeyList = new ArrayList<PrimaryKey>();
	/**
	 * 索引信息列表
	 */
	@ToString.Exclude
	private List<IndexInfo> indexInfoList = new ArrayList<IndexInfo>();
	/**
	 * 列名列表
	 */
	@ToString.Exclude
	private List<Column> columnList = new ArrayList<Column>();
	/**
	 * 主键列名集合
	 */
	@ToString.Exclude
	private Set<String> pkColumns = new LinkedHashSet<String>();
	/**
	 * 列名与列信息对象映射
	 */
	@ToString.Exclude
	private Map<String, Column> columnMap = MapKit.<String, Column>newCaseInsensitiveLinkedHashMap();
	/**
	 * 索引列表
	 */
	@ToString.Exclude
	private List<Index> indexList = new ArrayList<>();

	/**
	 * 处理自定义的附加字段值
	 */
	@SuppressWarnings("ALL")
	public void fit() {
		// region 索引信息处理
		Set<IndexInfo> sortedIndexInfos = new TreeSet<IndexInfo>(new Comparator<IndexInfo>() {
			@Override
			public int compare(IndexInfo o1, IndexInfo o2) {
				if (o1 == o2) {
					return 0;
				}
				if (o1 == null) {
					return 1;
				}
				if (o2 == null) {
					return -1;
				}
				int i = o1.getIndexName().compareTo(o2.getIndexName());
				if (i == 0) {
					if (o1.getOrdinalPosition() < o2.getOrdinalPosition()) {
						return -1;
					} else {
						return 1;
					}
				} else {
					return i;
				}
			}
		});
		for (IndexInfo idx : indexInfoList) {
			sortedIndexInfos.add(idx);
			if (!idx.isNonUnique()) {
				idx.setIsUnique("YES");
			} else {
				idx.setIsUnique("NO");
			}
		}
		// endregion
		// region 自定义索引信息处理
		for (IndexInfo idxInfo : sortedIndexInfos) {
			if (indexList.size() > 0
				&& indexList.get(indexList.size() - 1).getIndexName().equals(idxInfo.getIndexName())) {
				Index idx = indexList.get(indexList.size() - 1);
				idx.setColumnNames(idx.getColumnNames() + "," + idxInfo.getColumnName());
			} else {
				Index idx = new Index();
				idx.setTableCatalog(idxInfo.getTableCatalog());
				idx.setTableSchema(idxInfo.getTableSchema());
				idx.setTableName(idxInfo.getTableName());
				idx.setIndexName(idxInfo.getIndexName());
				idx.setColumnNames(idxInfo.getColumnName());
				idx.setAscOrDesc(idxInfo.getAscOrDesc());
				idx.setNonUnique(idxInfo.isNonUnique());
				idx.setUnique(!idxInfo.isNonUnique());
				idx.setIsUnique(idxInfo.getIsUnique());
				indexList.add(idx);
			}
		}
		// endregion

		// 主键列名集合
		for (PrimaryKey pk : primaryKeyList) {
			pkColumns.add(pk.getColumnName());
		}

		// 列名与列信息对象映射
		for (Column col : columnList) {
			columnMap.put(col.getColumnName(), col);

			// 主键约束字段赋值
			if (pkColumns.contains(col.getColumnName())) {
				col.setIsPrimaryKey("YES");
				col.setPrimaryKey(true);
			}else{
				col.setIsPrimaryKey("NO");
				col.setPrimaryKey(false);
			}

			// 非空约束字段赋值
			if (col.getNullable() == DatabaseMetaData.columnNoNulls) {
				col.setIsNotNullable("YES");
				col.setNotNull(true);
			}else{
				col.setIsNotNullable("NO");
				col.setNotNull(false);
			}

			// 自增列
			if ("YES".equalsIgnoreCase(col.getIsAutoincrement())){
				col.setAutoincrement(true);
			}else {
				col.setAutoincrement(false);
			}

			// 虚拟列
			if ("YES".equalsIgnoreCase(col.getIsGeneratedcolumn())){
				col.setGenerated(true);
			}else {
				col.setGenerated(false);
			}

			// 列类型处理
			int dataType = col.getDataType();
			switch (dataType) {
				case Types.CHAR:
				case Types.VARCHAR:
				case Types.NCHAR:
				case Types.NVARCHAR:
					col.setColumnType(col.getTypeName() + "(" + col.getColumnSize() + ")");
					break;
				case Types.NUMERIC:
				case Types.DECIMAL:
				case Types.DOUBLE:
				case Types.FLOAT:
					int decimalDigits = col.getDecimalDigits();
					col.setColumnType(col.getTypeName() + "(" + col.getColumnSize()
						+ (decimalDigits > 0 ? "," + decimalDigits : "") + ")");
					break;
				default:
					col.setColumnType(col.getTypeName());
			}
		}
	}

}
