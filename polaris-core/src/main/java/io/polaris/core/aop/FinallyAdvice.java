package io.polaris.core.aop;

import java.lang.reflect.Method;

/**
 * 后置通知，在目标方法与所有拦截器执行后执行，类似于finally块处理
 *
 * @author Qt
 * @since May 13, 2024
 */
public interface FinallyAdvice extends Advice {

	/**
	 * 后置通知执行逻辑
	 *
	 * @param target 目标对象实例
	 * @param method 目标方法对象
	 * @param args 方法调用参数数组
	 * @param retVal 方法返回值
	 * @param e 方法执行过程中抛出的异常
	 * @throws Throwable 方法执行过程中抛出的异常
	 */
	void after(Object target, Method method, Object[] args, Object retVal, Throwable e) throws Throwable;

}
