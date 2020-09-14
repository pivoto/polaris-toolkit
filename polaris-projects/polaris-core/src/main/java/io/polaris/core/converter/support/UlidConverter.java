package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.lang.JavaType;
import io.polaris.core.ulid.Ulid;

/**
 * @author Qt
 * @since 1.8
 */
public class UlidConverter extends AbstractSimpleConverter<Ulid> {
	private final JavaType<Ulid> targetType = JavaType.of(Ulid.class);

	@Override
	public JavaType<Ulid> getTargetType() {
		return targetType;
	}

	@Override
	protected Ulid doConvert(Object value, JavaType<Ulid> targetType) {
		return Ulid.from(asString(value));
	}
}
