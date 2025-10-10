package io.polaris.core.converter;

import io.polaris.core.io.Serializations;
import io.polaris.core.json.JsonSerializer;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.lang.copier.Copiers;
import io.polaris.core.lang.copier.CopyOptions;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.service.StatefulServiceLoader;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

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
		this.copyOptions = copyOptions.converter((t, s) -> Converters.convert(t, s));
	}

	@Override
	public JavaType<T> getTargetType() {
		return this.targetType;
	}

	@Override
	protected <S> T doConvert(@Nonnull S value, JavaType<T> targetType, JavaType<S> sourceType) {
		if (sourceType.getRawType() instanceof Class) {
			if (targetType.getRawClass().isAssignableFrom((Class<?>) sourceType.getRawType())){
				return (T) value;
			}
		} else if (targetType.getRawType() == sourceType.getRawType()) {
			return (T) value;
		}

		if (value instanceof Map || Beans.isBeanClass(value.getClass())) {
			T target = Reflects.newInstanceIfPossible(targetType.getRawClass());
			return Copiers.copy(value, targetType.getRawType(), target, copyOptions);
		}
		if (value instanceof byte[]) {
			return (T) Serializations.deserialize((byte[]) value);
		}

		if (value instanceof CharSequence) {
			// 扩展json实现，
			Optional<JsonSerializer> optional = StatefulServiceLoader.load(JsonSerializer.class).optionalService();
			if (optional.isPresent()) {
				String json = value.toString();
				return optional.get().deserialize(json, targetType.getRawType());
			}
		}

		throw new ConversionException("源对象类型不支持");
	}
}
