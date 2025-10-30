package io.polaris.core.concurrent.async;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import io.polaris.core.concurrent.Executors;
import io.polaris.core.concurrent.Schedules;
import io.polaris.core.concurrent.Threads;
import io.polaris.core.concurrent.async.impl.DefaultAsyncThreadPoolFactory;
import io.polaris.core.lang.Retry;
import io.polaris.core.service.SpiLoaders;
import io.polaris.core.service.StatefulServiceLoader;

/**
 * @author Qt
 * @since Aug 06, 2025
 */
public class AsyncInvokers {
	private static volatile AsyncExecutor defaultAsyncExecutor;
	private static volatile AsyncScheduler defaultAsyncScheduler;
	private static volatile ExecutorService asyncExecutor;
	private static volatile ScheduledExecutorService asyncScheduler;

	private AsyncInvokers() {
	}

	public static void asyncExecutor(ExecutorService executor) {
		if (executor == null) {
			asyncExecutor = null;
		} else {
			asyncExecutor = executor;
			if (defaultAsyncExecutor != null) {
				AsyncExecutor curr = null;
				synchronized (AsyncInvokers.class) {
					if (defaultAsyncExecutor != null) {
						if (defaultAsyncExecutor.executor != executor) {
							curr = defaultAsyncExecutor;
							defaultAsyncExecutor = null;
						}
					}
				}
				if (curr != null) {
					curr.shutdownAsync();
				}
			}
		}
	}

	public static void asyncScheduler(ScheduledExecutorService scheduler) {
		if (scheduler == null) {
			asyncScheduler = null;
		} else {
			asyncScheduler = scheduler;
			if (defaultAsyncScheduler != null) {
				AsyncScheduler curr = null;
				synchronized (AsyncInvokers.class) {
					if (defaultAsyncScheduler != null) {
						if (defaultAsyncScheduler.scheduler != scheduler) {
							curr = defaultAsyncScheduler;
							defaultAsyncScheduler = null;
						}
					}
				}
				if (curr != null) {
					curr.shutdownAsync();
				}
			}
		}
	}


	public static ExecutorService asyncExecutor() {
		if (asyncExecutor != null) {
			return asyncExecutor;
		}
		return defaultAsyncExecutor().executor;
	}

	public static ScheduledExecutorService asyncScheduler() {
		if (asyncScheduler != null) {
			return asyncScheduler;
		}
		return defaultAsyncScheduler().scheduler;
	}

	private static AsyncExecutor defaultAsyncExecutor() {
		if (defaultAsyncExecutor == null) {
			synchronized (AsyncInvokers.class) {
				if (defaultAsyncExecutor == null) {
					defaultAsyncExecutor = new AsyncExecutor();
				}
			}
		}
		return defaultAsyncExecutor;
	}

	private static AsyncScheduler defaultAsyncScheduler() {
		if (defaultAsyncScheduler == null) {
			synchronized (AsyncInvokers.class) {
				if (defaultAsyncScheduler == null) {
					defaultAsyncScheduler = new AsyncScheduler();
				}
			}
		}
		return defaultAsyncScheduler;
	}


	public static void doAsync(Consumer<ExecutorService> consumer) {
		consumer.accept(asyncExecutor());
	}

	public static Future<?> doAsync(Runnable task) {
		return asyncExecutor().submit(task);
	}

	public static <T> Future<T> doAsync(Supplier<T> task) {
		return asyncExecutor().submit(Executors.callable(task));
	}

	public static Future<?> doAsync(Runnable task, long delay) {
		return asyncScheduler().schedule(task, delay, TimeUnit.MILLISECONDS);
	}

	public static Future<?> doAsync(Runnable task, long delay, TimeUnit unit) {
		return asyncScheduler().schedule(task, delay, unit);
	}

	public static <T> Future<T> doAsync(Supplier<T> task, long delay) {
		return asyncScheduler().schedule(Executors.callable(task), delay, TimeUnit.MILLISECONDS);
	}

