package io.polaris.core.lang.bean;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.asm.reflect.BeanAccess;
import io.polaris.core.asm.reflect.BeanLambdaAccess;
import io.polaris.core.asm.reflect.BeanPropertyInfo;
import io.polaris.core.collection.Iterables;
import io.polaris.core.converter.Converters;
import io.polaris.core.lang.Types;
import io.polaris.core.lang.bean.property.ListPropertyBuilder;
import io.polaris.core.lang.bean.property.PropertyBuilder;
import io.polaris.core.lang.bean.property.StdPropertyBuilder;
import io.polaris.core.lang.copier.Copiers;
import io.polaris.core.lang.copier.CopyOptions;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.map.Maps;
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
		T target = targetSupplier.get();
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

	public static Map<String, Object> copyBean(Object bean, Map<String, Object> targetMap, boolean isUnderlineCase, boolean ignoreNull) {
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


	public static <T> PropertyBuilder<T> newPropertyBuilder(T dest) {
		return new StdPropertyBuilder<>(dest);
	}

	public static <T> PropertyBuilder<T> newPropertyBuilder(Class<T> destType) {
		return new StdPropertyBuilder<>(destType);
	}

	public static <T> PropertyBuilder<List<T>> newPropertyBuilder(List<T> list, Class<T> type) {
		return new ListPropertyBuilder<T>(list, type);
	}

	public static <T> PropertyBuilder<List<T>> newPropertyBuilder(List<T> list, Class<T> type, int size) {
		return new ListPropertyBuilder<T>(list, type, size);
	}


	public static void setProperty(Object bean, String name, Object value) {
		if (bean instanceof Map) {
			//noinspection rawtypes,unchecked
			((Map) bean).put(name, value);
		} else {
			PropertyAccessor accessor = getIndexedFieldAndPropertyAccessor(bean.getClass(), name);
			if (accessor != null && accessor.hasSetter()) {
				Type type = accessor.type();
				if (value == null && Types.isPrimitive(Types.getClass(type))) {
					// 基本类型不能赋null
					return;
				}
				if (value != null) {
					// 非null值转换类型
					value = Converters.convertQuietly(type, value);
					if (value != null) {
						// 转换失败忽略
						accessor.set(bean, value);
					}
				} else {
					// 直接赋值为null
					accessor.set(bean, null);
				}
			}
		}
	}


	public static Object getProperty(Object bean, String name) {
		if (bean instanceof Map) {
			//noinspection rawtypes
			return ((Map) bean).get(name);
		} else {
			PropertyAccessor accessor = getIndexedFieldAndPropertyAccessor(bean.getClass(), name);
			return accessor != null && accessor.hasGetter() ? accessor.get(bean) : null;
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


	@Nullable
	public static <T> PropertyAccessor getIndexedPropertyAccessor(Class<T> beanType, String name) {
		return getIndexedPropertyAccessors(beanType).get(name);
	}

	@Nullable
	public static <T> PropertyAccessor getIndexedFieldAndPropertyAccessor(Class<T> beanType, String name) {
		return getIndexedFieldAndPropertyAccessors(beanType).get(name);
	}

	@Nullable
	public static <T> PropertyAccessor getLambdaPropertyAccessor(Class<T> beanType, String name) {
		return getLambdaPropertyAccessors(beanType).get(name);
	}

	@Nullable
	public static <T> PropertyAccessor getLambdaFieldAndPropertyAccessor(Class<T> beanType, String name) {
		return getLambdaFieldAndPropertyAccessors(beanType).get(name);
	}

	@Nonnull
	public static <T> Map<String, PropertyAccessor> getIndexedPropertyAccessors(Class<T> beanType) {
		Map<String, PropertyAccessor>[] metadata = IndexedCache.getMetadata(beanType);
		return metadata[0];
	}

	@Nonnull
	public static <T> Map<String, PropertyAccessor> getIndexedFieldAndPropertyAccessors(Class<T> beanType) {
		Map<String, PropertyAccessor>[] metadata = IndexedCache.getMetadata(beanType);
		return metadata[1];
	}

	@Nonnull
	public static <T> Map<String, PropertyAccessor> getLambdaPropertyAccessors(Class<T> beanType) {
		Map<String, PropertyAccessor>[] metadata = LambdaCache.getMetadata(beanType);
		return metadata[0];
	}

	@Nonnull
	public static <T> Map<String, PropertyAccessor> getLambdaFieldAndPropertyAccessors(Class<T> beanType) {
		Map<String, PropertyAccessor>[] metadata = LambdaCache.getMetadata(beanType);
		return metadata[1];
	}


	static class IndexedCache {
		private static final Map<Class<?>, Map<String, PropertyAccessor>[]> PROPERTIES = Maps.newSoftMap(new ConcurrentHashMap<>());

		@SuppressWarnings("unchecked")
		private static <T> Map<String, PropertyAccessor>[] createMetadata(Class<T> beanType) {
			BeanAccess<T> access = BeanAccess.get(beanType);
			Map<String, BeanPropertyInfo> accessProperties = access.properties();
			Map<String, PropertyAccessor> properties = new HashMap<>(accessProperties.size());
			Map<String, PropertyAccessor> propertiesWithFields = new HashMap<>(accessProperties.size());

			for (Map.Entry<String, BeanPropertyInfo> entry : accessProperties.entrySet()) {
				BeanPropertyInfo beanPropertyInfo = entry.getValue();
				Type propertyGenericType = beanPropertyInfo.getPropertyGenericType();
				String propertyName = beanPropertyInfo.getPropertyName();
				if (beanPropertyInfo.getField() != null) {
					int fieldIndex = access.getFieldIndex(propertyName);
					PropertyAccessor accessor = new PropertyFieldIndexedAccessor(access, propertyGenericType, fieldIndex);
					propertiesWithFields.put(propertyName, accessor);
				} else {
					int getterIndex = access.getGetterIndex(propertyName);
					int setterIndex = access.getSetterIndex(propertyName);
					PropertyAccessor accessor = new PropertyIndexedAccessor(access, propertyGenericType, getterIndex, setterIndex);
					properties.put(propertyName, accessor);
					propertiesWithFields.put(propertyName, accessor);
				}
			}
			return new Map[]{Collections.unmodifiableMap(properties), Collections.unmodifiableMap(propertiesWithFields)};
		}

		@Nonnull
		static <T> Map<String, PropertyAccessor>[] getMetadata(Class<T> beanType) {
			return PROPERTIES.computeIfAbsent(beanType, IndexedCache::createMetadata);
		}
	}

	static class LambdaCache {
		private static final Map<Class<?>, Map<String, PropertyAccessor>[]> PROPERTIES = Maps.newSoftMap(new ConcurrentHashMap<>());

		@SuppressWarnings("unchecked")
		private static <T> Map<String, PropertyAccessor>[] createMetadata(Class<T> beanType) {
			BeanLambdaAccess<T> access = BeanLambdaAccess.get(beanType);
			Map<String, BeanPropertyInfo> accessProperties = access.properties();
			Map<String, PropertyAccessor> properties = new HashMap<>(accessProperties.size());
			Map<String, PropertyAccessor> propertiesWithFields = new HashMap<>(accessProperties.size());
			for (Map.Entry<String, BeanPropertyInfo> entry : accessProperties.entrySet()) {
				BeanPropertyInfo beanPropertyInfo = entry.getValue();
				Type propertyGenericType = beanPropertyInfo.getPropertyGenericType();
				String propertyName = beanPropertyInfo.getPropertyName();
				if (beanPropertyInfo.getField() != null) {
					Function<Object, Object> getter = access.getFieldGetter(propertyName);
					BiConsumer<Object, Object> setter = access.getFieldSetter(propertyName);
					PropertyAccessor accessor = new PropertyLambdaAccessor(access, propertyGenericType, getter, setter);
					propertiesWithFields.put(propertyName, accessor);
				} else {
					Function<Object, Object> getter = access.getGetter(propertyName);
					BiConsumer<Object, Object> setter = access.getSetter(propertyName);
					PropertyAccessor accessor = new PropertyLambdaAccessor(access, propertyGenericType, getter, setter);
					properties.put(propertyName, accessor);
					propertiesWithFields.put(propertyName, accessor);
				}
			}
			return new Map[]{Collections.unmodifiableMap(properties), Collections.unmodifiableMap(propertiesWithFields)};
		}

		@Nonnull
		static <T> Map<String, PropertyAccessor>[] getMetadata(Class<T> beanType) {
			return PROPERTIES.computeIfAbsent(beanType, LambdaCache::createMetadata);
		}
	}
}
