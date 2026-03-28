package io.polaris.mybatis.interceptor;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import javax.annotation.Nullable;

import io.polaris.core.reflect.Methods;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.tuple.Ref;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * @author Qt
 * @since Feb 27, 2026
 */
@Slf4j
public class InterceptorHelper {

	private static volatile InterceptorHelper instance;
	private volatile Function<RoutingStatementHandler, StatementHandler> getDelegate;
	private volatile Function<BaseStatementHandler, MappedStatement> getMappedStatement;
	private volatile Function<BoundSql, Map<String, Object>> getAdditionalParameters;
	private final Map<String, Ref<Method>> statementMethodCache = new ConcurrentHashMap<>();

	private InterceptorHelper() {
	}

	public static InterceptorHelper instance() {
		if (instance == null) {
			synchronized (InterceptorHelper.class) {
				if (instance == null) {
					instance = new InterceptorHelper();
				}
			}
		}
		return instance;
	}

	@Nullable
	public Method getStatementMethod(String statementId) {
		Ref<Method> ref = statementMethodCache.get(statementId);
		if (ref == null) {
			synchronized (statementMethodCache) {
				ref = statementMethodCache.get(statementId);
				if (ref == null) {
					try {
						int idx = statementId.lastIndexOf(".");
						String mapperClassName = statementId.substring(0, idx);
						String mapperMethodName = statementId.substring(idx + 1);
						Class<?> mapperClass = Class.forName(mapperClassName);
						List<Method> methods = Reflects.getPublicMethods(mapperClass, m ->
							!m.isDefault() && !Modifier.isStatic(m.getModifiers())
								&& mapperMethodName.equals(m.getName()));
						if (methods.size() == 1) {
							ref = Ref.of(methods.get(0));
						}
					} catch (Exception e) {
						log.warn("", e);
					}
					if (ref == null) {
						ref = Ref.of(null);
					}
					statementMethodCache.put(statementId, ref);
				}
			}
		}
		return ref.get();
	}


	@SuppressWarnings("unchecked")
	private <O, F> Function<O, F> getPrivateGetter(Class<O> clazz, String fieldName, Class<F> fieldType) {
		try {
			MethodHandle methodHandle = Methods.findPrivateGetter(clazz, fieldName, fieldType);
			Field field = Reflects.getField(clazz, fieldName);
			Reflects.setAccessible(field);
			return handler -> {
				try {
					return (F) methodHandle.invoke(handler);
				} catch (Throwable e1) {
					try {
						if (field == null) {
							return null;
						}
						return (F) field.get(handler);
					} catch (Throwable e2) {
						log.warn("", e2);
						return null;
					}
				}
			};
		} catch (Exception e) {
			Field field = Reflects.getField(clazz, fieldName);
			Reflects.setAccessible(field);
			return handler -> {
				try {
					if (field == null) {
						return null;
					}
					return (F) field.get(handler);
				} catch (Throwable ex) {
					log.warn("", ex);
					return null;
				}
			};
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getAdditionalParameters(BoundSql boundSql) {
		if (getAdditionalParameters == null) {
			synchronized (this) {
				if (getAdditionalParameters == null) {
					@SuppressWarnings("rawtypes")
					Class<Map<String, Object>> fieldType = (Class) Map.class;
					getAdditionalParameters = getPrivateGetter(BoundSql.class, "additionalParameters", fieldType);
				}
			}
		}
		return getAdditionalParameters.apply(boundSql);
	}

	public MappedStatement getMappedStatement(BaseStatementHandler statementHandler) {
		if (getMappedStatement == null) {
			synchronized (this) {
				if (getMappedStatement == null) {
					getMappedStatement = getPrivateGetter(BaseStatementHandler.class, "mappedStatement", MappedStatement.class);
				}
			}
		}
		return getMappedStatement.apply(statementHandler);
	}

	public StatementHandler getDelegate(RoutingStatementHandler statementHandler) {
		if (getDelegate == null) {
			synchronized (this) {
				if (getDelegate == null) {
					getDelegate = getPrivateGetter(RoutingStatementHandler.class, "mappedStatement", StatementHandler.class);
				}
			}
		}
		return getDelegate.apply(statementHandler);
	}
}
