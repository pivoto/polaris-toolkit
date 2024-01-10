package io.polaris.core.jdbc;

import io.polaris.core.jdbc.base.*;
import io.polaris.core.jdbc.sql.PreparedSql;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.string.Strings;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings({"unused"})
public class Jdbcs {
	private static final ILogger log = ILoggers.of(Jdbcs.class);
	private static int defaultFetchSize = 100;

	public static void setDefaultFetchSize(int fetchSize) {
		Jdbcs.defaultFetchSize = fetchSize;
	}

	public static int getDefaultFetchSize() {
		return Jdbcs.defaultFetchSize;
	}

	public static DataSource getDataSource(String jndiName) throws SQLException {
		try {
			Context ctx = new InitialContext();
			return (DataSource) ctx.lookup(jndiName);
		} catch (NamingException e) {
			try {
				return (DataSource) ((Context) new InitialContext().lookup("java:comp/env"))
					.lookup(jndiName);
			} catch (Exception e1) {
				throw new SQLException("Can't lookup " + jndiName);
			}
		}
	}

	public static Connection getConnection(String jndiName) throws SQLException {
		return getDataSource(jndiName).getConnection();
	}

	public static Connection getConnection(String driver, String url, Properties info) throws SQLException {
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
		return DriverManager.getConnection(url, info);
	}

	public static Connection getConnection(String url, Properties info) throws SQLException {
		return getConnection(null, url, info);
	}

	public static Connection getConnection(String driver, String url, String user, String password, boolean remark) throws SQLException {
		if (remark) {
			Properties info = new Properties();
			info.setProperty("remarks", "true");//common
			info.setProperty("remarksReporting", "true");//oracle
			info.setProperty("useInformationSchema", "true");//mysql
			info.setProperty("user", user);
			info.setProperty("password", password);
			return getConnection(driver, url, info);
		}
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
		return DriverManager.getConnection(url, user, password);
	}

	public static Connection getConnection(String driver, String url, String user, String password) throws SQLException {
		return getConnection(driver, url, user, password, true);
	}

	public static Connection getConnection(String url, String user, String password, boolean remark) throws SQLException {
		return getConnection(null, url, user, password, remark);
	}

	public static Connection getConnection(String url, String user, String password) throws SQLException {
		return getConnection(null, url, user, password, true);
	}


	public static <R extends AutoCloseable> void close(R r) {
		try {
			if (r != null) {
				r.close();
			}
		} catch (Exception ignored) {
		}
	}

	public static void close(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (Exception ignored) {
		}
	}


	public static <T> T query(Connection conn, SqlNode sqlNode
		, ResultExtractor<T> resultExtractor) throws SQLException {
		PreparedSql sql = sqlNode.asPreparedSql();
		return query(conn, sql.getText(), buildStatementSetting(sql.getBindings()), resultExtractor);
	}

	public static <T> T query(Connection conn, String sql, Iterable<?> parameters
		, ResultExtractor<T> resultExtractor) throws SQLException {
		return query(conn, sql, buildStatementSetting(parameters), resultExtractor);
	}

	public static <T> T query(Connection conn, String sql, Object[] parameters
		, ResultExtractor<T> resultExtractor) throws SQLException {
		return query(conn, sql, buildStatementSetting(parameters), resultExtractor);
	}

	public static <T> T query(Connection conn, String sql, ResultExtractor<T> resultExtractor) throws SQLException {
		return query(conn, sql, (PreparedStatementSetting) null, resultExtractor);
	}

	public static <T> List<T> query(Connection conn, String sql, RowMapper<T> mapper) throws SQLException {
		return query(conn, sql, (PreparedStatementSetting) null, rs -> {
			List<T> list = new ArrayList<>();
			while (rs.next()) {
				list.add(mapper.map(rs));
			}
			return list;
		});
	}


