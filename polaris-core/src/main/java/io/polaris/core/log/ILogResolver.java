package io.polaris.core.log;

/**
 * @author Qt
 */
public interface ILogResolver {

	default Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}

	Logger getLogger(String name);

}
