package io.awesome.dbv.exp;

import io.awesome.dbv.model.Column;
import io.awesome.dbv.model.Index;
import io.awesome.dbv.model.PrimaryKey;
import io.awesome.dbv.model.Table;
import io.awesome.dbv.toolkit.StringKit;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class ExcelTplToolkit {

	private static final String EXCEL_TPL_NAME = "/tables.xlsm";

	public static InputStream getTemplateResourceStream() {

		InputStream in = ExcelTplToolkit.class.getResourceAsStream(EXCEL_TPL_NAME);
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
			XSSFCell cell = row.getCell(4);
			String table = cell.getStringCellValue();
			if (org.apache.commons.lang3.StringUtils.isNotBlank(table)) {
				list.add(table.trim());
			}
		}

		return list;
	}

	public static void addTable(XSSFWorkbook book, Table table) {
		XSSFSheet tplSheet = book.getSheet("模板");

		String sheetName = StringKit.coalesce(table.getTableName());

		if (book.getSheet(sheetName) != null) {
			book.removeSheetAt(book.getSheetIndex(sheetName));
		}
		XSSFSheet sheet = book.createSheet(sheetName);

		for (int i = 0; i < 11; i++) {
			copy(tplSheet, i, 0, sheet, i, 0, 'Q' - 'A');
		}
		sheet.addMergedRegion(new CellRangeAddress(2, 4, 0, 'J' - 'A'));
		sheet.getRow(0).getCell(1).setCellValue(sheetName);
		sheet.getRow(2).getCell(0).setCellValue(table.getRemarks());
		sheet.addMergedRegion(new CellRangeAddress(6, 7, 1, 1));
		sheet.addMergedRegion(new CellRangeAddress(6, 7, 2, 'J' - 'A'));


		// columns
		int iRow = 11;
		int colNum = 0;
		for (Column col : table.getColumnList()) {
			copy(tplSheet, 11, 0, sheet, iRow, 0, 'Q' - 'A');
			sheet.getRow(iRow).getCell(0).setCellValue(++colNum);
			sheet.getRow(iRow).getCell(1).setCellValue(col.getColumnName());
			sheet.getRow(iRow).getCell(2).setCellValue(StringKit.coalesce(col.getRemarks(), col.getColumnName()));

			String columnType = col.getColumnType();
			/*if(col.getColumnSize()>0){
				columnType += "("+col.getColumnSize();
				if (col.getDecimalDigits() > 0) {
					columnType += ","+col.getDecimalDigits();
				}
				columnType += ")";
			}*/
			sheet.getRow(iRow).getCell(5).setCellValue(columnType);

//			sheet.getRow(iRow).getCell(3).setCellValue(col.getTypeName());
//			sheet.getRow(iRow).getCell(4).setCellValue(col.getColumnSize());
//			if (col.getDecimalDigits() > 0) {
//				sheet.getRow(iRow).getCell(5).setCellValue(col.getDecimalDigits());
//			}
			if (col.isPrimaryKey()) {
				sheet.getRow(iRow).getCell(6).setCellValue("Y");
			}
			if (col.isNotNull()) {
				sheet.getRow(iRow).getCell(7).setCellValue("Y");
			}
			if (StringKit.isNotEmpty(col.getColumnDef())) {
				sheet.getRow(iRow).getCell(8).setCellValue(col.getColumnDef());
			}
			if ("YES".equalsIgnoreCase(col.getIsAutoincrement())) {
				sheet.getRow(iRow).getCell(9).setCellValue("自增长列");// 备注
			}
			iRow++;
		}

		copy(tplSheet, 20, 0, sheet, iRow, 0, 'Q' - 'A');// empty col line
		sheet.getRow(iRow).getCell(0).setCellValue(++colNum);
		iRow++;
		copy(tplSheet, 21, 0, sheet, iRow, 0, 'Q' - 'A');// end col

		iRow++;
		copy(tplSheet, 22, 0, sheet, iRow, 0, 'Q' - 'A');
		iRow++;
		copy(tplSheet, 23, 0, sheet, iRow, 0, 'Q' - 'A');
		// index
		copy(tplSheet, 24, 0, sheet, ++iRow, 0, 'Q' - 'A');

		int idxNum = 0;
		idxLoop:
		for (Index idx : table.getIndexList()) {
			for (PrimaryKey pk : table.getPrimaryKeyList()) {
				if (pk.getPkName().equals(idx.getIndexName())) {
					continue idxLoop;
				}
			}

			copy(tplSheet, 25, 0, sheet, ++iRow, 0, 'Q' - 'A');
			sheet.getRow(iRow).getCell(0).setCellValue(++idxNum);
			sheet.getRow(iRow).getCell(1).setCellValue(idx.getIndexName());
			String[] columnNames = idx.getColumnNames().split(",", 6);
			int iCol = 2;
			for (String columnName : columnNames) {
				sheet.getRow(iRow).getCell(iCol++).setCellValue(columnName);
			}
			sheet.getRow(iRow).getCell(8).setCellValue(idx.isUnique() ? "Y" : "N");
			sheet.getRow(iRow).getCell(9).setCellValue("D".equals(idx.getAscOrDesc()) ? "DESC" : "ASC");
		}
		copy(tplSheet, 30, 0, sheet, ++iRow, 0, 'Q' - 'A');
		copy(tplSheet, 31, 0, sheet, ++iRow, 0, 'Q' - 'A');
		for (int i = 0; i < 'K' - 'A'; i++) {
			sheet.setColumnWidth(i, tplSheet.getColumnWidth(i));
		}
		/*for (int i = 0; i < 'K' - 'A'; i++) {
			sheet.autoSizeColumn(i, true);
		}*/


		sheet.addMergedRegion(new CellRangeAddress(2, 2, 'L' - 'A', 'P' - 'A'));
		sheet.addMergedRegion(new CellRangeAddress(3, iRow, 'L' - 'A', 'P' - 'A'));

	}

	@SuppressWarnings("deprecation")
	private static void copy(XSSFSheet tplSheet, int tplRow, int tplCol, XSSFSheet sheet, int row, int col, int cols) {
		XSSFRow tplSheetRow = tplSheet.getRow(tplRow);
		XSSFRow sheetRow = sheet.createRow(row);
		if (tplSheetRow == null)
			return;
//		sheetRow.setRowStyle(tplSheetRow.getRowStyle());
		for (int i = 0; i < cols; i++) {
			XSSFCell tplSheetCell = tplSheetRow.getCell(tplCol + i);
			XSSFCell sheetCell = sheetRow.createCell(col + i);
			if (tplSheetCell == null) {
				continue;
			}
			XSSFComment cellComment = tplSheetCell.getCellComment();
			if (cellComment != null)
				sheetCell.setCellComment(cellComment);
			XSSFCellStyle cellStyle = tplSheetCell.getCellStyle();
			if (cellStyle != null)
				sheetCell.setCellStyle(cellStyle);
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
					if (cellFormula != null)
						sheetCell.setCellFormula(cellFormula);
					break;
				case Cell.CELL_TYPE_BLANK:
					break;
				default:
					break;
			}
			XSSFHyperlink hyperlink = tplSheetCell.getHyperlink();
			if (hyperlink != null)
				sheetCell.setHyperlink(hyperlink);
		}
	}
}
