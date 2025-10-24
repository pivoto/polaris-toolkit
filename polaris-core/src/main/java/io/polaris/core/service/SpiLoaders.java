package io.polaris.core.service;

import io.polaris.core.service.provider.ServiceListProvider;
import io.polaris.core.service.provider.ServiceMapProvider;
import io.polaris.core.service.provider.ServiceProvider;

/**
 * @author Qt
 * @since Oct 24, 2025
 */
public class SpiLoaders {

	public static <S> ServiceLoader<S> load(Class<S> type) {
		return ServiceLoader.of(type);
	}

	public static <S> ServiceLoader<S> load(Class<S> type, ClassLoader loader) {
		return io.polaris.core.service.ServiceLoader.of(type, loader);
	}

	public static <S> StatefulServiceLoader<S> loadStateful(Class<S> service) {
		return StatefulServiceLoader.load(service);
	}

	public static <S> StatefulServiceLoader<S> loadStateful(Class<S> service, ClassLoader classLoader) {
		return StatefulServiceLoader.load(service, classLoader);
	}

	public static <S> void clearStateful(Class<S> service) {
		StatefulServiceLoader.clear(service);
	}

	public static <S> ServiceProvider<S> loadProvider(Class<S> clazz) {
		return ServiceProvider.of(clazz);
	}

	public static <S> ServiceMapProvider<S> loadMapProvider(Class<S> clazz) {
		return ServiceMapProvider.of(clazz);
	}

	public static <S> ServiceListProvider<S> loadListProvider(Class<S> clazz) {
		return ServiceListProvider.of(clazz);
	}

}
