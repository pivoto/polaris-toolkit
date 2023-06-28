package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;
import io.polaris.core.reflect.Reflects;

/**
 * @author Qt
 * @since 1.8
 */
public class ClassConverter extends AbstractConverter<Class<?>> {
	@Override
	protected Class<?> convertInternal(Object value, Class<? extends Class<?>> targetType) {
		String str = convertToStr(value);
		try {
			Class<?> c = Reflects.loadClass(str.trim());
			return c;
		} catch (ClassNotFoundException e) {
			throw new UnsupportedOperationException(e);
		}
	}
}
