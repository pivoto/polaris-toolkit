package io.polaris.core.aop;

import java.lang.reflect.Method;

/**
 * 前置通知，在目标方法调用之前执行
 *
 * @author Qt
 * @since May 13, 2024
 */
public interface BeforeAdvice extends Advice {

	/**
	 * 前置通知处理
	 *
	 * @param proxy 代理对象实例
	 * @param target 目标对象实例
	 * @param method 目标方法对象
	 * @param args 方法参数数组
	 */
	void before(Object proxy, Object target, Method method, Object[] args) throws Throwable;

}
