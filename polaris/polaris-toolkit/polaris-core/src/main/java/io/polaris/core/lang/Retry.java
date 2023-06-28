package io.polaris.core.lang;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * @author Qt
 * @since 1.8
 */
@Slf4j
public class Retry {


	public static <T> T doRetry(Callable<T> callable, int retryTimes, long sleepTimeInMilliSecond,
															Class<? extends Throwable>... retryExceptionClasses) throws Exception {
		return doRetry(callable, retryTimes, sleepTimeInMilliSecond, false, retryExceptionClasses);
	}

	public static <T> T doRetry(Callable<T> callable, int retryTimes, long sleepTimeInMilliSecond, boolean exponential,
															Class<? extends Throwable>... retryExceptionClasses) throws Exception {
		if (null == callable) {
			throw new IllegalArgumentException("入参callable不能为空!");
		}
		if (retryTimes < 1) {
			throw new IllegalArgumentException("入参retryTimes不能小于1!");
		}
		Throwable saveException = null;
		for (int i = 0; i <= retryTimes; i++) {
			try {
				return callable.call();
			} catch (Throwable e) {
				saveException = e;
				if (!waitForRetry(i, retryTimes, sleepTimeInMilliSecond, exponential, saveException, retryExceptionClasses)) {
					throw saveException instanceof Exception ?
						(Exception) saveException : new RuntimeException(saveException);
				}
			}
		}
		throw saveException instanceof Exception ?
			(Exception) saveException : new RuntimeException(saveException);
	}

	public static void doRetry(Runnable runnable, int retryTimes, long sleepTimeInMilliSecond,
														 Class<? extends Throwable>... retryExceptionClasses) {
		doRetry(runnable, retryTimes, sleepTimeInMilliSecond, false, retryExceptionClasses);
	}

	public static void doRetry(Runnable runnable, int retryTimes, long sleepTimeInMilliSecond, boolean exponential,
														 Class<? extends Throwable>... retryExceptionClasses) {
		if (null == runnable) {
			throw new IllegalArgumentException("入参runnable不能为空!");
		}
		if (retryTimes < 1) {
			throw new IllegalArgumentException("入参retryTimes不能小于1!");
		}
		Throwable saveException = null;
		for (int i = 0; i <= retryTimes; i++) {
			try {
				runnable.run();
				return;
			} catch (Throwable e) {
				saveException = e;
				if (!waitForRetry(i, retryTimes, sleepTimeInMilliSecond, exponential, saveException, retryExceptionClasses)) {
					throw saveException instanceof RuntimeException ?
						(RuntimeException) saveException : new RuntimeException(saveException);
				}
			}
		}
		throw saveException instanceof RuntimeException ?
			(RuntimeException) saveException : new RuntimeException(saveException);
	}

	private static boolean waitForRetry(int retryTime, int retryTimes, long sleepTimeInMilliSecond, boolean exponential, Throwable exception, Class<? extends Throwable>[] retryExceptionClasses) {
		boolean retryable = true;
		if (retryExceptionClasses != null && retryExceptionClasses.length > 0) {
			retryable = false;
			for (Class<? extends Throwable> retryExceptionClass : retryExceptionClasses) {
				if (retryExceptionClass.isInstance(exception)) {
					retryable = true;
					break;
				}
			}
		}
		if (!retryable) {
			return false;
		}
		if (retryTime < retryTimes) {
			if (sleepTimeInMilliSecond > 0) {
				long sleepMills = sleepTimeInMilliSecond;
				if (exponential) {
					sleepMills = sleepTimeInMilliSecond * (long) Math.pow(2, retryTime);
				}
				log.info("程序执行过程失败! 预计在{}ms后第{}次重试", sleepMills, retryTime + 1);
				try {
					Thread.sleep(sleepMills);
				} catch (InterruptedException interruptedException) {
				}
			} else {
				log.info("程序执行过程失败! 第{}次重试", retryTime + 1);
			}
		}
		return true;
	}
}
