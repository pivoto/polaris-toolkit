package io.polaris.core.guid;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import io.polaris.core.service.StatefulServiceLoader;

/**
 * @author Qt
 * @since 1.8
 */
public class Guids {
	private static final Map<String, Guid> guidCache = new ConcurrentHashMap<>();
	private static final StatefulServiceLoader<GuidNodeStrategyProvider> guidNodeStrategyProvider = StatefulServiceLoader.load(GuidNodeStrategyProvider.class);

	public static GuidNodeStrategy getNodeStrategy() {
		return guidNodeStrategyProvider.optionalService()
			.map(GuidNodeStrategyProvider::get)
			.orElse(LocalNodeStrategy.getInstance(null));
	}

	public static GuidNodeStrategy getNodeStrategy(String app) {
		return guidNodeStrategyProvider.optionalService()
			.map(p -> p.get(app))
			.orElse(LocalNodeStrategy.getInstance(app));
	}

	public static Guid getInstance() {
		return getInstance(detectStackTraceClassName(), null);
	}

	public static Guid getInstance(Class<?> key) {
		return getInstance(key.getName(), null);
	}

	public static Guid getInstance(String key) {
		return getInstance(key, null);
	}

	public static Guid getInstance(Class<?> key, String app) {
		return getInstance(key.getName(), app);
	}

	public static Guid getInstance(String key, String app) {
		key = String.valueOf(key);
		app = String.valueOf(app);
		Guid guid = guidCache.get(key);
		if (guid == null) {
			synchronized (guidCache) {
				guid = guidCache.get(key);
				if (guid == null) {
					guid = Guid.newInstance(Guids.getNodeStrategy(app));
					guidCache.put(key, guid);
				}
			}
		}
		return guid;
	}

	@Nullable
	static String detectStackTraceClassName() {
		String name = null;
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		for (int i = 1; i < elements.length; i++) {
			String className = elements[i].getClassName();
			if (!Guids.class.getName().equals(className)
				&& !Guid.class.getName().equals(className)
			) {
				name = className;
				break;
			}
		}
		return name;
	}
}
