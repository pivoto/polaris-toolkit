package io.polaris.mybatis.provider;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;
import io.polaris.core.lang.bean.BeanMap;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.tuple.Tuple2;
import io.polaris.mybatis.annotation.MapperEntity;
import io.polaris.mybatis.mapper.EntityMapper;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;

/**
 * @author Qt
 * @since  Sep 11, 2023
 */
public abstract class BaseProviderMethodResolver implements ProviderMethodResolver {

	static final ThreadLocal<Tuple2<Object, Map<String, Object>>> ADDITIONAL_PARAMETERS = new ThreadLocal<>();

	static void bindAdditionalParameters(Object parameterObject, Map<String, Object> additionalParameters) {
		ADDITIONAL_PARAMETERS.set(Tuple2.of(parameterObject, additionalParameters));
	}

	static void clearAdditionalParameters() {
		ADDITIONAL_PARAMETERS.remove();
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
		MapperEntity declared = mapperMethod.getAnnotation(MapperEntity.class);
		if (declared != null) {
			return declared.entity();
		}
		Class<?> entityClass = null;
		Class<?> mapperType = context.getMapperType();
		if (EntityMapper.class.isAssignableFrom(mapperType)) {
			Type actualType = JavaType.of(mapperType).getActualType(EntityMapper.class, 0);
			entityClass = Types.getClass(actualType);
		}
		if (entityClass == null || entityClass == Object.class) {
			throw new IllegalStateException("未知实体类型！");
		}
		return entityClass;
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
