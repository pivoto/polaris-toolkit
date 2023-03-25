package io.polaris.toolkit.spring.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.bind.RelaxedNames;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @version Dec 28, 2021
 * @since 1.8
 */
@SuppressWarnings("DuplicatedCode")
@Slf4j
public class Binders {

	public static <T> T bind(Environment environment, Class<T> targetClass, String prefix) {
		T target = BeanUtils.instantiate(targetClass);
		return bind(environment, target, prefix);
	}

	public static <T> T bind(Environment environment, T target, String prefix) {
		Map<String, String> properties = subProperties(environment, "");
		RelaxedDataBinder binder = new RelaxedDataBinder(target, prefix);
		binder.bind(new MutablePropertyValues(properties));
		return target;
	}

	public static Map<String, String> subProperties(Environment environment, String prefix) {
		// 替换以下方法以支持解密属性
		// Map<String, Object> properties = new RelaxedPropertyResolver(environment).getSubProperties("");
		Map<String, String> properties = new HashMap<>();
		if (!(environment instanceof ConfigurableEnvironment)) {
			return properties;
		}
		RelaxedNames keyPrefixes = new RelaxedNames(prefix);
		MutablePropertySources propertySources = ((ConfigurableEnvironment) environment).getPropertySources();
		for (PropertySource<?> source : propertySources) {
			if (source instanceof EnumerablePropertySource) {
				String[] propertyNames = ((EnumerablePropertySource<?>) source).getPropertyNames();
				if (propertyNames == null) {
					continue;
				}
				for (String name : propertyNames) {
					String key = getSubKey(name, keyPrefixes);
					if (key != null && !properties.containsKey(key)) {
						// 调用 environment 方法以支持自动解密
						properties.put(key, environment.getProperty(name));
					}
				}
			}
		}
		return properties;
	}

	private static String getSubKey(String name, RelaxedNames keyPrefix) {
		for (String candidateKeyPrefix : keyPrefix) {
			if (name.startsWith(candidateKeyPrefix)) {
				return name.substring((candidateKeyPrefix).length());
			}
		}
		return null;
	}


	public static <T> T bind(Map<String, String> properties, Class<T> targetClass, String prefix, Map<String, List<String>> aliasMap) {
		T target = BeanUtils.instantiate(targetClass);
		return bind(properties, target, prefix, aliasMap);
	}

	public static <T> T bind(Map<String, String> properties, T target, String prefix, Map<String, List<String>> aliasMap) {
		RelaxedDataBinder binder = new RelaxedDataBinder(target, prefix);
		if (aliasMap != null) {
			if (aliasMap != null) {
				aliasMap.forEach((name, aliasList) -> {
					String[] aliasArray = aliasList.toArray(new String[0]);
					binder.withAlias(name, aliasArray);
				});
			}
		}
		binder.bind(new MutablePropertyValues(properties));
		return target;
	}

	public static Map<String, String> toProperties(Object source, String prefix) {
		Map<String, String> properties = new HashMap<>();
		toProperties(source, properties, prefix);
		return properties;
	}

	public static void toProperties(Object source, Map<String, String> properties, String prefix) {
		Class<?> sourceClass = source.getClass();
		if (ClassUtils.isPrimitiveOrWrapper(sourceClass) || sourceClass.isEnum()) {
			properties.put(withPrefix("", prefix), source.toString());
		} else if (sourceClass.isArray()) {
			toArrayProperties(source, properties, prefix);
		} else if (source instanceof Collection) {
			toCollectionProperties(((Collection<?>) source), properties, prefix);
		} else if (source instanceof Map) {
			toMapProperties(((Map<?, ?>) source), properties, prefix);
		} else {
			PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(sourceClass);
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				String name = propertyDescriptor.getName();
				Method readMethod = propertyDescriptor.getReadMethod();
				Method writeMethod = propertyDescriptor.getWriteMethod();
				// getter and setter
				if (readMethod != null && writeMethod != null) {
					try {
						if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
							readMethod.setAccessible(true);
						}
						Object value = readMethod.invoke(source);
						if (value == null) {
							continue;
						}
						Class<?> valueClass = value.getClass();
						String key = withPrefix(name, prefix);
						if (ClassUtils.isPrimitiveOrWrapper(valueClass) || valueClass.isEnum()) {
							properties.put(key, value.toString());
						} else {
							toProperties(value, properties, key);
						}
					} catch (Throwable ex) {
						log.warn("", ex);
					}
				}
			}
		}
	}

	private static String withPrefix(String key, String prefix) {
		if (StringUtils.hasText(prefix)) {
			if (prefix.endsWith(".") || key.startsWith("[")) {
				return prefix + key;
			}
			return prefix + "." + key;
		}
		return key;
	}

	private static void toArrayProperties(Object source, Map<String, String> properties, String prefix) {
		int len = Array.getLength(source);
		for (int i = 0; i < len; i++) {
			Object o = Array.get(source, i);
			String key = withPrefix("[" + i + "]", prefix);
			if (o == null) {
				properties.put(key, "");
			} else if (ClassUtils.isPrimitiveOrWrapper(o.getClass()) || o.getClass().isEnum()) {
				properties.put(key, o.toString());
			} else {
				toProperties(o, properties, key);
			}
		}
	}

	private static void toCollectionProperties(Collection source, Map<String, String> properties, String prefix) {
		int i = 0;
		for (Object o : ((Collection<?>) source)) {
			String key = withPrefix("[" + i + "]", prefix);
			if (o == null) {
				properties.put(key, "");
			} else if (ClassUtils.isPrimitiveOrWrapper(o.getClass()) || o.getClass().isEnum()) {
				properties.put(key, o.toString());
			} else {
				toProperties(o, properties, key);
			}
		}
	}

	private static void toMapProperties(Map source, Map<String, String> properties, String prefix) {
		for (Map.Entry<?, ?> entry : ((Map<?, ?>) source).entrySet()) {
			String key = withPrefix(entry.getKey().toString(), prefix);
			Object o = entry.getValue();
			if (o == null) {
				properties.put(key, "");
			} else if (ClassUtils.isPrimitiveOrWrapper(o.getClass()) || o.getClass().isEnum()) {
				properties.put(key, o.toString());
			} else {
				toProperties(o, properties, key);
			}
		}
	}

}
