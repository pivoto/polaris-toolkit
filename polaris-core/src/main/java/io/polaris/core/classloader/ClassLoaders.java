package io.polaris.core.classloader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

/**
 * @author Qt
 * @since Oct 01, 2024
 */
public enum ClassLoaders {

	INSTANCE;

	private final AtomicLong prependIndex = new AtomicLong(0);
	private final AtomicLong appendIndex = new AtomicLong(0);
	// 使用WeakHashMap,如果类加载器被回收释放时,则自动移除
	private final Map<ClassLoader, Long> loaders = new WeakHashMap<>();
	private volatile long changeId = 0L;

	ClassLoaders() {
		appendClassLoader(Thread.currentThread().getContextClassLoader());
		appendClassLoader(ClassLoaders.class.getClassLoader());
	}

	public void appendClassLoader(ClassLoader loader) {
		loaders.putIfAbsent(loader, appendIndex.getAndIncrement());
		synchronized (this) {
			changeId++;
		}
	}

	public void prependClassLoader(ClassLoader loader) {
		loaders.putIfAbsent(loader, prependIndex.decrementAndGet());
		synchronized (this) {
			changeId++;
		}
	}

	public long changeId() {
		return changeId;
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Collection<ClassLoader> classLoaders = getClassLoaders(null);
		return loadClass(classLoaders, name);
	}

	public Class<?> loadClass(Collection<ClassLoader> classLoaders, String name) throws ClassNotFoundException {
		for (ClassLoader classLoader : classLoaders) {
			try {
				Class<?> c = classLoader.loadClass(name);
				return c;
			} catch (Throwable ignored) {
			}
		}
		throw new ClassNotFoundException(name);
	}

	public Collection<ClassLoader> getClassLoaders(Predicate<ClassLoader> predicate) {
		TreeMap<Long, ClassLoader> map = new TreeMap<>();
		// 按装入的顺序排序,越早装入的优先级越高
		for (Map.Entry<ClassLoader, Long> entry : loaders.entrySet()) {
			if (predicate != null && !predicate.test(entry.getKey())) {
				continue;
			}
			map.put(entry.getValue(), entry.getKey());
		}
		return map.values();
	}


	public ClassLoader newTargetSideClassLoader(ClassLoader targetClassLoader) {
		return new TargetSideClassLoader(targetClassLoader);
	}

	static class TargetSideClassLoader extends ClassLoader {
		private final ClassLoader targetClassLoader;

		public TargetSideClassLoader(ClassLoader targetClassLoader) {
			super(targetClassLoader);
			this.targetClassLoader = targetClassLoader;
		}

		private Class<?> readClass(final String name) {
			String path = '/' + name.replace('.', '/') + ".class";
			try (InputStream in = targetClassLoader.getResourceAsStream(path);) {
				if (in != null) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					byte[] buf = new byte[4096];
					for (int i = in.read(buf); i != -1; i = in.read(buf)) {
						bos.write(buf, 0, i);
					}
					byte[] bytes = bos.toByteArray();
					Class<?> clazz = defineClass(name, bytes, 0, bytes.length);
					return clazz;
				}
			} catch (Throwable e) {
				// noinspection CallToPrintStackTrace
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
			synchronized (getClassLoadingLock(name)) {
				Class<?> loadedClass = findLoadedClass(name);
				if (loadedClass == null) {
					try {
						loadedClass = Class.forName(name, false, targetClassLoader);
					} catch (ClassNotFoundException ignored) {
					}
				}
				if (loadedClass == null) {
					try {
						loadedClass = readClass(name);
					} catch (Throwable ignored) {
					}
				}
				if (loadedClass != null) {
					if (resolve) {
						resolveClass(loadedClass);
					}
					return loadedClass;
				}
			}
			try {
				Collection<ClassLoader> classLoaders = ClassLoaders.INSTANCE.getClassLoaders(cl ->
					cl != targetClassLoader && cl != TargetSideClassLoader.this);
				Class<?> c = ClassLoaders.INSTANCE.loadClass(classLoaders, name);
				return c;
			} catch (ClassNotFoundException ignored) {
			}
			return super.loadClass(name, resolve);
		}

	}
}
