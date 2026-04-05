package io.polaris.core.asm.reflect;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nullable;

import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.Strings;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Bean 属性信息
 * <p>封装 Java Bean 属性的详细信息，包括属性名、类型、getter/setter 方法和字段</p>
 *
 * @author Qt
 * @since Apr 11, 2024
 */
@Getter
@EqualsAndHashCode
@ToString
public class BeanPropertyInfo {

	/**
	 * 属性名称
	 */
	private final String propertyName;
	/**
	 * 属性的泛型类型
	 */
	private final java.lang.reflect.Type propertyGenericType;
	/**
	 * 属性的原始类型
	 */
	private final Class<?> propertyType;
	/**
	 * Setter 方法
	 */
	private final Method writeMethod;
	/**
	 * Getter 方法
	 */
	private final Method readMethod;
	/**
	 * 直接访问字段
	 */
	private final Field field;

	/**
	 * 构造函数
	 * <p>要求：存在属性方法时，直接字段为空，只能通过属性方法访问属性</p>
	 *
	 * @param propertyName 属性名
	 * @param propertyGenericType 属性泛型类型
	 * @param propertyType 属性原始类型
	 * @param writeMethod Setter 方法
	 * @param readMethod Getter 方法
	 * @param field 直接访问字段
	 */
	private BeanPropertyInfo(String propertyName, Type propertyGenericType, Class<?> propertyType, Method writeMethod, Method readMethod, Field field) {
		this.propertyName = propertyName;
		this.propertyGenericType = propertyGenericType;
		this.propertyType = propertyType;
		this.writeMethod = writeMethod;
		this.readMethod = readMethod;
		this.field = field;
	}

	/**
	 * 判断是否存在 Setter 方法或字段
	 *
	 * @return 如果存在返回 true，否则返回 false
	 */
	public boolean hasSetter() {
		return writeMethod != null || field != null;
	}

	/**
	 * 判断是否存在 Getter 方法或字段
	 *
	 * @return 如果存在返回 true，否则返回 false
	 */
	public boolean hasGetter() {
		return readMethod != null || field != null;
	}

	/**
	 * 从属性描述符创建 BeanPropertyInfo 实例
	 *
	 * @param propertyDescriptor Java Bean 属性描述符
	 * @return BeanPropertyInfo 实例
	 */
	public static BeanPropertyInfo of(PropertyDescriptor propertyDescriptor) {
		return of(propertyDescriptor.getName(), propertyDescriptor.getWriteMethod(), propertyDescriptor.getReadMethod(), null);
	}

	private static BeanPropertyInfo of(String propertyName, Type propertyGenericType, Class<?> propertyType, Method writeMethod, Method readMethod, Field field) {
		return new BeanPropertyInfo(propertyName, propertyGenericType, propertyType
			, writeMethod, readMethod, field);
	}

	/**
	 * 创建 BeanPropertyInfo 实例
	 *
	 * @param propertyName 属性名
	 * @param writeMethod Setter 方法
	 * @param readMethod Getter 方法
	 * @param field 直接访问字段
	 * @return BeanPropertyInfo 实例
	 */
	private static BeanPropertyInfo of(String propertyName, Method writeMethod, Method readMethod, Field field) {
		propertyName = Strings.trimToNull(propertyName);
		if (propertyName == null) {
			throw new IllegalArgumentException("propertyName is required");
		}
		if (readMethod == null && writeMethod == null && field == null) {
			throw new IllegalArgumentException("readMethodName or writeMethodName or field is required");
		}
		if (field != null) {
			if (!field.getName().equals(propertyName)) {
				throw new IllegalArgumentException("field name must be same as propertyName");
			}
			if (writeMethod != null || readMethod != null) {
				throw new IllegalArgumentException("field must be null when writeMethod or readMethod exists");
			}
		}
		Type propertyGenericType = null;
		Class<?> propertyType = null;
		if (writeMethod != null) {
			propertyGenericType = writeMethod.getGenericParameterTypes()[0];
			propertyType = writeMethod.getParameterTypes()[0];
		} else if (field != null) {
			propertyGenericType = field.getGenericType();
			propertyType = field.getType();
		} else {
			propertyGenericType = readMethod.getGenericReturnType();
			propertyType = readMethod.getReturnType();
		}
		if (propertyType == null) {
			propertyType = Object.class;
		}
		if (propertyGenericType == null) {
			propertyGenericType = propertyType;
		}

		return BeanPropertyInfo.of(propertyName, propertyGenericType, propertyType
			, writeMethod, readMethod, field);
	}

