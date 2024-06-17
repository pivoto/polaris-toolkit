package io.polaris.core;

import java.time.Instant;

import io.polaris.core.converter.Converters;
import io.polaris.core.time.Dates;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since  Feb 18, 2024
 */
public class TestConsole {
	private static boolean printable = Converters.convertQuietly(boolean.class, System.getProperty("test.console.printable", "true"), true);


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

	public static void printx(String msg, Object... args) {
		if (!printable) {
			return;
		}
		System.out.printf("[%s] [%s] %s%n", Dates.YYYY_MM_DD_HH_MM_SS_SSS.format(Instant.now()),
			Strings.padStart(Thread.currentThread().getName(), 10, ' '),
			Strings.format(msg, args));
	}

	public static void print(Object... args) {
		print("", args);
	}

	public static void println(Object... args) {
		println("", args);
	}

	public static void printx(Object... args) {
		printx("", args);
	}

	@SuppressWarnings("all")
	public static void printStackTrace(Throwable t) {
		if (!printable) {
			return;
		}
		t.printStackTrace();
	}

}
