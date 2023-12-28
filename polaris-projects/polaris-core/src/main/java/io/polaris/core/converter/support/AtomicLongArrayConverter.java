package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.converter.Converters;
import io.polaris.core.lang.JavaType;

import java.util.concurrent.atomic.AtomicLongArray;

/**
 * @author Qt
 * @since 1.8
 */
public class AtomicLongArrayConverter extends AbstractSimpleConverter<AtomicLongArray> {
	private final JavaType<AtomicLongArray> targetType = JavaType.of(AtomicLongArray.class);

	@Override
	public JavaType<AtomicLongArray> getTargetType() {
		return targetType;
	}

	@Override
	protected AtomicLongArray doConvert(Object value, JavaType<AtomicLongArray> targetType) {
		return new AtomicLongArray(Converters.convert(long[].class, value));
	}
}
