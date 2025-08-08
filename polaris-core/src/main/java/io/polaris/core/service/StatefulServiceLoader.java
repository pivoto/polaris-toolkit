package io.polaris.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author Qt
 * @version Jan 13, 2022
 * @since 1.8
 */
public class StatefulServiceLoader<S> implements Iterable<S> {
	private static final Logger log = Loggers.of(StatefulServiceLoader.class);
	private final static Map<Class<?>, State<?>> store = new ConcurrentHashMap<>();
	private final ServiceLoader<S> serviceLoader;

	private StatefulServiceLoader(ServiceLoader<S> serviceLoader) {
		this.serviceLoader = serviceLoader;
	}

	private StatefulServiceLoader(Class<S> service, ClassLoader loader) {
		this(ServiceLoader.of(service, loader));
	}

	public static <S> StatefulServiceLoader<S> load(Class<S> service) {
		return load(service, Thread.currentThread().getContextClassLoader());
	}

	public static <S> StatefulServiceLoader<S> load(Class<S> service, ClassLoader classLoader) {
		StatefulServiceLoader<S> loader = null;
		State<S> ref = (State<S>) store.get(service);
		if (ref == null) {
			synchronized (store) {
				ref = (State<S>) store.get(service);
				if (ref == null) {
					loader = new StatefulServiceLoader<>(service, classLoader);
					ref = new State<>(classLoader, loader);
					store.put(service, ref);
				}
			}
		}
		if (loader == null) {
			loader = ref.getByClassLoader(classLoader);
			if (loader == null) {
				synchronized (ref) {
					loader = ref.getByClassLoader(classLoader);
					if (loader == null) {
						loader = new StatefulServiceLoader<>(service, classLoader);
						ref.update(classLoader, loader);
					}
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

	@Override
	public Iterator<S> iterator() {
		return new Iterator<S>() {
			final Iterator<Service<S>> iter = serviceLoader.iterator();

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


	public ServiceLoader<S> serviceLoader() {
		return serviceLoader;
	}

	public Optional<S> optionalService() {
		return Optional.ofNullable(serviceLoader.get())
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
		return Optional.ofNullable(serviceLoader.get(propertyName, propertyValue))
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
		for (Service<S> service : serviceLoader) {
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
		serviceLoader.getNamings().forEach((k, v) -> {
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
		private Map<ClassLoader, StatefulServiceLoader<S>> map = new ConcurrentHashMap<>();
		@Getter(AccessLevel.PACKAGE)
		private ClassLoader lastClassLoader;
		@Getter(AccessLevel.PACKAGE)
		private StatefulServiceLoader<S> lastServiceLoader;

		State(ClassLoader lastClassLoader, StatefulServiceLoader<S> lastServiceLoader) {
			update(lastClassLoader, lastServiceLoader);
		}

		void update(ClassLoader lastClassLoader, StatefulServiceLoader<S> lastServiceLoader) {
			this.lastClassLoader = lastClassLoader;
			this.lastServiceLoader = lastServiceLoader;
			this.map.put(lastClassLoader, lastServiceLoader);
		}

		StatefulServiceLoader<S> getByClassLoader(ClassLoader loader) {
			if (lastClassLoader == loader) {
				return lastServiceLoader;
			}
			return map.get(loader);
		}

	}
}
