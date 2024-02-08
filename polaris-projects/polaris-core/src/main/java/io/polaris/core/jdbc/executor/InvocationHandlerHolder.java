package io.polaris.core.jdbc.executor;

import java.lang.reflect.InvocationHandler;

/**
 * @author Qt
 * @since 1.8,  Feb 06, 2024
 */
interface InvocationHandlerHolder {

	InvocationHandler $handler();

}
