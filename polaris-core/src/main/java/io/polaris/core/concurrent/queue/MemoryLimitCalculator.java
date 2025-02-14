package io.polaris.core.concurrent.queue;

import java.util.concurrent.TimeUnit;

/**
 * @author Qt
 * @since Feb 14, 2025
 */
public class MemoryLimitCalculator {
	private static final long refreshInterval = TimeUnit.MILLISECONDS.toNanos(50);
	private static volatile long refreshTime;
	private static volatile long maxAvailable;

	private static void checkAndRefresh() {
		long now = System.nanoTime();
		if (now - refreshTime >= refreshInterval) {
			refreshTime = now;
			maxAvailable = Runtime.getRuntime().freeMemory();
		}
	}

	public static long maxAvailable() {
		checkAndRefresh();
		return maxAvailable;
	}

	public static long calculate(double percentage) {
		checkAndRefresh();
		if (percentage <= 0) {
			return 0;
		}
		if (percentage >= 1) {
			return maxAvailable();
		}
		return (long) (maxAvailable() * percentage);
	}

	public static long defaultLimit() {
		checkAndRefresh();
		return (long) (maxAvailable() * 0.8);
	}
}
