package io.polaris.core.log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since  Jan 10, 2024
 */
public class ILoggers {
	private static final Map<String, ILogger> CACHE = new ConcurrentHashMap<>();

	public static ILogger of(Class<?> c) {
		return of(c.getName());
	}

	public static ILogger of(String name) {
		return CACHE.computeIfAbsent(name, k -> newLogger(name));
	}

	public static ILogger of() {
		return of(detectLoggerName());
	}

	private static String detectLoggerName() {
		String name = null;
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		for (int i = 1; i < elements.length; i++) {
			String className = elements[i].getClassName();
			if (!ILoggers.class.getName().equals(className)) {
				name = className;
				break;
			}
		}
		return Strings.coalesce(name, "");
	}

	private static ILogger newLogger(String name) {
		try {
			org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(name);
			if (logger instanceof org.slf4j.spi.LocationAwareLogger) {
				return new Slf4jAwareLogger((org.slf4j.spi.LocationAwareLogger) logger);
			} else {
				return new Slf4jLogger(logger);
			}
		} catch (Throwable e) { // no dependency
			return new StdoutLogger(name);
		}
	}
}
