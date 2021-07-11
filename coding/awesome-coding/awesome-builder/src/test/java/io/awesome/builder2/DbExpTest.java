package io.awesome.builder2;

import io.awesome.dbv.Dbv;
import io.awesome.dbv.cfg.Configurations;
import io.awesome.dbv.cfg.DatabaseCfg;
import io.awesome.dbv.exp.DbvExp;
import io.awesome.dbv.model.Column;
import io.awesome.dbv.model.Table;
import io.awesome.dbv.model.TableType;
import io.awesome.dbv.toolkit.DBKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Qt
 * @version Jun 09, 2020
 */
public class DbExpTest {

	@Test
	public void testNcmisCsv() throws Exception {
		String schema = "NCMIS";
		String csvFile = "C:/data/ncmis.csv";
		writeCsvFile(schema, csvFile);
	}

	@Test
	public void testYcloansCsv() throws Exception {
		String schema = "YCLOANS";
		String csvFile = "C:/data/ycloans.csv";
		writeCsvFile(schema, csvFile);
	}

	private void writeCsvFile(String schema, String csvFile) throws IOException, SQLException {
		Connection conn = Dbv.getConnection(getClass().getPackage().getName().replace(".", "/") + "/jdbc.xml");
		try (
			PrintStream ps = new PrintStream(csvFile, "gbk");
		) {
			ps.println("\"英文表名\",\"中文表名\",\"字段名称\",\"字段注释\",\"字段类型\",\"主键\",\"非空\"");
			List<Table> tables = Dbv.readTables(conn.getMetaData(), null, schema, null, "TABLE");
			for (Table table : tables) {
				String tableName = table.getTableName();
				String tableComment = StringUtils.trimToEmpty(table.getRemarks()).replace("\r", "").replace("\n", " ");
				List<Column> columnList = table.getColumnList();
				for (Column column : columnList) {
					String columnName = column.getColumnName().replace("\r", "").replace("\n", " ");
					String columnType = column.getColumnType().replace("\r", "").replace("\n", " ");
					String columnComment = StringUtils.trimToEmpty(column.getRemarks()).replace("\r", "").replace("\n", " ");
					String isPrimaryKey = column.getIsPrimaryKey();
					String isNotNullable = column.getIsNotNullable();
					ps.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n", tableName, tableComment, columnName, columnComment, columnType, isPrimaryKey, isNotNullable);
				}
			}
			ps.flush();
		}
	}


	@Test
	public void testNcmisUpdate() throws Exception {
		try (
			Connection conn = Dbv.getConnection(getClass().getPackage().getName().replace(".", "/") + "/jdbc.xml");
			InputStream in = new FileInputStream("C:/coding/xc/ncmis0.xlsm");
		) {
			XSSFWorkbook book = DbvExp.newXSSFWorkbook(in);

			for (String sheetTable : DbvExp.getTables(book)) {
				List<Table> tables = Dbv.readTables(conn.getMetaData(), null, "NCMIS", sheetTable.toUpperCase(), "TABLE");
				if(tables.size()>0){
					Table table = tables.get(0);
					System.out.println("更新表: "+ table.getTableName());
					table.setTableName(sheetTable);
					DbvExp.addTable(book, table);
				}
			}

			FileOutputStream out = new FileOutputStream(new File("C:/coding/xc/ncmis.xlsm"));
			book.write(out);
			out.flush();
			out.close();
		}
	}

	@Test
	public void testNcmis() throws Exception {
		try (
			Connection conn = Dbv.getConnection(getClass().getPackage().getName().replace(".", "/") + "/jdbc.xml");
			InputStream in = DbvExp.getTemplateResourceStream();
			FileOutputStream out = new FileOutputStream(new File("C:/data/ncmis.xlsm"));
		) {
			XSSFWorkbook book = DbvExp.newXSSFWorkbook(in);
			List<Table> tables = Dbv.readTables(conn.getMetaData(), null, "NCMIS", null, "TABLE");
			for (Table table : tables) {
				if(table.getTableName().startsWith("DR$")
					||table.getTableName().startsWith("TMP")){
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
	public void testYcloans() throws Exception {
		try (
			Connection conn = Dbv.getConnection(getClass().getPackage().getName().replace(".", "/") + "/jdbc.xml");
			InputStream in = DbvExp.getTemplateResourceStream();
			FileOutputStream out = new FileOutputStream(new File("C:/data/ycloans.xlsm"));
		) {
			XSSFWorkbook book = DbvExp.newXSSFWorkbook(in);
			List<Table> tables = Dbv.readTables(conn.getMetaData(), null, "YCLOANS", null, "TABLE");
			for (Table table : tables) {
				DbvExp.addTableIndex(book, table);
				DbvExp.addTable(book, table);
			}
			book.write(out);
			out.flush();
		}
	}

	@Test
	public void testDbv() throws FileNotFoundException, SQLException {
		DatabaseCfg cfg = Configurations.getDatabaseCfg(getClass().getPackage().getName().replace(".", "/") + "/jdbc.xml");
		Connection conn = Dbv.getConnection(cfg);
		try {
			DatabaseMetaData metaData = conn.getMetaData();
			List<TableType> types = Dbv.readTableTypes(metaData);
			System.out.println(types);
			List<Table> tables = Dbv.readTables(metaData, "", "NCMIS", "S_COM_CDE", "TABLE");
			System.out.println(tables);
			Table table = tables.get(0);
			System.out.println(table.getPkColumns());
			System.out.println(table.getRemarks());
			System.out.println(table.getIndexList());
			System.out.println(table.getColumnList());
		} finally {
			DBKit.close(conn);
		}
	}

}
