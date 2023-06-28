package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;
import io.polaris.core.converter.ConverterRegistry;
import io.polaris.core.lang.Types;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;

/**
 * @author Qt
 * @since 1.8
 */
public class ReferenceConverter extends AbstractConverter<Reference> {
	private final Class<? extends Reference> targetType;

	public ReferenceConverter(Class<? extends Reference> targetType) {
		this.targetType = targetType;
	}

	@Override
	protected Reference convertInternal(Object value, Class<? extends Reference> targetType) {
		Object targetValue = null;
		final Type paramType = Types.getTypeArgument(targetType);
		if (false == Types.isUnknown(paramType)) {
			targetValue = ConverterRegistry.INSTANCE.convert(paramType, value);
		}
		if (null == targetValue) {
			targetValue = value;
		}
		if (this.targetType == WeakReference.class) {
			return new WeakReference(targetValue);
		} else if (this.targetType == SoftReference.class) {
			return new SoftReference(targetValue);
		}
		throw new UnsupportedOperationException();
	}
}
