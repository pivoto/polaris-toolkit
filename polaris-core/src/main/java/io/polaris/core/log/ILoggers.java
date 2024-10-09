package io.polaris.core.log;

import io.polaris.core.log.support.DefaultLoggerResolver;
import io.polaris.core.log.support.DynamicLoggerResolver;
import io.polaris.core.log.support.StdoutLogger;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since Jan 10, 2024
 */
public class ILoggers {
	/** 日志对象构造工厂实现。在javaagent环境下需要尽早注入以适配应用自身的日志配置 */
	private static ILogResolver RESOLVER = new DefaultLoggerResolver();

	public static ILogger of(Class<?> c) {
		if (RESOLVER == null) {
			return new StdoutLogger(c.getName());
		}
		return RESOLVER.getLogger(c);
	}

	public static ILogger of(String name) {
		if (RESOLVER == null) {
			return new StdoutLogger(name);
		}
		return RESOLVER.getLogger(name);
	}

	public static ILogger of() {
		return of(detectLoggerName());
	}

	private static String detectLoggerName() {
		String name = null;
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		for (int i = 1; i < elements.length; i++) {
			String className = elements[i].getClassName();
			if (!ILoggers.class.getName().equals(className)) {
				name = className;
				break;
			}
		}
		return Strings.coalesce(name, "");
	}

	public static void setResolver(ILogResolver resolver) {
		RESOLVER = resolver;
	}

}
