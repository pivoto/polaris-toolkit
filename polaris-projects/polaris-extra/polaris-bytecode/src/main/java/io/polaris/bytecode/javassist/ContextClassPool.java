package io.polaris.bytecode.javassist;

import javassist.ClassPool;
import javassist.LoaderClassPath;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class ContextClassPool {

	private static ContextClassPool instance = new ContextClassPool();
	private Map<ClassLoader, ClassPool> pools = new HashMap<>();
	private final ClassPool defaultPool;

	private ContextClassPool() {
		ClassLoader classLoader = ContextClassPool.class.getClassLoader();
		defaultPool = new ClassPool(ClassPool.getDefault());
		defaultPool.appendClassPath(new LoaderClassPath(classLoader));
		pools.put(classLoader, defaultPool);
	}

	public static ContextClassPool instance() {
		return instance;
	}

	public ClassPool getClassPool(ClassLoader loader) {
		if (loader == null) {
			return defaultPool;
		}
		ClassPool pool = pools.get(loader);
		if (pool != null) {
			return pool;
		}
		synchronized (this) {
			pool = pools.get(loader);
			if (pool != null) {
				return pool;
			}
			pool = new ClassPool(defaultPool);
			pool.appendClassPath(new LoaderClassPath(loader));
			pools.put(loader, pool);
			return pool;
		}
	}

}
