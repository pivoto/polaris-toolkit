package io.polaris.mybatis.provider;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;

import io.polaris.core.lang.bean.BeanMap;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.map.Maps;
import io.polaris.mybatis.enhance.EnhancedAutoMapperHelper;
import io.polaris.mybatis.scripting.EnhanceBoundSqlLanguageDriver;
import io.polaris.mybatis.support.MapperHelperContext;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;

/**
 * @author Qt
 * @since Sep 11, 2023
 */
public abstract class BaseProviderMethodResolver implements ProviderMethodResolver {

	@Override
	public Method resolveMethod(ProviderContext context) {
		String methodName = context.getMapperMethod().getName();
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
		return EnhancedAutoMapperHelper.getEntityClassOrThrows(context);
	}


	protected static String provideSql(Object parameterObject, ProviderContext context, BiFunction<Map<String, Object>, ProviderContext, String> function) {
		try {
			Map<String, Object> map = toParameterBindings(context.getMapperMethod(), parameterObject);
			String sql = function.apply(map, context);
			return sql;
		} catch (RuntimeException e) {
			MapperHelperContext.clearAdditionalParameters();
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> toParameterBindings(Method mapperMethod, Object parameterObject) {
		if (parameterObject instanceof Map && Maps.isModifiable((Map<?, ?>) parameterObject)) {
			return (Map<String, Object>) parameterObject;
		} else {
			if (!MapperHelperContext.isEnabledEnhanceBoundSql()
				&& !hasEnhanceBoundSqlLanguageDriver(mapperMethod)
			) {
				Map<String, Object> additionalParameters = MapperHelperContext.getBoundAdditionalParameters(parameterObject);
				if (additionalParameters != null) {
					return additionalParameters;
				}
				if (parameterObject == null) {
					// 参数为空时，使用空MAP，不接收任何绑定
					return Collections.emptyMap();
				}
				throw new IllegalArgumentException("请使用Map类型参数或明确声明参数键名");
			}
			return toParameterBindings(parameterObject);
		}
	}

	private static boolean hasEnhanceBoundSqlLanguageDriver(Method mapperMethod) {
		Lang lang = mapperMethod.getAnnotation(Lang.class);
		if (lang == null) {
			return false;
		}
		return EnhanceBoundSqlLanguageDriver.class.isAssignableFrom(lang.value());
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
		if (parameterObject instanceof Map && Maps.isModifiable((Map<?, ?>) parameterObject)) {
			return (Map) parameterObject;
		}
		Map<String, Object> additionalParameters = MapperHelperContext.bindAdditionalParameters(parameterObject);
		if (parameterObject != null) {
			BeanMap<Object> beanMap = Beans.newBeanMap(parameterObject);
			additionalParameters.putAll(beanMap);
		}
		return additionalParameters;
	}

}
