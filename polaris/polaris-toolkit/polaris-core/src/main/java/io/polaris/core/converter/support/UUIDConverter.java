package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;

import java.util.UUID;

/**
 * @author Qt
 * @since 1.8
 */
public class UUIDConverter extends AbstractConverter<UUID> {
	@Override
	protected UUID convertInternal(Object value, Class<? extends UUID> targetType) {
		return UUID.fromString(convertToStr(value));
	}
}
