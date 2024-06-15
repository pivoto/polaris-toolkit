package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.lang.JavaType;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Qt
 * @since 1.8
 */
public class AtomicReferenceConverter extends AbstractSimpleConverter<AtomicReference> {
	private final JavaType<AtomicReference> targetType = JavaType.of(AtomicReference.class);

	@Override
	public JavaType<AtomicReference> getTargetType() {
		return targetType;
	}

	@Override
	protected AtomicReference doConvert(Object value, JavaType<AtomicReference> targetType) {
		if (value instanceof AtomicReference) {
			return (AtomicReference) value;
		}
		return new AtomicReference<>(value);
	}
}
