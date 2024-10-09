package io.polaris.core.log.support;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import io.polaris.core.collection.ObjectArrays;
import io.polaris.core.log.ILogger;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 */
public class JdkLogger implements ILogger {
	static final String INNER_CALLER = JdkLogger.class.getName();
	private final Logger logger;

	public JdkLogger(String name) {
		this.logger = Logger.getLogger(name);
	}

	public JdkLogger(Class clazz) {
		this.logger = Logger.getLogger(clazz.getName());
	}

	private void fillSource(LogRecord record) {
		fillSource(record, null);
	}

	private void fillSource(LogRecord record, Throwable throwable) {
		if (throwable != null) {
			record.setThrown(throwable);
		}
		if (record.getSourceClassName() == null) {
			if (throwable == null) {
				throwable = new Throwable();
			}
			final StackTraceElement[] frames = throwable.getStackTrace();
			for (int i = 0; i < frames.length; i++) {
				StackTraceElement frame = frames[i];
				String cname = frame.getClassName();
				if (!cname.equals(INNER_CALLER)) {
					record.setSourceClassName(cname);
					record.setSourceMethodName(frame.getMethodName());
					break;
				}
			}
		}
	}


	@Override
	public boolean isTraceEnabled() {
		return logger.isLoggable(Level.ALL);
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isLoggable(Level.FINE);
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isLoggable(Level.INFO);
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isLoggable(Level.WARNING);
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isLoggable(Level.SEVERE);
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
		if (isTraceEnabled()) {
			LogRecord lr = new LogRecord(Level.ALL, Strings.format(msg, arguments));
			fillSource(lr, t);
			logger.log(lr);
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
		if (isDebugEnabled()) {
			LogRecord lr = new LogRecord(Level.FINE, Strings.format(msg, arguments));
			fillSource(lr, t);
			logger.log(lr);
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
		if (isInfoEnabled()) {
			LogRecord lr = new LogRecord(Level.INFO, Strings.format(msg, arguments));
			fillSource(lr, t);
			logger.log(lr);
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
		if (isWarnEnabled()) {
			LogRecord lr = new LogRecord(Level.WARNING, Strings.format(msg, arguments));
			fillSource(lr, t);
			logger.log(lr);
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
		if (isErrorEnabled()) {
			LogRecord lr = new LogRecord(Level.SEVERE, Strings.format(msg, arguments));
			fillSource(lr, t);
			logger.log(lr);
		}
	}

	@Override
	public void error(Throwable t, String msg, Object... arguments) {
		error(msg, arguments, t);
	}

}
