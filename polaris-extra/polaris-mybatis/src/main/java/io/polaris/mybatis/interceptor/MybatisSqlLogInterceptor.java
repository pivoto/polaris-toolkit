package io.polaris.mybatis.interceptor;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.polaris.core.concurrent.ThreadLocalSpec;
import io.polaris.core.concurrent.Threads;
import io.polaris.core.lang.bean.BeanMap;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.reflect.Methods;
import io.polaris.core.regex.Patterns;
import io.polaris.core.tuple.Tuple3;
import io.polaris.mybatis.support.InterceptorHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.jdbc.BaseJdbcLogger;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;

/**
 * @author Qt
 * @since Jun 21, 2024
 */
@Intercepts({
	@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
	@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
	@Signature(type = StatementHandler.class, method = "queryCursor", args = {Statement.class}),
	@Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
	@Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})
})
@Slf4j
public class MybatisSqlLogInterceptor implements Interceptor {

	private static final ThreadLocalSpec<Tuple3<StatementHandler, Method, Long>> local = Threads.newThreadLocal();
	//	private static Field columnValuesField;
	private static MethodHandle columnValuesField;

	static {
		try {
			@SuppressWarnings("unchecked")
			Class<List<Object>> fieldType = (Class) List.class;
			columnValuesField = Methods.findPrivateGetter(BaseJdbcLogger.class, "columnValues", fieldType);
		} catch (Exception ignored) {
		}
		/* try {
			columnValuesField = BaseJdbcLogger.class.getDeclaredField("columnValues");
			columnValuesField.setAccessible(true);
		} catch (Exception ignored) {
		} */
	}

	@Setter
	@Getter
	private boolean showParameters = true;
	@Setter
	@Getter
	private String errorSqlId = "ERRORSQLID";

	public MybatisSqlLogInterceptor() {
	}


	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object target = invocation.getTarget();
		Method invocationMethod = invocation.getMethod();
		StatementHandler statementHandler = (StatementHandler) target;
		long startTime = System.currentTimeMillis();

