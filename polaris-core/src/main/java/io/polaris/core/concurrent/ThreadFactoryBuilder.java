package io.polaris.core.concurrent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import io.polaris.core.concurrent.thread.ThreadProvider;

/**
 * @author Qt
 * @since May 10, 2026
 */
public class ThreadFactoryBuilder {
	static final String DEFAULT_PREFIX = "pool";
	static final Map<String, AtomicInteger> poolNumbers = new ConcurrentHashMap<>();
	private String prefix;
	private boolean daemon = false;
	private boolean withPoolNumber = true;
	private Integer priority;
	private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
	private ThreadGroup threadGroup;

	public ThreadFactoryBuilder() {
		this(DEFAULT_PREFIX);
	}

	public ThreadFactoryBuilder(String prefix) {
		if (prefix == null) {
			prefix = DEFAULT_PREFIX;
		}
		this.prefix = prefix;
	}

	public ThreadFactoryBuilder withPrefix(String prefix) {
		if (prefix == null) {
			prefix = DEFAULT_PREFIX;
		}
		this.prefix = prefix;
		return this;
	}

	public ThreadFactoryBuilder withPoolNumber() {
		this.withPoolNumber = true;
		return this;
	}

	public ThreadFactoryBuilder withPoolNumber(boolean withPoolNumber) {
		this.withPoolNumber = withPoolNumber;
		return this;
	}

	public ThreadFactoryBuilder withDaemon(boolean daemon) {
		this.daemon = daemon;
		return this;
	}

	public ThreadFactoryBuilder withPriority(int priority) {
		if (priority < Thread.MIN_PRIORITY || priority > Thread.MAX_PRIORITY) {
			this.priority = null;
		} else {
			this.priority = priority;
		}
		return this;
	}

	public ThreadFactoryBuilder withUncaughExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
		this.uncaughtExceptionHandler = uncaughtExceptionHandler;
		return this;
	}

	public ThreadFactoryBuilder withThreadGroup(ThreadGroup threadGroup) {
		this.threadGroup = threadGroup;
		return this;
	}

	private String calculatePrefix() {
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		if (withPoolNumber) {
			AtomicInteger poolNumber = poolNumbers.computeIfAbsent(prefix, key -> new AtomicInteger(0));
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
		return sb.toString();
	}

	public ThreadFactory build() {
		String calculatedPrefix = calculatePrefix();
		final AtomicInteger tid = new AtomicInteger(0);
		return r -> {
			String name = calculatedPrefix + tid.incrementAndGet();
			Thread t = newThread(threadGroup, r, name);
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
		};
	}

	@Nonnull
	protected Thread newThread(ThreadGroup threadGroup, Runnable target, String name) {
		Thread t;
		if (threadGroup != null) {
			t = Threads.newThread(threadGroup, target, name);
		} else {
			t = Threads.newThread(target, name);
		}
		return t;
	}

	protected String getPrefix() {
		return prefix;
	}

	protected boolean isDaemon() {
		return daemon;
	}

	protected boolean isWithPoolNumber() {
		return withPoolNumber;
	}

	protected Integer getPriority() {
		return priority;
	}

	protected Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
		return uncaughtExceptionHandler;
	}

	protected ThreadGroup getThreadGroup() {
		return threadGroup;
	}
}
