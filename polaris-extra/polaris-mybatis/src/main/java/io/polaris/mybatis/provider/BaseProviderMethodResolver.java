package io.polaris.mybatis.provider;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;
import io.polaris.core.lang.bean.BeanMap;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.tuple.Ref;
import io.polaris.core.tuple.Tuple2;
import io.polaris.mybatis.annotation.MapperEntity;
import io.polaris.mybatis.annotation.WithLogicDeleted;
import io.polaris.mybatis.mapper.EntityMapper;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

/**
 * @author Qt
 * @since Sep 11, 2023
 */
public abstract class BaseProviderMethodResolver implements ProviderMethodResolver {

	static final ThreadLocal<Tuple2<Object, Map<String, Object>>> ADDITIONAL_PARAMETERS = new ThreadLocal<>();
	static final ThreadLocal<Boolean> QUERY_EXISTS_BY_COUNT = new ThreadLocal<>();
	static final Map<Tuple2<Method, Class<?>>, Boolean> LOGIC_DELETED_MAP = new ConcurrentHashMap<>();
	static final Map<Tuple2<Method, Class<?>>, Ref<Class<?>>> ENTITY_CLASS_MAP = new ConcurrentHashMap<>();


	public static boolean isQueryExistsByCount() {
		Boolean bool = QUERY_EXISTS_BY_COUNT.get();
		return bool == null || bool;
	}

	public static void setQueryExistsByCount(boolean withoutCount) {
		QUERY_EXISTS_BY_COUNT.set(withoutCount);
	}

	public static void clearQueryExistsByCount() {
		QUERY_EXISTS_BY_COUNT.remove();
	}

	static void bindAdditionalParameters(Object parameterObject, Map<String, Object> additionalParameters) {
		ADDITIONAL_PARAMETERS.set(Tuple2.of(parameterObject, additionalParameters));
	}

	static void clearAdditionalParameters() {
		ADDITIONAL_PARAMETERS.remove();
	}

	static BoundSql getBoundSql(SqlSource sqlSource, Object parameterObject) {
		try {
			BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
			Tuple2<Object, Map<String, Object>> tuple = ADDITIONAL_PARAMETERS.get();
			// 存在扩展参数则创建一个扩展的SqlSource以动态追加
			if (tuple != null && tuple.getFirst() == parameterObject) {
				Map<String, Object> params = tuple.getSecond();
				if (params != null && !params.isEmpty()) {
					// 追加额外参数
					tuple.getSecond().forEach(boundSql::setAdditionalParameter);
				}
			}
			return boundSql;
		} finally {
			// 用完即清理
			ADDITIONAL_PARAMETERS.remove();
		}
	}

	@Override
	public Method resolveMethod(ProviderContext context) {
		String methodName = context.getMapperMethod().getName();
		try {
			return getClass().getMethod(methodName, Map.class, ProviderContext.class);
		} catch (NoSuchMethodException ignore) {
		}
		try {
			return getClass().getMethod("provideSql", Map.class, ProviderContext.class);
		} catch (NoSuchMethodException ignore) {
		}
		try {
			return getClass().getMethod(methodName, Object.class, ProviderContext.class);
		} catch (NoSuchMethodException ignore) {
		}
		try {
			return getClass().getMethod("provideSql", Object.class, ProviderContext.class);
		} catch (NoSuchMethodException ignore) {
		}
		return ProviderMethodResolver.super.resolveMethod(context);
	}


	protected static Class<?> getEntityClass(ProviderContext context) {
		Method mapperMethod = context.getMapperMethod();
		Class<?> mapperType = context.getMapperType();

		Ref<Class<?>> ref = ENTITY_CLASS_MAP.computeIfAbsent(Tuple2.of(mapperMethod, context.getMapperType()),
			k -> Ref.of(innerGetEntityClass(mapperMethod, mapperType)));
		Class<?> entityClass = ref.get();
		if (entityClass != null) {
			return entityClass;
		}
		throw new IllegalStateException("未知实体类型！");
	}

