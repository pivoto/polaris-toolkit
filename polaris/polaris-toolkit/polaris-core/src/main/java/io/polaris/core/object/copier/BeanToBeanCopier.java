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
public class BeanToBeanCopier<S, T> extends BaseCopier<S, T> {
	private final Type targetType;

	/**
	 * @param source      来源Map
	 * @param target      目标Bean对象
	 * @param targetType  目标泛型类型
	 * @param copyOptions 拷贝选项
	 */
	public BeanToBeanCopier(S source, T target, Type targetType, CopyOptions copyOptions) {
		super(source, target, copyOptions);
		this.targetType = targetType;
	}

	@Override
	public T copy() {
		Class<?> actualEditable = target.getClass();
		if (null != options.editable) {
			actualEditable = options.editable;
		}
		Map<String, PropertyDescriptor> spds = options.ignoreCase ? new CaseInsensitiveMap(new HashMap<>()) : new HashMap<>();
		Map<String, PropertyDescriptor> tpds = options.ignoreCase ? new CaseInsensitiveMap(new HashMap<>()) : new HashMap<>();

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(source.getClass());
			for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
				Method readMethod = pd.getReadMethod();
				if (readMethod == null || Reflects.isGetClassMethod(readMethod)){
					continue;
				}
				spds.put(pd.getName(), pd);
			}
		} catch (IntrospectionException ignore) {
		}
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(actualEditable);
			for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
				Method writeMethod = pd.getWriteMethod();
				if (writeMethod == null) {
					continue;
				}
				tpds.put(pd.getName(), pd);
			}
		} catch (IntrospectionException ignore) {
		}

		spds.forEach((name, spd) -> {
			Method readMethod = spd.getReadMethod();
			name = options.editPropertyName(name);
			if (name == null) {
				return;
			}
			if (!options.isIncludePropertyName(name)) {
				return;
			}

			PropertyDescriptor tpd = tpds.get(name);
			if (tpd == null) {
				return;
			}
			Method writeMethod = tpd.getWriteMethod();
			try {
				Object value = Reflects.invoke(source, readMethod);
				Class<?> propertyType = tpd.getPropertyType();
				if (!options.filterProperty(name, propertyType, value)) {
					return;
				}
				if (!options.override) {
					Method targetReadMethod = tpd.getReadMethod();
					if (targetReadMethod != null) {
						Object orig = Reflects.invokeQuietly(target, targetReadMethod);
						if (orig != null) {
							return;
						}
					}
				}
				Object newValue = options.convert(propertyType, value);
				newValue = options.editPropertyValue(name, newValue);
				if (newValue == null && options.ignoreNull) {
					return;
				}
				Reflects.invoke(target, writeMethod, newValue);
			} catch (ReflectiveOperationException e) {
				if (!options.ignoreError) {
					throw new UnsupportedOperationException(e);
				}
			}
		});
		return this.target;
	}
}