	public static List<Map<String, Object>> queryForList(Connection conn, String sql) throws SQLException {
		return query(conn, sql, (PreparedStatementSetting) null, new ResultMapListExtractor());
	}

	public static List<Map<String, Object>> queryForList(Connection conn, String sql, Iterable<?> parameters) throws SQLException {
		return query(conn, sql, buildStatementSetting(parameters), new ResultMapListExtractor());
	}

	public static List<Map<String, Object>> queryForList(Connection conn, String sql, Object[] parameters) throws SQLException {
		return query(conn, sql, buildStatementSetting(parameters), new ResultMapListExtractor());
	}

	public static <T> List<T> queryForList(Connection conn, String sql, Class<T> beanType) throws SQLException {
		return query(conn, sql, (PreparedStatementSetting) null, new ResultBeanListExtractor<>(beanType));
	}

	public static <T> List<T> queryForList(Connection conn, String sql, Iterable<?> parameters, Class<T> beanType) throws SQLException {
		return query(conn, sql, buildStatementSetting(parameters), new ResultBeanListExtractor<>(beanType));
	}

	public static <T> List<T> queryForList(Connection conn, String sql, Object[] parameters, Class<T> beanType) throws SQLException {
		return query(conn, sql, buildStatementSetting(parameters), new ResultBeanListExtractor<>(beanType));
	}

	public static <T> List<T> queryForList(Connection conn, String sql, BeanMapping<T> mapping) throws SQLException {
		return query(conn, sql, (PreparedStatementSetting) null, new ResultBeanMappingListExtractor<>(mapping));
	}

	public static <T> List<T> queryForList(Connection conn, String sql, Iterable<?> parameters, BeanMapping<T> mapping) throws SQLException {
		return query(conn, sql, buildStatementSetting(parameters), new ResultBeanMappingListExtractor<>(mapping));
	}

	public static <T> List<T> queryForList(Connection conn, String sql, Object[] parameters, BeanMapping<T> mapping) throws SQLException {
		return query(conn, sql, buildStatementSetting(parameters), new ResultBeanMappingListExtractor<>(mapping));
	}

	public static Map<String, Object> queryForMap(Connection conn, String sql) throws SQLException {
		return query(conn, sql, (PreparedStatementSetting) null, new ResultMapExtractor());
	}

	public static Map<String, Object> queryForMap(Connection conn, String sql, Iterable<?> parameters) throws SQLException {
		return query(conn, sql, buildStatementSetting(parameters), new ResultMapExtractor());
	}

	public static Map<String, Object> queryForMap(Connection conn, String sql, Object[] parameters) throws SQLException {
		return query(conn, sql, buildStatementSetting(parameters), new ResultMapExtractor());
	}

	public static <T> T queryForObject(Connection conn, String sql, Class<T> beanType) throws SQLException {
		return query(conn, sql, (PreparedStatementSetting) null, new ResultBeanExtractor<>(beanType));
	}

	public static <T> T queryForObject(Connection conn, String sql, Iterable<?> parameters, Class<T> beanType) throws SQLException {
		return query(conn, sql, parameters, new ResultBeanExtractor<>(beanType));
	}

	public static <T> T queryForObject(Connection conn, String sql, Object[] parameters, Class<T> beanType) throws SQLException {
		return query(conn, sql, parameters, new ResultBeanExtractor<>(beanType));
	}

	public static <T> T queryForObject(Connection conn, String sql, BeanMapping<T> mapping) throws SQLException {
		return query(conn, sql, (PreparedStatementSetting) null, new ResultBeanMappingExtractor<>(mapping));
	}

	public static <T> T queryForObject(Connection conn, String sql, Iterable<?> parameters, BeanMapping<T> mapping) throws SQLException {
		return query(conn, sql, parameters, new ResultBeanMappingExtractor<>(mapping));
	}

	public static <T> T queryForObject(Connection conn, String sql, Object[] parameters, BeanMapping<T> mapping) throws SQLException {
		return query(conn, sql, parameters, new ResultBeanMappingExtractor<>(mapping));
	}

