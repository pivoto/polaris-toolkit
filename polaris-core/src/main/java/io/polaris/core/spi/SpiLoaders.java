package io.polaris.core.spi;

import io.polaris.core.spi.provider.ServiceListProvider;
import io.polaris.core.spi.provider.ServiceMapProvider;
import io.polaris.core.spi.provider.ServiceProvider;

/**
 * @author Qt
 * @since Oct 24, 2025
 */
public class SpiLoaders {

	public static <S> SpiLoader<S> load(Class<S> type) {
		return SpiLoader.of(type);
	}

	public static <S> SpiLoader<S> load(Class<S> type, ClassLoader loader) {
		return SpiLoader.of(type, loader);
	}

	public static <S> StatefulSpiLoader<S> loadStateful(Class<S> service) {
		return StatefulSpiLoader.of(service);
	}

	public static <S> StatefulSpiLoader<S> loadStateful(Class<S> service, ClassLoader classLoader) {
		return StatefulSpiLoader.of(service, classLoader);
	}

	public static <S> void clearStateful(Class<S> service) {
		StatefulSpiLoader.clear(service);
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
