package io.polaris.core.jdbc;

import io.polaris.core.lang.Strings;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Qt
 * @since 1.8
 */
@Slf4j
public class Jdbcs {

	public static Connection getConnection(String driver, String url, String username, String password) {
		try {
			if (Strings.isBlank(driver)) {
				driver = JdbcDriver.parse(url).getDriverClassName();
				if (Strings.isBlank(driver)) {
					throw new IllegalArgumentException("无法从url中获得驱动类");
				}
			}
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("找不到驱动：" + driver);
		}
		try {
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			log.error("获取Jdbc链接失败", e);
			throw new IllegalArgumentException("获取Jdbc链接失败：" + e.getMessage());
		}
	}

	public static <R extends AutoCloseable> void close(R r) {
		try {
			r.close();
		} catch (Exception ignored) {
		}
	}

	public static void close(Connection connection) {
		try {
			connection.close();
		} catch (Exception ignored) {
		}
	}
}
