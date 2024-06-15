package io.polaris.core.guid;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

/**
 * @author Qt
 * @since 1.8
 */
public class LocalNodeStrategy implements GuidNodeStrategy {
	private static final Map<String, LocalNodeStrategy> cache = new ConcurrentHashMap<>();

	private final String appName;
	private final int nodeId;
	private final int bizSize;

	private LocalNodeStrategy(String appName) {
		this.appName = appName;
		this.bizSize = 12;
		this.nodeId = LocalNode.nextNodeId(appName, 12);
	}

	public static LocalNodeStrategy getInstance(@Nullable String name) {
		name = String.valueOf(name);
		LocalNodeStrategy o = cache.get(name);
		if (o == null) {
			synchronized (cache) {
				o = cache.get(name);
				if (o == null) {
					o = new LocalNodeStrategy(name);
					cache.put(name, o);
				}
			}
		}
		return o;
	}

	@Override
	public int bitSize() {
		return bizSize;
	}

	@Override
	public int nodeId() {
		return nodeId;
	}
}

