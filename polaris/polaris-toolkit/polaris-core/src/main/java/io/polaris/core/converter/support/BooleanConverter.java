package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Qt
 * @since 1.8
 */
public class BooleanConverter extends AbstractConverter<Boolean> {
	@Override
	protected Boolean convertInternal(Object value, Class<? extends Boolean> targetType) {
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		if (value instanceof Number) {
			// 0为false，其它数字为true
			return 0 != ((Number) value).intValue();
		}
		return Boolean.parseBoolean(convertToStr(value));
	}
}
