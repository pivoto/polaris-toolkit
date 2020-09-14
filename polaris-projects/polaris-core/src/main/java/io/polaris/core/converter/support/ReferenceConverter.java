package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.converter.ConverterRegistry;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;

/**
 * @author Qt
 * @since 1.8
 */
public class ReferenceConverter<T extends Reference> extends AbstractSimpleConverter<T> {
	private final JavaType<T> targetType;

	public ReferenceConverter(Class<T> targetType) {
		this.targetType = JavaType.of((Type) targetType);
	}

	public ReferenceConverter(JavaType<T> targetType) {
		this.targetType = targetType;
	}

	@Override
	public JavaType<T> getTargetType() {
		return targetType;
	}


	@Override
	protected T doConvert(Object value, JavaType<T> targetType) {
		Object targetValue = null;
		final Type paramType = targetType.getActualType(Reference.class, 0);
		if (!Types.isUnknown(paramType)) {
			targetValue = ConverterRegistry.INSTANCE.convert(paramType, value);
		}
		if (targetValue == null) {
			targetValue = value;
		}
		if (this.targetType.getRawClass() == WeakReference.class) {
			return (T) new WeakReference(targetValue);
		} else if (this.targetType.getRawClass() == SoftReference.class) {
			return (T) new SoftReference(targetValue);
		}
		throw new UnsupportedOperationException();
	}
}
