package io.polaris.core.lang.bean.property;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nullable;

import io.polaris.core.lang.annotation.Annotations;
import io.polaris.core.lang.bean.property.annotation.IgnoredProperties;
import io.polaris.core.lang.bean.property.annotation.IgnoredProperty;
import io.polaris.core.reflect.Reflects;

/**
 * @author Qt
 * @since Apr 30, 2026
 */
public class BeanProperties {
	/**
	 * 对类的属性进行分类
	 * <p>将属性划分为 setter、getter、field 三种类型</p>
	 *
	 * @param beanType Bean 类型
	 * @return 分类结果
	 */
	public static BeanPropertiesClassification classify(Class<?> beanType) {
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
		return new BeanPropertiesClassification(properties, setters, getters, fields);
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
		Map<String, IgnoredInfo> ignoredInfos = new LinkedHashMap<>();

		PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor pd : pds) {
			Method writeMethod = pd.getWriteMethod();
			Method readMethod = pd.getReadMethod();
			if (Reflects.isGetClassMethod(readMethod)) {
				continue;
			}
			String propertyName = pd.getName();
			if (writeMethod != null) {
				if (writeMethod.isAnnotationPresent(IgnoredProperty.class)) {
					writeMethod = null;
					ignoredInfos.computeIfAbsent(propertyName, IgnoredInfo::new)
						.ignoredWriter = true;
				}
			}
			if (readMethod != null) {
				if (readMethod.isAnnotationPresent(IgnoredProperty.class)) {
					readMethod = null;
					ignoredInfos.computeIfAbsent(propertyName, IgnoredInfo::new)
						.ignoredReader = true;
				}
			}
			if (writeMethod != null || readMethod != null) {
				BeanPropertyInfo info = BeanPropertyInfo.of(propertyName, writeMethod, readMethod);
				rs.put(info.getPropertyName(), info);
			}
		}

		Map<String, Field> fields = new LinkedHashMap<>();
		{
			Class<?> nextClass = beanType;
			while (nextClass != null && nextClass != Object.class) {
				// 解析类级别配置的忽略属性信息
				IgnoredProperties ignoredProperties = Annotations.getMergedAnnotation(nextClass, IgnoredProperties.class);
				if (ignoredProperties != null) {
					for (String name : ignoredProperties.value()) {
						ignoredInfos.computeIfAbsent(name, IgnoredInfo::new)
							.addIgnoredClass(nextClass);
					}
				}

				Field[] declaredFields = nextClass.getDeclaredFields();
				for (int i = 0, n = declaredFields.length; i < n; i++) {
					Field field = declaredFields[i];
					int modifiers = field.getModifiers();
					// 只支持public，视为另一种公开属性，忽略private、final、static等字段
					if (!Modifier.isPublic(modifiers)
						|| Modifier.isFinal(modifiers)
						|| Modifier.isStatic(modifiers)) {
						continue;
					}
					// 忽略字段
					if (field.isAnnotationPresent(IgnoredProperty.class)) {
						ignoredInfos.computeIfAbsent(field.getName(), IgnoredInfo::new)
							.addField(field);
						continue;
					}

					// 方法级或子类级忽略字段
					IgnoredInfo ignoredInfo = ignoredInfos.get(field.getName());
					if (ignoredInfo != null && ignoredInfo.isIgnoredField(field)) {
						continue;
					}

					// 已在属性列表中，则忽略
					if (rs.containsKey(field.getName())) {
						continue;
					}

					fields.putIfAbsent(field.getName(), field);
				}
				nextClass = nextClass.getSuperclass();
			}
		}

		// 经过字段与类级忽略配置解析后，再次过滤属性
		Iterator<Map.Entry<String, BeanPropertyInfo>> it = rs.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, BeanPropertyInfo> next = it.next();
			String name = next.getKey();
			IgnoredInfo ignoredInfo = ignoredInfos.get(name);
			if (ignoredInfo != null) {
				BeanPropertyInfo value = next.getValue();
				boolean changed = false;
				Method readMethod = value.getReadMethod();
				if (readMethod != null) {
					if (ignoredInfo.isIgnoredMethod(readMethod)) {
						readMethod = null;
						changed = true;
					}
				}
				Method writeMethod = value.getWriteMethod();
				if (writeMethod != null) {
					if (ignoredInfo.isIgnoredMethod(writeMethod)) {
						writeMethod = null;
						changed = true;
					}
				}
				if (readMethod == null && writeMethod == null) {
					it.remove();
				} else if (changed) {
					BeanPropertyInfo newInfo = BeanPropertyInfo.of(value.getPropertyName(), value.getPropertyGenericType(), value.getPropertyType(), writeMethod, readMethod, value.getField());
					next.setValue(newInfo);
				}
			}
		}

		// 追加直接字段属性
		for (Map.Entry<String, Field> entry : fields.entrySet()) {
			String name = entry.getKey();
			// 针对缺少方法的情况，添加直接字段
			if (!rs.containsKey(name)) {
				BeanPropertyInfo info = BeanPropertyInfo.of(entry.getValue());
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

	private static class IgnoredInfo {
		private final String name;
		private boolean ignoredReader;
		private boolean ignoredWriter;
		private List<Field> ignoredFields;
		private List<Class<?>> ignoredClasses;

		public IgnoredInfo(String name) {
			this.name = name;
		}

		public String name() {
			return name;
		}

		public IgnoredInfo addIgnoredClass(Class<?> clazz) {
			if (ignoredClasses == null) {
				ignoredClasses = new ArrayList<>();
			}
			ignoredClasses.add(clazz);
			return this;
		}

		public IgnoredInfo addField(Field field) {
			if (ignoredFields == null) {
				ignoredFields = new ArrayList<>();
			}
			ignoredFields.add(field);
			return this;
		}

		private boolean isIgnoredMethod(Method method) {
			if (ignoredClasses != null) {
				for (int i = 0; i < ignoredClasses.size(); i++) {
					Class<?> c = ignoredClasses.get(i);
					if (method.getDeclaringClass().isAssignableFrom(c)) {
						// 子类的忽略字段可覆盖父类
						return true;
					}
				}
			}
			if (ignoredFields != null) {
				for (int i = 0; i < ignoredFields.size(); i++) {
					Field sub = ignoredFields.get(i);
					if (method.getDeclaringClass().isAssignableFrom(sub.getType())) {
						// 子类的忽略字段可覆盖父类
						return true;
					}
				}
			}
			return false;
		}

		public boolean isIgnoredField(Field field) {
			// 存在方法级读或写配置，字段也必须忽略
			if (ignoredReader || ignoredWriter) {
				return true;
			}
			if (ignoredClasses != null) {
				for (int i = 0; i < ignoredClasses.size(); i++) {
					Class<?> c = ignoredClasses.get(i);
					if (field.getType().isAssignableFrom(c)) {
						// 子类的忽略字段可覆盖父类
						return true;
					}
				}
			}
			if (ignoredFields != null) {
				for (int i = 0; i < ignoredFields.size(); i++) {
					Field sub = ignoredFields.get(i);
					if (field.getType().isAssignableFrom(sub.getType())) {
						// 子类的忽略字段可覆盖父类
						return true;
					}
				}
			}
			return false;
		}
	}
}
