package io.polaris.dbv;

import io.polaris.dbv.exp.DbvExp;
import io.polaris.dbv.model.*;
import io.polaris.dbv.toolkit.StringKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Slf4j
public class DbvTest extends DbvBaseTest {

	@Test
	public void test00() throws SQLException {
		DatabaseMetaData metaData = conn.getMetaData();
		show(ResultSetFetcher.fetchList(metaData.getTableTypes()));
		ResultSet rs = metaData.getTables(null, null, "BS_USER", new String[]{"TABLE"});
		List<Map<String, Object>> list = ResultSetFetcher.fetchList(rs);
		show(list);
	}

	@Test
	public void test01() throws SQLException {
		List<Catalog> catalogs = Dbv.readCatalog(conn.getMetaData());
		show(catalogs);
	}

	@Test
	public void test02() throws SQLException {
		List<Schema> schemas = Dbv.readSchemas(conn.getMetaData());
		show(schemas);
	}

	@Test
	public void test03() throws SQLException {
		List<Table> tables = Dbv.readTables(conn.getMetaData(), null, null, "BS_USER", null);
		show(tables);
		for (Table table : tables) {
			List<PrimaryKey> primaryKeys = Dbv.readPrimaryKeys(conn.getMetaData(), table);
			show(primaryKeys);
		}
	}

	@Test
	public void test04() throws SQLException {
		List<Column> columns = Dbv.readColumns(conn.getMetaData(), null, null, "BS_USER", null);
		show(columns);
	}

	@Test
	public void test05() throws SQLException {
		List<IndexInfo> indexInfos = Dbv.readIndexes(conn.getMetaData(), null, null, "BS_USER");
		show(indexInfos);
	}

	@Test
	public void testExpExcel() throws IOException, SQLException {
		try (
				InputStream in = DbvExp.getTemplateResourceStream();
				FileOutputStream out = new FileOutputStream(new File("/tmp/BASESV.xlsm"));
		) {
			XSSFWorkbook book = DbvExp.newXSSFWorkbook(in);
			List<Table> tables = Dbv.readTables(conn.getMetaData(), null, "BASESV", null, "TABLE");
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
		String schema = "booster";
		String csvFile = "/tmp/booster.csv";
		writeCsvFile(schema, csvFile);
	}

	private void writeCsvFile(String schema, String csvFile) throws IOException, SQLException {
		try (
				PrintStream ps = new PrintStream(csvFile, "gbk");
		) {
			ps.println("\"英文表名\",\"中文表名\",\"字段名称\",\"字段注释\",\"字段类型\",\"主键\",\"非空\"");
			List<Table> tables = Dbv.readTables(conn.getMetaData(), schema, null, null, "TABLE");
			for (Table table : tables) {
				String tableName = table.getTableName();
				String tableComment = StringKit.trimToEmpty(table.getRemarks()).replace("\r", "").replace("\n", " ");
				List<Column> columnList = table.getColumnList();
				for (Column column : columnList) {
					String columnName = column.getColumnName().replace("\r", "").replace("\n", " ");
					String columnType = column.getColumnType().replace("\r", "").replace("\n", " ");
					String columnComment = StringKit.trimToEmpty(column.getRemarks()).replace("\r", "").replace("\n", " ");
					String isPrimaryKey = column.getIsPrimaryKey();
					String isNotNullable = column.getIsNotNullable();
					ps.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n", tableName, tableComment, columnName, columnComment, columnType, isPrimaryKey, isNotNullable);
				}
			}
			ps.flush();
		}
	}

}
