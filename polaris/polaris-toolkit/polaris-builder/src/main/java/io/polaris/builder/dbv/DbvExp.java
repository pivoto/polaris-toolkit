package io.polaris.builder.dbv;

import io.polaris.core.jdbc.dbv.model.Column;
import io.polaris.core.jdbc.dbv.model.Index;
import io.polaris.core.jdbc.dbv.model.PrimaryKey;
import io.polaris.core.jdbc.dbv.model.Table;
import io.polaris.core.string.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Qt
 * @version Jun 09, 2020
 */
@SuppressWarnings("All")
public class DbvExp {
	private static final String EXCEL_TPL_NAME = "/META-INF/dbv/tables.xlsm";
	private static final ExcelTemplateConfig DEFAULT_TEMPLATE_CONFIG = new ExcelTemplateConfig();
	public static final ExcelIndexConfig DEFAULT_INDEX_CONFIG = new ExcelIndexConfig();


	public static InputStream getTemplateExcelStream() {
		InputStream in = DbvExp.class.getResourceAsStream(EXCEL_TPL_NAME);
		return in;
	}

	public static XSSFWorkbook newXSSFWorkbook(InputStream in) throws IOException {
		XSSFWorkbook book = new XSSFWorkbook(in);
		return book;
	}

	public static List<String> getTables(XSSFWorkbook book) {
		XSSFSheet idxSheet = book.getSheet("目录");

		List<String> list = new ArrayList<>();
		int minRow = 4;
		int maxRow = idxSheet.getLastRowNum();
		for (int iRow = minRow; iRow <= maxRow; iRow++) {
			XSSFRow row = idxSheet.getRow(iRow);
			XSSFCell cell = row.getCell(3);
			String table = cell.getStringCellValue();
			if (StringUtils.isNotBlank(table)) {
				list.add(table.trim());
			}
		}

		return list;
	}

	public static void addTableIndex(XSSFWorkbook book, Table table) {
		addTableIndex(book, table, DEFAULT_INDEX_CONFIG);
	}

	public static void addTableIndex(XSSFWorkbook book, Table table, ExcelIndexConfig config) {
		XSSFSheet idxSheet = book.getSheet(config.getIndexSheetName());
		int minRow = config.getTableRowBegin();
		int maxRow = idxSheet.getLastRowNum();
		int blankRow = maxRow + 1;
		for (int iRow = minRow; iRow <= maxRow; iRow++) {
			XSSFRow row = idxSheet.getRow(iRow);
			XSSFCell cell = row.getCell(3);
			String stringCellValue = cell.getStringCellValue();
			if (StringUtils.isNotBlank(stringCellValue)) {
				if (stringCellValue.equalsIgnoreCase(table.getTableName())) {
					return;
				}
			}
		}

		copy(idxSheet, config.getTableRowTemplate(), 0, idxSheet, blankRow, 0, 'Z' - 'A');
		XSSFRow row = idxSheet.getRow(blankRow);
		row.getCell(config.getTableColName()).setCellValue(table.getTableName());
		String label = Strings.coalesce(table.getRemarks(), "");
		String remarks = "";
		int labelSplitIdx = label.indexOf('\n');
		if (labelSplitIdx > 0) {
			remarks = label.substring(labelSplitIdx + 1);
			label = label.substring(0, labelSplitIdx);
		}
		row.getCell(config.getTableColLabel()).setCellValue(label);
		row.getCell(config.getTableColLink()).setCellFormula("HYPERLINK(\"#'\"&D" + (blankRow + 1) + "&\"'!A1\",\"====>>\")");
		row.getCell(config.getTableColRemark()).setCellValue(remarks);
	}

	public static void addTable(XSSFWorkbook book, Table table) {
		addTable(book, table, DEFAULT_TEMPLATE_CONFIG);
	}

