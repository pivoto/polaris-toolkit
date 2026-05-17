package io.polaris.core.jdbc.executor;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.stream.Collectors;

import io.polaris.core.concurrent.ThreadLocalSpec;
import io.polaris.core.concurrent.Threads;
import io.polaris.core.jdbc.base.JdbcOptions;
import io.polaris.core.jdbc.base.ResultExtractor;
import io.polaris.core.jdbc.base.ResultSetVisitor;
import io.polaris.core.jdbc.sql.BoundSql;
import io.polaris.core.jdbc.sql.PreparedSql;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.lang.Types;
import io.polaris.core.time.Dates;

/**
 * @author Qt
 * @since May 06, 2026
 */
@SuppressWarnings("unchecked")
public class JdbcRecorders {

	private static final ThreadLocalSpec<BlockingDeque<SqlNode>> recorder = Threads.newThreadLocal();

	public static <T> T of(Class<T> interfaceClass) {
		RecordJdbcExecutor<T> handler = new RecordJdbcExecutor<>(interfaceClass);
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass, InvocationHandlerHolder.class}, handler);
	}

	private static void record(SqlNode sqlNode) {
		BlockingDeque<SqlNode> deque = recorder.get();
		if (deque == null) {
			deque = new java.util.concurrent.LinkedBlockingDeque<>();
			recorder.set(deque);
		}
		deque.offer(sqlNode);
	}

	public static SqlNode pollFirst() {
		BlockingDeque<SqlNode> deque = recorder.get();
		if (deque == null) {
			return null;
		}
		return deque.pollFirst();
	}

	public static SqlNode pollLast() {
		BlockingDeque<SqlNode> deque = recorder.get();
		if (deque == null) {
			return null;
		}
		return deque.pollLast();
	}

	public static List<SqlNode> pollAll() {
		BlockingDeque<SqlNode> deque = recorder.get();
		if (deque == null) {
			return Collections.emptyList();
		}
		List<SqlNode> list = new ArrayList<>();
		deque.drainTo(list);
		return list;
	}

	public static BoundSql pollFirstBoundSql() {
		SqlNode sqlNode = pollFirst();
		if (sqlNode == null) {
			return null;
		}
		return sqlNode.asBoundSql();
	}

	public static BoundSql pollLastBoundSql() {
		SqlNode sqlNode = pollLast();
		if (sqlNode == null) {
			return null;
		}
		return sqlNode.asBoundSql();
	}

	public static List<BoundSql> pollAllBoundSql() {
		return pollAll().stream().map(SqlNode::asBoundSql).collect(Collectors.toList());
	}

	public static PreparedSql pollFirstPreparedSql() {
		SqlNode sqlNode = pollFirst();
		if (sqlNode == null) {
			return null;
		}
		return sqlNode.asPreparedSql();
	}

	public static PreparedSql pollLastPreparedSql() {
		SqlNode sqlNode = pollLast();
		if (sqlNode == null) {
			return null;
		}
		return sqlNode.asPreparedSql();
	}

	public static List<PreparedSql> pollAllPreparedSql() {
		return pollAll().stream().map(SqlNode::asPreparedSql).collect(Collectors.toList());
	}

	public static String pollFirstSql() {
		return asSqlString(pollFirst());
	}

	public static String pollLastSql() {
		return asSqlString(pollLast());
	}

	public static List<String> pollALlSql() {
		return pollAll().stream().map(JdbcRecorders::asSqlString).collect(Collectors.toList());
	}

	public static String pollALlSqlText() {
		List<SqlNode> list = pollAll();
		StringBuilder sb = new StringBuilder();
		for (SqlNode sqlNode : list) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(asSqlString(sqlNode));
		}
		return sb.toString();
	}


	public static String asSqlString(SqlNode sqlNode) {
		if (sqlNode == null) {
			return null;
		}
		PreparedSql preparedSql = sqlNode.asPreparedSql();
		String sql = preparedSql.getText();
		List<Object> bindings = preparedSql.getBindings();
		char[] chs = sql.toCharArray();

		Iterator<Object> it = bindings.iterator();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < chs.length; i++) {
			char ch = chs[i];
			if (ch == '\'') {
				i++;
				sb.append(ch).append(chs[i]);
			} else if (ch == '?') {
				if (it.hasNext()) {
					Object value = it.next();
					if (value instanceof String || value instanceof Character) {
						sb.append("'").append(value).append("'");
					} else if (value instanceof Date) {
						sb.append(Dates.formatDefault((Date) value));
					} else if (value instanceof TemporalAccessor) {
						sb.append(Dates.formatDefault((TemporalAccessor) value));
					} else if (value != null) {
						sb.append(value);
					} else {
						sb.append("null");
					}
				} else {
					sb.append("?");
				}
			} else if (ch == '\n') {
				sb.append(' ');
			} else if (ch == '\r') {
				sb.append(' ');
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}


	static class RecordJdbcExecutor<T> extends JdbcExecutor<T> {

		protected RecordJdbcExecutor(Class<T> interfaceClass) {
			super(interfaceClass);
		}

		@Override
		public Object doInvoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object rs = super.doInvoke(proxy, method, args);
			if (rs == null) {
				Class<?> returnType = method.getReturnType();
				if (Types.isPrimitive(returnType)) {
					// 基本类型，则返回默认值，防止自动拆包报NPE
					return Types.getDefaultValue(returnType);
				} else if (List.class.isAssignableFrom(returnType)) {
					return Collections.emptyList();
				} else if (Collection.class.isAssignableFrom(returnType)) {
					return Collections.emptyList();
				}
			}
			return rs;
		}

		@Override
		protected int doBatchUpdate(JdbcBatch batch, Connection conn, SqlNode sqlNode, JdbcOptions options, Object generatedKeyBinding) throws SQLException {
			record(sqlNode);
			return -1;
		}

		@Override
		protected int doUpdate(Connection conn, SqlNode sqlNode, JdbcOptions options, Object generatedKeyBinding) throws SQLException {
			record(sqlNode);
			return -1;
		}

		@Override
		protected void doQuery(Connection conn, SqlNode sqlNode, JdbcOptions options, ResultSetVisitor resultSetVisitor) throws SQLException {
			record(sqlNode);
		}

		@Override
		protected Object doQuery(Connection conn, SqlNode sqlNode, JdbcOptions options, ResultExtractor<?> extractor) throws SQLException {
			record(sqlNode);
			return null;
		}
	}

}
