package io.polaris.core.asm.proxy;

import java.lang.reflect.Method;

import javax.annotation.Nonnull;

import io.polaris.core.annotation.ByteCodeProcessing;

/**
 * 调用上下文接口，封装方法调用的相关信息和执行逻辑
 *
 * @author Qt
 * @since May 11, 2024
 */
@ByteCodeProcessing
public interface Invocation {

	/**
	 * 获取代理对象
	 *
	 * @return 当前的代理对象实例
	 */
	@Nonnull
	Object getProxy();

	/**
	 * 获取被调用的方法对象
	 *
	 * @return 当前调用的 Method 对象
	 */
	@Nonnull
	Method getMethod();

	/**
	 * 执行方法调用
	 *
	 * @param target 要调用方法的对象实例
	 * @param args 方法调用参数数组
	 * @return 方法执行结果
	 * @throws Throwable 方法执行过程中可能抛出的任何异常
	 */
	Object invoke(Object target, Object[] args) throws Throwable;

}
