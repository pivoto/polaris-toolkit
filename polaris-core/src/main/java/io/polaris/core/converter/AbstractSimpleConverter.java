package io.polaris.core.converter;

import java.util.Optional;

import io.polaris.core.collection.Iterables;
import io.polaris.core.json.JsonSerializer;
import io.polaris.core.lang.JavaType;
import io.polaris.core.service.StatefulServiceLoader;

/**
 * @author Qt
 * @since 1.8
 */
public abstract class AbstractSimpleConverter<T> extends AbstractConverter<T> {

	protected final <S> T doConvert(S value, JavaType<T> targetType, JavaType<S> sourceType) {
		return doConvert(value, targetType);
	}

//	@Override
//	public T convert(Object value) {
//		if (value == null) {
//			return null;
//		}
//		JavaType<T> targetType = getTargetType();
//		if (targetType.getRawType() instanceof Class && targetType.isInstance(value)) {
//			// 无泛型且类型匹配
//			return targetType.cast(value);
//		}
//		/*if (!Map.class.isAssignableFrom(targetType.getRawClass())
//			&& !Collection.class.isAssignableFrom(targetType.getRawClass())) {
//			if (targetType.isInstance(value)) {
//				return targetType.cast(value);
//			}
//		}*/
//		return doConvert(value, targetType);
//	}

	protected abstract T doConvert(Object value, JavaType<T> targetType);

	protected String asComplexString(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof CharSequence) {
			return value.toString();
		}
		if (value instanceof Character) {
			return String.valueOf((char) value);
		}
		// 扩展json实现，
		Optional<JsonSerializer> optional = StatefulServiceLoader.load(JsonSerializer.class).optionalService();
		if (optional.isPresent()) {
			return optional.get().serialize(value);
		}

		if (value.getClass().isArray()) {
			return Iterables.toArrayString(value);
		}
		return value.toString();
	}

	protected String asSimpleString(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof CharSequence) {
			return value.toString();
		}
		if (value instanceof Character) {
			return String.valueOf((char) value);
		}
		return value.toString();
	}


}
