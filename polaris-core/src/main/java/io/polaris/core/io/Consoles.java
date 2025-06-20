package io.polaris.core.io;

import java.time.Instant;
import java.util.concurrent.Callable;

import io.polaris.core.converter.Converters;
import io.polaris.core.env.GlobalStdEnv;
import io.polaris.core.string.Strings;
import io.polaris.core.time.Dates;

/**
 * @author Qt
 * @since Sep 08, 2023
 */
public class Consoles {
	private static final String KEY_LOG_ENABLED = Consoles.class.getName() + ".printable";
	private static boolean printable = Converters.convertQuietly(boolean.class, GlobalStdEnv.get(KEY_LOG_ENABLED, "true"), true);

	public static void setPrintable(boolean printable) {
		Consoles.printable = printable;
	}

	public static void log(String msg, Object... args) {
		if (!printable) {
			return;
		}
		System.out.printf("[%s] [%s] %s%n", Dates.YYYY_MM_DD_HH_MM_SS_SSS.format(Instant.now()),
			Strings.padStart(Thread.currentThread().getName(), 10, ' '),
			Strings.format(msg, args));
	}

	public static void log(Object... args) {
		log("", args);
	}

	public static void println(Callable<?> supplier) {
		try {
			println(supplier.call());
		} catch (Throwable t) {
			printStackTrace(t);
		}
	}

	public static void print(String msg, Object... args) {
		if (!printable) {
			return;
		}
		System.out.print(Strings.format(msg, args));
	}

	public static void println(String msg, Object... args) {
		if (!printable) {
			return;
		}
		System.out.println(Strings.format(msg, args));
	}

	public static void print(Object... args) {
		print("", args);
	}

	public static void println(Object... args) {
		println("", args);
	}

	@SuppressWarnings("all")
	public static void printStackTrace(Throwable t) {
		if (!printable) {
			return;
		}
		t.printStackTrace(System.out);
	}


}
