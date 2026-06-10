package io.polaris.core.spi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.classpath.ClassPathResources;
import io.polaris.core.classpath.reader.ClassMetadata;
import io.polaris.core.classpath.reader.MetadataReader;
import io.polaris.core.classpath.reader.MetadataReaderFactory;
import io.polaris.core.classpath.reader.impl.DefaultMetadataReaderFactory;
import io.polaris.core.spi.condition.SpiConditionResult;
import io.polaris.core.spi.condition.SpiConditions;

/**
 * @author Qt
 * @since Mar 13, 2026
 */
public class SpiLoader<S> implements Iterable<Service<S>> {
	/// Spi相关类不使用日志组件，防止日志组件中存在SPI实现的循环依赖

	private final Class<S> type;
	private final ClassLoader loader;
	private final AtomicBoolean loaded = new AtomicBoolean(false);
	private List<Service<S>> providers;
	private List<Service<S>> wrappers;
	private Map<String, Service<S>> namings;

	public SpiLoader(final Class<S> type, final ClassLoader loader) {
		this.type = type;
		this.loader = loader == null ? defaultClassLoader() : loader;
	}

	public SpiLoader(final Class<S> type) {
		this(type, defaultClassLoader());
	}

	public static <S> SpiLoader<S> of(Class<S> type) {
		return new SpiLoader<>(type);
	}

	public static <S> SpiLoader<S> of(Class<S> type, ClassLoader loader) {
		return new SpiLoader<>(type, loader);
	}

