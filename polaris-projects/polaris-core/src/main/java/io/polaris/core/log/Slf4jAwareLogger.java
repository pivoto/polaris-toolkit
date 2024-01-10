package io.polaris.core.log;

import io.polaris.core.collection.ObjectArrays;
import org.slf4j.spi.LocationAwareLogger;

/**
 * @author Qt
 * @since 1.8,  Aug 04, 2023
 */
public class Slf4jAwareLogger implements ILogger {
	private static String FQCN = Loggers.class.getName();

	private LocationAwareLogger log;

	public Slf4jAwareLogger(LocationAwareLogger log) {
		this.log = log;
	}


	@Override
	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}

	@Override
	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}

	@Override
	public boolean isWarnEnabled() {
		return log.isWarnEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return log.isErrorEnabled();
	}

	@Override
	public void trace(String msg) {
		trace(msg, ObjectArrays.EMPTY, null);
	}

	@Override
	public void trace(String msg, Object... arguments) {
		trace(msg, arguments, null);
	}

	@Override
	public void trace(String msg, Throwable t) {
		trace(msg, ObjectArrays.EMPTY, t);
	}

	@Override
	public void trace(String msg, Object[] arguments, Throwable t) {
		if (log != null && log.isTraceEnabled()) {
			log.log(null, FQCN, LocationAwareLogger.TRACE_INT, msg, arguments, t);
		}
	}

	@Override
	public void trace(Throwable t, String msg, Object... arguments) {
		trace(msg, arguments, t);
	}

	@Override
	public void debug(String msg) {
		debug(msg, ObjectArrays.EMPTY, null);
	}

	@Override
	public void debug(String msg, Object... arguments) {
		debug(msg, arguments, null);
	}

	@Override
	public void debug(String msg, Throwable t) {
		debug(msg, ObjectArrays.EMPTY, t);
	}

	@Override
	public void debug(String msg, Object[] arguments, Throwable t) {
		if (log != null && log.isDebugEnabled()) {
			log.log(null, FQCN, LocationAwareLogger.DEBUG_INT, msg, arguments, t);
		}
	}

	@Override
	public void debug(Throwable t, String msg, Object... arguments) {
		debug(msg, arguments, t);
	}

	@Override
	public void info(String msg) {
		info(msg, ObjectArrays.EMPTY, null);
	}

	@Override
	public void info(String msg, Object... arguments) {
		info(msg, arguments, null);
	}

	@Override
	public void info(String msg, Throwable t) {
		info(msg, ObjectArrays.EMPTY, t);
	}

	@Override
	public void info(String msg, Object[] arguments, Throwable t) {
		if (log != null && log.isInfoEnabled()) {
			log.log(null, FQCN, LocationAwareLogger.INFO_INT, msg, arguments, t);
		}
	}

	@Override
	public void info(Throwable t, String msg, Object... arguments) {
		info(msg, arguments, t);
	}

	@Override
	public void warn(String msg) {
		warn(msg, ObjectArrays.EMPTY, null);
	}

	@Override
	public void warn(String msg, Object... arguments) {
		warn(msg, arguments, null);
	}

	@Override
	public void warn(String msg, Throwable t) {
		warn(msg, ObjectArrays.EMPTY, t);
	}

	@Override
	public void warn(String msg, Object[] arguments, Throwable t) {
		if (log != null && log.isWarnEnabled()) {
			log.log(null, FQCN, LocationAwareLogger.WARN_INT, msg, arguments, t);
		}
	}

	@Override
	public void warn(Throwable t, String msg, Object... arguments) {
		warn(msg, arguments, t);
	}

	@Override
	public void error(String msg) {
		error(msg, ObjectArrays.EMPTY, null);
	}

	@Override
	public void error(String msg, Object... arguments) {
		error(msg, arguments, null);
	}

	@Override
	public void error(String msg, Throwable t) {
		error(msg, ObjectArrays.EMPTY, t);
	}


	@Override
	public void error(String msg, Object[] arguments, Throwable t) {
		if (log != null && log.isErrorEnabled()) {
			log.log(null, FQCN, LocationAwareLogger.ERROR_INT, msg, arguments, t);
		}
	}

	@Override
	public void error(Throwable t, String msg, Object... arguments) {
		error(msg, arguments, t);
	}


}
