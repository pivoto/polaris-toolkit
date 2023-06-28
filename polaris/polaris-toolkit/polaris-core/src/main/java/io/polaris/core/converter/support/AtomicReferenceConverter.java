package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;
import io.polaris.core.converter.ConverterRegistry;
import io.polaris.core.lang.Types;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Qt
 * @since 1.8
 */
public class AtomicReferenceConverter extends AbstractConverter<AtomicReference> {
	@Override
	protected AtomicReference convertInternal(Object value, Class<? extends AtomicReference> targetType) {
		Object targetValue = null;
		final Type paramType = Types.getTypeArgument(AtomicReference.class);
		if (false == Types.isUnknown(paramType)) {
			targetValue = ConverterRegistry.INSTANCE.convert(paramType, value);
		}
		if (null == targetValue) {
			targetValue = value;
		}
		return new AtomicReference<>(targetValue);
	}
}
