package io.polaris.builder.code;

import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.log.support.StdoutLogger;

/**
 * @author Qt
 * @since 1.8
 */
public class CodeLogger {
	private static final ILogger stdLog;
	private static final ILogger slf4jLog;
	private static ILogger log;

	static {
		slf4jLog = ILoggers.of("code.builder" );
		if (slf4jLog instanceof StdoutLogger) {
			stdLog = slf4jLog;
		} else {
			stdLog = new StdoutLogger("code.builder" );
		}
		log = slf4jLog;
	}

	public static void withStd(boolean withStd) {
		CodeLogger.log = withStd ? CodeLogger.stdLog : CodeLogger.slf4jLog;
	}

	public static void debug(String format, Object... arguments) {
		log.debug(format, arguments);
	}

	public static void debug(String msg, Throwable t) {
		log.debug(msg, t);
	}

	public static void info(String format, Object... arguments) {
		log.info(format, arguments);
	}

	public static void info(String msg, Throwable t) {
		log.info(msg, t);
	}

	public static void warn(String format, Object... arguments) {
		log.warn(format, arguments);
	}

	public static void warn(String msg, Throwable t) {
		log.warn(msg, t);
	}

	public static void error(String format, Object... arguments) {
		log.error(format, arguments);
	}

	public static void error(String msg, Throwable t) {
		log.error(msg, t);
	}
}
