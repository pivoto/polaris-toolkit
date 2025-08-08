package io.polaris.core.log;

/**
 * @author Qt
 */
public interface LogResolver {

	default Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}

	Logger getLogger(String name);

}
