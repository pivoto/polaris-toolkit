package io.polaris.core.jdbc;

import lombok.Getter;

/**
 * @author Qt
 * @since 1.8
 */
public enum JdbcDriver {

	UNKNOWN(null, null),
	DERBY("Apache Derby", "org.apache.derby.jdbc.EmbeddedDriver", "org.apache.derby.jdbc.EmbeddedXADataSource",
		"SELECT 1 FROM SYSIBM.SYSDUMMY1"),
	H2("H2", "org.h2.Driver", "org.h2.jdbcx.JdbcDataSource", "SELECT 1"),
	HSQLDB("HSQL Database Engine", "org.hsqldb.jdbc.JDBCDriver", "org.hsqldb.jdbc.pool.JDBCXADataSource",
		"SELECT COUNT(*) FROM INFORMATION_SCHEMA.SYSTEM_USERS"),
	SQLITE("SQLite", "org.sqlite.JDBC"),
	MYSQL("MySQL", "com.mysql.cj.jdbc.Driver", "com.mysql.cj.jdbc.MysqlXADataSource", "SELECT 1"),
	MARIADB("MySQL", "org.mariadb.jdbc.Driver", "org.mariadb.jdbc.MariaDbDataSource", "SELECT 1"),
	GAE(null, "com.google.appengine.api.rdbms.AppEngineDriver"),
	ORACLE("Oracle", "oracle.jdbc.OracleDriver", "oracle.jdbc.xa.client.OracleXADataSource",
		"SELECT 'Hello' from DUAL"),
	POSTGRESQL("PostgreSQL", "org.postgresql.Driver", "org.postgresql.xa.PGXADataSource", "SELECT 1"),
	REDSHIFT("Redshift", "com.amazon.redshift.jdbc.Driver", null, "SELECT 1"),
	HANA("HDB", "com.sap.db.jdbc.Driver", "com.sap.db.jdbcext.XADataSourceSAP", "SELECT 1 FROM SYS.DUMMY"
		, new String[]{"sap"}),
	JTDS(null, "net.sourceforge.jtds.jdbc.Driver"),
	SQLSERVER("Microsoft SQL Server", "com.microsoft.sqlserver.jdbc.SQLServerDriver",
		"com.microsoft.sqlserver.jdbc.SQLServerXADataSource", "SELECT 1"),
	FIREBIRD("Firebird", "org.firebirdsql.jdbc.FBDriver", "org.firebirdsql.ds.FBXADataSource",
		"SELECT 1 FROM RDB$DATABASE", new String[]{"firebirdsql", "firebird"}),
	DB2("DB2", "com.ibm.db2.jcc.DB2Driver", "com.ibm.db2.jcc.DB2XADataSource", "SELECT 1 FROM SYSIBM.SYSDUMMY1"),
	DB2_AS400("DB2 UDB for AS/400", "com.ibm.as400.access.AS400JDBCDriver",
		"com.ibm.as400.access.AS400JDBCXADataSource", "SELECT 1 FROM SYSIBM.SYSDUMMY1"
		, new String[]{"as400"}),
	TERADATA("Teradata", "com.teradata.jdbc.TeraDriver"),
	INFORMIX("Informix Dynamic Server", "com.informix.jdbc.IfxDriver", null, "select count(*) from systables"
		, new String[]{"informix-sqli", "informix-direct"}),
	TESTCONTAINERS(null, "org.testcontainers.jdbc.ContainerDatabaseDriver", null, null, new String[]{"tc"}),
	;

	@Getter
	private final String productName;
	@Getter
	private final String driverClassName;
	@Getter
	private final String xaDataSourceClassName;
	@Getter
	private final String validationQuery;
	private final String[] urlPrefixes;

	JdbcDriver(String productName, String driverClassName) {
		this(productName, driverClassName, null);
	}

	JdbcDriver(String productName, String driverClassName, String xaDataSourceClassName) {
		this(productName, driverClassName, xaDataSourceClassName, null);
	}

	JdbcDriver(String productName, String driverClassName, String xaDataSourceClassName, String validationQuery) {
		this(productName, driverClassName, xaDataSourceClassName, validationQuery, null);
	}

	JdbcDriver(String productName, String driverClassName, String xaDataSourceClassName, String validationQuery, String[] urlPrefixes) {
		this.productName = productName;
		this.driverClassName = driverClassName;
		this.xaDataSourceClassName = xaDataSourceClassName;
		this.validationQuery = validationQuery;
		this.urlPrefixes = urlPrefixes;
	}

	public String[] getUrlPrefixes() {
		if (urlPrefixes == null || urlPrefixes.length == 0) {
			return new String[]{"jdbc:" + name().toLowerCase() + ":"};
		}
		return urlPrefixes;
	}

	private String prefix() {
		return "jdbc:" + name().toLowerCase() + ":";
	}

	public static JdbcDriver parse(String url) {
		if (url != null && url.startsWith("jdbc")) {
			for (JdbcDriver driver : values()) {
				if (driver == UNKNOWN) {
					continue;
				}
				for (String prefix : driver.getUrlPrefixes()) {
					if (url.startsWith(prefix)) {
						return driver;
					}
				}
			}
		}
		return UNKNOWN;
	}

}
