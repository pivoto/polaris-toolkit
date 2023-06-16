package io.polaris.dbv.exp;

import lombok.Data;

/**
 * @author Qt
 * @since 1.8
 */
@Data
public class ExcelIndexConfig {
	private String indexSheetName = "目录";
	private int tableRowBegin = 4;
	private int tableRowTemplate = 4;
	private int tableColName = 3;
	private int tableColLabel = 4;
	private int tableColLink = 5;
	private int tableColRemark = 6;


}