	public static void addTable(XSSFWorkbook book, Table table, ExcelTemplateConfig config) {
		XSSFSheet tplSheet = book.getSheet(config.getTemplateSheetName());

		String sheetName = Strings.coalesce(table.getTableName());

		if (book.getSheet(sheetName) != null) {
			book.removeSheetAt(book.getSheetIndex(sheetName));
		}
		XSSFSheet sheet = book.createSheet(sheetName);


		for (int i = 0; i <= config.getColumnRowBegin(); i++) {
			// 列标题前的所有单元复制
			copy(tplSheet, i, 0, sheet, i, 0, 'Z' - 'A');
		}


		// columns
		int iRow = config.getColumnRowContent();
		int colNum = 0;
		for (Column col : table.getColumnList()) {
			copy(tplSheet, config.getColumnRowContent(), 0, sheet, iRow, 0, 'Z' - 'A');
			sheet.getRow(iRow).getCell(config.getColumnColSeq()).setCellType(CellType.NUMERIC);
			sheet.getRow(iRow).getCell(config.getColumnColSeq()).setCellValue(++colNum);
			sheet.getRow(iRow).getCell(config.getColumnColName()).setCellValue(col.getColumnName());
			String columnLabel = Strings.coalesce(col.getRemarks(), col.getColumnName());
			String columnRemark = "";
			int columnLabelSplitIdx = columnLabel.indexOf('\n');
			if (columnLabelSplitIdx > 0) {
				columnRemark = columnLabel.substring(columnLabelSplitIdx + 1);
				columnLabel = columnLabel.substring(0, columnLabelSplitIdx);
			}
			sheet.getRow(iRow).getCell(config.getColumnColLabel()).setCellValue(columnLabel);

			String columnType = col.getColumnType();
			/*if (col.getColumnSize() > 0) {
				columnType += "(" + col.getColumnSize();
				if (col.getDecimalDigits() > 0) {
					columnType += "," + col.getDecimalDigits();
				}
				columnType += ")";
			}*/
			sheet.getRow(iRow).getCell(config.getColumnColType()).setCellValue(columnType);
			if (col.isPrimaryKey()) {
				sheet.getRow(iRow).getCell(config.getColumnColPrimary()).setCellValue("Y");
			}
			if (col.isNotNull()) {
				sheet.getRow(iRow).getCell(config.getColumnColNonnull()).setCellValue("Y");
			}
			if (StringUtils.isNotBlank(col.getColumnDef())) {
				sheet.getRow(iRow).getCell(config.getColumnColDefault()).setCellValue(col.getColumnDef());
			}
			if (StringUtils.isNotBlank(columnRemark)) {
				sheet.getRow(iRow).getCell(config.getColumnColRemark()).setCellValue(columnRemark);
			}
			/*if ("YES".equalsIgnoreCase(col.getIsAutoincrement())) {
				sheet.getRow(iRow).getCell(7).setCellValue("自增长列");// 备注
			}*/
			iRow++;
		}


		copy(tplSheet, config.getColumnRowContent(), 0, sheet, iRow, 0, 'Z' - 'A');// empty col line
		sheet.getRow(iRow).getCell(0).setCellValue(++colNum);
		iRow++;
		copy(tplSheet, config.getColumnRowEnd(), 0, sheet, iRow, 0, 'Z' - 'A');// end col

		iRow++;
		copy(tplSheet, Integer.max(config.getIndexRowBegin() - 1, config.getColumnRowEnd() + 1), 0, sheet, iRow, 0, 'Z' - 'A');
		// index
		copy(tplSheet, config.getIndexRowBegin(), 0, sheet, ++iRow, 0, 'Z' - 'A');
		sheet.addMergedRegion(new CellRangeAddress(iRow, iRow, config.getIndexColFields(), config.getIndexColUnique() - 1));

		int idxNum = 0;
		idxLoop:
		for (Index idx : table.getIndexList()) {
			for (PrimaryKey pk : table.getPrimaryKeyList()) {
				if (pk.getPkName().equals(idx.getIndexName())) {
					continue idxLoop;
				}
			}

			copy(tplSheet, config.getIndexRowContent(), 0, sheet, ++iRow, 0, 'Z' - 'A');
			sheet.addMergedRegion(new CellRangeAddress(iRow, iRow, config.getIndexColFields(), config.getIndexColUnique() - 1));
			sheet.getRow(iRow).getCell(config.getIndexColSeq()).setCellType(CellType.NUMERIC);
			sheet.getRow(iRow).getCell(config.getIndexColSeq()).setCellValue(++idxNum);
			sheet.getRow(iRow).getCell(config.getIndexColName()).setCellValue(idx.getIndexName());
			sheet.getRow(iRow).getCell(config.getIndexColFields()).setCellValue(idx.getColumnNames());
			sheet.getRow(iRow).getCell(config.getIndexColUnique()).setCellValue(idx.isUnique() ? "Y" : "N");
		}
		copy(tplSheet, config.getIndexRowContent(), 0, sheet, ++iRow, 0, 'Z' - 'A');// empty idx line
		sheet.getRow(iRow).getCell(config.getIndexColSeq()).setCellValue(++idxNum);
		copy(tplSheet, config.getIndexRowEnd(), 0, sheet, ++iRow, 0, 'Z' - 'A');
		for (int i = 0; i < 'Z' - 'A'; i++) {
			sheet.setColumnWidth(i, tplSheet.getColumnWidth(i));
		}
		/*for (int i = 0; i < 'I' - 'A'; i++) {
			sheet.autoSizeColumn(i, true);
		}*/

		for (CellMergeArea area : config.getMergeAreas()) {
			sheet.addMergedRegion(new CellRangeAddress(
				area.getFirstRow() < 0 ? iRow : area.getFirstRow(),
				area.getLastRow() < 0 ? iRow : area.getLastRow(),
				area.getFirstCol(), area.getLastCol()));
		}
		sheet.getRow(config.getTableNameCell().getRow()).getCell(config.getTableNameCell().getCol()).setCellValue(sheetName);
		sheet.getRow(config.getTableRemarkCell().getRow()).getCell(config.getTableRemarkCell().getCol()).setCellValue(table.getRemarks());
	}

