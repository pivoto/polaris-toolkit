package io.polaris.core.converter;

import java.lang.reflect.Type;

/**
 * @author Qt
 * @since Jan 20, 2026
 */
@FunctionalInterface
public interface Convertable {

	<T> T to(Type type);

	default <T> T to(Class<T> type) {
		return to((Type)type);
	}

}