	public static Object queryForObject(Connection conn, String sql) throws SQLException {
		return query(conn, sql, (PreparedStatementSetting) null, new ResultSingleExtractor());
	}

	public static Object queryForObject(Connection conn, String sql, Iterable<?> parameters) throws SQLException {
		return query(conn, sql, buildStatementSetting(parameters), new ResultSingleExtractor());
	}

	public static Object queryForObject(Connection conn, String sql, Object[] parameters) throws SQLException {
		return query(conn, sql, buildStatementSetting(parameters), new ResultSingleExtractor());
	}

	public static int update(Connection conn, SqlNode sql) throws SQLException {
		PreparedSql preparedSql = sql.asPreparedSql();
		return update(conn, preparedSql.getText(), buildStatementSetting(preparedSql.getBindings()));
	}

	public static int update(Connection conn, String sql) throws SQLException {
		return update(conn, sql, (PreparedStatementSetting) null);
	}

	public static int update(Connection conn, String sql, Iterable<?> parameters) throws SQLException {
		return update(conn, sql, buildStatementSetting(parameters));
	}

	public static int update(Connection conn, String sql, Object[] parameters) throws SQLException {
		return update(conn, sql, buildStatementSetting(parameters));
	}

	@SuppressWarnings({"unchecked", "SqlSourceToSinkFlow"})
	public static <T> T query(Connection conn, String sql, PreparedStatementSetting statementSetting, ResultExtractor<T> resultExtractor) throws SQLException {
		if (conn == null) {
			throw new SQLException("没有得到数据库连接！");
		}
		Statement stmt = null;
		ResultSet rs = null;
		try {
			if (statementSetting != null) {
				PreparedStatement pstmt = conn.prepareStatement(sql);
				stmt = pstmt;
				stmt.setFetchSize(defaultFetchSize);
				statementSetting.set(pstmt);
				rs = pstmt.executeQuery();
			} else {
				stmt = conn.createStatement();
				stmt.setFetchSize(defaultFetchSize);
				rs = stmt.executeQuery(sql);
			}
			if (resultExtractor != null) {
				return resultExtractor.visit(rs);
			} else {
				return (T) new ResultMapListExtractor().visit(rs);
			}
		} catch (SQLException e) {
			log.error("查询方法执行异常，语句：" + sql);
			throw e;
		} finally {
			close(rs);
			close(stmt);
		}
	}


	@SuppressWarnings("SqlSourceToSinkFlow")
	public static int update(Connection conn, String sql, PreparedStatementSetting setting) throws SQLException {
		if (conn == null) {
			throw new SQLException("没有得到数据库连接！");
		}
		Statement stmt = null;
		try {
			if (setting != null) {
				PreparedStatement pstmt = conn.prepareStatement(sql);
				stmt = pstmt;
				setting.set(pstmt);
				return pstmt.executeUpdate();
			} else {
				stmt = conn.createStatement();
				return stmt.executeUpdate(sql);
			}
		} catch (SQLException e) {
			log.error("更新方法执行异常，语句：" + sql);
			throw e;
		} finally {
			close(stmt);
		}
	}

	public static PreparedStatementSetting buildStatementSetting(Iterable<?> parameters) {
		return st -> {
			int i = 1;
			for (Object o : parameters) {
				if (o == null) {
					st.setNull(i, Types.VARCHAR);
				} else {
					st.setObject(i, o);
				}
				i++;
			}
		};
	}

	public static PreparedStatementSetting buildStatementSetting(Object[] parameters) {
		return st -> {
			for (int i = 0; i < parameters.length; i++) {
				Object o = parameters[i];
				if (o == null) {
					st.setNull(i + 1, Types.VARCHAR);
				} else {
					st.setObject(i + 1, o);
				}
			}
		};
	}

}
