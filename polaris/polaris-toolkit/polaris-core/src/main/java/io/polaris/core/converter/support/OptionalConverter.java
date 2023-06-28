package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;

import java.util.Optional;

/**
 * @author Qt
 * @since 1.8
 */
public class OptionalConverter extends AbstractConverter<Optional<?>> {
	@Override
	protected Optional<?> convertInternal(Object value, Class<? extends Optional<?>> targetType) {
		return Optional.ofNullable(value);
	}
}
