package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.primitive.Booleans;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Qt
 * @since 1.8
 */
public class AtomicBooleanConverter extends AbstractSimpleConverter<AtomicBoolean> {
	private final JavaType<AtomicBoolean> targetType = JavaType.of(AtomicBoolean.class);

	@Override
	public JavaType<AtomicBoolean> getTargetType() {
		return targetType;
	}

	@Override
	protected AtomicBoolean doConvert(Object value, JavaType<AtomicBoolean> targetType) {
		if (value instanceof Boolean) {
			return new AtomicBoolean((Boolean) value);
		}
		if (value instanceof Number) {
			return new AtomicBoolean(0 != ((Number) value).intValue());
		}
		String valueStr = asSimpleString(value);
		return new AtomicBoolean(Booleans.parseBoolean(valueStr));
	}
}
