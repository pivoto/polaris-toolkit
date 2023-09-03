package io.polaris.core.service;

import io.polaris.core.log.ILogger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8
 */
public class ServiceLoader<S> implements Iterable<Service<S>> {
	private static final ILogger log = ILogger.of(ServiceLoader.class);
	public static final String[] PREFIX = {"META-INF/services/"};
	private final Class<S> type;
	private final ClassLoader loader;
	private final AtomicBoolean loaded = new AtomicBoolean(false);
	private List<Service<S>> providers;
	private List<Service<S>> wrappers;
	private Map<String, Service<S>> namings;

	public ServiceLoader(final Class<S> type, final ClassLoader loader) {
		this.type = type;
		this.loader = loader == null ? defaultClassLoader() : loader;
	}

	public ServiceLoader(final Class<S> type) {
		this(type, defaultClassLoader());
	}

	public static <S> ServiceLoader<S> of(Class<S> type) {
		return new ServiceLoader<>(type);
	}

	public static <S> ServiceLoader<S> of(Class<S> type, ClassLoader loader) {
		return new ServiceLoader<>(type, loader);
	}

	public static ClassLoader defaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ignored) {
		}
		if (cl == null) {
			cl = ServiceLoader.class.getClassLoader();
			if (cl == null) {
				try {
					cl = ClassLoader.getSystemClassLoader();
				} catch (Throwable ignored) {
				}
			}
		}
		return cl;
	}

	public List<Service<S>> getProviders() {
		load();
		return providers;
	}

	public Map<String, Service<S>> getNamings() {
		load();
		return namings;
	}

	@Override
	public Iterator<Service<S>> iterator() {
		load();
		return providers.iterator();
	}

	private void load() {
		if (!loaded.get()) {
			synchronized (this) {
				if (!loaded.get()) {
					loadClasses();
					loaded.set(true);
				}
			}
		}
	}

	private void loadClasses() {
		final List<Class<S>> classes = new ArrayList<>();
		loadDirectory(classes);
		List<Service<S>> services = new ArrayList<>();
		List<Service<S>> wrappers = new ArrayList<>();
		Map<String, Service<S>> namings = new HashMap<>();
		if (!classes.isEmpty()) {
			List<Service<S>> ordered = new ArrayList<>();
			List<Service<S>> others = new ArrayList<>();
			List<Service<S>> defaults = new ArrayList<>();

			Function<S, S> wrapperBuilder = instance -> {
				if (this.wrappers != null && !this.wrappers.isEmpty()) {
					int count = this.wrappers.size();
					for (int i = count - 1; i >= 0; i--) {
						Service<S> wrapper = this.wrappers.get(i);
						instance = wrapper.newPureInstance(new Class[]{type}, new Object[]{instance});
					}
				}
				return instance;
			};
			for (Class o : classes) {
				Class<S> clazz = (Class<S>) o;
				Map<String, String> properties = new HashMap<>();
				String serviceName = null;

				if (clazz.isAnnotationPresent(ServiceName.class)) {
					serviceName = clazz.getAnnotation(ServiceName.class).value();
					properties.put("name", serviceName);
				}
				if (clazz.isAnnotationPresent(ServiceProperty.class)) {
					ServiceProperty anno = clazz.getAnnotation(ServiceProperty.class);
					properties.put(anno.name(), anno.value());
				} else if (clazz.isAnnotationPresent(ServiceProperties.class)) {
					ServiceProperties annos = clazz.getAnnotation(ServiceProperties.class);
					for (ServiceProperty anno : annos.value()) {
						properties.put(anno.name(), anno.value());
					}
				}

				properties = properties.isEmpty() ? null : Collections.unmodifiableMap(properties);
				if (clazz.isAnnotationPresent(ServiceOrder.class)) {
					int order = clazz.getAnnotation(ServiceOrder.class).value();
					ordered.add(new Service<S>(clazz, properties, serviceName, order, wrapperBuilder));
				} else if (clazz.isAnnotationPresent(ServiceDefault.class)) {
					int order = clazz.getAnnotation(ServiceDefault.class).value();
					defaults.add(new Service<S>(clazz, properties, serviceName, order, wrapperBuilder));
				} else {
					others.add(new Service<S>(clazz, properties, serviceName, 0, wrapperBuilder));
				}
			}
			Collections.sort(ordered, Service.defaultComparator);
			Collections.sort(others, Service.defaultComparator);
			Collections.sort(defaults, Service.defaultComparator);

			for (List<Service<S>> list : new List[]{ordered, others, defaults}) {
				for (Service<S> service : list) {
					if (isWrapper(service.getServiceClass())) {
						wrappers.add(service);
					} else {
						services.add(service);
						if (service.getServiceName() != null) {
							namings.putIfAbsent(service.getServiceName(), service);
						}
					}
				}
			}

		}
		this.providers = Collections.unmodifiableList(services);
		this.wrappers = Collections.unmodifiableList(wrappers);
		this.namings = Collections.unmodifiableMap(namings);
	}

	private void loadDirectory(List<Class<S>> classes) {
		String typeName = type.getName();
		for (String dir : PREFIX) {
			String filename = dir + typeName;
			try {
				Enumeration<URL> urls = loader.getResources(filename);
				while (urls.hasMoreElements()) {
					URL url = urls.nextElement();
					loadResources(classes, url);
				}
			} catch (Throwable ignored) {
			}
		}
	}

	private boolean isWrapper(Class<?> clazz) {
		Constructor<?>[] constructors = clazz.getConstructors();
		for (Constructor<?> constructor : constructors) {
			if (constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0] == type) {
				return true;
			}
		}
		return false;
	}

	private void loadResources(final List<Class<S>> classes, final URL url) throws IOException {
		try (InputStream in = url.openStream();) {
			BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				line = line.trim().replaceAll("#.*", "");
				if (line.length() > 0 && !line.startsWith("#")) {
					try {
						Class<?> clazz = Class.forName(line, true, loader);
						if (type.isAssignableFrom(clazz)) {
							classes.add((Class<S>) clazz);
						} else {
							log.warn("加载服务类失败，类型不匹配：" + line);
						}
					} catch (Throwable e) {
						log.warn("加载服务类失败：" + line, e);
					}
				}
			}
		} catch (Throwable e) {
			log.warn("加载服务类失败，资源读取错误：" + url, e);
		}
	}

	@Nullable
	private S getSingleton(@Nullable Service<S> s) {
		return Optional.ofNullable(s).map(Service::getSingleton).orElse(null);
	}

	@Nullable
	private S getPureSingleton(@Nullable Service<S> s) {
		return Optional.ofNullable(s).map(Service::getPureSingleton).orElse(null);
	}

	@Nullable
	public Service<S> get() {
		load();
		return providers.isEmpty() ? null : providers.get(0);
	}

	@Nullable
	public S getSingleton() {
		return getSingleton(get());
	}

	@Nullable
	public S getPureSingleton() {
		return getPureSingleton(get());
	}

	@Nullable
	public Service<S> get(String name) {
		load();
		return this.namings.get(name);
	}

	@Nullable
	public S getSingleton(String name) {
		return getSingleton(get(name));
	}

	@Nullable
	public S getPureSingleton(String name) {
		return getPureSingleton(get(name));
	}

	@Nullable
	public Service<S> get(String propertyName, String propertyValue) {
		load();
		for (Service<S> service : providers) {
			String value = service.getProperty(propertyName);
			if (Objects.equals(propertyValue, value)) {
				return service;
			}
		}
		return null;
	}

	@Nullable
	public S getSingleton(String propertyName, String propertyValue) {
		return getSingleton(get(propertyName, propertyValue));
	}

	@Nullable
	public S getPureSingleton(String propertyName, String propertyValue) {
		return getPureSingleton(get(propertyName, propertyValue));
	}

	@Nullable
	public Service<S> get(@Nonnull Function<Service<S>, Boolean> matcher) {
		load();
		for (Service<S> service : providers) {
			if (matcher.apply(service)) {
				return service;
			}
		}
		return null;
	}

	@Nullable
	public S getSingleton(@Nonnull Function<Service<S>, Boolean> matcher) {
		return getSingleton(get(matcher));
	}

	@Nullable
	public S getPureSingleton(@Nonnull Function<Service<S>, Boolean> matcher) {
		return getPureSingleton(get(matcher));
	}

	public void reload() {
		loaded.set(false);
		load();
	}
}