	public static <T> Future<T> doAsync(Supplier<T> task, long delay, TimeUnit unit) {
		return asyncScheduler().schedule(Executors.callable(task), delay, unit);
	}


	@SafeVarargs
	public static <T> Future<T> doAsyncRetry(@Nonnull Supplier<T> supplier, int retryCount, Class<? extends Throwable>... retryException) {
		return doAsyncRetry(supplier, retryCount, 0, false, retryException);
	}

	@SafeVarargs
	public static <T> Future<T> doAsyncRetry(@Nonnull Supplier<T> supplier, int retryCount, long interval, Class<? extends Throwable>... retryException) {
		return doAsyncRetry(supplier, retryCount, interval, false, retryException);
	}

	@SafeVarargs
	public static <T> Future<T> doAsyncRetry(@Nonnull Supplier<T> supplier, int retryCount, long interval, boolean exponential, Class<? extends Throwable>... retryException) {
		if (interval == 0) {
			return Retry.doRetry(asyncExecutor(), supplier, retryCount, retryException);
		}
		return Retry.doRetry(asyncScheduler(), supplier, retryCount, interval, exponential, retryException);
	}


	@SafeVarargs
	public static Future<?> doAsyncRetry(@Nonnull Runnable runnable, int retryCount, Class<? extends Throwable>... retryException) {
		return doAsyncRetry(runnable, retryCount, 0, false, retryException);
	}

	@SafeVarargs
	public static Future<?> doAsyncRetry(@Nonnull Runnable runnable, int retryCount, long interval, Class<? extends Throwable>... retryException) {
		return doAsyncRetry(runnable, retryCount, interval, false, retryException);
	}

	@SafeVarargs
	public static Future<?> doAsyncRetry(@Nonnull Runnable runnable, int retryCount, long interval, boolean exponential, Class<? extends Throwable>... retryException) {
		if (interval == 0) {
			return Retry.doRetry(asyncExecutor(), runnable, retryCount, retryException);
		}
		return Retry.doRetry(asyncScheduler(), runnable, retryCount, interval, exponential, retryException);
	}


	private static class AsyncExecutor {
		private final ExecutorService executor;
		private final boolean canShutdown;

		public AsyncExecutor() {
			StatefulServiceLoader<AsyncThreadPoolFactory> loader = SpiLoaders.loadStateful(AsyncThreadPoolFactory.class);
			AsyncThreadPoolFactory factory = loader.optionalService().orElseGet(DefaultAsyncThreadPoolFactory::new);
			executor = factory.buildExecutor();
			canShutdown = factory.canShutdownExecutor();
			if (canShutdown) {
				// 添加关闭钩子
				Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
			}
		}

		public void shutdown() {
			if (canShutdown) {
				if (executor instanceof ThreadPoolExecutor) {
					ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
					if (Executors.hasRunningTasks(threadPoolExecutor)) {
						for (int i = 0; i < 200; i++) {
							if (Executors.hasRunningTasks(threadPoolExecutor)) {
								break;
							}
							Threads.sleep(100);
						}
					}
				}
				Executors.shutdown(executor);
			}
		}

		public void shutdownAsync() {
			if (canShutdown) {
				new Thread(this::shutdown).start();
			}
		}
	}

	private static class AsyncScheduler {
		private final ScheduledExecutorService scheduler;
		private final boolean canShutdown;

		public AsyncScheduler() {
			StatefulServiceLoader<AsyncThreadPoolFactory> loader = SpiLoaders.loadStateful(AsyncThreadPoolFactory.class);
			AsyncThreadPoolFactory factory = loader.optionalService().orElseGet(DefaultAsyncThreadPoolFactory::new);
			scheduler = factory.buildScheduledExecutor();
			canShutdown = factory.canShutdownScheduledExecutor();
			if (canShutdown) {
				// 添加关闭钩子
				Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
			}
		}

		public void shutdown() {
			if (canShutdown) {
				Schedules.shutdown(scheduler);
			}
		}

		public void shutdownAsync() {
			if (canShutdown) {
				new Thread(this::shutdown).start();
			}
		}
	}


}
