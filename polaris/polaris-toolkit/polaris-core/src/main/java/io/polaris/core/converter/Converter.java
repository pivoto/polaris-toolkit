package io.polaris.core.converter;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface Converter<T> {

	T convert(Object value);

	default T convertOrDefault(Object value, T defaultValue) {
		T t = convert(value);
		if (t == null) {
			t = defaultValue;
		}
		return t;
	}

	default T convertQuietly(Object value) {
		try {
			return convert(value);
		} catch (Exception e) {
			return null;
		}
	}

	default T convertQuietly(Object value, T defaultValue) {
		try {
			return convertOrDefault(value, defaultValue);
		} catch (Exception e) {
			return null;
		}
	}

}
