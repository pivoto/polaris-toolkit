package io.polaris.core.service.provider;

import io.polaris.core.service.StatefulServiceLoader;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Qt
 * @version Jan 14, 2022
 * @since 1.8
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
		return new ServiceProvider<>(() -> StatefulServiceLoader.load(clazz).service());
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
