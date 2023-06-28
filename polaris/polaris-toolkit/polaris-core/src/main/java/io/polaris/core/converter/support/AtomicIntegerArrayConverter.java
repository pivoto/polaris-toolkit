package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;
import io.polaris.core.converter.ConverterRegistry;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @author Qt
 * @since 1.8
 */
public class AtomicIntegerArrayConverter extends AbstractConverter<AtomicIntegerArray> {
	@Override
	protected AtomicIntegerArray convertInternal(Object value, Class<? extends AtomicIntegerArray> targetType) {
		return new AtomicIntegerArray(ConverterRegistry.INSTANCE.convert(int[].class, value));
	}
}
