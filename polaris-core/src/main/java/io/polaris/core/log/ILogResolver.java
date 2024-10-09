package io.polaris.core.log;

/**
 * @author Qt
 */
public interface ILogResolver {

	default ILogger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}

	ILogger getLogger(String name);

}
