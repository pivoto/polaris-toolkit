package io.polaris.core.asm.proxy;

import java.lang.reflect.Method;

import javax.annotation.Nonnull;

import io.polaris.core.annotation.ByteCodeProcessing;
import lombok.Getter;

/**
 * 默认调用实现类，封装单个拦截器的调用上下文
 *
 * @author Qt
 * @since May 11, 2024
 */
@ByteCodeProcessing
public class DefaultInvocation implements Invocation {

	/** 方法在目标方法数组中的索引 */
	private final int index;
	/** 代理对象实例 */
	private final Object proxy;
	/** 被调用的方法对象 */
	private final Method method;
	/** 原始调用器，用于直接调用目标方法 */
	private final Invoker rawInvoker;
	/** 父类调用器，用于调用父类方法 */
	private final Invoker superInvoker;

	/**
	 * 构造默认调用上下文
	 *
	 * @param index 方法索引位置
	 * @param proxy 代理对象实例
	 * @param method 被调用的方法
	 * @param rawInvoker 原始方法调用器
	 * @param superInvoker 父类方法调用器
	 */
	@ByteCodeProcessing
	public DefaultInvocation(int index, Object proxy, Method method, Invoker rawInvoker, Invoker superInvoker) {
		this.index = index;
		this.proxy = proxy;
		this.method = method;
		this.rawInvoker = rawInvoker;
		this.superInvoker = superInvoker;
	}

	@Nonnull
	@Override
	public Object getProxy() {
		return proxy;
	}

	@Nonnull
	@Override
	public Method getMethod() {
		return method;
	}

	/**
	 * 执行方法调用
	 * <p>
	 * 如果调用对象与代理对象不同，则使用原始调用器；否则使用父类调用器
	 *
	 * @param target 要调用方法的对象实例
	 * @param args 方法调用参数数组
	 * @return 方法执行结果
	 * @throws Throwable 方法执行过程中抛出的异常
	 */
	@Override
	public Object invoke(Object target, Object[] args) throws Throwable {
		if (proxy == target) {
			return superInvoker.invoke(index, proxy, target, args);
		} else {
			return rawInvoker.invoke(index, proxy, target, args);
		}
	}

}
