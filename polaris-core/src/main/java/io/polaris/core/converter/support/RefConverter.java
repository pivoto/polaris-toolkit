package io.polaris.core.converter.support;

import java.lang.reflect.Type;
import java.util.function.Function;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.converter.Converters;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;
import io.polaris.core.tuple.LazyRef;
import io.polaris.core.tuple.Ref;
import io.polaris.core.tuple.ValueRef;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("rawtypes")
public class RefConverter<T extends Ref> extends AbstractSimpleConverter<T> {
	private final JavaType<T> targetType;
	private final Function<Object, T> factory;

	public RefConverter(Class<T> targetType) {
		this(JavaType.of((Type) targetType));
	}

	@SuppressWarnings("unchecked")
	public RefConverter(JavaType<T> targetType) {
		this.targetType = targetType;
		if (this.targetType.getRawClass() == ValueRef.class) {
			factory = targetValue -> (T) new ValueRef<>(targetValue);
		} else if (this.targetType.getRawClass() == LazyRef.class) {
			factory = targetValue -> (T) new LazyRef<>(() -> targetValue);
		} else if (this.targetType.getRawClass() == Ref.class) {
			factory = targetValue -> (T) new ValueRef<>(targetValue);
		} else {
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
		final Type paramType = targetType.getActualType(Ref.class, 0);
		if (!Types.isUnknown(paramType)) {
			targetValue = Converters.convert(paramType, value);
		}
		if (targetValue == null) {
			targetValue = value;
		}
		return factory.apply(targetValue);
	}
}
