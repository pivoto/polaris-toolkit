package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.lang.JavaType;

import java.util.UUID;

/**
 * @author Qt
 * @since 1.8
 */
public class UUIDConverter extends AbstractSimpleConverter<UUID> {
	private final JavaType<UUID> targetType = JavaType.of(UUID.class);

	@Override
	public JavaType<UUID> getTargetType() {
		return targetType;
	}

	@Override
	protected UUID doConvert(Object value, JavaType<UUID> targetType) {
		return UUID.fromString(asSimpleString(value));
	}
}