	@Nullable
	private static Class<?> innerGetEntityClass(Method mapperMethod, Class<?> mapperType) {
		// 从方法上获取
		MapperEntity declared = mapperMethod.getAnnotation(MapperEntity.class);
		Class<?> entityClass = null;
		if (declared != null) {
			entityClass = declared.entity();
			if (entityClass != null && entityClass != Object.class) {
				return entityClass;
			}
		}
		// 从Mapper接口类上获取
		declared = mapperType.getAnnotation(MapperEntity.class);
		if (declared != null) {
			entityClass = declared.entity();
			if (entityClass != null && entityClass != Object.class) {
				return entityClass;
			}
		}
		// 从方法所在接口类上获取
		Class<?> declaringClass = mapperMethod.getDeclaringClass();
		if (declaringClass != mapperType) {
			declared = declaringClass.getAnnotation(MapperEntity.class);
			if (declared != null) {
				entityClass = declared.entity();
				if (entityClass != null && entityClass != Object.class) {
					return entityClass;
				}
			}
		}
		// 从基类泛型中获取
		if (EntityMapper.class.isAssignableFrom(mapperType)) {
			Type actualType = JavaType.of(mapperType).getActualType(EntityMapper.class, 0);
			entityClass = Types.getClass(actualType);
			if (entityClass != null && entityClass != Object.class) {
				return entityClass;
			}
		}
		return null;
	}

	protected static boolean withLogicDeleted(Map<String, Object> bindings, ProviderContext context) {
		try {
			// fix for MapperMethod.ParamMap.get()
			if (bindings.containsKey(BindingKeys.WITH_LOGIC_DELETED)) {
				Object sld = bindings.get(BindingKeys.WITH_LOGIC_DELETED);
				if (sld != null) {
					if (sld instanceof Boolean) {
						return (Boolean) sld;
					}
				}
			}
		} catch (Exception ignored) {
		}
		return withLogicDeleted(context);
	}

	protected static boolean withLogicDeleted(ProviderContext context) {
		Method mapperMethod = context.getMapperMethod();
		Class<?> mapperType = context.getMapperType();

		return LOGIC_DELETED_MAP.computeIfAbsent(Tuple2.of(mapperMethod, context.getMapperType()),
			k -> innerGetWithLogicDeleted(mapperMethod, mapperType));
	}

	private static boolean innerGetWithLogicDeleted(Method mapperMethod, Class<?> mapperType) {
		// 从方法上获取
		WithLogicDeleted declared = mapperMethod.getAnnotation(WithLogicDeleted.class);
		Class<?> entityClass = null;
		if (declared != null) {
			return declared.value();
		}
		// 从Mapper接口类上获取
		declared = mapperType.getAnnotation(WithLogicDeleted.class);
		if (declared != null) {
			return declared.value();
		}
		// 从方法所在接口类上获取
		Class<?> declaringClass = mapperMethod.getDeclaringClass();
		if (declaringClass != mapperType) {
			declared = declaringClass.getAnnotation(WithLogicDeleted.class);
			if (declared != null) {
				return declared.value();
			}
		}
		return false;
	}


	protected static String provideSql(Object parameterObject, ProviderContext context, BiFunction<Map<String, Object>, ProviderContext, String> function) {
		try {
			Map<String, Object> map = toParameterBindings(context.getMapperMethod(), parameterObject);
			String sql = function.apply(map, context);
			return sql;
		} catch (RuntimeException e) {
			clearAdditionalParameters();
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	protected static Map<String, Object> toParameterBindings(Method mapperMethod, Object parameterObject) {
		if (parameterObject instanceof Map) {
			return (Map<String, Object>) parameterObject;
		} else {
			if (!hasProviderSqlSourceDriver(mapperMethod)) {
				if (parameterObject == null) {
					// 参数为空时，使用空MAP，不接收任何绑定
					return Collections.emptyMap();
				}
				throw new IllegalArgumentException("请使用Map类型参数或明确声明参数键名");
			}
			return toParameterBindings(parameterObject);
		}
	}

	private static boolean hasProviderSqlSourceDriver(Method mapperMethod) {
		Lang lang = mapperMethod.getAnnotation(Lang.class);
		if (lang == null) {
			return false;
		}
		return ProviderSqlSourceDriver.class == lang.value();
	}

	/**
	 * 将原Mybatis参数转换为Map类型以便在自定义SqlProvider中可进行额外的动态参数绑定。
	 * 对于原参数是Map类型，则直接返回。
	 * 负作用是如果此Map是只读的，则在添加额外参数键值时会报错。
	 * 对于原则数是非Map类型，则创建一个Map并添加此参数的所有可用属性。
	 *
	 * @param parameterObject
	 * @return
	 */
	@SuppressWarnings({"all"})
	private static Map<String, Object> toParameterBindings(Object parameterObject) {
		if (parameterObject instanceof Map) {
			return (Map) parameterObject;
		}
		Map<String, Object> additionalParameters = new HashMap<>();
		if (parameterObject != null) {
			BeanMap<Object> beanMap = Beans.newBeanMap(parameterObject);
			additionalParameters.putAll(beanMap);
		}
		bindAdditionalParameters(parameterObject, additionalParameters);
		return additionalParameters;
	}

}
