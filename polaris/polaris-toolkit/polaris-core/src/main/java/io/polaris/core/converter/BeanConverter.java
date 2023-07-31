package io.polaris.core.converter;

import io.polaris.core.io.Serializations;
import io.polaris.core.json.IJsonSerializer;
import io.polaris.core.lang.JavaType;
import io.polaris.core.object.Beans;
import io.polaris.core.object.Copiers;
import io.polaris.core.object.copier.CopyOptions;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.service.StatefulServiceLoader;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

/**
 * @author Qt
 * @since 1.8
 */
public class BeanConverter<T> extends AbstractConverter<T> {
	private final JavaType<T> targetType;
	private final CopyOptions copyOptions;

	public BeanConverter(Type beanType) {
		this(beanType, CopyOptions.create().ignoreError(true));
	}

	public BeanConverter(Type beanType, CopyOptions copyOptions) {
		this(JavaType.of(beanType), copyOptions);
	}

	public BeanConverter(JavaType<T> beanType, CopyOptions copyOptions) {
		this.targetType = beanType;
		this.copyOptions = copyOptions.converter((t, s) -> ConverterRegistry.INSTANCE.convert(t, s));
	}

	@Override
	public JavaType<T> getTargetType() {
		return this.targetType;
	}

	@Override
	protected <S> T doConvert(S value, JavaType<T> targetType, JavaType<S> sourceType) {
		if (sourceType.getRawType() instanceof Class) {
			if (targetType.getRawClass().isAssignableFrom((Class<?>) sourceType.getRawType())){
				return (T) value;
			}
		} else if (targetType.getRawType() == sourceType.getRawType()) {
			return (T) value;
		}

		if (value instanceof Map || Beans.isBeanClass(value.getClass())) {
			T target = Reflects.newInstanceIfPossible(targetType.getRawClass());
			return Copiers.copy(value, target, targetType.getRawType(), copyOptions);
		}
		if (value instanceof byte[]) {
			return (T) Serializations.deserialize((byte[]) value);
		}

		if (value instanceof CharSequence) {
			// 扩展json实现，
			Optional<IJsonSerializer> optional = StatefulServiceLoader.load(IJsonSerializer.class).optionalService();
			if (optional.isPresent()) {
				String json = value.toString();
				return optional.get().deserialize(json, targetType.getRawType());
			}
		}

		throw new UnsupportedOperationException();
	}
}
