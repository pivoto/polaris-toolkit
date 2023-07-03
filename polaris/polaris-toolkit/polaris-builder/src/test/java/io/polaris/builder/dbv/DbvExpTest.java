package io.polaris.builder.dbv;

import io.polaris.core.jdbc.Jdbcs;
import io.polaris.core.jdbc.dbv.Dbv;
import io.polaris.core.jdbc.dbv.model.Column;
import io.polaris.core.jdbc.dbv.model.Table;
import io.polaris.core.string.Strings;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

class DbvExpTest {

	private static Connection conn;

	@BeforeAll
	static void beforeAll() throws SQLException {
		conn = Jdbcs.getConnection("jdbc:oracle:thin:@localhost:1521/cmisdb", "basesv", "basesv");
	}

	@AfterAll
	static void afterAll() {
		Jdbcs.close(conn);
	}

	@Test
	public void testExpExcel() throws IOException, SQLException {
		try (
			InputStream in = DbvExp.getTemplateExcelStream();
			FileOutputStream out = new FileOutputStream(new File("/tmp/BASESV.xlsm"));
		) {
			XSSFWorkbook book = DbvExp.newXSSFWorkbook(in);
			List<Table> tables = Dbv.readTables(conn.getMetaData(), null, "BASESV", null, "TABLE");
			for (Table table : tables) {
				if(table.getTableName().startsWith("DR$")
					||table.getTableName().startsWith("TMP")){
					continue;
				}
				if (!table.getTableName().startsWith("ARM")){
					continue;
				}
				System.out.println("处理表: "+ table.getTableName());
				DbvExp.addTableIndex(book, table);
				DbvExp.addTable(book, table);
			}
			book.write(out);
			out.flush();
		}
	}

	@Test
	public void testExpExcelUpdate() throws IOException, SQLException {
		try (
			InputStream in = new FileInputStream("/tmp/BASESV.xlsm");
			FileOutputStream out = new FileOutputStream(new File("/tmp/BASESV1.xlsm"));
		) {
			XSSFWorkbook book = DbvExp.newXSSFWorkbook(in);
			for (String sheetTable : DbvExp.getTables(book)) {
				List<Table> tables = Dbv.readTables(conn.getMetaData(), null, "BASESV", sheetTable.toUpperCase(), "TABLE");
				if(tables.size()>0){
					Table table = tables.get(0);
					System.out.println("更新表: "+ table.getTableName());
					table.setTableName(sheetTable);
					DbvExp.addTable(book, table);
				}
			}
			book.write(out);
			out.flush();
		}
	}

	@Test
	public void testCsv() throws Exception {
		String schema = "BASESV";
		String csvFile = "/tmp/basesv.csv";
		try (
			PrintStream ps = new PrintStream(csvFile, "gbk");
		) {
			ps.println("\"英文表名\",\"中文表名\",\"字段名称\",\"字段注释\",\"字段类型\",\"主键\",\"非空\"");
			List<Table> tables = Dbv.readTables(conn.getMetaData(),null,  schema, null, "TABLE");
			for (Table table : tables) {
				String tableName = table.getTableName();
				String tableComment = Strings.trimToEmpty(table.getRemarks()).replace("\r", "").replace("\n", " ");
				List<Column> columnList = table.getColumnList();
				for (Column column : columnList) {
					String columnName = column.getColumnName().replace("\r", "").replace("\n", " ");
					String columnType = column.getColumnType().replace("\r", "").replace("\n", " ");
					String columnComment = Strings.trimToEmpty(column.getRemarks()).replace("\r", "").replace("\n", " ");
					String isPrimaryKey = column.getIsPrimaryKey();
					String isNotNullable = column.getIsNotNullable();
					ps.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n", tableName, tableComment, columnName, columnComment, columnType, isPrimaryKey, isNotNullable);
				}
			}
			ps.flush();
		}
	}

}

