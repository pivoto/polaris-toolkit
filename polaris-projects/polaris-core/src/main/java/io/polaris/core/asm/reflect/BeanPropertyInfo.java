package io.polaris.core.asm.reflect;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.polaris.core.string.Strings;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Qt
 * @since 1.8,  Apr 11, 2024
 */
@Getter
@EqualsAndHashCode
@ToString
public class BeanPropertyInfo {

	private final String propertyName;
	private final java.lang.reflect.Type propertyGenericType;
	private final Class propertyType;
	private final Method writeMethod;
	private final Method readMethod;
	private final Field field;

	/**
	 * 要求：存在属性方法时，直接字段为空，只能通过属性方法访问属性
	 */
	private BeanPropertyInfo(String propertyName, Type propertyGenericType, Class propertyType, Method writeMethod, Method readMethod, Field field) {
		this.propertyName = propertyName;
		this.propertyGenericType = propertyGenericType;
		this.propertyType = propertyType;
		this.writeMethod = writeMethod;
		this.readMethod = readMethod;
		this.field = field;
	}


	public static BeanPropertyInfo of(PropertyDescriptor propertyDescriptor) {
		return of(propertyDescriptor.getName(), propertyDescriptor.getWriteMethod(), propertyDescriptor.getReadMethod(), null);
	}

	private static BeanPropertyInfo of(String propertyName, Type propertyGenericType, Class propertyType, Method writeMethod, Method readMethod, Field field) {
		return new BeanPropertyInfo(propertyName, propertyGenericType, propertyType
			, writeMethod, readMethod, field);
	}

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
		}
		Type propertyGenericType = null;
		Class propertyType = null;
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

	public static List<BeanPropertyInfo> listOf(Class beanType) {
		Map<String, BeanPropertyInfo> map = mapOf(beanType);
		List<BeanPropertyInfo> list = new ArrayList<>(map.size());
		list.addAll(map.values());
		return list;
	}

	public static Map<String, BeanPropertyInfo> mapOf(Class beanType) {
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
			if (writeMethod != null || readMethod != null) {
				BeanPropertyInfo info = BeanPropertyInfo.of(pd.getName(), writeMethod, readMethod, null);
				rs.put(info.getPropertyName(), info);
			}
		}
		Map<String, Field> fields = new LinkedHashMap<>();
		{
			Class nextClass = beanType;
			while (nextClass != null && nextClass != Object.class) {
				Field[] declaredFields = nextClass.getDeclaredFields();
				for (int i = 0, n = declaredFields.length; i < n; i++) {
					Field field = declaredFields[i];
					int modifiers = field.getModifiers();
					// 忽略私有字段、final字段
					if (Modifier.isPrivate(modifiers) || Modifier.isFinal(modifiers)) {
						continue;
					}
					fields.putIfAbsent(field.getName(), field);
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

}
