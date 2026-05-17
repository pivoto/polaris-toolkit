package io.polaris.mybatis.enhance.printer;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import io.polaris.core.concurrent.ThreadLocalSpec;
import io.polaris.core.concurrent.Threads;
import io.polaris.core.jdbc.sql.PreparedSql;
import io.polaris.core.time.Dates;
import io.polaris.mybatis.enhance.EnhancedConfiguration;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * @author Qt
 * @since Apr 03, 2026
 */
public class SqlRecorder {
	private static class Holder {
		private static final SqlRecorder DEFAULT = new SqlRecorder();
		private static final Map<Configuration, SqlRecorder> instances = new ConcurrentHashMap<>();
	}

	public static SqlRecorder ofDefault() {
		return Holder.DEFAULT;
	}

	public static SqlRecorder of(Configuration configuration) {
		if (Holder.DEFAULT.configuration.equals(configuration)) {
			return Holder.DEFAULT;
		}
		return Holder.instances.computeIfAbsent(configuration, SqlRecorder::new);
	}

	private final Configuration configuration;
	private final Map<Method, RecorderMapperMethodInvoker> methodCache;
	private final ThreadLocalSpec<BlockingDeque<BoundSql>> recorder = Threads.newThreadLocal();

	private SqlRecorder() {
		this(new EnhancedConfiguration());
	}

	private SqlRecorder(Configuration configuration) {
		this.configuration = configuration;
		this.methodCache = new ConcurrentHashMap<>();
	}


	@SuppressWarnings("unchecked")
	public <M> M get(Class<M> mapperInterface) {
		if (!configuration.hasMapper(mapperInterface)) {
			configuration.addMapper(mapperInterface);
		}
		Consumer<BoundSql> consumer = this::record;
		RecorderMapperProxy<M> mapperProxy = new RecorderMapperProxy<>(mapperInterface, methodCache, configuration, consumer);
		return (M) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
	}


	private void record(BoundSql boundSql) {
		BlockingDeque<BoundSql> deque = recorder.get();
		if (deque == null) {
			deque = new java.util.concurrent.LinkedBlockingDeque<>();
			recorder.set(deque);
		}
		deque.offer(boundSql);
	}

	public BoundSql pollFirstBoundSql() {
		BlockingDeque<BoundSql> deque = recorder.get();
		if (deque == null) {
			return null;
		}
		return deque.pollFirst();
	}

	public BoundSql pollLastBoundSql() {
		BlockingDeque<BoundSql> deque = recorder.get();
		if (deque == null) {
			return null;
		}
		return deque.pollLast();
	}

	public List<BoundSql> pollAllBoundSql() {
		BlockingDeque<BoundSql> deque = recorder.get();
		if (deque == null) {
			return Collections.emptyList();
		}
		List<BoundSql> list = new ArrayList<>();
		deque.drainTo(list);
		return list;
	}

	public PreparedSql pollFirstPreparedSql() {
		return asPreparedSql(pollFirstBoundSql());
	}

	public PreparedSql pollLastPreparedSql() {
		return asPreparedSql(pollLastBoundSql());
	}

	public List<PreparedSql> pollALlPreparedSql() {
		List<BoundSql> list = pollAllBoundSql();
		List<PreparedSql> result = new ArrayList<>();
		for (BoundSql boundSql : list) {
			result.add(asPreparedSql(boundSql));
		}
		return result;
	}

	public String pollFirstSql() {
		return asSqlString(pollFirstBoundSql());
	}

	public String pollLastSql() {
		return asSqlString(pollLastBoundSql());
	}

	public List<String> pollALlSql() {
		List<BoundSql> list = pollAllBoundSql();
		List<String> result = new ArrayList<>();
		for (BoundSql boundSql : list) {
			result.add(asSqlString(boundSql));
		}
		return result;
	}

	public String pollALlSqlText() {
		List<BoundSql> list = pollAllBoundSql();
		StringBuilder sb = new StringBuilder();
		for (BoundSql boundSql : list) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(asSqlString(boundSql));
		}
		return sb.toString();
	}


	public PreparedSql asPreparedSql(BoundSql boundSql){
		String sql = boundSql.getSql();
		TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
		Object parameterObject = boundSql.getParameterObject();
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		List<Object> values = new ArrayList<>();
		for (ParameterMapping mapping : parameterMappings) {
			String propertyName = mapping.getProperty();
			Object value;
			if (boundSql.hasAdditionalParameter(propertyName)) {
				value = boundSql.getAdditionalParameter(propertyName);
			} else if (parameterObject == null) {
				value = null;
			} else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
				value = parameterObject;
			} else {
				MetaObject metaObject = configuration.newMetaObject(parameterObject);
				value = metaObject.getValue(propertyName);
			}
			values.add(value);
		}
		return new PreparedSql(sql, values);
	}

	public String asSqlString(BoundSql boundSql) {
		String sql = boundSql.getSql();
		char[] chs = sql.toCharArray();

		TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
		Object parameterObject = boundSql.getParameterObject();
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		Iterator<ParameterMapping> it = parameterMappings.iterator();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < chs.length; i++) {
			char ch = chs[i];
			if (ch == '\'') {
				i++;
				sb.append(ch).append(chs[i]);
			} else if (ch == '?') {
				if (it.hasNext()) {
					ParameterMapping mapping = it.next();
					String propertyName = mapping.getProperty();
					Object value;
					if (boundSql.hasAdditionalParameter(propertyName)) {
						value = boundSql.getAdditionalParameter(propertyName);
					} else if (parameterObject == null) {
						value = null;
					} else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
						value = parameterObject;
					} else {
						MetaObject metaObject = configuration.newMetaObject(parameterObject);
						value = metaObject.getValue(propertyName);
					}
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


}
