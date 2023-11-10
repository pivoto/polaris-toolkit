package io.polaris.core.jdbc;

import io.polaris.core.jdbc.sql.PreparedSql;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.log.ILogger;
import io.polaris.core.map.Maps;
import io.polaris.core.string.Strings;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.lang.reflect.Array;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Qt
 * @since 1.8
 */
public class Jdbcs {
	private static final ILogger log = ILogger.of(Jdbcs.class);

	public static DataSource getDataSource(String jndiName) throws SQLException {
		try {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(jndiName);
			return ds;
		} catch (NamingException e) {
			try {
				DataSource ds = (DataSource) ((Context) new InitialContext().lookup("java:comp/env"))
					.lookup(jndiName);
				return ds;
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
		Connection conn = DriverManager.getConnection(url, info);
		return conn;
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


	public static <T> T query(Connection conn, SqlNode sqlNode,
							  QueryCallback<T> queryCallback) throws SQLException {
		PreparedSql sql = sqlNode.asPreparedSql();
		return query(conn, sql.getText(), (Object) sql.getBindings(), queryCallback);
	}

	public static <T> T query(Connection conn, String sql, Iterable<?> parameters,
							  QueryCallback<T> queryCallback) throws SQLException {
		return query(conn, sql, (Object) parameters, queryCallback);
	}

	public static <T> T query(Connection conn, String sql, Object[] parameters,
							  QueryCallback<T> queryCallback) throws SQLException {
		return query(conn, sql, (Object) parameters, queryCallback);
	}

	public static <T> T query(Connection conn, String sql, QueryCallback<T> queryCallback)
		throws SQLException {
		return query(conn, sql, (Object) null, queryCallback);
	}

	public static <T> List<T> query(Connection conn, String sql, RowMapper<T> mapper)
		throws SQLException {
		return query(conn, sql, (Object) null, rs -> {
			List<T> list = new ArrayList<>();
			while (rs.next()) {
				list.add(mapper.map(rs));
			}
			return list;
		});
	}

	public static List<Map<String, Object>> queryForList(Connection conn, String sql)
		throws SQLException {
		return query(conn, sql, (Object) null, new DefaultQueryCallback());
	}

	public static List<Map<String, Object>> queryForList(Connection conn, String sql,
														 Iterable<?> parameters) throws SQLException {
		return query(conn, sql, parameters, new DefaultQueryCallback());
	}

	public static List<Map<String, Object>> queryForList(Connection conn, String sql,
														 Object[] parameters) throws SQLException {
		return query(conn, sql, parameters, new DefaultQueryCallback());
	}

	public static Map<String, Object> queryForMap(Connection conn, String sql) throws SQLException {
		return query(conn, sql, (Object) null, new UniqueRowQueryCallback());
	}

	public static Map<String, Object> queryForMap(Connection conn, String sql,
												  Iterable<?> parameters) throws SQLException {
		return query(conn, sql, parameters, new UniqueRowQueryCallback());
	}

	public static Map<String, Object> queryForMap(Connection conn, String sql, Object[] parameters)
		throws SQLException {
		return query(conn, sql, parameters, new UniqueRowQueryCallback());
	}

	public static Object queryForObject(Connection conn, String sql) throws SQLException {
		return query(conn, sql, (Object) null, new UniqueQueryCallback());
	}

	public static Object queryForObject(Connection conn, String sql, Iterable<?> parameters)
		throws SQLException {
		return query(conn, sql, parameters, new UniqueQueryCallback());
	}

	public static Object queryForObject(Connection conn, String sql, Object[] parameters)
		throws SQLException {
		return query(conn, sql, parameters, new UniqueQueryCallback());
	}

	public static int update(Connection conn, SqlNode sql) throws SQLException {
		PreparedSql preparedSql = sql.asPreparedSql();
		return update(conn, preparedSql.getText(), (Object) preparedSql.getBindings());
	}

	public static int update(Connection conn, String sql) throws SQLException {
		return update(conn, sql, (Object) null);
	}

	public static int update(Connection conn, String sql, Iterable<?> parameters)
		throws SQLException {
		return update(conn, sql, (Object) parameters);
	}

	public static int update(Connection conn, String sql, Object[] parameters) throws SQLException {
		return update(conn, sql, (Object) parameters);
	}

	@SuppressWarnings("unchecked")
	static <T> T query(Connection conn, String sql, Object parameters, QueryCallback<T> queryCallback)
		throws SQLException {
		if (conn == null) {
			throw new SQLException("没有得到数据库连接！");
		}
		Statement stmt = null;
		ResultSet rs = null;
		try {
			if (parameters != null) {
				PreparedStatement pstmt = conn.prepareStatement(sql);
				stmt = pstmt;
				setParameter(pstmt, parameters);
				rs = pstmt.executeQuery();
			} else {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
			}
			if (queryCallback != null) {
				return queryCallback.visit(rs);
			} else {
				return (T) new DefaultQueryCallback().visit(rs);
			}
		} catch (SQLException e) {
			log.error("查询方法执行异常，语句：" + sql);
			throw e;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException se) {
				}
			}
		}
	}

	static void setParameter(PreparedStatement pstmt, Object parameters)
		throws ArrayIndexOutOfBoundsException, IllegalArgumentException, SQLException {
		if (parameters.getClass().isArray()) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(parameters); i++) {
				Object o = Array.get(parameters, i);
				if (o == null) {
					pstmt.setNull(i + 1, Types.VARCHAR);
				} else {
					pstmt.setObject(i + 1, o);
				}
			}
		} else if (parameters instanceof Iterable<?>) {
			int i = 1;
			for (Object o : (Iterable<?>) parameters) {
				if (o == null) {
					pstmt.setNull(i, Types.VARCHAR);
				} else {
					pstmt.setObject(i, o);
				}
				i++;
			}
		}
	}

	static int update(Connection conn, String sql, Object parameters) throws SQLException {
		if (conn == null) {
			throw new SQLException("没有得到数据库连接！");
		}
		Statement stmt = null;
		try {
			if (parameters != null) {
				PreparedStatement pstmt = conn.prepareStatement(sql);
				stmt = pstmt;
				setParameter(pstmt, parameters);
				return pstmt.executeUpdate();
			} else {
				stmt = conn.createStatement();
				return stmt.executeUpdate(sql);
			}
		} catch (SQLException e) {
			log.error("更新方法执行异常，语句：" + sql);
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException se) {
				}
			}
		}
	}

	/**
	 * @author Qt
	 * @since 1.8
	 */
	static class DefaultQueryCallback implements QueryCallback<List<Map<String, Object>>> {
		@Override
		public List<Map<String, Object>> visit(ResultSet rs) throws SQLException {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

			ResultSetMetaData meta = rs.getMetaData();
			int cnt = meta.getColumnCount();
			while (rs.next()) {
				Map<String, Object> map = Maps.newUpperCaseLinkedHashMap();
				for (int i = 1; i <= cnt; i++) {
					map.put(meta.getColumnLabel(i).toUpperCase(), rs.getObject(i));
				}
				list.add(map);
			}
			return list;
		}
	}

	/**
	 * @author Qt
	 * @since 1.8
	 */
	static class UniqueQueryCallback implements QueryCallback<Object> {
		@Override
		public Object visit(ResultSet rs) throws SQLException {
			Object o = null;
			if (rs.next()) {
				o = rs.getObject(1);
			}
			return o;
		}
	}

	/**
	 * @author Qt
	 * @since 1.8
	 */
	static class UniqueRowQueryCallback implements QueryCallback<Map<String, Object>> {
		@Override
		public Map<String, Object> visit(ResultSet rs) throws SQLException {
			ResultSetMetaData meta = rs.getMetaData();
			int cnt = meta.getColumnCount();
			Map<String, Object> map = Maps.newUpperCaseLinkedHashMap();
			if (rs.next()) {
				for (int i = 1; i <= cnt; i++) {
					map.put(meta.getColumnLabel(i).toUpperCase(), rs.getObject(i));
				}
			}
			return map;
		}
	}
}