	@SuppressWarnings({"deprecation", "DuplicatedCode"})
	private static void copy(XSSFSheet tplSheet, int tplRow, int tplCol, XSSFSheet sheet, int row, int col, int cols) {
		XSSFRow tplSheetRow = tplSheet.getRow(tplRow);
		XSSFRow sheetRow = sheet.createRow(row);
		if (tplSheetRow == null) {
			return;
		}
		//sheetRow.setRowStyle(tplSheetRow.getRowStyle());
		for (int i = 0; i < cols; i++) {
			XSSFCell tplSheetCell = tplSheetRow.getCell(tplCol + i);
			XSSFCell sheetCell = sheetRow.createCell(col + i);
			if (tplSheetCell == null) {
				continue;
			}
			XSSFComment cellComment = tplSheetCell.getCellComment();
			if (cellComment != null) {
				sheetCell.setCellComment(cellComment);
			}
			XSSFCellStyle cellStyle = tplSheetCell.getCellStyle();
			if (cellStyle != null) {
				sheetCell.setCellStyle(cellStyle);
			}
			int cellType = tplSheetCell.getCellType();
			sheetCell.setCellType(cellType);
			switch (cellType) {
				case Cell.CELL_TYPE_STRING:
					sheetCell.setCellValue(tplSheetCell.getStringCellValue());
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					sheetCell.setCellValue(tplSheetCell.getBooleanCellValue());
					break;
				case Cell.CELL_TYPE_NUMERIC:
					sheetCell.setCellValue(tplSheetCell.getNumericCellValue());
					break;
				case Cell.CELL_TYPE_ERROR:
					sheetCell.setCellValue(tplSheetCell.getErrorCellValue());
					break;
				case Cell.CELL_TYPE_FORMULA:
					String cellFormula = tplSheetCell.getCellFormula();
					if (cellFormula != null) {
						sheetCell.setCellFormula(cellFormula);
					}
					break;
				case Cell.CELL_TYPE_BLANK:
					break;
				default:
					break;
			}
			XSSFHyperlink hyperlink = tplSheetCell.getHyperlink();
			if (hyperlink != null) {
				sheetCell.setHyperlink(hyperlink);
			}
		}
	}


}
