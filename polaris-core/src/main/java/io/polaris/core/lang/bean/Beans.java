package io.polaris.core.lang.bean;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.asm.reflect.BeanAccess;
import io.polaris.core.asm.reflect.BeanCopier;
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
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.map.Maps;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.StringCases;

/**
 * @author Qt
 * @since 1.8
 */
public class Beans {
	private static final Logger log = Loggers.of(Beans.class);

	public static boolean isBeanClass(@Nonnull Class clazz) {
		for (Method method : clazz.getMethods()) {
			// 检测包含标准的setter方法即视为标准的JavaBean
			if (Reflects.isSetterMethod(method)) {
				return true;
			}
		}
		return false;
	}

	public static <T> BeanMapBuilder<T> newBeanMapBuilder(@Nonnull T bean) {
		return BeanMapBuilder.of(bean);
	}

	public static <T> BeanMap<T> newBeanMap(@Nonnull T bean, @Nonnull Class<?> beanType, BeanMapOptions options) {
		return BeanMapBuilder.of(bean).beanType(beanType).options(options).build();
	}

	public static <T> BeanMap<T> newBeanMap(@Nonnull T bean, @Nonnull Class<?> beanType) {
		return BeanMapBuilder.of(bean).beanType(beanType).build();
	}

	public static <T> BeanMap<T> newBeanMap(@Nonnull T bean) {
		return new BeanMapBuilder<T>(bean).build();
	}

