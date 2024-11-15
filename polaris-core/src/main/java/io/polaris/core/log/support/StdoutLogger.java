package io.polaris.core.log.support;

import java.time.Instant;

import io.polaris.core.collection.ObjectArrays;
import io.polaris.core.consts.SystemKeys;
import io.polaris.core.function.ConsumerWithArgs4;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.Level;
import io.polaris.core.string.Strings;
import io.polaris.core.time.Dates;

/**
 * @author Qt
 * @since Aug 04, 2023
 */
public class StdoutLogger implements ILogger {
	private final String name;
	private final Level level;
	private ConsumerWithArgs4<Level, String, Object[], Throwable> printer;

	public StdoutLogger(String name) {
		this.name = name;
		Level level;
		try {
			String levelStr = System.getProperty(SystemKeys.LOGGER_LEVEL + "." + name);
			level = Level.valueOf(Strings.coalesce(levelStr, Level.DEBUG.name()).toLowerCase());
		} catch (Throwable e) {
			level = Level.DEBUG;
		}
		this.level = level;
	}

	public StdoutLogger(String name, Level level, ConsumerWithArgs4<Level, String, Object[], Throwable> printer) {
		this.name = name;
		this.level = level;
		this.printer = printer;
	}

	private void print(Level level, String msg, Object[] arguments, Throwable t) {
		if (this.level.ordinal() > level.ordinal()) {
			return;
		}
		if (printer != null) {
			printer.accept(level, msg, arguments, t);
			return;
		}
		String delimiter = " ";
		long tid = Thread.currentThread().getId();
		if (arguments != null && arguments.length > 0) {
			System.out.println(Dates.YYYY_MM_DD_HH_MM_SS_SSS.format(Instant.now()) + delimiter + level + delimiter
				+ "[" + tid + "]" + delimiter + name + delimiter + Strings.format(msg, arguments));
		} else {
			System.out.println(Dates.YYYY_MM_DD_HH_MM_SS_SSS.format(Instant.now()) + delimiter + level + delimiter
				+ "[" + tid + "]" + delimiter + name + delimiter + msg);
		}
		if (t != null) {
			t.printStackTrace(System.out);
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
		print(Level.TRACE, msg, arguments, t);
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
		print(Level.DEBUG, msg, arguments, t);
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
		print(Level.INFO, msg, arguments, t);
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
		print(Level.WARN, msg, arguments, t);
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
		print(Level.ERROR, msg, arguments, t);
	}

	@Override
	public void error(Throwable t, String msg, Object... arguments) {
		error(msg, arguments, t);
	}

}
