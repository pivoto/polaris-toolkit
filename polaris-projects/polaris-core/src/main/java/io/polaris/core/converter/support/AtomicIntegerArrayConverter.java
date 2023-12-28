package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.converter.Converters;
import io.polaris.core.lang.JavaType;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @author Qt
 * @since 1.8
 */
public class AtomicIntegerArrayConverter extends AbstractSimpleConverter<AtomicIntegerArray> {
	private final JavaType<AtomicIntegerArray> targetType = JavaType.of(AtomicIntegerArray.class);

	@Override
	public JavaType<AtomicIntegerArray> getTargetType() {
		return targetType;
	}

	@Override
	protected AtomicIntegerArray doConvert(Object value, JavaType<AtomicIntegerArray> targetType) {
		return new AtomicIntegerArray(Converters.convert(int[].class, value));
	}
}
