package io.polaris.core.jdbc.executor;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.polaris.core.map.Maps;


/**
 * @author Qt
 * @since 1.8,  Feb 05, 2024
 */
@SuppressWarnings("unused")
public class JdbcExecutors {
	private static final ThreadLocal<Connection> currentConnection = new ThreadLocal<>();
	private static final Map<Class<?>, Object> executorCache = Maps.newSoftMap(new ConcurrentHashMap<>());

	@SuppressWarnings("unchecked")
	public static <T> T createExecutor(Class<T> interfaceClass) {
		return (T) executorCache.computeIfAbsent(interfaceClass, JdbcExecutors::doCreateExecutor);
	}

	@SuppressWarnings("unchecked")
	private static <T> T doCreateExecutor(Class<T> interfaceClass) {
		JdbcExecutor<T> handler = new JdbcExecutor<>(interfaceClass);
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass, InvocationHandlerHolder.class}, handler);
	}

	@SuppressWarnings("unchecked")
	public static <T> T createExecutor(Class<T> interfaceClass, Connection connection, boolean batch) {
		StatefulJdbcExecutor<T> handler = new StatefulJdbcExecutor<>(interfaceClass, connection, batch);
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass, InvocationHandlerHolder.class, JdbcBatchExecutor.class}, handler);
	}


	public static <T> void doBatch(Class<T> interfaceClass, Connection connection, Consumer<T> consumer) {
		T executor = null;
		try {
			executor = createExecutor(interfaceClass, connection, true);
			consumer.accept(executor);
		} finally {
			if (executor instanceof JdbcBatchExecutor) {
				try {
					((JdbcBatchExecutor) executor).flush();
				} catch (SQLException ignored) {
				}
			}
		}
	}

	public static <T> void doWithConnection(Class<T> interfaceClass, Connection connection, Consumer<T> consumer) {
		setCurrentConnection(connection);
		try {
			T executor = createExecutor(interfaceClass);
			consumer.accept(executor);
		} finally {
			clearCurrentConnection();
		}
	}

	public static <T> T doWithConnection(Connection connection, Supplier<T> executor) {
		setCurrentConnection(connection);
		try {
			return executor.get();
		} finally {
			clearCurrentConnection();
		}
	}

	public static void doWithConnection(Connection connection, Runnable executor) {
		setCurrentConnection(connection);
		try {
			executor.run();
		} finally {
			clearCurrentConnection();
		}
	}

	public static Connection getCurrentConnection() {
		return currentConnection.get();
	}

	public static void setCurrentConnection(Connection connection) {
		currentConnection.set(connection);
	}

	public static void clearCurrentConnection() {
		currentConnection.remove();
	}


}
