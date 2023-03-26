package io.polaris.core.service.provider;

import io.polaris.core.service.StatefulServiceLoader;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
public class ServiceMapProvider<S> implements Iterable<Map.Entry<String, S>> {

	private final Supplier<Map<String, S>> factory;
	private volatile boolean initialized = false;
	private Map<String, S> service;

	public ServiceMapProvider(Supplier<Map<String, S>> factory) {
		this.factory = factory;
	}


	public static <S> ServiceMapProvider<S> of(Supplier<Map<String, S>> factory) {
		return new ServiceMapProvider<>(factory);
	}

	public static <S> ServiceMapProvider<S> of(Class<S> clazz) {
		Supplier<Map<String, S>> supplier = () -> StatefulServiceLoader.load(clazz).serviceMap();
		return new ServiceMapProvider<>(supplier);
	}

	public Optional<Map<String, S>> optional() {
		return Optional.ofNullable(get());
	}

	public Map<String, S> get() {
		if (!initialized) {
			synchronized (this) {
				if (!initialized) {
					service = factory.get();
					if (service == null) {
						service = Collections.emptyMap();
					}
					initialized = true;
				}
			}
		}
		return service;
	}

	@Override
	public Iterator<Map.Entry<String, S>> iterator() {
		return get().entrySet().iterator();
	}

	public Set<Map.Entry<String, S>> entrySet() {
		return get().entrySet();
	}

}
