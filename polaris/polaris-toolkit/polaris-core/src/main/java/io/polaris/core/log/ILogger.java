package io.polaris.core.log;

/**
 * @author Qt
 * @since 1.8,  Aug 04, 2023
 */
public interface ILogger {
	static final Object[] EMPTY = new Object[0];

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


	boolean isTraceEnabled();

	boolean isDebugEnabled();

	boolean isInfoEnabled();

	boolean isWarnEnabled();

	boolean isErrorEnabled();

	void trace(String msg);

	void trace(String msg, Object... arguments);

	void trace(String msg, Throwable t);

	void trace(String msg, Object[] arguments, Throwable t);

	void trace(Throwable t, String msg, Object... arguments);

	void debug(String msg);

	void debug(String msg, Object... arguments);

	void debug(String msg, Throwable t);

	void debug(String msg, Object[] arguments, Throwable t);

	void debug(Throwable t, String msg, Object... arguments);

	void info(String msg);

	void info(String msg, Object... arguments);

	void info(String msg, Throwable t);

	void info(String msg, Object[] arguments, Throwable t);

	void info(Throwable t, String msg, Object... arguments);

	void warn(String msg);

	void warn(String msg, Object... arguments);

	void warn(String msg, Throwable t);

	void warn(String msg, Object[] arguments, Throwable t);

	void warn(Throwable t, String msg, Object... arguments);

	void error(String msg);

	void error(String msg, Object... arguments);

	void error(String msg, Throwable t);

	void error(String msg, Object[] arguments, Throwable t);

	void error(Throwable t, String msg, Object... arguments);
}
