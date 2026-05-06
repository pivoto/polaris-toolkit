package io.polaris.mybatis.enhance.log;


import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;

/**
 * @author Qt
 * @see NoLoggingImpl
 * @since Aug 04, 2024
 */
public class DebuggableLog implements Log {

	private final Log log;

	public DebuggableLog(String clazz) {
		this.log = OriginalLogFactory.getLog(clazz);
	}

	@Override
	public boolean isDebugEnabled() {
		// 强制启用输出Debug，以便在日志插件中获取绑定变量等信息
		return true;
	}

	@Override
	public boolean isTraceEnabled() {
		if (log != null) {
			return log.isTraceEnabled();
		}
		return false;
	}

	@Override
	public void error(String s, Throwable e) {
		if (log != null) {
			log.error(s, e);
		}
	}

	@Override
	public void error(String s) {
		if (log != null) {
			log.error(s);
		}
	}

	@Override
	public void debug(String s) {
		if (log != null && log.isDebugEnabled()) {
			log.debug(s);
		}
	}

	@Override
	public void trace(String s) {
		if (log != null) {
			log.trace(s);
		}
	}

	@Override
	public void warn(String s) {
		if (log != null) {
			log.warn(s);
		}
	}

}
