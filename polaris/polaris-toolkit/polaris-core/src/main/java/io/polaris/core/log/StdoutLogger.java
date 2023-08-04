package io.polaris.core.log;

import io.polaris.core.date.Dates;
import io.polaris.core.string.Strings;

import java.time.Instant;

/**
 * @author Qt
 * @since 1.8,  Aug 04, 2023
 */
public class StdoutLogger implements ILogger {
	private final String name;
	private Level level;

	public StdoutLogger(String name) {
		this.name = name;
		try {
			String level = System.getProperty("logger.level." + name);
			this.level = Level.valueOf(Strings.coalesce(level, Level.DEBUG.name()).toLowerCase());
		} catch (Throwable e) {
			this.level = Level.DEBUG;
		}
	}

	private void print(Level level, String msg, Object[] arguments, Throwable t) {
		if (this.level.ordinal() > level.ordinal()) {
			return;
		}
		String delimiter = " ";
		if (arguments != null && arguments.length > 0) {
			System.out.println(Dates.YYYY_MM_DD_HH_MM_SS_SSS.format(Instant.now()) + delimiter + level + delimiter + name + delimiter + Strings.format(msg, arguments));
		} else {
			System.out.println(Dates.YYYY_MM_DD_HH_MM_SS_SSS.format(Instant.now()) + delimiter + level + delimiter + name + delimiter + msg);
		}
		if (t != null) {
			t.printStackTrace();
		}
	}

	@Override
	public boolean isTraceEnabled() {
		return level.ordinal() <= Level.TRACE.ordinal();
	}

	@Override
	public boolean isDebugEnabled() {
		return level.ordinal() <= Level.DEBUG.ordinal();
	}

	@Override
	public boolean isInfoEnabled() {
		return level.ordinal() <= Level.INFO.ordinal();
	}

	@Override
	public boolean isWarnEnabled() {
		return level.ordinal() <= Level.WARN.ordinal();
	}

	@Override
	public boolean isErrorEnabled() {
		return level.ordinal() <= Level.ERROR.ordinal();
	}

	@Override
	public void trace(String msg) {
		trace(msg, ILogger.EMPTY, null);
	}

	@Override
	public void trace(String msg, Object... arguments) {
		trace(msg, arguments, null);
	}

	@Override
	public void trace(String msg, Throwable t) {
		trace(msg, ILogger.EMPTY, t);
	}

	@Override
	public void trace(String msg, Object[] arguments, Throwable t) {
		print(Level.TRACE, msg, arguments, t);
	}

	@Override
	public void trace(Throwable t, String msg, Object... arguments) {
		trace(msg, arguments, t);
	}

	@Override
	public void debug(String msg) {
		debug(msg, ILogger.EMPTY, null);
	}

	@Override
	public void debug(String msg, Object... arguments) {
		debug(msg, arguments, null);
	}

	@Override
	public void debug(String msg, Throwable t) {
		debug(msg, ILogger.EMPTY, t);
	}

	@Override
	public void debug(String msg, Object[] arguments, Throwable t) {
		print(Level.DEBUG, msg, arguments, t);
	}


	@Override
	public void debug(Throwable t, String msg, Object... arguments) {
		debug(msg, arguments, t);
	}

	@Override
	public void info(String msg) {
		info(msg, ILogger.EMPTY, null);
	}

	@Override
	public void info(String msg, Object... arguments) {
		info(msg, arguments, null);
	}

	@Override
	public void info(String msg, Throwable t) {
		info(msg, ILogger.EMPTY, t);
	}

	@Override
	public void info(String msg, Object[] arguments, Throwable t) {
		print(Level.INFO, msg, arguments, t);
	}

	@Override
	public void info(Throwable t, String msg, Object... arguments) {
		info(msg, arguments, t);
	}

	@Override
	public void warn(String msg) {
		warn(msg, ILogger.EMPTY, null);
	}

	@Override
	public void warn(String msg, Object... arguments) {
		warn(msg, arguments, null);
	}

	@Override
	public void warn(String msg, Throwable t) {
		warn(msg, ILogger.EMPTY, t);
	}

	@Override
	public void warn(String msg, Object[] arguments, Throwable t) {
		print(Level.WARN, msg, arguments, t);
	}

	@Override
	public void warn(Throwable t, String msg, Object... arguments) {
		warn(msg, arguments, t);
	}

	@Override
	public void error(String msg) {
		error(msg, ILogger.EMPTY, null);
	}

	@Override
	public void error(String msg, Object... arguments) {
		error(msg, arguments, null);
	}

	@Override
	public void error(String msg, Throwable t) {
		error(msg, ILogger.EMPTY, t);
	}


	@Override
	public void error(String msg, Object[] arguments, Throwable t) {
		print(Level.ERROR, msg, arguments, t);
	}

	@Override
	public void error(Throwable t, String msg, Object... arguments) {
		error(msg, arguments, t);
	}

}
