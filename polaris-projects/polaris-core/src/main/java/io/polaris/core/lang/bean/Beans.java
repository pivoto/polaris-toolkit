package io.polaris.core.lang.bean;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import io.polaris.core.collection.Iterables;
import io.polaris.core.lang.copier.Copiers;
import io.polaris.core.lang.copier.CopyOptions;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.StringCases;

/**
 * @author Qt
 * @since 1.8
 */
public class Beans {
	private static final ILogger log = ILoggers.of(Beans.class);

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
		return BeanMapBuilder.of(bean);
	}

	public static <T> BeanMap<T> newBeanMap(T bean, Class<?> beanType, BeanMapOptions options) {
		return BeanMapBuilder.of(bean).beanType(beanType).options(options).build();
	}

	public static <T> BeanMap<T> newBeanMap(T bean, Class<?> beanType) {
		return BeanMapBuilder.of(bean).beanType(beanType).build();
	}

	public static <T> BeanMap<T> newBeanMap(T bean) {
		return new BeanMapBuilder<T>(bean).build();
	}

	public static <T> BeanMap<T> newBeanMap(T bean, BiFunction<Type, Object, Object> converter) {
		return new BeanMapBuilder<T>(bean)
			.options(BeanMapOptions.newOptions().enableConverter(true).converter(converter))
			.build();
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
		Function<String, String> keyMapping = null;
		if (properties.length > 0) {
			mapSize = properties.length;
			Set<String> propertiesSet = Iterables.asSet(properties);
			keyMapping = property -> propertiesSet.contains(property) ? property : null;
		}
		// 指明了要复制的属性 所以不忽略null值
		return copyBean(bean, new LinkedHashMap<>(mapSize), false, keyMapping);
	}

	public static Map<String, Object> copyBean(Object bean, Map<String, Object> targetMap, boolean ignoreNull, Function<String, String> keyMapping) {
		if (null == bean) {
			return null;
		}
		return Copiers.create(bean, targetMap, CopyOptions.create().ignoreNull(ignoreNull).keyMapping(keyMapping)).copy();
	}


	public static <T> BeanPropertyBuilder<T> newBeanPropertyBuilder(T dest) {
		return BeanPropertyBuilders.of(dest);
	}

	public static <T> BeanPropertyBuilder<T> newBeanPropertyBuilder(Class<T> destType) {
		return BeanPropertyBuilders.of(destType);
	}

	public static <T> BeanPropertyBuilder<List<T>> newBeanPropertyBuilder(List<T> list, Class<T> type) {
		return BeanPropertyBuilders.of(list, type);
	}

	public static <T> BeanPropertyBuilder<List<T>> newBeanPropertyBuilder(List<T> list, Class<T> type, int size) {
		return BeanPropertyBuilders.of(list, type, size);
	}


	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void setProperty(final Object bean, final String name, final Object value) {
		if (bean instanceof Map) {
			((Map) bean).put(name, value);
		} else {
			Beans.newBeanMap(bean).put(name, value);
		}
	}

	@SuppressWarnings("rawtypes")
	public static Object getProperty(Object bean, String name) {
		if (bean instanceof Map) {
			return ((Map) bean).get(name);
		} else {
			return Beans.newBeanMap(bean).get(name);
		}
	}

	public static Object getPathProperty(Object o, String property) {
		return getPathProperty(o, parseProperty(property));
	}

	public static void setPathProperty(Object o, String property, Object val) {
		setPathProperty(o, parseProperty(property), val);
	}


	@SuppressWarnings({"unchecked", "rawtypes"})
	static void setPathProperty(Object obj, Deque<String> properties, Object val) {
		String property = properties.pollLast();
		Object matrix = obj;
		if (!properties.isEmpty()) {
			matrix = getPathProperty(obj, properties);
		}
		try {
			if (matrix != null) {
				if (matrix instanceof List) {
					((List) matrix).set(Integer.parseInt(property), val);
				} else if (matrix.getClass().isArray()) {
					Array.set(matrix, Integer.parseInt(property), val);
				} else if (matrix instanceof Collection) {
					List list = new ArrayList<>(((Collection) matrix));
					list.set(Integer.parseInt(property), val);
					((Collection) matrix).clear();
					((Collection) matrix).addAll(list);
					list.clear();
				} else {
					setProperty(matrix, property, val);
				}
			}
		} catch (Exception e) {
			log.trace(e.getMessage(), e);
		}
	}

	@SuppressWarnings({"rawtypes"})
	static Object getPathProperty(Object obj, Deque<String> properties) {
		Object val = obj;
		for (String property : properties) {
			try {
				Object matrix = val;
				if (matrix instanceof List) {
					val = ((List) matrix).get(Integer.parseInt(property));
				} else if (matrix.getClass().isArray()) {
					val = Array.get(matrix, Integer.parseInt(property));
				} else if (matrix instanceof Collection) {
					Iterator iter = ((Collection) matrix).iterator();
					int idx = Integer.parseInt(property);
					for (int i = 0; i < idx; i++) {
						iter.next();
					}
					val = iter.next();
				} else {
					val = getProperty(val, property);
				}
				if (val == null) {
					break;
				}
			} catch (Exception e) {
				log.trace(e.getMessage(), e);
				return null;
			}
		}
		return val;
	}

	public static Deque<String> parseProperty(String property) {
		char[] charArray = property.toCharArray();
		StringBuilder sb = new StringBuilder();
		boolean escape = false;
		boolean bracket = false;
		Deque<String> queue = new ArrayDeque<>();
		for (char c : charArray) {
			if (escape) {
				sb.append(c);
				escape = false;
				continue;
			} else if (c == '\\') {
				escape = true;
				continue;
			}
			if (bracket) {
				if (c == ']') {
					bracket = false;
					if (sb.length() > 0) {
						queue.add(sb.toString());
						sb.delete(0, sb.length());
					}
					continue;
				}
			} else {
				if (c == '.') {
					if (sb.length() > 0) {
						queue.add(sb.toString());
						sb.delete(0, sb.length());
					}
					continue;
				} else if (c == '[') {
					bracket = true;
					if (sb.length() > 0) {
						queue.add(sb.toString());
						sb.delete(0, sb.length());
					}
					continue;
				} else if (c == ']') {
					throw new IllegalArgumentException(property);
				}
			}
			sb.append(c);
		}
		if (escape || bracket) {
			throw new IllegalArgumentException(property);
		}
		if (sb.length() > 0) {
			queue.add(sb.toString());
		}
		return queue;
	}

}
