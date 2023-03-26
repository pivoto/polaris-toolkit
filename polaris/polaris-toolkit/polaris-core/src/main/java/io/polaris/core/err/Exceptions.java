package io.polaris.core.err;

import java.util.function.Function;
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


}
