package io.polaris.mybatis.enhance.log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogException;
import org.apache.ibatis.logging.LogFactory;

/**
 * @author Qt
 * @since Apr 01, 2026
 */
public class OriginalLogFactory {

	private static Constructor<? extends Log> logConstructor;
	private static Class<? extends Log> logImplClass;

	public static Class<? extends Log> getLogImplClass() {
		if (logImplClass == null) {
			useOriginalLogging();
		}
		return logImplClass;
	}

	public static Log getLog(Class<?> clazz) {
		return getLog(clazz.getName());
	}

	public static Log getLog(String logger) {
		if (logImplClass == null || DebuggableLog.class.isAssignableFrom(logImplClass)) {
			return null;
		}
		try {
			return logConstructor.newInstance(logger);
		} catch (Throwable t) {
			throw new LogException("Error creating logger for logger " + logger + ".  Cause: " + t, t);
		}
	}

	@SuppressWarnings("unchecked")
	public static void useOriginalLogging() {
		try {
			Field field = LogFactory.class.getDeclaredField("logConstructor");
			field.setAccessible(true);
			logConstructor = (Constructor<? extends Log>) field.get(null);
			logImplClass = logConstructor.getDeclaringClass();
		} catch (Exception ignored) {
		}
	}

	public static void useOriginalLogging(Class<? extends Log> implClass) {
		if (implClass == null || DebuggableLog.class.isAssignableFrom(implClass)) {
			useOriginalLogging();
			return;
		}
		try {
			Constructor<? extends Log> candidate = implClass.getConstructor(String.class);
			Log log = candidate.newInstance(LogFactory.class.getName());
			if (log.isDebugEnabled()) {
				log.debug("Logging initialized using '" + implClass + "' adapter.");
			}
			logConstructor = candidate;
			logImplClass = implClass;
		} catch (Throwable t) {
			throw new LogException("Error setting Log implementation.  Cause: " + t, t);
		}
	}
}
