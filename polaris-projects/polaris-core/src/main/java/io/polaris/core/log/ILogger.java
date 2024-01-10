package io.polaris.core.log;

/**
 * @author Qt
 * @since 1.8,  Aug 04, 2023
 */
public interface ILogger {

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
