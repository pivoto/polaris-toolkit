package io.polaris.core.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author Qt
 * @since Mar 13, 2026
 */
public class StatefulSpiLoader<S> implements Iterable<S> {
	private static final Logger log = Loggers.of(StatefulSpiLoader.class);
	private final static Map<Class<?>, StatefulSpiLoader.State<?>> store = new ConcurrentHashMap<>();
	private final SpiLoader<S> spiLoader;

	private StatefulSpiLoader(SpiLoader<S> spiLoader) {
		this.spiLoader = spiLoader;
	}

	private StatefulSpiLoader(Class<S> service, ClassLoader loader) {
		this(SpiLoader.of(service, loader));
	}

	public static <S> StatefulSpiLoader<S> of(Class<S> service) {
		return of(service, Thread.currentThread().getContextClassLoader());
	}

	@SuppressWarnings("unchecked")
	public static <S> StatefulSpiLoader<S> of(Class<S> service, ClassLoader classLoader) {
		StatefulSpiLoader<S> loader = null;
		StatefulSpiLoader.State<S> ref = (StatefulSpiLoader.State<S>) store.get(service);
		if (ref == null) {
			synchronized (store) {
				ref = (StatefulSpiLoader.State<S>) store.get(service);
				if (ref == null) {
					loader = new StatefulSpiLoader<>(service, classLoader);
					ref = new StatefulSpiLoader.State<>(classLoader, loader);
					store.put(service, ref);
				}
			}
		}
		if (loader == null) {
			loader = ref.getByClassLoader(classLoader);
			if (loader == null) {
				ref.lock.lock();
				try {
					loader = ref.getByClassLoader(classLoader);
					if (loader == null) {
						loader = new StatefulSpiLoader<>(service, classLoader);
						ref.update(classLoader, loader);
					}
				} finally {
					ref.lock.unlock();
				}
			}
		}
		return loader;
	}

	public static <S> void clear(Class<S> service) {
		store.computeIfPresent(service, (k, v) -> {
			v.map.clear();
			return null;
		});
	}

	@Nonnull
	@Override
	public Iterator<S> iterator() {
		return new Iterator<S>() {
			final Iterator<Service<S>> iter = spiLoader.iterator();

			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public S next() {
				return iter.next().getSingleton();
			}
		};
	}


	public SpiLoader<S> spiLoader() {
		return spiLoader;
	}

	public Optional<S> optionalService() {
		return Optional.ofNullable(spiLoader.get())
			.map(s -> {
				try {
					return s.getSingleton();
				} catch (Throwable e) {
					log.error(e.getMessage(), e);
					return null;
				}
			});
	}

	@Nullable
	public S service() {
		return optionalService().orElse(null);
	}

	@Nullable
	public S service(String propertyName, String propertyValue) {
		return Optional.ofNullable(spiLoader.get(propertyName, propertyValue))
			.map(s -> {
				try {
					return s.getSingleton();
				} catch (Throwable e) {
					log.error(e.getMessage(), e);
					return null;
				}
			}).orElse(null);
	}

	public List<S> serviceList() {
		List<S> list = new ArrayList<>();
		for (Service<S> service : spiLoader) {
			try {
				list.add(service.getSingleton());
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			}
		}
		return list;
	}

	public Map<String, S> serviceMap() {
		Map<String, S> map = new HashMap<>();
		spiLoader.getNamings().forEach((k, v) -> {
			try {
				map.put(k, v.getSingleton());
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			}
		});
		return map;
	}

	/**
	 * @version Jan 13, 2022
	 * @since 1.8
	 */
	static class State<S> {
		private final Map<ClassLoader, StatefulSpiLoader<S>> map = new ConcurrentHashMap<>();
		private final Lock lock = new ReentrantLock();
		@Getter(AccessLevel.PACKAGE)
		private ClassLoader lastClassLoader;
		@Getter(AccessLevel.PACKAGE)
		private StatefulSpiLoader<S> lastServiceLoader;

		State(ClassLoader lastClassLoader, StatefulSpiLoader<S> lastServiceLoader) {
			update(lastClassLoader, lastServiceLoader);
		}

		void update(ClassLoader lastClassLoader, StatefulSpiLoader<S> lastServiceLoader) {
			this.lastClassLoader = lastClassLoader;
			this.lastServiceLoader = lastServiceLoader;
			this.map.put(lastClassLoader, lastServiceLoader);
		}

		StatefulSpiLoader<S> getByClassLoader(ClassLoader loader) {
			if (lastClassLoader == loader) {
				return lastServiceLoader;
			}
			return map.get(loader);
		}

	}

}
