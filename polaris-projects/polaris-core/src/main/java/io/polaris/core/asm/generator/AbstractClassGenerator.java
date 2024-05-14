package io.polaris.core.asm.generator;

import java.lang.ref.WeakReference;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import io.polaris.core.asm.internal.AsmReflects;
import io.polaris.core.asm.internal.ClassNameReader;
import io.polaris.core.consts.SystemKeys;
import io.polaris.core.err.BytecodeOperationException;
import io.polaris.core.tuple.Tuple2;
import lombok.Getter;
import org.objectweb.asm.ClassReader;

/**
 * @author Qt
 * @since May 10, 2024
 */
public abstract class AbstractClassGenerator implements ClassGenerator {

	private static volatile Map<ClassLoader, ClassLoaderData> CACHE = new WeakHashMap<>();

	private static final boolean DEFAULT_USE_CACHE = Boolean.parseBoolean(
		System.getProperty(SystemKeys.JAVA_CLASS_GENERATOR_CACHE, "true"));
	private boolean frozen = false;
	private GeneratorStrategy strategy = DefaultGeneratorStrategy.INSTANCE;
	private NamingPolicy namingPolicy = DefaultNamingPolicy.INSTANCE;
	private ClassLoader classLoader;
	private String packageName;
	private String baseName;
	private Tuple2<AbstractClassGenerator, Object> key;
	private boolean useCache = DEFAULT_USE_CACHE;
	private String className;
	private boolean attemptLoad;

	protected AbstractClassGenerator() {
		setPackageName(getClass().getPackage().getName());
		setBaseName(getClass().getSimpleName());
	}

	protected boolean isFrozen() {
		return frozen;
	}

	protected boolean isEditable() {
		return !frozen;
	}

	protected void checkState() {
		if (frozen) {
			throw new IllegalStateException();
		}
	}

	protected void setPackageName(String packageName) {
		checkState();
		this.packageName = packageName;
	}

	protected void setBaseName(String baseName) {
		checkState();
		this.baseName = baseName;
	}

	protected void setKey(Object key) {
		checkState();
		this.key = Tuple2.of(this, key);
	}

	protected final String getClassName() {
		return className;
	}

	private void setClassName(String className) {
		checkState();
		this.className = className;
	}

	private String generateClassName(Predicate<String> dupChecker) {
		return namingPolicy.getClassName(packageName, baseName, key.getSecond(), dupChecker);
	}

	public void setClassLoader(ClassLoader classLoader) {
		checkState();
		this.classLoader = classLoader;
	}


	public void namingPolicy(NamingPolicy namingPolicy) {
		checkState();
		if (namingPolicy == null)
			namingPolicy = DefaultNamingPolicy.INSTANCE;
		this.namingPolicy = namingPolicy;
	}

	public NamingPolicy namingPolicy() {
		return namingPolicy;
	}

	public void useCache(boolean useCache) {
		checkState();
		this.useCache = useCache;
	}

	public boolean useCache() {
		return useCache;
	}

	public void attemptLoad(boolean attemptLoad) {
		checkState();
		this.attemptLoad = attemptLoad;
	}

	public boolean attemptLoad() {
		return attemptLoad;
	}

	protected void strategy(GeneratorStrategy strategy) {
		checkState();
		if (strategy == null)
			strategy = DefaultGeneratorStrategy.INSTANCE;
		this.strategy = strategy;
	}

	protected GeneratorStrategy strategy() {
		return strategy;
	}


	public ClassLoader getClassLoader() {
		ClassLoader t = classLoader;
		if (t == null) {
			t = getDefaultClassLoader();
		}
		if (t == null) {
			t = Thread.currentThread().getContextClassLoader();
		}
		if (t == null) {
			t = getClass().getClassLoader();
		}
		if (t == null) {
			throw new IllegalStateException("Cannot determine classloader");
		}
		return t;
	}

	protected abstract ClassLoader getDefaultClassLoader();

	protected ProtectionDomain getProtectionDomain() {
		return null;
	}

	/*protected Class<?> generate(Object key) {
		setKey(key);
		return generate();
	} */

	protected Class<?> generateClass() {
		ClassLoader loader = getClassLoader();
		Map<ClassLoader, ClassLoaderData> cache = CACHE;
		ClassLoaderData data = cache.get(loader);
		if (data == null) {
			synchronized (AbstractClassGenerator.class) {
				cache = CACHE;
				data = cache.get(loader);
				if (data == null) {
					Map<ClassLoader, ClassLoaderData> newCache = new WeakHashMap<>(cache);
					data = new ClassLoaderData();
					newCache.put(loader, data);
					CACHE = newCache;
				}
			}
		}
		Class<?> clazz = data.get(this, loader, useCache());
		// freeze
		this.frozen = true;
		return clazz;
	}

	protected Class<?> generate(ClassLoader classLoader, ClassLoaderData data) {
		try {
			if (classLoader == null) {
				throw new IllegalArgumentException("ClassLoader is null ");
			}
			synchronized (classLoader) {
				String name = generateClassName(data.getDupChecker());
				data.reserveName(name);
				this.setClassName(name);
			}
			Class<?> clazz;
			if (attemptLoad) {
				try {
					clazz = classLoader.loadClass(getClassName());
					return clazz;
				} catch (ClassNotFoundException e) {
					// ignore
				}
			}
			byte[] b = strategy.generate(this);
			String className = ClassNameReader.getClassName(new ClassReader(b));
			ProtectionDomain protectionDomain = getProtectionDomain();
			synchronized (classLoader) { // just in case
				if (protectionDomain == null) {
					clazz = AsmReflects.defineClass(className, b, classLoader);
				} else {
					clazz = AsmReflects.defineClass(className, b, classLoader, protectionDomain);
				}
			}
			return clazz;
		} catch (RuntimeException | Error e) {
			throw e;
		} catch (Exception e) {
			throw new BytecodeOperationException(e);
		}
	}


	protected static class ClassLoaderData {
		private final Set<String> reservedClassNames = new HashSet<>();
		private final Map<Tuple2<AbstractClassGenerator, Object>, WeakReference<Class<?>>> generatedClasses = new ConcurrentHashMap<>();
		@Getter
		private final Predicate<String> dupChecker = reservedClassNames::contains;

		public ClassLoaderData() {
		}

		public void reserveName(String name) {
			reservedClassNames.add(name);
		}

		public Class<?> get(AbstractClassGenerator generator, ClassLoader classLoader, boolean useCache) {
			if (!useCache) {
				return generator.generate(classLoader, ClassLoaderData.this);
			} else {
				WeakReference<Class<?>> cachedValue = generatedClasses.computeIfAbsent(generator.key, k -> new WeakReference<>(generator.generate(classLoader, this)));
				return cachedValue.get();
			}
		}

	}

}
