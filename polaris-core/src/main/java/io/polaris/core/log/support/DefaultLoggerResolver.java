package io.polaris.core.log.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.polaris.core.log.ILogResolver;
import io.polaris.core.log.Logger;

/**
 * @author Qt
 */
public class DefaultLoggerResolver implements ILogResolver {
	private final Map<String, Logger> CACHE = new ConcurrentHashMap<>();

	@Override
	public Logger getLogger(String name) {
		return CACHE.computeIfAbsent(name, k -> newLogger(name));
	}


	private Logger newLogger(String name) {
		Logger logger = getDirectSlf4jLogger(name);
		if (logger != null) {
			return logger;
		}
		return new StdoutLogger(name);
	}

	private static Logger getDirectSlf4jLogger(String name) {
		try {
			org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(name);
			if (logger instanceof org.slf4j.spi.LocationAwareLogger) {
				return new Slf4jAwareLogger((org.slf4j.spi.LocationAwareLogger) logger);
			} else {
				return new Slf4jLogger(logger);
			}
		} catch (Throwable ignored) { // no dependency
		}
		return null;
	}
}
