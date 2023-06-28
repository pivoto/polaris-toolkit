package io.polaris.dbv.exp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Qt
 * @since 1.8
 */
@Data
public class ExcelTemplateConfig {
	private String templateSheetName = "模板";
	/** 字段起始行/标题行 */
	private int columnRowBegin = 10;
	/** 字段内容行 */
	private int columnRowContent = 11;
	/** 字段结束行 */
	private int columnRowEnd = 21;

	/** 字段的序号列 */
	private int columnColSeq = 0;
	/** 字段的名称列 */
	private int columnColName = 1;
	/** 字段的中文描述列 */
	private int columnColLabel = 2;
	/** 字段类型列 */
	private int columnColType = 3;
	/** 字段主键标识列 */
	private int columnColPrimary = 4;
	/** 字段非空标识列 */
	private int columnColNonnull = 5;
	/** 字段默认值列 */
	private int columnColDefault = 6;
	/** 字段备注说明列 */
	private int columnColRemark = 7;

	/** 索引起始行/标题行 */
	private int indexRowBegin = 24;
	/** 索引内容行 */
	private int indexRowContent = 25;
	/** 索引结束行 */
	private int indexRowEnd = 31;
	/** 索引序号列 */
	private int indexColSeq = 0;
	/** 索引名称列 */
	private int indexColName = 1;
	/** 索引字段列表列 */
	private int indexColFields = 2;
	/** 索引唯一约束标识列 */
	private int indexColUnique = 6;
	/** 索引备注列 */
	private int indexColRemark = 7;
	/** 表名单元格 */
	private CellArea tableNameCell = new CellArea(0, 1);
	/** 表备注单元格 */
	private CellArea tableRemarkCell = new CellArea(2, 0);
	/** 固定合并区域 */
	private List<CellMergeArea> mergeAreas = new ArrayList<>();

	public ExcelTemplateConfig() {
		mergeAreas.add(new CellMergeArea(2, 4, 0, 'H' - 'A'));
		mergeAreas.add(new CellMergeArea(6, 7, 1, 1));
		mergeAreas.add(new CellMergeArea(6, 7, 2, 'H' - 'A'));
		mergeAreas.add(new CellMergeArea(2, 2, 'J' - 'A', 'N' - 'A'));
		mergeAreas.add(new CellMergeArea(3, -1, 'J' - 'A', 'N' - 'A'));

	}


}