	public static ClassLoader defaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ignored) {
		}
		if (cl == null) {
			cl = SpiLoader.class.getClassLoader();
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

	@Nonnull
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


	@SuppressWarnings({"all"})
	private void loadClasses() {
		final Set<Class<S>> classes = loadSpiClasses();
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

			SpiHelper spiHelper = SpiHelper.instance();

			for (Class o : classes) {
				Class<S> clazz = (Class<S>) o;
				Map<String, String> properties = new HashMap<>();
				String serviceName = spiHelper.getName(clazz);
				if (serviceName != null) {
					properties.put("name", serviceName);
				}
				Map<String, String> map = spiHelper.getProperties(clazz);
				if (map != null && !map.isEmpty()) {
					properties.putAll(map);
				}
				// 空集则置为null
				properties = properties.isEmpty() ? null : Collections.unmodifiableMap(properties);

				// ordered > others > defaults
				if (spiHelper.isOrdered(clazz)) {
					// 有序服务
					Integer order = spiHelper.getOrder(clazz);
					if (order == null) {
						order = Integer.MAX_VALUE;
					}
					ordered.add(new Service<S>(clazz, properties, serviceName, order, wrapperBuilder));
				} else if (spiHelper.isDefault(clazz)) {
					// 默认服务
					Integer order = spiHelper.getDefaultOrder(clazz);
					if (order == null) {
						order = Integer.MAX_VALUE;
					}
					defaults.add(new Service<S>(clazz, properties, serviceName, order, wrapperBuilder));
				} else {
					// 其他服务
					others.add(new Service<S>(clazz, properties, serviceName, 0, wrapperBuilder));
				}
			}
			Collections.sort(ordered, Service.defaultComparator);
			Collections.sort(others, Service.defaultComparator);
			Collections.sort(defaults, Service.defaultComparator);

			// ordered > others > defaults
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

	private boolean isWrapper(Class<?> clazz) {
		Constructor<?>[] constructors = clazz.getConstructors();
		for (Constructor<?> constructor : constructors) {
			if (constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0] == type) {
				return true;
			}
		}
		return false;
	}

	private Set<Class<S>> loadSpiClasses() {
		Set<String> classNames = new LinkedHashSet<>();
		loadDirectory(classNames);
		loadMetadata(classNames);
		Set<Class<S>> classes = new LinkedHashSet<>();
		for (String className : classNames) {
			try {
				Class<?> clazz = Class.forName(className, true, loader);
				if (!type.isAssignableFrom(clazz)) {
					System.err.println("加载服务类失败，类型不匹配：" + className);
					continue;
				}
				SpiConditionResult conditionResult = SpiConditions.matches(clazz);
				if (!conditionResult.isMatched()) {
					System.err.println("加载服务类失败，条件不满足：" + className + "，原因：" + conditionResult.getMessage());
					continue;
				}
				//noinspection unchecked
				classes.add((Class<S>) clazz);
			} catch (Throwable e) {
				System.err.println("加载服务类失败：" + className + "，原因：" + e.getMessage());
			}
		}
		return classes;
	}

	private void loadMetadata(Set<String> classNames) {
		SpiHelper spiHelper = SpiHelper.instance();
		String[] patterns = spiHelper.loadingFilePatterns();
		if (patterns.length == 0) {
			return;
		}
		MetadataReaderFactory factory = new DefaultMetadataReaderFactory();
		for (String pattern : patterns) {
			try {
				Set<String> resourceNames = ClassPathResources.getResourceNames(loader, pattern);
				for (String resourceName : resourceNames) {
					try (InputStream inputStream = loader.getResourceAsStream(resourceName);) {
						MetadataReader metadataReader = factory.getMetadataReader(inputStream);
						ClassMetadata classMetadata = metadataReader.getClassMetadata();
						String className = classMetadata.getClassName();
						if (classNames.contains(className)) {
							// 忽略已加载的类
							continue;
						}
						if (spiHelper.isSpiClass(classMetadata, this.type)) {
							// 匹配服务类
							classNames.add(className);
						}
					} catch (Throwable e) {
						System.err.println("加载服务类失败，资源读取错误：" + resourceName + "，原因：" + e.getMessage());
					}
				}
			} catch (Throwable ignored) {
			}
		}
	}

	private void loadDirectory(Set<String> classNames) {
		String typeName = type.getName();
		String[] directories = SpiHelper.instance().loadingDirectories();
		for (String dir : directories) {
			String filename = dir + typeName;
			try {
				Enumeration<URL> urls = loader.getResources(filename);
				while (urls.hasMoreElements()) {
					URL url = urls.nextElement();
					loadResources(classNames, url);
				}
			} catch (Throwable ignored) {
			}
		}
	}

	private void loadResources(final Set<String> classNames, final URL url) {
		try (InputStream in = url.openStream()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				line = line.trim().replaceAll("#.*", "");
				if (!line.isEmpty() && !line.startsWith("#")) {
					classNames.add(line);
				}
			}
		} catch (Throwable e) {
			System.err.println("加载服务类失败，资源读取错误：" + url + "，原因：" + e.getMessage());
		}
	}

	public void reload() {
		loaded.set(false);
		load();
	}

	@Nonnull
	public Optional<Service<S>> optional() {
		return Optional.ofNullable(get());
	}

	@Nonnull
	public Optional<Service<S>> optional(String name) {
		return Optional.ofNullable(get(name));
	}

	@Nonnull
	public Optional<Service<S>> optional(String propertyName, String propertyValue) {
		return Optional.ofNullable(get(propertyName, propertyValue));
	}

	@Nonnull
	public Optional<Service<S>> optional(@Nonnull Predicate<Service<S>> matcher) {
		return Optional.ofNullable(get(matcher));
	}

	@Nullable
	public Service<S> get() {
		load();
		return providers.isEmpty() ? null : providers.get(0);
	}

	@Nullable
	public S getSingleton() {
		Service<S> service = get();
		if (service == null) {
			return null;
		}
		return service.getSingleton();
	}

	@Nullable
	public S getPureSingleton() {
		Service<S> service = get();
		if (service == null) {
			return null;
		}
		return service.getPureSingleton();
	}

	@Nullable
	public Service<S> get(String name) {
		load();
		return this.namings.get(name);
	}

	@Nullable
	public S getSingleton(String name) {
		Service<S> service = get(name);
		if (service == null) {
			return null;
		}
		return service.getSingleton();
	}

	@Nullable
	public S getPureSingleton(String name) {
		Service<S> service = get(name);
		if (service == null) {
			return null;
		}
		return service.getPureSingleton();
	}

	@Nonnull
	public List<Service<S>> getAll(String propertyName, String propertyValue) {
		load();
		List<Service<S>> rs = new ArrayList<>();
		for (Service<S> service : providers) {
			String value = service.getProperty(propertyName);
			if (Objects.equals(propertyValue, value)) {
				rs.add(service);
			}
		}
		return rs;
	}

	@Nonnull
	public List<S> getAllSingleton(String propertyName, String propertyValue) {
		List<S> rs = new ArrayList<>();
		List<Service<S>> all = getAll(propertyName, propertyValue);
		for (Service<S> service : all) {
			if (service == null) {
				continue;
			}
			S o = service.getSingleton();
			if (o == null) {
				continue;
			}
			rs.add(o);
		}
		return rs;
	}

	@Nonnull
	public List<S> getAllPureSingleton(String propertyName, String propertyValue) {
		List<S> rs = new ArrayList<>();
		List<Service<S>> all = getAll(propertyName, propertyValue);
		for (Service<S> service : all) {
			if (service == null) {
				continue;
			}
			S o = service.getPureSingleton();
			if (o == null) {
				continue;
			}
			rs.add(o);
		}
		return rs;
	}

	@Nonnull
	public List<Service<S>> getAll(@Nonnull Predicate<Service<S>> matcher) {
		load();
		List<Service<S>> rs = new ArrayList<>();
		for (Service<S> service : providers) {
			if (matcher.test(service)) {
				rs.add(service);
			}
		}
		return rs;
	}

	@Nonnull
	public List<S> getAllSingleton(@Nonnull Predicate<Service<S>> matcher) {
		List<S> rs = new ArrayList<>();
		List<Service<S>> all = getAll(matcher);
		for (Service<S> service : all) {
			if (service == null) {
				continue;
			}
			S o = service.getSingleton();
			if (o == null) {
				continue;
			}
			rs.add(o);
		}
		return rs;
	}

	@Nonnull
	public List<S> getAllPureSingleton(@Nonnull Predicate<Service<S>> matcher) {
		List<S> rs = new ArrayList<>();
		List<Service<S>> all = getAll(matcher);
		for (Service<S> service : all) {
			if (service == null) {
				continue;
			}
			S o = service.getPureSingleton();
			if (o == null) {
				continue;
			}
			rs.add(o);
		}
		return rs;
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
		Service<S> service = get(propertyName, propertyValue);
		if (service == null) {
			return null;
		}
		return service.getSingleton();
	}

	@Nullable
	public S getPureSingleton(String propertyName, String propertyValue) {
		Service<S> service = get(propertyName, propertyValue);
		if (service == null) {
			return null;
		}
		return service.getPureSingleton();
	}

	@Nullable
	public Service<S> get(@Nonnull Predicate<Service<S>> matcher) {
		load();
		for (Service<S> service : providers) {
			if (matcher.test(service)) {
				return service;
			}
		}
		return null;
	}

	@Nullable
	public S getSingleton(@Nonnull Predicate<Service<S>> matcher) {
		Service<S> service = get(matcher);
		if (service == null) {
			return null;
		}
		return service.getSingleton();
	}

	@Nullable
	public S getPureSingleton(@Nonnull Predicate<Service<S>> matcher) {
		Service<S> service = get(matcher);
		if (service == null) {
			return null;
		}
		return service.getPureSingleton();
	}
}
