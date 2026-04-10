package io.polaris.core.aop;

import java.lang.reflect.Method;

/**
 * 后置通知，在目标方法调用后且无异常抛出时执行，晚于 {@link AroundAdvice}
 *
 * @author Qt
 * @since May 13, 2024
 */
public interface AfterAdvice extends Advice {

	/**
	 * 后置通知
	 *
	 * @param target 目标对象实例
	 * @param method 目标方法对象
	 * @param args 方法参数数组
	 * @param retVal 方法返回值
	 * @throws Throwable 方法执行过程中抛出的异常
	 */
	void after(Object target, Method method, Object[] args, Object retVal) throws Throwable;

}
