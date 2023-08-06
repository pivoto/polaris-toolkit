package io.polaris.core.lang.bean;

import io.polaris.core.collection.Iterables;
import io.polaris.core.lang.copier.Copiers;
import io.polaris.core.lang.copier.CopyOptions;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.StringCases;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
public class Beans {

	public static boolean isBeanClass(Class clazz) {
		for (Method method : clazz.getMethods()) {
			// 检测包含标准的setter方法即视为标准的JavaBean
			if (Reflects.isSetterMethod(method)) {
				return true;
			}
		}
		return false;
	}

	public static <T> BeanMapBuilder<T> newBeanMapBuilder(T bean) {
		return new BeanMapBuilder<T>(bean);
	}

	public static <T> BeanMap<T> newBeanMap(T bean
		, Class<?> beanType, BiFunction<Object, Type, Object> converter
		, Function<String, Object> fallbackGetter
		, BiConsumer<String, Object> fallbackSetter) {
		return new BeanMap<>(bean, beanType, converter, fallbackGetter, fallbackSetter);
	}

	public static <T> BeanMap<T> newBeanMap(T bean, Class<?> beanType) {
		return newBeanMap(bean, beanType, null, null, null);
	}

	public static <T> BeanMap<T> newBeanMap(T bean) {
		return newBeanMap(bean, bean.getClass(), null, null, null);
	}

	public static <T> BeanMap<T> newBeanMap(T bean, BiFunction<Object, Type, Object> converter) {
		return newBeanMap(bean, bean.getClass(), converter, null, null);
	}


	public static <T> T copyBean(Object source, Class<T> clazz) {
		return copyBean(source, clazz, null);
	}

	public static <T> T copyBean(Object source, Class<T> clazz, CopyOptions options) {
		return copyBean(source, () -> Reflects.newInstanceIfPossible(clazz), options);
	}

	public static <T> T copyBean(Object source, Supplier<T> targetSupplier, CopyOptions options) {
		if (null == source || null == targetSupplier) {
			return null;
		}
		final T target = targetSupplier.get();
		copyBean(source, target, options);
		return target;
	}

	public static void copyBean(Object source, Object target, CopyOptions copyOptions) {
		if (null == source) {
			return;
		}
		Copiers.create(source, target, (copyOptions != null ? copyOptions : CopyOptions.create())).copy();
	}

	public static Map<String, Object> copyBean(Object bean, boolean isUnderlineCase, boolean ignoreNull) {
		if (null == bean) {
			return null;
		}
		return copyBean(bean, new LinkedHashMap<>(), isUnderlineCase, ignoreNull);
	}

	public static Map<String, Object> copyBean(Object bean, Map<String, Object> targetMap, final boolean isUnderlineCase, boolean ignoreNull) {
		if (null == bean) {
			return null;
		}
		return copyBean(bean, targetMap, ignoreNull, key -> isUnderlineCase ? StringCases.camelToUnderlineCase(key) : key);
	}

	public static Map<String, Object> copyBean(Object bean, String... properties) {
		int mapSize = 16;
		Function<String, String> keyEditor = null;
		if (properties.length > 0) {
			mapSize = properties.length;
			Set<String> propertiesSet = Iterables.asSet(properties);
			keyEditor = property -> propertiesSet.contains(property) ? property : null;
		}
		// 指明了要复制的属性 所以不忽略null值
		return copyBean(bean, new LinkedHashMap<>(mapSize, 1), false, keyEditor);
	}

	public static Map<String, Object> copyBean(Object bean, Map<String, Object> targetMap, boolean ignoreNull, Function<String, String> keyEditor) {
		if (null == bean) {
			return null;
		}
		return Copiers.create(bean, targetMap, CopyOptions.create().ignoreNull(ignoreNull).nameEditor(keyEditor)).copy();
	}

}
