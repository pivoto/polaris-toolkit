package io.polaris.core.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * @author Qt
 * @since Jan 26, 2025
 */
public class Threads {

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ignored) {
		}
	}

	public static void sleep(long millis, int nanos) {
		try {
			Thread.sleep(millis, nanos);
		} catch (InterruptedException ignored) {
		}
	}

	public static void sleep(long time, TimeUnit unit) {
		try {
			unit.sleep(time);
		} catch (InterruptedException ignored) {
		}
	}

	public static void sleep(long millis, boolean interrupt) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ignored) {
			if (interrupt) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public static void sleep(long millis, int nanos, boolean interrupt) {
		try {
			Thread.sleep(millis, nanos);
		} catch (InterruptedException ignored) {
			if (interrupt) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public static void sleep(long time, TimeUnit unit, boolean interrupt) {
		try {
			unit.sleep(time);
		} catch (InterruptedException ignored) {
			if (interrupt) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public static void join(Thread thread, long millis) {
		try {
			thread.join(millis);
		} catch (InterruptedException ignored) {
		}
	}

	public static void join(Thread thread, long millis, int nanos) {
		try {
			thread.join(millis, nanos);
		} catch (InterruptedException ignored) {
		}
	}

	public static void join(Thread thread, long time, TimeUnit unit) {
		try {
			unit.timedJoin(thread, time);
		} catch (InterruptedException ignored) {
		}
	}

	public static void join(Thread thread, long millis, boolean interrupt) {
		try {
			thread.join(millis);
		} catch (InterruptedException ignored) {
			if (interrupt) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public static void join(Thread thread, long millis, int nanos, boolean interrupt) {
		try {
			thread.join(millis, nanos);
		} catch (InterruptedException ignored) {
			if (interrupt) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public static void join(Thread thread, long time, TimeUnit unit, boolean interrupt) {
		try {
			unit.timedJoin(thread, time);
		} catch (InterruptedException ignored) {
			if (interrupt) {
				Thread.currentThread().interrupt();
			}
		}
	}

}
