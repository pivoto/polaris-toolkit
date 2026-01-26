package io.polaris.core.log.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.polaris.core.log.LogResolver;
import io.polaris.core.log.Logger;

/**
 * @author Qt
 */
public class DefaultLoggerResolver implements LogResolver {
	private final Map<String, Logger> CACHE = new ConcurrentHashMap<>();

	@Override
	public Logger getLogger(String name) {
		Logger log = CACHE.get(name);
		if (log != null) {
			return log;
		}
		// 避免死锁，将 newLogger 操作放在 CACHE.computeIfAbsent 外部
		Logger logger = newLogger(name);
		return CACHE.computeIfAbsent(name, k -> logger);
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
