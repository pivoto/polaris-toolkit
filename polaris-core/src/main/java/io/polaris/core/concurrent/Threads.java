package io.polaris.core.concurrent;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Qt
 * @since Jan 26, 2025
 */
public class Threads {

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
