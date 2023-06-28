package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;
import io.polaris.core.converter.ConverterRegistry;

import java.util.concurrent.atomic.AtomicLongArray;

/**
 * @author Qt
 * @since 1.8
 */
public class AtomicLongArrayConverter extends AbstractConverter<AtomicLongArray> {
	@Override
	protected AtomicLongArray convertInternal(Object value, Class<? extends AtomicLongArray> targetType) {
		return new AtomicLongArray(ConverterRegistry.INSTANCE.convert(long[].class, value));
	}
}