		if ("prepare".equals(invocationMethod.getName())) {
			try {
				Object rs = invocation.proceed();
				local.set(Tuple3.of(statementHandler, invocationMethod, startTime));
				return rs;
			} catch (Throwable e) {
				local.remove();
				BoundSql boundSql = statementHandler.getBoundSql();
				String sql = boundSql.getSql();
				sql = formatCrLf(sql);
				String sqlId = getSqlId(statementHandler);
				long elapsedTime = System.currentTimeMillis() - startTime;
				log.error("耗时:{}ms sqlId:{} 条数:NaN 执行SQL:{}", elapsedTime, sqlId, sql);
				throw e;
			}
		} else {
			String sqlId = getSqlId(statementHandler);
			List<Object> paramList = null;
			if (showParameters) {
				paramList = getParamValuesByStatement((Statement) invocation.getArgs()[0]);
			}
			boolean success = false;
			String rows = "NaN";
			try {
				Object o = invocation.proceed();
				success = true;
				if (o instanceof Integer) {
					rows = String.valueOf(((Integer) o).intValue());
				} else if (o instanceof Collection) { // fix批处理时返回值空指针
					rows = String.valueOf(((Collection<?>) o).size());
				} else if (o instanceof Number) {
					rows = String.valueOf(((Number) o).intValue());
				} else if (o instanceof Cursor) {
					rows = "-1";
				}
				return o;
			} finally {
				Tuple3<StatementHandler, Method, Long> tuple = local.get();
				if (tuple != null) {
					long endTime = System.currentTimeMillis();
					local.remove();
					if (tuple.getFirst() == statementHandler && tuple.getSecond() == invocationMethod) {
						startTime = tuple.getThird();
					}
					long elapsedTime = endTime - startTime;
					BoundSql boundSql = statementHandler.getBoundSql();
					String sql = boundSql.getSql();
					sql = formatCrLf(sql);
					if (paramList != null) {
						sql = formatSqlWithParams(sql, paramList);
						if (success) {
							if (log.isDebugEnabled()) {
								log.debug("耗时:{}ms sqlId:{} 条数:{} 执行SQL:{}", elapsedTime, sqlId, rows, sql);
							}
						} else {
							if (log.isErrorEnabled()) {
								log.error("耗时:{}ms sqlId:{} 条数:{} 执行SQL:{}", elapsedTime, sqlId, rows, sql);
							}
						}
					} else {
						if (success) {
							if (log.isDebugEnabled()) {
								log.debug("耗时:{}ms sqlId:{} 条数:{} 执行SQL:{} 映射:{} 参数:{}", elapsedTime, sqlId, rows, sql, boundSql.getParameterMappings(), toParameterString(boundSql.getParameterObject()));
							}
						} else {
							if (log.isErrorEnabled()) {
								log.error("耗时:{}ms sqlId:{} 条数:{} 执行SQL:{} 映射:{} 参数:{}", elapsedTime, sqlId, rows, sql, boundSql.getParameterMappings(), toParameterString(boundSql.getParameterObject()));
							}
						}
					}
				}
			}
		}
	}


	private String toParameterString(Object parameterObject) {
		StringBuilder sb = new StringBuilder();
		toParameterString(parameterObject, sb);
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	private void toParameterString(Object parameterObject, StringBuilder sb) {
		if (parameterObject == null) {
			sb.append("null");
			return;
		}
		if (parameterObject instanceof Map) {
			sb.append("{");
			for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) parameterObject).entrySet()) {
				sb.append(entry.getKey()).append("=");
				toParameterString(entry.getValue(), sb);
				sb.append(",");
			}
			if (sb.charAt(sb.length() - 1) == ',') {
				sb.deleteCharAt(sb.length() - 1);
			}
			sb.append("}");
			return;
		}
		if (parameterObject instanceof Collection) {
			sb.append("[");
			for (Object obj : (Collection<Object>) parameterObject) {
				toParameterString(obj, sb);
				sb.append(",");
			}
			if (sb.charAt(sb.length() - 1) == ',') {
				sb.deleteCharAt(sb.length() - 1);
			}
			sb.append("]");
			return;
		}
		if (parameterObject instanceof char[]) {
			sb.append("char[").append(((char[]) parameterObject).length).append("]");
			return;
		}
		if (parameterObject instanceof byte[]) {
			sb.append("byte[").append(((byte[]) parameterObject).length).append("]");
			return;
		}
		if (parameterObject instanceof Object[]) {
			sb.append("Object[").append(((Object[]) parameterObject).length).append("]");
			return;
		}
		if (parameterObject.getClass().isArray()) {
			sb.append("array[").append(Array.getLength(parameterObject)).append("]");
			return;
		}
		if (parameterObject.getClass().isPrimitive()) {
			sb.append(parameterObject);
			return;
		}
		if (parameterObject instanceof String) {
			sb.append("\"");
			sb.append(parameterObject);
			sb.append("\"");
			return;
		}
		if (parameterObject instanceof Boolean
			|| parameterObject instanceof Number
			|| parameterObject instanceof Date
			|| parameterObject instanceof Enum
			|| parameterObject instanceof Character
			|| parameterObject instanceof UUID
			|| parameterObject instanceof Class
		) {
			sb.append(parameterObject);
			return;
		}
		if (Beans.isBeanClass(parameterObject.getClass())) {
			BeanMap<Object> map = Beans.newBeanMap(parameterObject);
			toParameterString(map, sb);
			return;
		}
		sb.append(parameterObject);
	}

	@Override
	public Object plugin(Object target) {
		if (target instanceof StatementHandler) {
			return Plugin.wrap(target, this);
		} else {
			return target;
		}
	}

	private MappedStatement getMappedStatement(StatementHandler statementHandler) {
		if (statementHandler instanceof BaseStatementHandler) {
			try {
				MappedStatement ms = InterceptorHelper.instance().getMappedStatement((BaseStatementHandler) statementHandler);
				if (ms == null) {
					ParameterHandler ph = statementHandler.getParameterHandler();
					if (ph instanceof DefaultParameterHandler) {
						ms = InterceptorHelper.instance().getMappedStatement((DefaultParameterHandler) ph);
					}
				}
				return ms;
			} catch (Exception ignored) {
			}
		} else {
			ParameterHandler ph = statementHandler.getParameterHandler();
			if (ph instanceof DefaultParameterHandler) {
				return InterceptorHelper.instance().getMappedStatement((DefaultParameterHandler) ph);
			}
		}
		return null;
	}

	private String getSqlId(StatementHandler statementHandler) {
		try {
			MappedStatement ms = getMappedStatement(statementHandler);
			if (ms != null) {
				String id = ms.getId();
				if (id.contains(".")) {
					String pre = id.substring(0, id.lastIndexOf("."));
					if (pre.contains(".")) {
						id = pre.substring(pre.lastIndexOf(".") + 1) + id.substring(id.lastIndexOf("."));
					}
				}
				return id;
			}
		} catch (Exception ignored) {
		}
		return errorSqlId;
	}

	@SuppressWarnings("unchecked")
	private List<Object> getParamValuesByStatement(Statement sm) {
		try {
			if (Proxy.isProxyClass(sm.getClass()) && columnValuesField != null) {
				InvocationHandler handler = Proxy.getInvocationHandler(sm);
				if (handler instanceof BaseJdbcLogger) {
					BaseJdbcLogger logger = (BaseJdbcLogger) handler;
					List<Object> list = (List<Object>) columnValuesField.invokeExact(logger);
					//List<Object> list = (List<Object>) columnValuesField.get(logger);
					if (list != null) {
						list = new ArrayList<>(list);
					}
					return list;
				}
			}
		} catch (Throwable ignored) {
		}
		return null;
	}


	private String formatCrLf(String sql) {
		sql = sql.replace('\r', ' ').replace('\n', ' ');
		return sql;
	}

	private String formatSqlWithParams(String sql, List<Object> paramValue) {
		if (paramValue == null || paramValue.isEmpty()) {
			return sql;
		}
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Patterns.getPattern("\\?");
		Matcher matcher = pattern.matcher(sql);
		int i = 0;
		while (matcher.find()) {
			if (i < paramValue.size()) {
				Object value = paramValue.get(i);
				if (value instanceof String || value instanceof Character) {
					matcher.appendReplacement(sb, ("'" + value + "'")
						.replace("\\", "\\\\").replace("$", "\\$"));
				} else {
					matcher.appendReplacement(sb, String.valueOf(value)
						.replace("\\", "\\\\").replace("$", "\\$"));
				}
			} else {
				matcher.appendReplacement(sb, "?");
			}
			i++;
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

}
