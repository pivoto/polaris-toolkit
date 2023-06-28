package io.polaris.core.converter;

import io.polaris.core.collection.Iterables;
import io.polaris.core.json.IJsonSerializer;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.service.StatefulServiceLoader;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * @author Qt
 * @since 1.8
 */
public abstract class AbstractConverter<T> implements Converter<T> {

	public Class<T> getTargetType() {
		return (Class<T>) Reflects.findParameterizedType(Converter.class, getClass(), 0);
	}

	@Override
	public T convert(Object value) {
		if (value == null) {
			return null;
		}
		Class<T> targetType = getTargetType();
		if (!Map.class.isAssignableFrom(targetType)
			&& !Collection.class.isAssignableFrom(targetType)) {
			if (targetType.isInstance(value)) {
				return targetType.cast(value);
			}
		}
		return convertInternal(value, targetType);
	}

	protected abstract T convertInternal(Object value, Class<? extends T> targetType);

	protected String convertToStr(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof CharSequence) {
			return value.toString();
		}
		// 扩展json实现，
		Optional<IJsonSerializer> optional = StatefulServiceLoader.load(IJsonSerializer.class).optionalService();
		if (optional.isPresent()) {
			return optional.get().serialize(value);
		}

		if (value.getClass().isArray()) {
			return Iterables.toArrayString(value);
		}
		if (value instanceof Character || value.getClass() == char.class) {
			return String.valueOf((char) value);
		}
		return value.toString();
	}


}
