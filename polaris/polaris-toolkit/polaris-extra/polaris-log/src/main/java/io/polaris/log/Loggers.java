package io.polaris.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

import java.util.StringJoiner;

/**
 * @author Qt
 * @since 1.8
 */
public class Loggers {
	private static String FQCN = Loggers.class.getName();
	private static String DFT_NAME;

	static {
		String[] arr = Loggers.class.getPackage().getName().split("\\.");
		StringJoiner joiner = new StringJoiner(".");
		joiner.add(arr[0]);
		if (arr.length > 1) {
			joiner.add(arr[1]);
		}
		DFT_NAME = joiner.toString();
	}

	public static LocationAwareLogger of() {
		return of(DFT_NAME);
	}

	public static LocationAwareLogger of(String name) {
		Logger logger = LoggerFactory.getLogger(name);
		if (logger instanceof LocationAwareLogger) {
			return (LocationAwareLogger) logger;
		}
		return null;
	}

	// region logName

	public static void trace(String logName, String msg, Object... arguments) {
		trace(logName, msg, arguments, null);
	}

	public static void trace(String logName, String msg, Throwable t) {
		trace(logName, msg, (Object[]) null, t);
	}

	public static void trace(String logName, String msg, Object[] arguments, Throwable t) {
		LocationAwareLogger log = of(logName);
		if (log != null && log.isTraceEnabled()) {
			log.log(null, FQCN, LocationAwareLogger.TRACE_INT, msg, arguments, t);
		}
	}

	public static void trace(String logName, Throwable t, String msg, Object... arguments) {
		trace(logName, msg, arguments, t);
	}


	public static void debug(String logName, String msg, Object... arguments) {
		debug(logName, msg, arguments, null);
	}

	public static void debug(String logName, String msg, Throwable t) {
		debug(logName, msg, (Object[]) null, t);
	}

	public static void debug(String logName, String msg, Object[] arguments, Throwable t) {
		LocationAwareLogger log = of(logName);
		if (log != null && log.isDebugEnabled()) {
			log.log(null, FQCN, LocationAwareLogger.DEBUG_INT, msg, arguments, t);
		}
	}

	public static void debug(String logName, Throwable t, String msg, Object... arguments) {
		debug(logName, msg, arguments, t);
	}


	public static void info(String logName, String msg, Object... arguments) {
		info(logName, msg, arguments, null);
	}

	public static void info(String logName, String msg, Throwable t) {
		info(logName, msg, (Object[]) null, t);
	}

	public static void info(String logName, String msg, Object[] arguments, Throwable t) {
		LocationAwareLogger log = of(logName);
		if (log != null && log.isInfoEnabled()) {
			log.log(null, FQCN, LocationAwareLogger.INFO_INT, msg, arguments, t);
		}
	}

	public static void info(String logName, Throwable t, String msg, Object... arguments) {
		info(logName, msg, arguments, t);
	}

	public static void warn(String logName, String msg, Object... arguments) {
		warn(logName, msg, arguments, null);
	}

	public static void warn(String logName, String msg, Throwable t) {
		warn(logName, msg, (Object[]) null, t);
	}

	public static void warn(String logName, String msg, Object[] arguments, Throwable t) {
		LocationAwareLogger log = of(logName);
		if (log != null && log.isWarnEnabled()) {
			log.log(null, FQCN, LocationAwareLogger.WARN_INT, msg, arguments, t);
		}
	}

	public static void warn(String logName, Throwable t, String msg, Object... arguments) {
		warn(logName, msg, arguments, t);
	}

	public static void error(String logName, String msg, Object... arguments) {
		error(logName, msg, arguments, null);
	}

	public static void error(String logName, String msg, Throwable t) {
		error(logName, msg, (Object[]) null, t);
	}

	public static void error(String logName, String msg, Object[] arguments, Throwable t) {
		LocationAwareLogger log = of(logName);
		if (log != null && log.isErrorEnabled()) {
			log.log(null, FQCN, LocationAwareLogger.ERROR_INT, msg, arguments, t);
		}
	}

	public static void error(String logName, Throwable t, String msg, Object... arguments) {
		error(logName, msg, arguments, t);
	}

	// endregion


	// region default log

	public static void trace(String msg) {
		trace(msg, (Object[]) null, null);
	}

	public static void trace(String msg, Object[] arguments) {
		trace(msg, arguments, null);
	}

	public static void trace(String msg, Throwable t) {
		trace(msg, (Object[]) null, t);
	}

	public static void trace(String msg, Object[] arguments, Throwable t) {
		LocationAwareLogger log = of();
		if (log != null && log.isTraceEnabled()) {
			log.log(null, FQCN, LocationAwareLogger.TRACE_INT, msg, arguments, t);
		}
	}

	public static void trace(Throwable t, String msg, Object... arguments) {
		trace(msg, arguments, t);
	}

	public static void debug(String msg) {
		debug(msg, (Object[]) null, null);
	}

	public static void debug(String msg, Object[] arguments) {
		debug(msg, arguments, null);
	}

	public static void debug(String msg, Throwable t) {
		debug(msg, (Object[]) null, t);
	}

	public static void debug(String msg, Object[] arguments, Throwable t) {
		LocationAwareLogger log = of();
		if (log != null && log.isDebugEnabled()) {
			log.log(null, FQCN, LocationAwareLogger.DEBUG_INT, msg, arguments, t);
		}
	}

	public static void debug(Throwable t, String msg, Object... arguments) {
		debug(msg, arguments, t);
	}

	public static void info(String msg) {
		info(msg, (Object[]) null, null);
	}

	public static void info(String msg, Object[] arguments) {
		info(msg, arguments, null);
	}

	public static void info(String msg, Throwable t) {
		info(msg, (Object[]) null, t);
	}

	public static void info(String msg, Object[] arguments, Throwable t) {
		LocationAwareLogger log = of();
		if (log != null && log.isInfoEnabled()) {
			log.log(null, FQCN, LocationAwareLogger.INFO_INT, msg, arguments, t);
		}
	}

	public static void info(Throwable t, String msg, Object... arguments) {
		info(msg, arguments, t);
	}

	public static void warn(String msg) {
		warn(msg, (Object[]) null, null);
	}

	public static void warn(String msg, Object[] arguments) {
		warn(msg, arguments, null);
	}

	public static void warn(String msg, Throwable t) {
		warn(msg, (Object[]) null, t);
	}

	public static void warn(String msg, Object[] arguments, Throwable t) {
		LocationAwareLogger log = of();
		if (log != null && log.isWarnEnabled()) {
			log.log(null, FQCN, LocationAwareLogger.WARN_INT, msg, arguments, t);
		}
	}

	public static void warn(Throwable t, String msg, Object... arguments) {
		warn(msg, arguments, t);
	}

	public static void error(String msg) {
		error(msg, (Object[]) null, null);
	}

	public static void error(String msg, Object[] arguments) {
		error(msg, arguments, null);
	}

	public static void error(String msg, Throwable t) {
		error(msg, (Object[]) null, t);
	}


	public static void error(String msg, Object[] arguments, Throwable t) {
		LocationAwareLogger log = of();
		if (log != null && log.isErrorEnabled()) {
			log.log(null, FQCN, LocationAwareLogger.ERROR_INT, msg, arguments, t);
		}
	}

	public static void error(Throwable t, String msg, Object... arguments) {
		error(msg, arguments, t);
	}

	// endregion
}
