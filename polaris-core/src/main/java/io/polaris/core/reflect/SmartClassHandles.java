package io.polaris.core.reflect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

/**
 * @author Qt
 * @since Sep 09, 2025
 */
public class SmartClassHandles {

	private static final Map<Class<?>, SmartClassHandle<?>> CLASS_HANDLES = new ConcurrentHashMap<>();

	@Nonnull
	public static <T> SmartClassHandle<T> newClassHandle(Class<T> clazz) {
		return new SmartClassHandle<>(clazz);
	}

	@SuppressWarnings("unchecked")
	public static <T> SmartClassHandle<T> get(Class<T> clazz) {
		return (SmartClassHandle<T>) CLASS_HANDLES.computeIfAbsent(clazz, (key) -> newClassHandle(clazz));
	}

	public static void clear() {
		CLASS_HANDLES.clear();
	}

	public static void clear(Class<?> clazz) {
		CLASS_HANDLES.remove(clazz);
	}

}
