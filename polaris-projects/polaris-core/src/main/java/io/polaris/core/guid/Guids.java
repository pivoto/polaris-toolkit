package io.polaris.core.guid;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since 1.8
 */
public class Guids {
	private static final Map<String, Guid> guidCache = new ConcurrentHashMap<>();

	public static Guid getInstance() {
		return getInstance(null);
	}

	public static Guid getInstance(String name) {
		name = String.valueOf(name);
		Guid guid = guidCache.get(name);
		if (guid == null) {
			synchronized (guidCache) {
				guid = guidCache.get(name);
				if (guid == null) {
					guid = Guid.newInstance(LocalNodeStrategy.getInstance(name));
					guidCache.put(name, guid);
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
