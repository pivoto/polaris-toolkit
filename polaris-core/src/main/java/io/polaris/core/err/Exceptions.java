package io.polaris.core.err;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import io.polaris.core.function.Executable;

/**
 * @author Qt
 * @since 1.8
 */
public class Exceptions {


	@SuppressWarnings("unchecked")
	public static <T extends Exception> T of(Throwable t, Class<T> type, Function<Throwable, T> builder) {
		if (t == null) {
			return builder.apply(t);
		}
		if (type.isAssignableFrom(t.getClass())) {
			return (T) t;
		}
		while (t.getCause() != null) {
			t = t.getCause();
			if (type.isAssignableFrom(t.getClass())) {
				return (T) t;
			}
		}
		return builder.apply(t);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Exception> T of(Throwable t, Class<T> type, Supplier<T> builder) {
		if (t == null) {
			return builder.get();
		}
		if (type.isAssignableFrom(t.getClass())) {
			return (T) t;
		}
		while (t.getCause() != null) {
			t = t.getCause();
			if (type.isAssignableFrom(t.getClass())) {
				return (T) t;
			}
		}
		return builder.get();
	}

	public static Throwable getRootCauseUntil(@Nonnull Throwable t, Predicate<Throwable> predicate) {
		while (t.getCause() != null) {
			t = t.getCause();
			if (predicate.test(t)) {
				return t;
			}
		}
		return t;
	}

	public static Throwable getRootCause(@Nonnull Throwable t) {
		while (t.getCause() != null) {
			t = t.getCause();
		}
		return t;
	}

	public static Throwable getRootCauseSafely(@Nonnull Throwable t) {
		Set<Throwable> set = new HashSet<>();
		while (t.getCause() != null && !set.contains(t)) {
			set.add(t);
			t = t.getCause();
		}
		return t;
	}

	public static Set<Throwable> getCausePath(@Nonnull Throwable t) {
		Set<Throwable> set = new LinkedHashSet<>();
		while (t != null && !set.contains(t)) {
			set.add(t);
			t = t.getCause();
		}
		return set;
	}

	public static Throwable hasCause(@Nonnull Throwable t, Predicate<Throwable> predicate) {
		while (t != null) {
			if (predicate.test(t)) {
				return t;
			}
			t = t.getCause();
		}
		return t;
	}

	public static Throwable hasCause(@Nonnull Throwable t, Class<? extends Throwable> type) {
		while (t != null) {
			if (type.isAssignableFrom(t.getClass())) {
				return t;
			}
			t = t.getCause();
		}
		return t;
	}

	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		return sw.toString();
	}

	public static void quietlyClose(AutoCloseable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Throwable e) {
				// ignore
			}
		}
	}

	public static void quietlyClose(AutoCloseable closeable, Throwable t) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Throwable e) {
				t.addSuppressed(e);
			}
		}
	}

	public static void runQuietly(Runnable runnable) {
		if (runnable != null) {
			try {
				runnable.run();
			} catch (Throwable e) {
				// ignore
			}
		}
	}

	public static void runQuietly(Runnable runnable, Throwable t) {
		if (runnable != null) {
			try {
				runnable.run();
			} catch (Throwable e) {
				t.addSuppressed(e);
			}
		}
	}

	public static <T> T getQuietly(Supplier<T> supplier) {
		if (supplier != null) {
			try {
				return supplier.get();
			} catch (Throwable e) {
				// ignore
			}
		}
		return null;
	}

	public static <T> T getQuietly(Supplier<T> supplier, Throwable t) {
		if (supplier != null) {
			try {
				return supplier.get();
			} catch (Throwable e) {
				t.addSuppressed(e);
			}
		}
		return null;
	}

	public static void executeQuietly(Executable executable) {
		if (executable != null) {
			try {
				executable.execute();
			} catch (Throwable e) {
				// ignore
			}
		}
	}

	public static void executeQuietly(Executable executable, Throwable t) {
		if (executable != null) {
			try {
				executable.execute();
			} catch (Throwable e) {
				t.addSuppressed(e);
			}
		}
	}


	public static <T> T callQuietly(Callable<T> callable) {
		if (callable != null) {
			try {
				return callable.call();
			} catch (Throwable e) {
				// ignore
			}
		}
		return null;
	}

	public static <T> T callQuietly(Callable<T> callable, Throwable t) {
		if (callable != null) {
			try {
				return callable.call();
			} catch (Throwable e) {
				t.addSuppressed(e);
			}
		}
		return null;
	}
}
