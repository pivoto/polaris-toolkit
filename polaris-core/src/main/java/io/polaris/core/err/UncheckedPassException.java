package io.polaris.core.err;

import io.polaris.core.function.Callable;

/**
 * @author Qt
 * @since Jan 29, 2026
 */
public class UncheckedPassException extends UncheckedException {
	private static final long serialVersionUID = 1L;
	private final Throwable target;

	public UncheckedPassException(Throwable target) {
		super((Throwable) null);
		this.target = target;
	}

	public UncheckedPassException(Throwable target, String errMsg) {
		super(errMsg, (Throwable) null);
		this.target = target;
	}


	public static <V> V doCallable(Callable<V> callable) throws UncheckedPassException {
		try {
			return callable.call();
		} catch (Exception e) {
			throw new UncheckedPassException(e);
		}
	}

	public static void doRunnable(Runnable runnable) throws UncheckedPassException {
		try {
			runnable.run();
		} catch (Exception e) {
			throw new UncheckedPassException(e);
		}
	}

	public static <V> V doCallable(Callable<V> callable, String errMsg) throws UncheckedPassException {
		try {
			return callable.call();
		} catch (Exception e) {
			throw new UncheckedPassException(e, errMsg);
		}
	}

	public static void doRunnable(Runnable runnable, String errMsg) throws UncheckedPassException {
		try {
			runnable.run();
		} catch (Exception e) {
			throw new UncheckedPassException(e, errMsg);
		}
	}


	public Throwable getTargetException() {
		return target;
	}

	@Override
	public Throwable getCause() {
		return target;
	}
}
