package io.polaris.core.concurrent.thread;

/**
 * @author Qt
 * @since May 18, 2026
 */
public class InternalRunnable implements Runnable {
	private final Runnable target;

	public InternalRunnable(Runnable target) {
		this.target = target;
	}

	@Override
	public void run() {
		try {
			target.run();
		} finally {
			InternalThreadLocal.removeAll();
		}
	}

	public static Runnable of(Runnable runnable) {
		return runnable instanceof InternalRunnable ? runnable : new InternalRunnable(runnable);
	}
}
