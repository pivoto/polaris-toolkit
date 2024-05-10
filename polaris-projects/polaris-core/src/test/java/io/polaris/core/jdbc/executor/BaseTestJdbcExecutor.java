package io.polaris.core.jdbc.executor;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import io.polaris.core.collection.Iterables;
import io.polaris.core.collection.ObjectArrays;
import io.polaris.core.function.Executable;
import io.polaris.core.function.ExecutableWithArg1;
import io.polaris.core.io.IO;
import io.polaris.core.jdbc.Jdbcs;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since  Feb 08, 2024
 */
public class BaseTestJdbcExecutor {
	private static final ILogger log = ILoggers.of(BaseTestJdbcExecutor.class);

	protected static Connection getConnection() throws SQLException {
		return Jdbcs.getConnection("jdbc:oracle:thin:@localhost:1521/cmisdb", "basesv", "basesv");
	}

	protected static void doWithConnection(ExecutableWithArg1<Connection> consumer) {
		Connection conn = null;
		try {
			conn = getConnection();
			JdbcExecutors.setCurrentConnection(conn);
			consumer.execute(conn);
		} catch (Exception e) {
			log.error(e, e.getMessage());
		} finally {
			JdbcExecutors.clearCurrentConnection();
			Jdbcs.close(conn);
		}
	}

	protected static boolean doTransaction(Connection conn, Executable runnable) {
		try {
			Jdbcs.beginTransaction(conn);
			runnable.execute();
			Jdbcs.commit(conn);
			return true;
		} catch (Throwable e) {
			log.error(e, e.getMessage());
			try {
				Jdbcs.rollback(conn);
			} catch (SQLException ex) {
				log.error(ex, ex.getMessage());
			}
			return false;
		}
	}

	protected static void tableCreate() {
		try (InputStream in = IO.getInputStream("table_create.sql", BaseTestJdbcExecutor.class);) {
			String str = IO.toString(in);
			String[] arr = str.split(";");
			doWithConnection(conn -> {
				for (String s : arr) {
					s = Strings.trimToNull(s);
					if (s == null) {
						continue;
					}
					log.info("table crate: {}", s);
					try {
						Jdbcs.update(conn, s);
					} catch (SQLException e) {
						log.error(e, e.getMessage());
					}
				}
			});
		} catch (IOException e) {
			log.error(e, e.getMessage());
		}
	}

	protected static void tableInsert() {
		try (InputStream in = IO.getInputStream("table_insert.sql", BaseTestJdbcExecutor.class);) {
			String str = IO.toString(in);
			String[] arr = str.split(";");
			doWithConnection(conn -> {
				for (String s : arr) {
					final String sql = Strings.trimToNull(s);
					if (sql == null) {
						continue;
					}
					log.info("table insert: {}", sql);
					doTransaction(conn, () -> {
						Jdbcs.update(conn, sql);
					});
				}
			});
		} catch (IOException e) {
			log.error(e, e.getMessage());
		}
	}

	protected static void tableDrop() {
		try (InputStream in = IO.getInputStream("table_drop.sql", BaseTestJdbcExecutor.class);) {
			String str = IO.toString(in);
			String[] arr = str.split(";");
			doWithConnection(conn -> {
				for (String s : arr) {
					s = Strings.trimToNull(s);
					if (s == null) {
						continue;
					}
					log.info("table drop: {}", s);
					try {
						Jdbcs.update(conn, s);
					} catch (SQLException e) {
						log.error(e, e.getMessage());
					}
				}
			});
		} catch (IOException e) {
			log.error(e, e.getMessage());
		}
	}

	protected static String toString(Object obj) {
		StringBuilder sb = new StringBuilder();
		if (obj instanceof Object[]) {
			obj = Iterables.asList((Object[]) obj);
		} else if (obj.getClass().isArray()) {
			obj = ObjectArrays.toList(obj);
		}

		if (obj instanceof Iterable) {
			Object first = null;
			int i = 0;
			for (Object o : (Iterable<?>) obj) {
				if (i == 0) {
					first = o;
				} else {
					if (i == 1) {
						sb.append("\n").append(0).append(". ").append(first);
						first = null;
					}
					sb.append("\n").append(i).append(". ").append(o);
				}
				i++;
			}
			if (first != null) {
				sb.append(first);
			}
		} else {
			sb.append(obj);
		}
		return sb.toString();
	}
}
