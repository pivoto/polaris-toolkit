package io.polaris.core.concurrent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import io.polaris.core.string.Strings;

/**
 * @author Qt
 */
public class PooledThreadFactory implements ThreadFactory {
	static final String DEFAULT_PREFIX = "pool";
	static final AtomicInteger poolNumber = new AtomicInteger(0);
	private final AtomicInteger tid = new AtomicInteger(0);
	private String prefix;
	private boolean daemon = false;
	private boolean withPoolNumber = true;
	private Integer priority;
	private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
	private String calculatedPrefix;

	public PooledThreadFactory() {
		this(DEFAULT_PREFIX);
	}

	public PooledThreadFactory(String prefix) {
		this.prefix = prefix;
		this.calculatedPrefix = null;
	}

	private void calculatePrefix() {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		if (withPoolNumber) {
			int pid = poolNumber.getAndIncrement();
			if (pid != 0) {
				if (sb.length() > 0) {
					sb.append("-");
				}
				sb.append(pid);
			}
		}
		if (sb.length() > 0) {
			sb.append("-");
		}
		calculatedPrefix = sb.toString();
	}

	public PooledThreadFactory withPrefix(String prefix) {
		if (prefix == null) {
			prefix = DEFAULT_PREFIX;
		}
		if (Strings.equals(this.prefix, prefix)) {
			return this;
		}
		this.prefix = prefix;
		this.calculatedPrefix = null;
		return this;
	}

	public PooledThreadFactory withPoolNumber() {
		if (this.withPoolNumber) {
			return this;
		}
		this.withPoolNumber = true;
		this.calculatedPrefix = null;
		return this;
	}

	public PooledThreadFactory withPoolNumber(boolean withPoolNumber) {
		if (this.withPoolNumber == withPoolNumber) {
			return this;
		}
		this.withPoolNumber = withPoolNumber;
		this.calculatedPrefix = null;
		return this;
	}

	public PooledThreadFactory withDaemon(boolean daemon) {
		if (this.daemon == daemon) {
			return this;
		}
		this.daemon = daemon;
		this.calculatedPrefix = null;
		return this;
	}

	public PooledThreadFactory withPriority(int priority) {
		if (priority < Thread.MIN_PRIORITY || priority > Thread.MAX_PRIORITY) {
			this.priority = null;
		} else {
			this.priority = priority;
		}
		return this;
	}

	public PooledThreadFactory withUncaughExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
		this.uncaughtExceptionHandler = uncaughtExceptionHandler;
		return this;
	}

	@Override
	public Thread newThread(Runnable r) {
		if (calculatedPrefix == null) {
			calculatePrefix();
		}
		Thread t = new Thread(r, calculatedPrefix + tid.incrementAndGet());
		if (daemon) {
			t.setDaemon(true);
		}
		if (priority != null) {
			t.setPriority(priority);
		}
		if (uncaughtExceptionHandler != null) {
			t.setUncaughtExceptionHandler(uncaughtExceptionHandler);
		} else {
			Thread.UncaughtExceptionHandler ueh = Thread.getDefaultUncaughtExceptionHandler();
			if (ueh != null) {
				t.setUncaughtExceptionHandler(ueh);
			}
		}
		return t;
	}

}