	public static <T> BeanMap<T> newBeanMap(@Nonnull T bean, BiFunction<Type, Object, Object> converter) {
		return new BeanMapBuilder<T>(bean)
			.options(BeanMapOptions.newOptions().enableConverter(true).converter(converter))
			.build();
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static <T> T copyBean(@Nonnull Object source, @Nonnull T target) {
		BeanCopier copier = BeanCopier.get(source.getClass());
		copier.copyBeanToBean(source, target);
		return target;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static <T> T copyBean(@Nonnull Object source, @Nonnull Class<T> clazz) {
		T target = Reflects.newInstanceIfPossible(clazz);
		BeanCopier copier = BeanCopier.get(source.getClass());
		copier.copyBeanToBean(source, target);
		return target;
	}

	public static <T> T copyBean(@Nonnull Object source, @Nonnull Class<T> clazz, CopyOptions options) {
		return copyBean(source, () -> Reflects.newInstanceIfPossible(clazz), options);
	}

	public static <T> T copyBean(@Nonnull Object source, @Nonnull Supplier<T> targetSupplier, CopyOptions options) {
		T target = targetSupplier.get();
		copyBean(source, target, options);
		return target;
	}

	public static void copyBean(@Nonnull Object source, @Nonnull Object target, @Nullable CopyOptions options) {
		Copiers.fastCopy(source, target, (options != null ? options : CopyOptions.create()));
	}

	public static Map<String, Object> newMapFromBean(@Nonnull Object bean, boolean isUnderlineCase, boolean ignoreNull) {
		return copyBeanToMap(bean, new LinkedHashMap<>(), isUnderlineCase, ignoreNull);
	}

	public static Map<String, Object> newMapFromBean(@Nonnull Object bean, String... properties) {
		int mapSize = 16;
		Function<String, String> keyMapping = null;
		if (properties.length > 0) {
			mapSize = properties.length;
			Set<String> propertiesSet = Iterables.asSet(properties);
			keyMapping = property -> propertiesSet.contains(property) ? property : null;
		}
		// 指明了要复制的属性 所以不忽略null值
		return copyBeanToMap(bean, new LinkedHashMap<>(mapSize), false, keyMapping);
	}

	public static Map<String, Object> copyBeanToMap(@Nonnull Object bean, @Nonnull Map<String, Object> targetMap, boolean isUnderlineCase, boolean ignoreNull) {
		return copyBeanToMap(bean, targetMap, ignoreNull, isUnderlineCase ? StringCases::camelToUnderlineCase : null);
	}

	public static Map<String, Object> copyBeanToMap(@Nonnull Object bean, @Nonnull Map<String, Object> targetMap, boolean ignoreNull, @Nullable Function<String, String> keyMapping) {
		return Copiers.fastCopy(bean, targetMap, CopyOptions.create().ignoreNull(ignoreNull).keyMapping(keyMapping));
	}


	public static <T> PropertyBuilder<T> newPropertyBuilder(@Nonnull T dest) {
		return new StdPropertyBuilder<>(dest);
	}

	public static <T> PropertyBuilder<T> newPropertyBuilder(@Nonnull Class<T> destType) {
		return new StdPropertyBuilder<>(destType);
	}

	public static <T> PropertyBuilder<List<T>> newPropertyBuilder(@Nonnull List<T> list, @Nonnull Class<T> type) {
		return new ListPropertyBuilder<T>(list, type);
	}

	public static <T> PropertyBuilder<List<T>> newPropertyBuilder(@Nonnull List<T> list, @Nonnull Class<T> type, int size) {
		return new ListPropertyBuilder<T>(list, type, size);
	}


	public static void setProperty(@Nonnull Object bean, @Nonnull String name, Object value) {
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


	public static Object getProperty(@Nonnull Object bean, @Nonnull String name) {
		if (bean instanceof Map) {
			//noinspection rawtypes
			return ((Map) bean).get(name);
		} else {
			PropertyAccessor accessor = getIndexedFieldAndPropertyAccessor(bean.getClass(), name);
			return accessor != null && accessor.hasGetter() ? accessor.get(bean) : null;
		}
	}

	public static Object getPathProperty(@Nonnull Object o, @Nonnull String property) {
		return getPathProperty(o, parseProperty(property));
	}

	public static void setPathProperty(@Nonnull Object o, @Nonnull String property, Object val) {
		setPathProperty(o, parseProperty(property), val);
	}


	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void setPathProperty(@Nonnull Object obj, @Nonnull Deque<String> properties, Object val) {
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
	private static Object getPathProperty(@Nonnull Object obj, @Nonnull Deque<String> properties) {
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

	public static Deque<String> parseProperty(@Nonnull String property) {
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
	public static <T> PropertyAccessor getIndexedPropertyAccessor(@Nonnull Class<T> beanType, @Nonnull String name) {
		return getIndexedPropertyAccessors(beanType).get(name);
	}

	@Nullable
	public static <T> PropertyAccessor getIndexedFieldAndPropertyAccessor(@Nonnull Class<T> beanType, @Nonnull String name) {
		return getIndexedFieldAndPropertyAccessors(beanType).get(name);
	}

	@Nullable
	public static <T> PropertyAccessor getLambdaPropertyAccessor(@Nonnull Class<T> beanType, @Nonnull String name) {
		return getLambdaPropertyAccessors(beanType).get(name);
	}

	@Nullable
	public static <T> PropertyAccessor getLambdaFieldAndPropertyAccessor(@Nonnull Class<T> beanType, @Nonnull String name) {
		return getLambdaFieldAndPropertyAccessors(beanType).get(name);
	}

	@Nonnull
	public static <T> Map<String, PropertyAccessor> getIndexedPropertyAccessors(@Nonnull Class<T> beanType) {
		Map<String, PropertyAccessor>[] metadata = IndexedCache.getMetadata(beanType);
		return metadata[0];
	}

	@Nonnull
	public static <T> Map<String, PropertyAccessor> getIndexedFieldAndPropertyAccessors(@Nonnull Class<T> beanType) {
		Map<String, PropertyAccessor>[] metadata = IndexedCache.getMetadata(beanType);
		return metadata[1];
	}

	@Nonnull
	public static <T> Map<String, PropertyAccessor> getLambdaPropertyAccessors(@Nonnull Class<T> beanType) {
		Map<String, PropertyAccessor>[] metadata = LambdaCache.getMetadata(beanType);
		return metadata[0];
	}

	@Nonnull
	public static <T> Map<String, PropertyAccessor> getLambdaFieldAndPropertyAccessors(@Nonnull Class<T> beanType) {
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
		@SuppressWarnings({"StatementWithEmptyBody", "ConstantValue"})
		static <T> Map<String, PropertyAccessor>[] getMetadata(Class<T> beanType) {
			Map<String, PropertyAccessor>[] rs = null;
			// 防止因对象回收后导致SoftMap结果丢失，尝试多次获取
			while ((rs = PROPERTIES.computeIfAbsent(beanType, IndexedCache::createMetadata)) == null) {
			}
			return rs;
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
		@SuppressWarnings({"StatementWithEmptyBody", "ConstantValue"})
		static <T> Map<String, PropertyAccessor>[] getMetadata(Class<T> beanType) {
			Map<String, PropertyAccessor>[] rs = null;
			// 防止因对象回收后导致SoftMap结果丢失，尝试多次获取
			while ((rs = PROPERTIES.computeIfAbsent(beanType, LambdaCache::createMetadata)) == null) {
			}
			return rs;
		}
	}
}
