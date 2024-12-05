package io.polaris.core.err;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

/**
 * @author Qt
 * @since 1.8
 */
public class Exceptions {


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
}
