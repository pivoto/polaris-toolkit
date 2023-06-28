package io.polaris.core.object;

import io.polaris.core.collection.Iterables;
import io.polaris.core.object.copier.CopyOptions;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.StringCases;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
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

	public static <T> T toBean(Object source, Class<T> clazz) {
		return toBean(source, clazz, null);
	}

	public static <T> T toBean(Object source, Class<T> clazz, CopyOptions options) {
		return toBean(source, () -> Reflects.newInstanceIfPossible(clazz), options);
	}

	public static <T> T toBean(Object source, Supplier<T> targetSupplier, CopyOptions options) {
		if (null == source || null == targetSupplier) {
			return null;
		}
		final T target = targetSupplier.get();
		copyProperties(source, target, options);
		return target;
	}

	public static void copyProperties(Object source, Object target, CopyOptions copyOptions) {
		if (null == source) {
			return;
		}
		Copiers.create(source, target, (copyOptions != null ? copyOptions : CopyOptions.create())).copy();
	}

	public static Map<String, Object> beanToMap(Object bean, boolean isUnderlineCase, boolean ignoreNull) {
		if (null == bean) {
			return null;
		}
		return beanToMap(bean, new LinkedHashMap<>(), isUnderlineCase, ignoreNull);
	}

	public static Map<String, Object> beanToMap(Object bean, Map<String, Object> targetMap, final boolean isUnderlineCase, boolean ignoreNull) {
		if (null == bean) {
			return null;
		}
		return beanToMap(bean, targetMap, ignoreNull, key -> isUnderlineCase ? StringCases.camelToUnderlineCase(key) : key);
	}

	public static Map<String, Object> beanToMap(Object bean, String... properties) {
		int mapSize = 16;
		Function<String, String> keyEditor = null;
		if (properties.length > 0) {
			mapSize = properties.length;
			Set<String> propertiesSet = Iterables.asSet(properties);
			keyEditor = property -> propertiesSet.contains(property) ? property : null;
		}
		// 指明了要复制的属性 所以不忽略null值
		return beanToMap(bean, new LinkedHashMap<>(mapSize, 1), false, keyEditor);
	}

	public static Map<String, Object> beanToMap(Object bean, Map<String, Object> targetMap, boolean ignoreNull, Function<String, String> keyEditor) {
		if (null == bean) {
			return null;
		}
		return Copiers.create(bean, targetMap, CopyOptions.create().ignoreNull(ignoreNull).propertyNameEditor(keyEditor)).copy();
	}
}
