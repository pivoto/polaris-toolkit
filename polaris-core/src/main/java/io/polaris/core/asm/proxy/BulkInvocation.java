package io.polaris.core.asm.proxy;

import java.lang.reflect.Method;

import lombok.Getter;

/**
 * 批量调用实现类，支持多个拦截器的链式调用
 *
 * @author Qt
 * @since May 11, 2024
 */
public class BulkInvocation implements Invocation {

	/**
	 * 方法在目标方法数组中的索引
	 */
	private final int index;

	/**
	 * 代理对象实例
	 */
	@Getter
	private final Object proxy;

	/**
	 * 被调用的方法对象
	 */
	@Getter
	private final Method method;

	/**
	 * 原始调用器，用于直接调用目标方法
	 */
	private final Invoker rawInvoker;

	/**
	 * 父类调用器，用于调用父类方法
	 */
	private final Invoker superInvoker;

	/**
	 * 拦截器数组，按顺序执行所有匹配的拦截器
	 */
	private final Interceptor[] interceptors;

	/**
	 * 当前拦截器执行游标，指向下一个要执行的拦截器
	 */
	private int cursor = 0;

	/**
	 * 构造批量调用上下文
	 *
	 * @param index 方法索引位置
	 * @param proxy 代理对象实例
	 * @param method 被调用的方法
	 * @param rawInvoker 原始方法调用器
	 * @param superInvoker 父类方法调用器
	 * @param interceptors 要执行的拦截器数组
	 */
	public BulkInvocation(int index, Object proxy, Method method, Invoker rawInvoker, Invoker superInvoker, Interceptor[] interceptors) {
		this.index = index;
		this.proxy = proxy;
		this.method = method;
		this.rawInvoker = rawInvoker;
		this.superInvoker = superInvoker;
		this.interceptors = interceptors;
	}

	/**
	 * 执行方法调用，按顺序执行所有拦截器
	 * <p>
	 * 如果还有未执行的拦截器，则继续执行下一个；否则根据调用对象决定使用原始调用器还是父类调用器
	 *
	 * @param target 要调用方法的对象实例
	 * @param args 方法调用参数数组
	 * @return 方法执行结果
	 * @throws Throwable 方法执行过程中抛出的异常
	 */
	@Override
	public Object invoke(Object target, Object[] args) throws Throwable {
		if (cursor < interceptors.length) {
			// 继续下一个拦截器
			return interceptors[cursor++].intercept(target, method, args, this);
		}
		if (proxy == target) {
			return superInvoker.invoke(index, proxy, target, args);
		} else {
			return rawInvoker.invoke(index, proxy, target, args);
		}
	}

}
