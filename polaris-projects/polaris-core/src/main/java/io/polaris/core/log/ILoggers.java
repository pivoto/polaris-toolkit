package io.polaris.core.log;

/**
 * @author Qt
 * @since 1.8,  Jan 10, 2024
 */
public class ILoggers {


	public static ILogger of(Class<?> c) {
		return of(c.getName());
	}

	public static ILogger of(String name) {
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
