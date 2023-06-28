package io.polaris.core.time;

/**
 * @author Qt
 * @since 1.8
 */
public class Times {

	public static long nanoTime(int repeat, Runnable runnable) {
		long start = System.nanoTime();
		for (int i = 0; i < repeat; i++) {
			runnable.run();
		}
		return System.nanoTime() - start;
	}

	public static long millsTime(int repeat, Runnable runnable) {
		return nanoTime(repeat, runnable) / 1000000;
	}
}
