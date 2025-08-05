package io.polaris.core.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Qt
 * @since 1.8
 */
public class PooledThreadFactory implements ThreadFactory {
	private static final AtomicInteger poolNumber = new AtomicInteger(0);
	private final AtomicInteger tid = new AtomicInteger(0);
	private final int pid;
	private String prefix;
	private boolean daemon;
	private boolean withPoolNumber = true;

	public PooledThreadFactory() {
		this.pid = poolNumber.incrementAndGet();
	}

	public PooledThreadFactory(String prefix) {
		this();
		this.prefix = prefix;
	}

	public PooledThreadFactory withPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public PooledThreadFactory withPoolNumber() {
		this.withPoolNumber = true;
		return this;
	}

	public PooledThreadFactory withPoolNumber(boolean withPoolNumber) {
		this.withPoolNumber = withPoolNumber;
		return this;
	}

	public PooledThreadFactory withDaemon(boolean daemon) {
		this.daemon = daemon;
		return this;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r, (prefix == null ? "pool" : prefix) + (withPoolNumber ? "-" + pid + "-" : "-") + tid.incrementAndGet());
		t.setDaemon(daemon);
		return t;
	}

}
