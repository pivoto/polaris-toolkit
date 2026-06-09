package io.polaris.core.converter;

import java.lang.ref.Reference;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.collection.Iterables;
import io.polaris.core.jdbc.sql.VarRef;
import io.polaris.core.json.JsonSerializer;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.spi.SpiLoaders;
import io.polaris.core.tuple.Ref;
import io.polaris.core.tuple.Tuple1;

/**
 * @author Qt
 */
public abstract class AbstractSimpleConverter<T> extends AbstractConverter<T> {

	@Override
	protected final <S> T doConvert(@Nonnull S value, JavaType<T> targetType, JavaType<S> sourceType) {
		Object obj = value;
		while (true) {
			if (targetType.getRawType() instanceof Class && targetType.isInstance(obj)) {
				// 无泛型且类型匹配
				return targetType.cast(obj);
			}
			if (obj instanceof Optional) {
				obj = ((Optional<?>) obj).orElse(null);
				continue;
			}
			if (obj instanceof Reference) {
				obj = ((Reference<?>) obj).get();
				continue;
			}
			if (obj instanceof Ref) {
				obj = ((Ref<?>) obj).get();
				continue;
			}
			if (obj instanceof VarRef) {
				obj = ((VarRef<?>) obj).getValue();
				continue;
			}
			if (obj instanceof Tuple1) {
				obj = ((Tuple1<?>) obj).getFirst();
				continue;
			}
			break;
		}
		return doConvert(value, targetType);
	}

	protected abstract T doConvert(@Nullable Object value, JavaType<T> targetType);

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
		Class<?> valueClass = value.getClass();
		boolean maybeJsonObj = value instanceof Map
			|| value instanceof Iterable
			|| valueClass.isArray()
			|| Beans.isBeanClass(valueClass);
		if (maybeJsonObj) {
			// 扩展json实现，
			Optional<JsonSerializer> optional = SpiLoaders.loadStateful(JsonSerializer.class).optionalService();
			if (optional.isPresent()) {
				return optional.get().serialize(value);
			}
		}
		if (valueClass.isArray()) {
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
