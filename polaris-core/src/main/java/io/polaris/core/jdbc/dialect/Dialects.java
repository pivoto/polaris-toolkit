package io.polaris.core.jdbc.dialect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import io.polaris.core.jdbc.JdbcDriver;

/**
 * @author Qt
 * @since Mar 05, 2025
 */
public class Dialects {

	private static final Map<JdbcDriver, Dialect> dialects = new ConcurrentHashMap<>();

	static {
		dialects.put(JdbcDriver.ORACLE, new OracleDialect());
		dialects.put(JdbcDriver.SQLSERVER, new SqlserverDialect());
		dialects.put(JdbcDriver.MYSQL, new MysqlDialect());
		dialects.put(JdbcDriver.POSTGRESQL, new PostgresqlDialect());
		dialects.put(JdbcDriver.HSQLDB, new HsqldbDialect());
		dialects.put(JdbcDriver.INFORMIX, new InformixDialect());
		dialects.put(JdbcDriver.DB2, new Db2Dialect());
		dialects.put(JdbcDriver.DB2_AS400, new Db2AS400Dialect());
	}

	@Nullable
	public static Dialect getDialect(JdbcDriver driver) {
		return dialects.get(driver);
	}

	public static void registerDialect(JdbcDriver driver, Dialect dialect) {
		dialects.put(driver, dialect);
	}


}
