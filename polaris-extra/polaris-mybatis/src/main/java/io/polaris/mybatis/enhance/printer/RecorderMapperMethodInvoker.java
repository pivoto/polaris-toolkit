package io.polaris.mybatis.enhance.printer;

import java.lang.reflect.Method;

/**
 * @author Qt
 * @since Apr 03, 2026
 */
interface RecorderMapperMethodInvoker {

	Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}
