package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.converter.ConversionException;
import io.polaris.core.converter.Converters;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8
 */
public class ReferenceConverter<T extends Reference> extends AbstractSimpleConverter<T> {
	private final JavaType<T> targetType;
	private final Function<Object,T> factory;

	public ReferenceConverter(Class<T> targetType) {
		this(JavaType.of((Type) targetType));
	}

	@SuppressWarnings("unchecked")
	public ReferenceConverter(JavaType<T> targetType) {
		this.targetType = targetType;
		if (this.targetType.getRawClass() == WeakReference.class) {
			factory = targetValue -> (T)new WeakReference<>(targetValue);
		} else if (this.targetType.getRawClass() == SoftReference.class) {
			factory = targetValue -> (T)new SoftReference<>(targetValue);
		}else{
			throw new IllegalArgumentException("目标类型不支持");
		}
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
			targetValue = Converters.convert(paramType, value);
		}
		if (targetValue == null) {
			targetValue = value;
		}
		return factory.apply(targetValue);
	}
}
