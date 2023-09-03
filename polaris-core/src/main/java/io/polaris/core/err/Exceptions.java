package io.polaris.core.err;

import javax.annotation.Nonnull;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
public class Exceptions {


	public static <T extends Exception> T of(Throwable e, Class<T> type, Function<Throwable, T> builder) {
		return type.isAssignableFrom(e.getClass()) ? (T) e : builder.apply(e);
	}

	public static <T extends Exception> T of(Throwable e, Class<T> type, Supplier<T> builder) {
		return type.isAssignableFrom(e.getClass()) ? (T) e : builder.get();
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

	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		return sw.toString();
	}
}
