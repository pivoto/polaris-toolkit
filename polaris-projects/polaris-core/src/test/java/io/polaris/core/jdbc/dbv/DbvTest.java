package io.polaris.core.jdbc.dbv;

import com.alibaba.fastjson2.JSON;
import io.polaris.core.jdbc.Jdbcs;
import io.polaris.core.jdbc.dbv.model.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

class DbvTest {

	private static Connection conn;

	@BeforeAll
	static void beforeAll() throws SQLException {
		conn = Jdbcs.getConnection("jdbc:oracle:thin:@localhost:1521/cmisdb", "basesv", "basesv");
	}

	@AfterAll
	static void afterAll() {
		Jdbcs.close(conn);
	}

	protected void show(Collection<?> collection) {
		for (Object o : collection) {
			System.out.printf("%s%n", JSON.toJSONString(o));
		}
	}

	@Test
	void test00() throws SQLException {
		DatabaseMetaData metaData = conn.getMetaData();
		show(ResultSetFetcher.fetchList(metaData.getTableTypes()));
		ResultSet rs = metaData.getTables(null, null, "ARM_USER", new String[]{"TABLE"});
		List<Map<String, Object>> list = ResultSetFetcher.fetchList(rs);
		show(list);
	}

	@Test
	void test01() throws SQLException {
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
		List<Table> tables = Dbv.readTables(conn.getMetaData(), null, null, "ARM_USER", null);
		show(tables);
		for (Table table : tables) {
			List<PrimaryKey> primaryKeys = Dbv.readPrimaryKeys(conn.getMetaData(), table);
			show(primaryKeys);
		}
	}


	@Test
	public void test04() throws SQLException {
		List<Column> columns = Dbv.readColumns(conn.getMetaData(), null, null, "ARM_USER", null);
		show(columns);
	}

	@Test
	public void test05() throws SQLException {
		List<IndexInfo> indexInfos = Dbv.readIndexes(conn.getMetaData(), null, null, "ARM_USER");
		show(indexInfos);
	}

}
