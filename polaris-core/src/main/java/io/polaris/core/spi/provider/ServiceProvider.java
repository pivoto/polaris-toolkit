package io.polaris.core.spi.provider;

import java.util.Optional;
import java.util.function.Supplier;

import io.polaris.core.spi.SpiLoaders;

/**
 * @author Qt
 * @since Jan 14, 2022
 */
public class ServiceProvider<S> {

	private final Supplier<S> factory;
	private volatile boolean initialized = false;
	private S service;

	public ServiceProvider(Supplier<S> factory) {
		this.factory = factory;
	}

	public static <S> ServiceProvider<S> of(Supplier<S> factory) {
		return new ServiceProvider<>(factory);
	}

	public static <S> ServiceProvider<S> of(Class<S> clazz) {
		return new ServiceProvider<>(() -> SpiLoaders.loadStateful(clazz).service());
	}

	public Optional<S> optional() {
		return Optional.ofNullable(get());
	}

	public S get() {
		if (!initialized) {
			synchronized (this) {
				if (!initialized) {
					service = factory.get();
					initialized = true;
				}
			}
		}
		return service;
	}
}
