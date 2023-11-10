package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.lang.JavaType;
import io.polaris.core.reflect.Reflects;

import java.lang.reflect.Type;

/**
 * @author Qt
 * @since 1.8
 */
public class ClassConverter extends AbstractSimpleConverter<Class<?>> {
	private final JavaType<Class<?>> targetType = JavaType.of((Type) Class.class);

	@Override
	public JavaType<Class<?>> getTargetType() {
		return targetType;
	}

	@Override
	protected Class<?> doConvert(Object value, JavaType<Class<?>> targetType) {
		String str = asString(value);
		try {
			Class<?> c = Reflects.loadClass(str.trim());
			return c;
		} catch (ClassNotFoundException e) {
			throw new UnsupportedOperationException(e);
		}
	}
}
