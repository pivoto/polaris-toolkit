package io.polaris.core.aop;

import java.lang.reflect.Method;

import io.polaris.core.tuple.ValueRef;

/**
 * 固定返回值拦截器，在目标方法调用之前，晚于{@link BeforeAdvice}执行
 *
 * @author Qt
 * @since May 13, 2024
 */
public interface FixedAdvice extends Advice {

	/**
	 * 获取固定返回值，本方法返回值不为null时，则结束目标方法调用并返回其引用的值，否则继续后续拦截器与方法调用
	 *
	 * @param target 目标对象实例
	 * @param method 目标方法对象
	 * @param args 方法参数数组
	 * @return 固定返回值的引用，如果返回null则继续后续拦截器与方法调用
	 */
	ValueRef<?> fixedValue(Object target, Method method, Object[] args);

}
