package io.polaris.core.aop;

import java.lang.reflect.Method;

/**
 * 异常通知，在目标方法抛出异常时执行，晚于 {@link AroundAdvice} 执行
 *
 * @author Qt
 * @since May 13, 2024
 */
public interface ThrowingAdvice extends Advice {

	/**
	 * 异常通知，在目标方法抛出异常时执行。
	 * 如果拦截处理中再次抛出异常，则该异常会先于目标方法抛出的异常返回给调用方
	 *
	 * @param proxy 代理对象实例
	 * @param target 目标对象实例
	 * @param method 目标方法对象
	 * @param args 方法参数数组
	 * @param e 抛出的异常
	 * @throws Throwable 拦截器再次抛出的异常
	 */
	void throwing(Object proxy, Object target, Method method, Object[] args, Throwable e) throws Throwable;

}
