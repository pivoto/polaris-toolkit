package io.polaris.core.aop;

import java.lang.reflect.Method;

import io.polaris.core.asm.proxy.Invocation;

/**
 * 环绕通知，在目标方法执行前后进行拦截处理，晚于{@link BeforeAdvice}与{@link FixedAdvice}，早于 {@link AfterAdvice}
 *
 * @author Qt
 * @since May 13, 2024
 */
public interface AroundAdvice extends Advice {

	/**
	 * 环绕通知处理
	 *
	 * @param target 目标对象实例
	 * @param method 目标方法对象
	 * @param args 方法参数数组
	 * @param invocation 目标方法调用器
	 * @return 最终返回值
	 * @throws Throwable 方法执行过程中抛出的异常
	 */
	Object around(Object target, Method method, Object[] args, Invocation invocation) throws Throwable;

}
