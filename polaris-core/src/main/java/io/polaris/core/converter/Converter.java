package io.polaris.core.converter;

import java.lang.reflect.Type;

import io.polaris.core.spi.Spi;

/**
 * @author Qt
 */
@FunctionalInterface
@Spi
public interface Converter<T> {

	<S> T convert(Type valueType, S value);

	default <S> T convert(S value) {
		return convert(value.getClass(), value);
	}

	default <S> T convertOrDefault(Type valueType, S value, T defaultValue) {
		T t = convert(valueType, value);
		if (t == null) {
			t = defaultValue;
		}
		return t;
	}

	default <S> T convertOrDefault(S value, T defaultValue) {
		return convertOrDefault(value.getClass(), value, defaultValue);
	}

}
