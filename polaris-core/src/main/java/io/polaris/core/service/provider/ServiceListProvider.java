package io.polaris.core.service.provider;

import io.polaris.core.service.StatefulServiceLoader;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Qt
 * @version Jan 14, 2022
 * @since 1.8
 */
public class ServiceListProvider<S> implements Iterable<S> {

	private final Supplier<List<S>> factory;
	private volatile boolean initialized = false;
	private List<S> service;

	public ServiceListProvider(Supplier<List<S>> factory) {
		this.factory = factory;
	}

	public static <S> ServiceListProvider<S> of(Supplier<List<S>> factory) {
		return new ServiceListProvider<>(factory);
	}

	public static <S> ServiceListProvider<S> of(Class<S> clazz) {
		return new ServiceListProvider<>(() -> StatefulServiceLoader.load(clazz).serviceList());
	}

	public Optional<List<S>> optional() {
		return Optional.ofNullable(get());
	}

	public List<S> get() {
		if (!initialized) {
			synchronized (this) {
				if (!initialized) {
					service = factory.get();
					if(service == null){
						service = Collections.emptyList();
					}
					initialized = true;
				}
			}
		}
		return service;
	}

	@Override
	public Iterator<S> iterator() {
		return get().iterator();
	}
}
