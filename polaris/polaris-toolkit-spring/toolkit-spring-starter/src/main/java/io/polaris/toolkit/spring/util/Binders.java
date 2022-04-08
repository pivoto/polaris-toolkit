package io.polaris.toolkit.spring.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
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
		return bind(environment, Bindable.of(targetClass), prefix);
	}

	private static <T> T bind(Environment environment, Bindable<T> bindable, String prefix) {
		Binder binder = Binder.get(environment);
		BindResult<T> bindResult = binder.bind(prefix, bindable);
		if (bindResult.isBound()) {
			return bindResult.get();
		}
		return null;
	}

	public static <T> T bind(Environment environment, T target, String prefix) {
		return bind(environment, Bindable.ofInstance(target), prefix);
	}

	public static Map<String, String> subProperties(Environment environment, String prefix) {
		Map<String, String> properties = new HashMap<>();
		if (!(environment instanceof ConfigurableEnvironment)) {
			return properties;
		}
		MutablePropertySources propertySources = ((ConfigurableEnvironment) environment).getPropertySources();
		for (PropertySource<?> source : propertySources) {
			if (source instanceof EnumerablePropertySource) {
				String[] propertyNames = ((EnumerablePropertySource<?>) source).getPropertyNames();
				if (propertyNames == null) {
					continue;
				}
				for (String name : propertyNames) {
					if (prefix.length() == 0) {
						properties.put(name, environment.getProperty(name));
					} else {
						String prefixLowerCase = prefix.replaceAll("[_\\-]", "").toLowerCase();
						if (name.replaceAll("[_\\-]", "").toLowerCase().startsWith(prefixLowerCase)) {
							int i = 0;
							int j = 0;
							while (i < prefix.length() && j < name.length()) {
								char n = Character.toLowerCase(name.charAt(j));
								char p = Character.toLowerCase(prefix.charAt(i));
								if (n == '-' || n == '_') {
									j++;
								} else if (p == n) {
									i++;
									j++;
								} else {
									break;
								}
							}
							if (i == prefix.length() && j < name.length()) {
								String key = name.substring(j);
								properties.put(key, environment.getProperty(name));
							}
						}
					}
				}
			}
		}
		return properties;
	}

	public static <T> T bind(Map<String, String> properties, Class<T> targetClass, String prefix, Map<String, List<String>> aliasMap) {
		return bind(properties, Bindable.of(targetClass), prefix, aliasMap);
	}

	private static <T> T bind(Map<String, String> properties, Bindable<T> bindable, String prefix, Map<String, List<String>> aliasMap) {
		ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
		ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases();
		if (aliasMap != null) {
			aliasMap.forEach((name, aliasList) -> {
				String[] aliasArray = aliasList.toArray(new String[0]);
				aliases.addAliases(name, aliasArray);
			});
		}
		Binder binder = new Binder(source.withAliases(aliases));
		BindResult<T> bindResult = binder.bind(prefix, bindable);
		if (bindResult.isBound()) {
			return bindResult.get();
		}
		return null;
	}

	public static <T> T bind(Map<String, String> properties, T target, String prefix, Map<String, List<String>> aliasMap) {
		return bind(properties, Bindable.ofInstance(target), prefix, aliasMap);
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
