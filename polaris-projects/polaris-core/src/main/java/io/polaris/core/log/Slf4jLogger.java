package io.polaris.core.log;

import io.polaris.core.collection.ObjectArrays;
import io.polaris.core.string.Strings;
import org.slf4j.Logger;

/**
 * @author Qt
 * @since  Aug 04, 2023
 */
public class Slf4jLogger implements ILogger {

	private Logger log;

	public Slf4jLogger(Logger log) {
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
			if (t == null) {
				if (arguments == null || arguments.length == 0) {
					log.trace(msg);
				} else {
					log.trace(msg, arguments);
				}
			}else{
				if (arguments == null || arguments.length == 0) {
					log.trace(msg, t);
				} else {
					log.trace(Strings.format(msg, arguments), t);
				}
			}
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
			if (t == null) {
				if (arguments == null || arguments.length == 0) {
					log.debug(msg);
				} else {
					log.debug(msg, arguments);
				}
			}else{
				if (arguments == null || arguments.length == 0) {
					log.debug(msg, t);
				} else {
					log.debug(Strings.format(msg, arguments), t);
				}
			}
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
			if (t == null) {
				if (arguments == null || arguments.length == 0) {
					log.info(msg);
				} else {
					log.info(msg, arguments);
				}
			}else{
				if (arguments == null || arguments.length == 0) {
					log.info(msg, t);
				} else {
					log.info(Strings.format(msg, arguments), t);
				}
			}
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
			if (t == null) {
				if (arguments == null || arguments.length == 0) {
					log.warn(msg);
				} else {
					log.warn(msg, arguments);
				}
			}else{
				if (arguments == null || arguments.length == 0) {
					log.warn(msg, t);
				} else {
					log.warn(Strings.format(msg, arguments), t);
				}
			}
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
			if (t == null) {
				if (arguments == null || arguments.length == 0) {
					log.error(msg);
				} else {
					log.error(msg, arguments);
				}
			}else{
				if (arguments == null || arguments.length == 0) {
					log.error(msg, t);
				} else {
					log.error(Strings.format(msg, arguments), t);
				}
			}
		}
	}

	@Override
	public void error(Throwable t, String msg, Object... arguments) {
		error(msg, arguments, t);
	}


}
