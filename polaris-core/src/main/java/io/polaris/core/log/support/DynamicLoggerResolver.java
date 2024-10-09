package io.polaris.core.log.support;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.polaris.core.classloader.ClassLoaders;
import io.polaris.core.env.GlobalStdEnv;
import io.polaris.core.log.ILogResolver;
import io.polaris.core.log.ILogger;

/**
 * @author Qt
 */
public class DynamicLoggerResolver implements ILogResolver {
	public static final String PREFER_DYNAMIC_SLF4J = DynamicLoggerResolver.class.getName() + ".prefer-dynamic-slf4j";
	private final Map<String, ILogger> CACHE = new ConcurrentHashMap<>();
	private volatile long lastLoaderChangeId = ClassLoaders.INSTANCE.changeId();
	private final boolean preferDynamicSlf4j;

	public DynamicLoggerResolver() {
		this.preferDynamicSlf4j = GlobalStdEnv.getBoolean(PREFER_DYNAMIC_SLF4J, false);
	}

	@Override
	public ILogger getLogger(String name) {
		if (ClassLoaders.INSTANCE.changeId() != lastLoaderChangeId) {
			lastLoaderChangeId = ClassLoaders.INSTANCE.changeId();
			CACHE.clear();
		}
		return CACHE.computeIfAbsent(name, k -> newLogger(name));
	}


	private ILogger newLogger(String name) {
		ILogger logger = null;
		if (preferDynamicSlf4j) {
			logger = getDynamicSlf4jLogger(name);
			if (logger != null) {
				return logger;
			}
		}
		logger = getDirectSlf4jLogger(name);
		if (logger != null) {
			return logger;
		}
		if (!preferDynamicSlf4j) {
			logger = getDynamicSlf4jLogger(name);
			if (logger != null) {
				return logger;
			}
		}
		return new StdoutLogger(name);
	}

	private static ILogger getDynamicSlf4jLogger(String name) {
		try {
			Class<?> classLoggerFactory = ClassLoaders.INSTANCE.loadClass("org.slf4j.LoggerFactory");
			Class<?> classLogger = ClassLoaders.INSTANCE.loadClass("org.slf4j.Logger");
			Class<?> classLocationAwareLogger = ClassLoaders.INSTANCE.loadClass("org.slf4j.spi.LocationAwareLogger");
			Class<?> classMarker = ClassLoaders.INSTANCE.loadClass("org.slf4j.Marker");
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			MethodType methodType = MethodType.methodType(classLogger, new Class[]{String.class});
			MethodHandle handle = lookup.findStatic(classLoggerFactory, "getLogger", methodType);
			Object logger = handle.invokeWithArguments(name);
			if (classLocationAwareLogger.isInstance(logger)) {
				return new DynamicSlf4jAwareLogger(classLocationAwareLogger, classMarker, logger);
			} else {
				return new DynamicSlf4jLogger(classLogger, logger);
			}
		} catch (Throwable e) {// no dependency
			// noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
		return null;
	}

	private static ILogger getDirectSlf4jLogger(String name) {
		try {
			org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(name);
			if (logger instanceof org.slf4j.spi.LocationAwareLogger) {
				return new Slf4jAwareLogger((org.slf4j.spi.LocationAwareLogger) logger);
			} else {
				return new Slf4jLogger(logger);
			}
		} catch (Throwable e) { // no dependency
			// noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
		return null;
	}
}
