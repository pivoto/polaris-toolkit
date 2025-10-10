package io.polaris.core.converter.support;

import java.lang.reflect.Type;
import java.util.Optional;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.lang.JavaType;

/**
 * @author Qt
 * @since 1.8
 */
public class OptionalConverter<T> extends AbstractSimpleConverter<Optional<T>> {
	private final JavaType<Optional<T>> targetType = JavaType.of((Type) Optional.class);

	@Override
	public JavaType<Optional<T>> getTargetType() {
		return targetType;
	}

	@SuppressWarnings({"unchecked"})
	@Override
	protected Optional<T> doConvert(Object value, JavaType<Optional<T>> targetType) {
		return (Optional<T>) Optional.ofNullable(value);
	}
}
