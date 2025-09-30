package io.polaris.core.jdbc.executor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * @author Qt
 * @since Feb 08, 2024
 */
public class OracleJdbcTest extends BaseOracleJdbcTest {
	private static final Logger log = Loggers.of();

	@BeforeAll
	static void beforeAll(TestInfo testInfo) {
		tableCreate();
		tableInsert();
	}

	@AfterAll
	static void afterAll() {
		tableDrop();
	}

	@Test
	void test02(TestInfo testInfo) {
		for (int j = 0; j < 10; j++) {
			doWithConnection(conn -> {

				String sql = "insert into t_demo_test03(id, name) values (seq_demo_test01.nextval,'test01')";
				PreparedStatement st = conn.prepareStatement(sql, new String[]{"id"});
				int row = st.executeUpdate();
				log.info("row: {}", row);
				ResultSet rs = st.getGeneratedKeys();
				ResultSetMetaData metaData = rs.getMetaData();
				int columnCount = metaData.getColumnCount();
				while (rs.next()) {
					for (int i = 1; i <= columnCount; i++) {
						log.info("generated key {}: {}", i, rs.getString(i));
					}
				}
			});
		}
	}

	@Test
	void test01(TestInfo testInfo) {
		for (int j = 0; j < 10; j++) {
			doWithConnection(conn -> {

				String sql = "insert into t_demo_test01(name) values ('test01')";
//			PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				PreparedStatement st = conn.prepareStatement(sql, new String[]{"id"});
				int row = st.executeUpdate();
				log.info("row: {}", row);
				ResultSet rs = st.getGeneratedKeys();
				ResultSetMetaData metaData = rs.getMetaData();
				int columnCount = metaData.getColumnCount();

				log.info("columnCount: {}", columnCount);
				while (rs.next()) {
					for (int i = 1; i <= columnCount; i++) {
						log.info("generated key {}: {} | {}", i, rs.getString(i), metaData.getColumnTypeName(i));
					}
				}
			});
		}
	}

	@Test
	void test00(TestInfo testInfo) {
		doWithConnection(conn -> {

			String sql = "select * from t_demo_test01";
			PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = st.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= columnCount; i++) {
					log.info("select {}: {}", i, rs.getString(i));
				}
			}
		});
	}

}