	/**
	 * 获取类的所有属性信息列表
	 *
	 * @param beanType Bean 类型
	 * @return 属性信息列表
	 */
	public static List<BeanPropertyInfo> listOf(Class<?> beanType) {
		Map<String, BeanPropertyInfo> map = mapOf(beanType);
		List<BeanPropertyInfo> list = new ArrayList<>(map.size());
		list.addAll(map.values());
		return list;
	}

	/**
	 * 获取类的所有属性信息映射表
	 *
	 * @param beanType Bean 类型
	 * @return 属性信息映射表（LinkedHashMap，保持插入顺序）
	 */
	public static Map<String, BeanPropertyInfo> mapOf(Class<?> beanType) {
		Map<String, BeanPropertyInfo> map = CacheHolder.get(beanType);
		if (map != null) {
			return map;
		}
		map = $mapOf(beanType);
		CacheHolder.put(beanType, map);
		return map;
	}

	private static Map<String, BeanPropertyInfo> $mapOf(Class<?> beanType) {
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(beanType);
		} catch (IntrospectionException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		Map<String, BeanPropertyInfo> rs = new LinkedHashMap<>();

		PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor pd : pds) {
			Method writeMethod = pd.getWriteMethod();
			Method readMethod = pd.getReadMethod();
			if (Reflects.isGetClassMethod(pd.getReadMethod())) {
				continue;
			}
			if (writeMethod != null || readMethod != null) {
				BeanPropertyInfo info = BeanPropertyInfo.of(pd.getName(), writeMethod, readMethod, null);
				rs.put(info.getPropertyName(), info);
			}
		}
		Map<String, Field> fields = new LinkedHashMap<>();
		{
			Class<?> nextClass = beanType;
			while (nextClass != null && nextClass != Object.class) {
				Field[] declaredFields = nextClass.getDeclaredFields();
				for (int i = 0, n = declaredFields.length; i < n; i++) {
					Field field = declaredFields[i];
					int modifiers = field.getModifiers();
					// 只支持public，视为另一种公开属性，忽略private、final、static等字段
					if (Modifier.isPublic(modifiers) && !Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers)) {
						fields.putIfAbsent(field.getName(), field);
					}
				}
				nextClass = nextClass.getSuperclass();
			}
		}
		for (Map.Entry<String, Field> entry : fields.entrySet()) {
			String name = entry.getKey();
			// 针对缺少方法的情况，添加直接字段
			if (!rs.containsKey(name)) {
				BeanPropertyInfo info = BeanPropertyInfo.of(name, null, null, entry.getValue());
				rs.put(info.getPropertyName(), info);
			}
		}
		return rs;
	}


	private static class CacheHolder {
		private static final Map<Class<?>, SoftReference<Map<String, BeanPropertyInfo>>> cache = new WeakHashMap<>();
		private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		private static final Lock writeLock = lock.writeLock();
		private static final Lock readLock = lock.readLock();

		@Nullable
		public static Map<String, BeanPropertyInfo> get(Class<?> beanType) {
			readLock.lock();
			try {
				SoftReference<Map<String, BeanPropertyInfo>> ref = cache.get(beanType);
				if (ref != null) {
					return ref.get();
				}
				return null;
			} finally {
				readLock.unlock();
			}
		}

		public static void put(Class<?> beanType, Map<String, BeanPropertyInfo> map) {
			writeLock.lock();
			try {
				cache.put(beanType, new SoftReference<>(map));
			} finally {
				writeLock.unlock();
			}
		}

	}

	/**
	 * 对类的属性进行分类
	 * <p>将属性划分为 setter、getter、field 三种类型</p>
	 *
	 * @param beanType Bean 类型
	 * @return 分类结果
	 */
	public static Classification classify(Class<?> beanType) {
		Map<String, BeanPropertyInfo> properties = mapOf(beanType);
		List<BeanPropertyInfo> setters = new ArrayList<>();
		List<BeanPropertyInfo> getters = new ArrayList<>();
		List<BeanPropertyInfo> fields = new ArrayList<>();
		for (Map.Entry<String, BeanPropertyInfo> entry : properties.entrySet()) {
			BeanPropertyInfo beanPropertyInfo = entry.getValue();
			if (beanPropertyInfo.getWriteMethod() != null) {
				setters.add(beanPropertyInfo);
			}
			if (beanPropertyInfo.getReadMethod() != null) {
				getters.add(beanPropertyInfo);
			}
			if (beanPropertyInfo.getField() != null) {
				fields.add(beanPropertyInfo);
			}
		}
		Classification rs = new Classification();
		rs.properties = properties;
		rs.setters = setters;
		rs.getters = getters;
		rs.fields = fields;
		return rs;
	}

	public static class Classification {
		public Map<String, BeanPropertyInfo> properties;
		public List<BeanPropertyInfo> setters;
		public List<BeanPropertyInfo> getters;
		public List<BeanPropertyInfo> fields;
	}
}
