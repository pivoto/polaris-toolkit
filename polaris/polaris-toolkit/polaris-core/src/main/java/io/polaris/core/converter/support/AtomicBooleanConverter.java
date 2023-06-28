package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Qt
 * @since 1.8
 */
public class AtomicBooleanConverter extends AbstractConverter<AtomicBoolean> {
	@Override
	protected AtomicBoolean convertInternal(Object value, Class<? extends AtomicBoolean> targetType) {
		if (value instanceof Boolean) {
			return new AtomicBoolean((Boolean) value);
		}
		if (value instanceof Number) {
			return new AtomicBoolean(0 != ((Number) value).intValue());
		}
		String valueStr = convertToStr(value);
		return new AtomicBoolean(Boolean.parseBoolean(valueStr));
	}
}
