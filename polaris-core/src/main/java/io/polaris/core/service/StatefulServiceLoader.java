package io.polaris.core.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.annotation.Alternative;
import io.polaris.core.spi.SpiLoader;
import io.polaris.core.spi.StatefulSpiLoader;

/**
 * @author Qt
 * @see SpiLoader
 * @see StatefulSpiLoader
 * @since Jan 13, 2022
 */
@Alternative(StatefulSpiLoader.class)
@Deprecated
public class StatefulServiceLoader<S> implements Iterable<S> {
	private final StatefulSpiLoader<S> raw;

	private StatefulServiceLoader(StatefulSpiLoader<S> raw) {
		this.raw = raw;
	}

	public static <S> StatefulServiceLoader<S> of(Class<S> service) {
		return new StatefulServiceLoader<>(StatefulSpiLoader.of(service));
	}

	public static <S> StatefulServiceLoader<S> of(Class<S> service, ClassLoader classLoader) {
		return new StatefulServiceLoader<>(StatefulSpiLoader.of(service, classLoader));
	}

	public static <S> void clear(Class<S> service) {
		StatefulSpiLoader.clear(service);
	}

	@Nonnull
	@Override
	public Iterator<S> iterator() {
		return this.raw.iterator();
	}


	public ServiceLoader<S> serviceLoader() {
		return new ServiceLoader<>(raw.spiLoader());
	}

	public Optional<S> optionalService() {
		return raw.optionalService();
	}

	@Nullable
	public S service() {
		return raw.service();
	}

	@Nullable
	public S service(String propertyName, String propertyValue) {
		return raw.service(propertyName, propertyValue);
	}

	public List<S> serviceList() {
		return raw.serviceList();
	}

	public Map<String, S> serviceMap() {
		return raw.serviceMap();
	}

}
