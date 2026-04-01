package io.polaris.mybatis.support;

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
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

/**
 * @author Qt
 * @since Feb 27, 2026
 */
@Slf4j
public class InterceptorHelper {

	private static volatile InterceptorHelper instance;
	private volatile Function<RoutingStatementHandler, StatementHandler> getDelegate;
	private volatile Function<BaseStatementHandler, MappedStatement> getMappedStatement;
	private volatile Function<DefaultParameterHandler, MappedStatement> getMappedStatement2;
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
	public Map<String, Object> getAdditionalParameters(BoundSql boundSql) {
		if (getAdditionalParameters == null) {
			synchronized (this) {
				if (getAdditionalParameters == null) {
					Field field = Reflects.getField(BoundSql.class, "additionalParameters");
					Reflects.setAccessible(field);
					try {
						@SuppressWarnings("rawtypes")
						Class<Map<String, Object>> fieldType = (Class) Map.class;
						MethodHandle methodHandle = Methods.findPrivateGetter(BoundSql.class, "additionalParameters", fieldType);
						getAdditionalParameters = instance -> {
							try {
								return (Map<String, Object>) methodHandle.invokeExact((BoundSql) instance);
							} catch (Throwable e1) {
								try {
									if (field == null) {
										return null;
									}
									return (Map<String, Object>) field.get(instance);
								} catch (Throwable ex) {
									log.warn("", ex);
									return null;
								}
							}
						};
					} catch (Throwable e) {
						getAdditionalParameters = instance -> {
							try {
								if (field == null) {
									return null;
								}
								return (Map<String, Object>) field.get(instance);
							} catch (Throwable ex) {
								log.warn("", ex);
								return null;
							}
						};
					}
				}
			}
		}
		return getAdditionalParameters.apply(boundSql);
	}

	public MappedStatement getMappedStatement(BaseStatementHandler statementHandler) {
		if (getMappedStatement == null) {
			synchronized (this) {
				if (getMappedStatement == null) {
					Field field = Reflects.getField(BaseStatementHandler.class, "mappedStatement");
					Reflects.setAccessible(field);
					try {
						MethodHandle methodHandle = Methods.findPrivateGetter(BaseStatementHandler.class, "mappedStatement", MappedStatement.class);
						getMappedStatement = instance -> {
							try {
								return (MappedStatement) methodHandle.invokeExact((BaseStatementHandler) instance);
							} catch (Throwable e1) {
								try {
									if (field == null) {
										return null;
									}
									return (MappedStatement) field.get(instance);
								} catch (Throwable ex) {
									log.warn("", ex);
									return null;
								}
							}
						};
					} catch (Throwable e) {
						getMappedStatement = instance -> {
							try {
								if (field == null) {
									return null;
								}
								return (MappedStatement) field.get(instance);
							} catch (Throwable ex) {
								log.warn("", ex);
								return null;
							}
						};
					}
				}
			}
		}
		return getMappedStatement.apply(statementHandler);
	}


	public MappedStatement getMappedStatement(DefaultParameterHandler statementHandler) {
		if (getMappedStatement2 == null) {
			synchronized (this) {
				if (getMappedStatement2 == null) {
					Field field = Reflects.getField(DefaultParameterHandler.class, "mappedStatement");
					Reflects.setAccessible(field);
					try {
						MethodHandle methodHandle = Methods.findPrivateGetter(DefaultParameterHandler.class, "mappedStatement", MappedStatement.class);
						getMappedStatement2 = instance -> {
							try {
								return (MappedStatement) methodHandle.invokeExact((DefaultParameterHandler) instance);
							} catch (Throwable e1) {
								try {
									if (field == null) {
										return null;
									}
									return (MappedStatement) field.get(instance);
								} catch (Throwable ex) {
									log.warn("", ex);
									return null;
								}
							}
						};
					} catch (Throwable e) {
						getMappedStatement2 = instance -> {
							try {
								if (field == null) {
									return null;
								}
								return (MappedStatement) field.get(instance);
							} catch (Throwable ex) {
								log.warn("", ex);
								return null;
							}
						};
					}
				}
			}
		}
		return getMappedStatement2.apply(statementHandler);
	}

	public StatementHandler getDelegate(RoutingStatementHandler statementHandler) {
		if (getDelegate == null) {
			synchronized (this) {
				if (getDelegate == null) {
					Field field = Reflects.getField(BaseStatementHandler.class, "delegate");
					Reflects.setAccessible(field);
					try {
						MethodHandle methodHandle = Methods.findPrivateGetter(RoutingStatementHandler.class, "delegate", StatementHandler.class);
						getDelegate = instance -> {
							try {
								return (StatementHandler) methodHandle.invokeExact((RoutingStatementHandler) instance);
							} catch (Throwable e1) {
								try {
									if (field == null) {
										return null;
									}
									return (StatementHandler) field.get(instance);
								} catch (Throwable ex) {
									log.warn("", ex);
									return null;
								}
							}
						};
					} catch (Throwable e) {
						getDelegate = instance -> {
							try {
								if (field == null) {
									return null;
								}
								return (StatementHandler) field.get(instance);
							} catch (Throwable ex) {
								log.warn("", ex);
								return null;
							}
						};
					}
				}
			}
		}
		return getDelegate.apply(statementHandler);
	}
}
