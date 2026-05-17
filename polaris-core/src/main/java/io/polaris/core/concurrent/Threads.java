package io.polaris.core.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.concurrent.thread.DefaultThreadProvider;
import io.polaris.core.concurrent.thread.ThreadProvider;
import io.polaris.core.spi.SpiLoaders;
import io.polaris.core.spi.StatefulSpiLoader;

/**
 * @author Qt
 * @since Jan 26, 2025
 */
public class Threads {
	private static class Holder {
		private static final ThreadProvider threadProvider = load();

		private static ThreadProvider load() {
			StatefulSpiLoader<ThreadProvider> loader = SpiLoaders.loadStateful(ThreadProvider.class);
			ThreadProvider provider = loader.service();
			if (provider == null) {
				provider = new DefaultThreadProvider();
			}
			return provider;
		}

	}


	@Nonnull
	public static ThreadProvider threadProvider() {
		return Holder.threadProvider;
	}

	public static Thread newThread(Runnable target, String name) {
		return Holder.threadProvider.newThread(target, name);
	}

	public static Thread newThread(ThreadGroup threadGroup, Runnable target, String name) {
		return Holder.threadProvider.newThread(threadGroup, target, name);
	}

	public static <T> ThreadLocalSpec<T> newThreadLocal() {
		return Holder.threadProvider.newThreadLocal();
	}

	public static <T> ThreadLocalSpec<T> newThreadLocal(Supplier<? extends T> supplier) {
		return Holder.threadProvider.newThreadLocal(supplier);
	}

	public static <T> ThreadLocalSpec<T> newThreadLocal(String name) {
		return Holder.threadProvider.newThreadLocal(name);
	}

	public static <T> ThreadLocalSpec<T> newThreadLocal(String name, Supplier<? extends T> supplier) {
		return Holder.threadProvider.newThreadLocal(name, supplier);
	}


	@Nullable
	public static Class<?> getCallerClass() {
		String className = getCallerClassName();
		if (className == null) {
			return null;
		}
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	@Nullable
	public static String getCallerClassName() {
		String threadClassName = Threads.class.getName();
		String className = null;
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		for (int i = 1; i < elements.length; i++) {
			String className0 = elements[i].getClassName();
			if (!threadClassName.equals(className0)) {
				className = className0;
				break;
			}
		}
		return className;
	}

	@Nullable
	public static Class<?> getCallerClass(@Nonnull Class<?> currentClass) {
		String className = getCallerClassName(currentClass);
		if (className == null) {
			return null;
		}
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	@Nullable
	public static String getCallerClassName(@Nonnull Class<?> currentClass) {
		String currentClassName = currentClass.getName();
		return getCallerClassName(currentClassName);
	}

	@Nullable
	public static String getCallerClassName(@Nonnull String currentClassName) {
		String threadClassName = Threads.class.getName();
		String className = null;
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		for (int i = 1; i < elements.length; i++) {
			String className0 = elements[i].getClassName();
			if (!threadClassName.equals(className0)
				&& !currentClassName.equals(className0)
			) {
				className = className0;
				break;
			}
		}
		return className;
	}

	@Nullable
	public static Class<?> getCallerClass(@Nonnull Class<?>... currentClasses) {
		String className = getCallerClassName(currentClasses);
		if (className == null) {
			return null;
		}
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	@Nullable
	public static String getCallerClassName(@Nonnull Class<?>... currentClasses) {
		String[] currentClassNames = new String[currentClasses.length];
		for (int i = 0; i < currentClasses.length; i++) {
			currentClassNames[i] = currentClasses[i].getName();
		}
		return getCallerClassName(currentClassNames);
	}

	@Nullable
	public static String getCallerClassName(@Nonnull String... currentClassNames) {
		String threadClassName = Threads.class.getName();
		String className = null;
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		loop:
		for (int i = 1; i < elements.length; i++) {
			String className0 = elements[i].getClassName();
			if (threadClassName.equals(className0)) {
				continue;
			}
			for (String currentClassName : currentClassNames) {
				if (currentClassName.equals(className0)) {
					continue loop;
				}
			}
			className = className0;
			break;
		}
		return className;
	}

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
