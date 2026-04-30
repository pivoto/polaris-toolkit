package io.polaris.core.lang.bean.property;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

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
	 * 从属性描述符创建 BeanPropertyInfo 实例
	 *
	 * @param propertyDescriptor Java Bean 属性描述符
	 * @return BeanPropertyInfo 实例
	 */
	public static BeanPropertyInfo of(PropertyDescriptor propertyDescriptor) {
		return of(propertyDescriptor.getName(), propertyDescriptor.getWriteMethod(), propertyDescriptor.getReadMethod());
	}

	/**
	 * 创建 BeanPropertyInfo 实例
	 *
	 * @param propertyName 属性名
	 * @param writeMethod Setter 方法
	 * @param readMethod Getter 方法
	 * @return BeanPropertyInfo 实例
	 */
	public static BeanPropertyInfo of(String propertyName, Method writeMethod, Method readMethod) {
		propertyName = Strings.trimToNull(propertyName);
		if (propertyName == null) {
			throw new IllegalArgumentException("propertyName is required");
		}
		if (readMethod == null && writeMethod == null) {
			throw new IllegalArgumentException("readMethodName or writeMethodName is required");
		}
		Type propertyGenericType = null;
		Class<?> propertyType = null;
		if (writeMethod != null) {
			propertyGenericType = writeMethod.getGenericParameterTypes()[0];
			propertyType = writeMethod.getParameterTypes()[0];
		} else {
			propertyGenericType = readMethod.getGenericReturnType();
			propertyType = readMethod.getReturnType();
		}
		return new BeanPropertyInfo(propertyName, propertyGenericType, propertyType
			, writeMethod, readMethod, null);
	}

	/**
	 * 创建 BeanPropertyInfo 实例
	 *
	 * @param field 直接访问字段
	 * @return BeanPropertyInfo 实例
	 */
	public static BeanPropertyInfo of(Field field) {
		if (field == null) {
			throw new IllegalArgumentException("field is required");
		}
		String propertyName = field.getName();
		Type propertyGenericType = field.getGenericType();
		Class<?> propertyType = field.getType();
		return new BeanPropertyInfo(propertyName, propertyGenericType, propertyType
			, null, null, field);
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
	static BeanPropertyInfo of(String propertyName, Method writeMethod, Method readMethod, Field field) {
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

		return new BeanPropertyInfo(propertyName, propertyGenericType, propertyType
			, writeMethod, readMethod, field);
	}


	static BeanPropertyInfo of(String propertyName, Type propertyGenericType, Class<?> propertyType, Method writeMethod, Method readMethod, Field field) {
		return new BeanPropertyInfo(propertyName, propertyGenericType, propertyType
			, writeMethod, readMethod, field);
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
}
