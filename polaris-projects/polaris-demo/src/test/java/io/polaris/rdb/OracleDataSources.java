package io.polaris.rdb;

import java.sql.SQLException;

import javax.sql.DataSource;

import oracle.jdbc.driver.OracleDriver;

import com.zaxxer.hikari.HikariDataSource;

/**
 * @author Qt
 * @since  Apr 24, 2024
 */
public class OracleDataSources {

	public static DataSource buildDataSource() {
		String jdbcUrl = System.getProperty("jdbcUrl", "jdbc:oracle:thin:@localhost:1521:cmisdb");
		String user = System.getProperty("jdbcUser", "cmis_config");
		String password = System.getProperty("jdbcPassword", "cmis_config");
		return buildDataSource(jdbcUrl, user, password);
	}

	public static DataSource buildDataSource(String jdbcUrl, String user, String password) {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(jdbcUrl);
		dataSource.setDriverClassName(OracleDriver.class.getName());
		dataSource.setUsername(user);
		dataSource.setPassword(password);
		return dataSource;
	}

	public static void main(String[] args) throws SQLException {
		System.out.println(buildDataSource().getConnection().getMetaData().getDatabaseProductName());
	}
}
