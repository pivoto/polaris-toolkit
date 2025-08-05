package io.polaris.core.concurrent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Qt
 * @since 1.8
 */
public class GroupThreadFactory implements ThreadFactory {
	private static final Map<String, AtomicInteger> poolNumbers = new ConcurrentHashMap<>();
	private final String prefix;
	private final AtomicInteger count = new AtomicInteger(0);
	private final boolean daemon;

	private GroupThreadFactory(String prefix, boolean daemon) {
		AtomicInteger poolNumber = poolNumbers.computeIfAbsent(prefix, key -> new AtomicInteger(0));
		int n = poolNumber.getAndIncrement();
		if (n == 0) {
			this.prefix = prefix + "-";
		} else {
			this.prefix = prefix + "-" + n + "-";
		}
		this.daemon = daemon;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r, prefix + count.incrementAndGet());
		if (daemon) {
			t.setDaemon(true);
		}
		return t;
	}

	public static GroupThreadFactory newInstance(String prefix) {
		return new GroupThreadFactory(prefix, false);
	}

	public static GroupThreadFactory newInstance(String prefix, boolean daemon) {
		return new GroupThreadFactory(prefix, daemon);
	}
}
