package io.polaris.core.guid;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Qt
 * @since 1.8
 */
public class Guids {
	private static final Map<String, Guid> guidCache = new ConcurrentHashMap<>();
	private static LocalNodeStrategy defaultStrategy = LocalNodeStrategy.getInstance("java");
	private static final Guid defaultInstance = new Guid(defaultStrategy);

	public static void setDefaultStrategy(LocalNodeStrategy defaultStrategy) {
		Guids.defaultStrategy = defaultStrategy;
	}

	public static Guid getInstance() {
		return defaultInstance;
	}

	public static Guid getInstance(String name) {
		if (name == null) {
			return defaultInstance;
		}
		Guid guid = guidCache.get(name);
		if (guid == null) {
			synchronized (guidCache) {
				guid = guidCache.get(name);
				if (guid == null) {
					guid = new Guid(defaultStrategy);
					guidCache.put(name, guid);
				}
			}
		}
		return guid;
	}

}
