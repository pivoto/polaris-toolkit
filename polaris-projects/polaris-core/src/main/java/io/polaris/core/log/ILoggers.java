package io.polaris.core.log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Qt
 * @since 1.8,  Jan 10, 2024
 */
public class ILoggers {
	private static Map<String, ILogger> CACHE = new ConcurrentHashMap<>();

	public static ILogger of(Class<?> c) {
		return of(c.getName());
	}

	public static ILogger of(String name) {
		return CACHE.computeIfAbsent(name, k -> newLogger(name));
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
