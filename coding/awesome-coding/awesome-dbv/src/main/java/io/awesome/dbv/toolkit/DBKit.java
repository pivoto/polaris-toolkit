package io.awesome.dbv.toolkit;

import lombok.extern.slf4j.Slf4j;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.lang.reflect.Array;
import java.sql.*;
import java.util.*;

/**
 * @author Qt
 */
@SuppressWarnings("ALL")
@Slf4j
public class DBKit {
	private static volatile DBKit instance;

	private DBKit() {
	}

	public static boolean close(Connection cn) {
		if (cn != null) {
			try {
				cn.close();
				return true;
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
				return true;
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean close(Statement st) {
		if (st != null) {
			try {
				st.close();
				return true;
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
				return false;
			}
		} else {
			return false;
		}
	}

	public static Connection getConnection(String jndiName) throws SQLException {
		// return new ConnectionSpy(getDataSource(jndiName).getConnection());
		return getDataSource(jndiName).getConnection();
	}

	public static Connection getConnection(String driver, String url, Properties info)
		throws SQLException {
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, info);
			// return new ConnectionSpy(conn);
			return conn;
		} catch (ClassNotFoundException e) {
			throw new SQLException(e.getMessage(), e);
		}
	}

	public static Connection getConnection(String driver, String url, String user, String pwd)
		throws SQLException {
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, user, pwd);
			// return new ConnectionSpy(conn);
			return conn;
		} catch (ClassNotFoundException e) {
			throw new SQLException(e.getMessage(), e);
		}
	}

	public static <T> T query(Connection conn, String sql, Collection<?> parameters,
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

	public static List<Map<String, Object>> queryForList(Connection conn, String sql)
		throws SQLException {
		return query(conn, sql, (Object) null, new DefaultQueryCallback());
	}

	public static List<Map<String, Object>> queryForList(Connection conn, String sql,
		Collection<?> parameters) throws SQLException {
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
		Collection<?> parameters) throws SQLException {
		return query(conn, sql, parameters, new UniqueRowQueryCallback());
	}

	public static Map<String, Object> queryForMap(Connection conn, String sql, Object[] parameters)
		throws SQLException {
		return query(conn, sql, parameters, new UniqueRowQueryCallback());
	}

	public static Object queryForObject(Connection conn, String sql) throws SQLException {
		return query(conn, sql, (Object) null, new UniqueQueryCallback());
	}

	public static Object queryForObject(Connection conn, String sql, Collection<?> parameters)
		throws SQLException {
		return query(conn, sql, parameters, new UniqueQueryCallback());
	}

	public static Object queryForObject(Connection conn, String sql, Object[] parameters)
		throws SQLException {
		return query(conn, sql, parameters, new UniqueQueryCallback());
	}

	public static int update(Connection conn, String sql) throws SQLException {
		return update(conn, sql, (Object) null);
	}

	public static int update(Connection conn, String sql, Collection<?> parameters)
		throws SQLException {
		return update(conn, sql, (Object) parameters);
	}

	public static int update(Connection conn, String sql, Object[] parameters) throws SQLException {
		return update(conn, sql, (Object) parameters);
	}

	private static DataSource getDataSource(String jndiName) throws SQLException {
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

	static DBKit getInstance() {
		if (instance == null) {
			synchronized (DBKit.class) {
				if (instance == null) {
					instance = new DBKit();
				}
			}
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	static <T> T query(Connection conn, String sql, Object parameters, QueryCallback<T> queryCallback)
		throws SQLException {
		if (conn != null) {
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
		} else {
			log.error("===========没有得到数据库连接！===========");
			throw new SQLException("没有得到数据库连接！");
		}
	}

	static void setParameter(PreparedStatement pstmt, Object parameters)
		throws ArrayIndexOutOfBoundsException, IllegalArgumentException, SQLException {
		if (parameters.getClass().isArray()) {
			for (int i = 0; i < Array.getLength(parameters); i++) {
				pstmt.setObject(i + 1, Array.get(parameters, i));
			}
		} else if (parameters instanceof Collection) {
			int i = 1;
			for (Object o : (Collection<?>) parameters) {
				pstmt.setObject(i, o);
				i++;
			}
		}
	}

	static int update(Connection conn, String sql, Object parameters) throws SQLException {
		if (conn != null) {
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
				log.error("查询方法执行异常，语句：" + sql);
				throw e;
			} finally {
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException se) {
					}
				}
			}
		} else {
			log.error("===========没有得到数据库连接！===========");
			throw new SQLException("没有得到数据库连接！");
		}
	}

	public static interface QueryCallback<T> {
		T visit(ResultSet rs) throws SQLException;
	}

	public static class DefaultQueryCallback implements QueryCallback<List<Map<String, Object>>> {
		@Override
		public List<Map<String, Object>> visit(ResultSet rs) throws SQLException {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

			ResultSetMetaData meta = rs.getMetaData();
			int cnt = meta.getColumnCount();
			while (rs.next()) {
				Map<String, Object> map = MapKit.<String, Object>newCaseInsensitiveLinkedHashMap();
				for (int i = 1; i <= cnt; i++) {
					map.put(meta.getColumnLabel(i).toUpperCase(), rs.getObject(i));
				}
				list.add(map);
			}
			return list;
		}
	}

	public static class UniqueQueryCallback implements QueryCallback<Object> {
		@Override
		public Object visit(ResultSet rs) throws SQLException {
			Object o = null;
			if (rs.next()) {
				o = rs.getObject(1);
			}
			return o;
		}
	}

	public static class UniqueRowQueryCallback implements QueryCallback<Map<String, Object>> {
		@Override
		public Map<String, Object> visit(ResultSet rs) throws SQLException {
			ResultSetMetaData meta = rs.getMetaData();
			int cnt = meta.getColumnCount();
			Map<String, Object> map = MapKit.<String, Object>newCaseInsensitiveLinkedHashMap();
			if (rs.next()) {
				for (int i = 1; i <= cnt; i++) {
					map.put(meta.getColumnLabel(i).toUpperCase(), rs.getObject(i));
				}
			}
			return map;
		}
	}

}
