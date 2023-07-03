package io.polaris.builder.code;

import io.polaris.core.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Qt
 * @since 1.8
 */
public class CodeLogger {
	private static final Logger log = LoggerFactory.getLogger("code.builder");
	private static boolean withStd;

	public static void withStd(boolean withStd) {
		CodeLogger.withStd = withStd;
	}

	public static void debug(String format, Object... arguments) {
		if (withStd) {
			System.out.println(Strings.format(format, arguments));
		} else {
			log.debug(format, arguments);
		}
	}

	public static void debug(String msg, Throwable t) {
		if (withStd) {
			System.out.println(msg);
			t.printStackTrace(System.err);
		} else {
			log.debug(msg, t);
		}
	}

	public static void info(String format, Object... arguments) {
		if (withStd) {
			System.out.println(Strings.format(format, arguments));
		} else {
			log.info(format, arguments);
		}
	}

	public static void info(String msg, Throwable t) {
		if (withStd) {
			System.out.println(msg);
			t.printStackTrace(System.err);
		} else {
			log.info(msg, t);
		}
	}

	public static void warn(String format, Object... arguments) {
		if (withStd) {
			System.out.println(Strings.format(format, arguments));
		} else {
			log.warn(format, arguments);
		}
	}

	public static void warn(String msg, Throwable t) {
		if (withStd) {
			System.out.println(msg);
			t.printStackTrace(System.err);
		} else {
			log.warn(msg, t);
		}
	}

	public static void error(String format, Object... arguments) {
		if (withStd) {
			System.out.println(Strings.format(format, arguments));
		} else {
			log.error(format, arguments);
		}
	}

	public static void error(String msg, Throwable t) {
		if (withStd) {
			System.out.println(msg);
			t.printStackTrace(System.err);
		} else {
			log.error(msg, t);
		}
	}
}
