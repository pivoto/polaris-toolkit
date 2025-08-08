package io.polaris.core.jdbc.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import io.polaris.core.collection.ObjectArrays;
import io.polaris.core.jdbc.Jdbcs;
import io.polaris.core.jdbc.base.BeanMappings;
import io.polaris.core.jdbc.base.DefaultParameterPreparer;
import io.polaris.core.jdbc.base.JdbcOptions;
import io.polaris.core.jdbc.base.StatementPreparer;
import io.polaris.core.jdbc.sql.PreparedSql;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.lang.bean.MetaObject;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.Loggers;

/**
 * @author Qt
 * @since  Feb 07, 2024
 */
public class JdbcBatch {
	private static final ILogger log = Loggers.of(JdbcBatch.class);
	private static final JdbcOptions DEFAULT_OPTIONS = JdbcOptions.ofDefault();
	private final List<BatchResult> resultList = new ArrayList<>();
	private String currentSql;
	private Connection currentConnection;
	private PreparedStatement currentStatement;
	private List<Object> currentBindingsList;
	private String[] currentKeyProperties;

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void flushCurrent() throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = currentStatement;
			if (st == null) {
				return;
			}
			log.debug("执行批处理SQL：{}", currentSql);
			int[] rows = st.executeBatch();
			String[] keyProperties = currentKeyProperties;
			if (ObjectArrays.isNotEmpty(keyProperties)) {
				rs = st.getGeneratedKeys();
				for (Object o : currentBindingsList) {
					if (rs.next()) {
						MetaObject metaObject = MetaObject.of((Class) o.getClass());
						for (int i = 0; i < keyProperties.length; i++) {
							MetaObject valMeta = metaObject.getPathProperty(keyProperties[i]);
							Object val = BeanMappings.getResultValue(rs, i + 1, valMeta);
							metaObject.setPathProperty(o, keyProperties[i], val);
						}
					} else {
						break;
					}
				}
			}
			resultList.add(new BatchResult(currentSql, rows));
			currentSql = null;
			currentConnection = null;
			currentStatement = null;
			currentBindingsList = null;
			currentKeyProperties = null;
		} finally {
			Jdbcs.close(rs);
			Jdbcs.close(st);
		}
	}

	public List<BatchResult> flush() throws SQLException {
		flushCurrent();
		return resultList;
	}

	public void update(@Nonnull Connection conn, @Nonnull String sql, @Nonnull JdbcOptions options,
		StatementPreparer preparer, Object generatedKeyBinding) throws SQLException {

		if (sql.equals(currentSql) && conn.equals(currentConnection)) {
			if (preparer != null) {
				preparer.setParameters(currentStatement, DefaultParameterPreparer.orDefault(options.getParameterPreparer()));
			}
			if (currentBindingsList != null) {
				currentBindingsList.add(generatedKeyBinding);
			}
			currentStatement.addBatch();
			return;
		}
		if (currentSql != null && currentConnection != null) {
			flushCurrent();
			currentSql = null;
			currentConnection = null;
			currentStatement = null;
			currentBindingsList = null;
		}

		PreparedStatement st = Jdbcs.prepareStatement(conn, sql, options);
		if (preparer != null) {
			preparer.setParameters(st, null);
		}
		st.addBatch();
		currentSql = sql;
		currentConnection = conn;
		currentStatement = st;
		String[] keyProperties = options.getKeyProperties();
		if (options.isUseGeneratedKeys() && ObjectArrays.isNotEmpty(keyProperties)) {
			currentKeyProperties = options.getKeyProperties();
			currentBindingsList = new ArrayList<>();
		}
	}


	public void update(Connection conn, SqlNode sql) throws SQLException {
		PreparedSql preparedSql = sql.asPreparedSql();
		update(conn, preparedSql.getText(), Jdbcs.preparerOfParameters(preparedSql.getBindings()));
	}


	public void update(Connection conn, SqlNode sql, @Nonnull JdbcOptions options) throws SQLException {
		PreparedSql preparedSql = sql.asPreparedSql();
		update(conn, preparedSql.getText(), options, Jdbcs.preparerOfParameters(preparedSql.getBindings()), null);
	}

	public void update(Connection conn, SqlNode sql, @Nonnull JdbcOptions options, Object generatedKeyBinding) throws SQLException {
		PreparedSql preparedSql = sql.asPreparedSql();
		update(conn, preparedSql.getText(), options, Jdbcs.preparerOfParameters(preparedSql.getBindings()), generatedKeyBinding);
	}

	public void update(Connection conn, String sql) throws SQLException {
		update(conn, sql, DEFAULT_OPTIONS, null, null);
	}

	public void update(Connection conn, String sql, Iterable<?> parameters) throws SQLException {
		update(conn, sql, DEFAULT_OPTIONS, Jdbcs.preparerOfParameters(parameters), null);
	}

	public void update(Connection conn, String sql, Object[] parameters) throws SQLException {
		update(conn, sql, DEFAULT_OPTIONS, Jdbcs.preparerOfParameters(parameters), null);
	}

	public void update(Connection conn, String sql, StatementPreparer preparer) throws SQLException {
		update(conn, sql, DEFAULT_OPTIONS, preparer, null);
	}


}
