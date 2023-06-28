package io.polaris.core.object.copier;

import io.polaris.core.lang.Types;
import io.polaris.core.reflect.Reflects;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("rawtypes")
public class BeanToMapCopier<T> extends BaseCopier<T,Map> {
	private final Type targetType;
	/**
	 * @param source      来源Map
	 * @param target      目标Map对象
	 * @param targetType  目标泛型类型
	 * @param copyOptions 拷贝选项
	 */
	public BeanToMapCopier(T source, Map target, Type targetType, CopyOptions copyOptions) {
		super(source, target, copyOptions);
		this.targetType = targetType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map copy() {
		Class<?> actualEditable = source.getClass();
		if (null != options.editable) {
			actualEditable = options.editable;
		}
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(actualEditable);
			for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
				try {
					String propertyName = pd.getName();
					Method readMethod = pd.getReadMethod();
					Class<?> propertyType = pd.getPropertyType();
					if (readMethod == null || Reflects.isGetClassMethod(readMethod)) {
						continue;
					}

					propertyName = options.editPropertyName(propertyName);
					if (propertyName == null) {
						continue;
					}
					if (!options.isIncludePropertyName(propertyName)) {
						continue;
					}
					Object value = Reflects.invoke(source, readMethod);

					if (!options.filterProperty(propertyName, propertyType, value)) {
						continue;
					}
					if (!options.override && target.get(propertyName)!= null) {
						continue;
					}

					final Type[] typeArguments = Types.getTypeArguments(this.targetType);
					if(typeArguments != null){
						value = this.options.convert(typeArguments[1], value);
						value = options.editPropertyValue(propertyName, value);
					}
					if (value == null && options.ignoreNull) {
						continue;
					}
					target.put(propertyName, value);
				} catch (Exception e) {
					if (!options.ignoreError){
						throw new UnsupportedOperationException(e);
					}
				}
			}
		} catch (IntrospectionException ignore) {
		}

		return this.target;
	}
}
