package io.polaris.core.lang.bean;

import io.polaris.core.collection.Iterables;
import io.polaris.core.lang.copier.Copiers;
import io.polaris.core.lang.copier.CopyOptions;
import io.polaris.core.log.ILogger;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.StringCases;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
public class Beans {
	private static final ILogger log = ILogger.of(Beans.class);

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
		return Copiers.create(bean, targetMap, CopyOptions.create().ignoreNull(ignoreNull).keyMapping(keyEditor)).copy();
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


	public static void setProperty(final Object bean, final String name, final Object value) {
		Beans.newBeanMap(bean).put(name, value);
	}

	public static Object getProperty(Object bean, String name) {
		return Beans.newBeanMap(bean).get(name);
	}

	public static Object getPathProperty(Object o, String property) {
		return getProperty(o, parseProperty(property));
	}

	public static void setPathProperty(Object o, String property, Object val) {
		setProperty(o, parseProperty(property), val);
	}


	@SuppressWarnings({"unchecked", "rawtypes"})
	static void setProperty(Object o, Deque<String> properties, Object val) {
		String property = properties.pollLast();
		Object matrix = o;
		if (!properties.isEmpty()) {
			matrix = getProperty(o, properties);
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
	static Object getProperty(Object o, Deque<String> properties) {
		Object val = o;
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

	static Deque<String> parseProperty(String property) {
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
