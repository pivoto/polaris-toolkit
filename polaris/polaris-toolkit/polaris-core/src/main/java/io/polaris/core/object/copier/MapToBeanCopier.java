package io.polaris.core.object.copier;

import io.polaris.core.map.CaseInsensitiveMap;
import io.polaris.core.reflect.Reflects;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("rawtypes")
public class MapToBeanCopier<T> extends BaseCopier<Map, T> {
	private final Type targetType;

	/**
	 * @param source      来源Map
	 * @param target      目标Bean对象
	 * @param targetType  目标泛型类型
	 * @param copyOptions 拷贝选项
	 */
	public MapToBeanCopier(Map source, T target, Type targetType, CopyOptions copyOptions) {
		super(source, target, copyOptions);
		this.targetType = targetType;
	}

	@Override
	public T copy() {
		Class<?> actualEditable = target.getClass();
		if (null != options.editable) {
			actualEditable = options.editable;
		}
		Map<String, PropertyDescriptor> pds = options.ignoreCase ? new CaseInsensitiveMap(new HashMap<>()) : new HashMap<>();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(actualEditable);
			for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
				Method writeMethod = pd.getWriteMethod();
				if (writeMethod == null) {
					continue;
				}
				pds.put(pd.getName(), pd);
			}
		} catch (IntrospectionException ignore) {
		}
		this.source.forEach((name, value) -> {
			try {
				if (null == name) {
					return;
				}
				String key = options.editPropertyName(name.toString());
				if (key == null) {
					return;
				}
				if (!options.isIncludePropertyName(key)) {
					return;
				}
				PropertyDescriptor pd = pds.isEmpty() ? null : pds.get(key);
				if (pd == null) {
					return;
				}
				Method writeMethod = pd.getWriteMethod();
				if (writeMethod == null) {
					return;
				}
				Class<?> propertyType = pd.getPropertyType();
				if (!options.filterProperty(key, propertyType, value)) {
					return;
				}
				if (!options.override) {
					Method targetReadMethod = pd.getReadMethod();
					if (targetReadMethod != null) {
						Object orig = Reflects.invokeQuietly(target, targetReadMethod);
						if (orig != null) {
							return;
						}
					}
				}

				Object newValue = options.convert(propertyType, value);
				newValue = options.editPropertyValue(key, newValue);
				if (newValue == null && options.ignoreNull) {
					return;
				}
				Reflects.invoke(target, writeMethod, newValue);
			} catch (Exception e) {
				if (!options.ignoreError) {
					throw new UnsupportedOperationException(e);
				}
			}
		});
		return this.target;
	}
}
