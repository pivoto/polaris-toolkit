package io.polaris.core.jdbc.executor;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.polaris.core.concurrent.Threads;
import io.polaris.core.map.Maps;


/**
 * @author Qt
 * @since Feb 05, 2024
 */
@SuppressWarnings("unchecked")
public class JdbcExecutors {
	private static final ThreadLocal<Connection> currentConnection = new ThreadLocal<>();
	private static final Map<Class<?>, Object> executorCache = Maps.newSoftMap(new ConcurrentHashMap<>());
	private static final Map<Class<?>, StatefulJdbcExecutor<?>> statefulJdbcExecutorCache = Maps.newSoftMap(new ConcurrentHashMap<>());

	@SuppressWarnings({"ConstantValue"})
	public static <T> T getExecutor(Class<T> interfaceClass) {
		T rs = null;
		// 防止因对象回收后导致SoftMap结果丢失，尝试多次获取
		while ((rs = (T) executorCache.computeIfAbsent(interfaceClass, JdbcExecutors::newExecutor)) == null) {
			Threads.sleep(0);
		}
		return rs;
	}

	public static <T> T newExecutor(Class<T> interfaceClass) {
		JdbcExecutor<T> handler = new JdbcExecutor<>(interfaceClass);
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass, InvocationHandlerHolder.class}, handler);
	}

	@SuppressWarnings("ConstantValue")
	public static <T> T getExecutor(Class<T> interfaceClass, Connection connection, boolean batch) {
		// 如果没有连接或批处理参数，则使用无状态执行器
		if (connection == null && !batch) {
			return getExecutor(interfaceClass);
		}

		StatefulJdbcExecutor<T> statefulJdbcExecutor = null;
		// 防止因对象回收后导致SoftMap结果丢失，尝试多次获取
		while ((statefulJdbcExecutor = (StatefulJdbcExecutor<T>) statefulJdbcExecutorCache.computeIfAbsent(interfaceClass, (k) -> new StatefulJdbcExecutor<>(interfaceClass, null, false))) == null) {
			Threads.sleep(0);
		}
		StatefulJdbcExecutor<T> handler = new StatefulJdbcExecutor<>(statefulJdbcExecutor, connection, batch);
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass, InvocationHandlerHolder.class, JdbcBatchExecutor.class}, handler);
	}

	public static <T> T newExecutor(Class<T> interfaceClass, Connection connection, boolean batch) {
		// 如果没有连接或批处理参数，则使用无状态执行器
		if (connection == null && !batch) {
			return newExecutor(interfaceClass);
		}
		StatefulJdbcExecutor<T> handler = new StatefulJdbcExecutor<>(interfaceClass, connection, batch);
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass, InvocationHandlerHolder.class, JdbcBatchExecutor.class}, handler);
	}


	public static <T> void doBatch(Class<T> interfaceClass, Connection connection, Consumer<T> consumer) {
		T executor = null;
		try {
			executor = getExecutor(interfaceClass, connection, true);
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
			T executor = getExecutor(interfaceClass);
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
