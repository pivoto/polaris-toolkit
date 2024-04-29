package io.polaris.core.io;

import io.polaris.core.time.Dates;
import io.polaris.core.string.Strings;

import java.time.Instant;

/**
 * @author Qt
 * @since 1.8,  Sep 08, 2023
 */
public class Consoles {

	public static void log(String msg, Object... args) {
		System.out.println(Strings.format(msg, args));
	}

	public static void logWithThread(String msg, Object... args) {
		System.out.printf("[%s] %s%n" ,
			Strings.padStart(Thread.currentThread().getName(), 10, ' '),
			Strings.format(msg, args));
	}

	public static void logWithTime(String msg, Object... args) {
		System.out.printf("[%s] [%s] %s%n" , Dates.YYYY_MM_DD_HH_MM_SS_SSS.format(Instant.now()),
			Strings.padStart(Thread.currentThread().getName(), 10, ' '),
			Strings.format(msg, args));
	}
}
