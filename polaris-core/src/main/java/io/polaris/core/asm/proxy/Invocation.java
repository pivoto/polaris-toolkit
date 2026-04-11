package io.polaris.core.asm.proxy;

import java.lang.reflect.Method;
import java.util.function.Function;

import javax.annotation.Nonnull;

import io.polaris.core.annotation.ByteCodeProcessing;
import io.polaris.core.function.ThrowableFunction;

/**
 * 调用上下文接口，封装方法调用的相关信息和执行逻辑
 *
 * @author Qt
 * @since May 11, 2024
 */
@ByteCodeProcessing
@FunctionalInterface
public interface Invocation {

	/**
	 * 执行方法调用
	 *
	 * @param target 要调用方法的对象实例
	 * @param args 方法调用参数数组
	 * @return 方法执行结果
	 * @throws Throwable 方法执行过程中可能抛出的任何异常
	 */
	Object invoke(Object target, Object[] args) throws Throwable;


	static Invocation noop() {
		return (target, args) -> null;
	}

	static Invocation ofFunction(@Nonnull Function<Object[], Object> function) {
		return (target, args) -> function.apply(args);
	}

	static Invocation ofMethod(@Nonnull Method method) {
		return method::invoke;
	}

	static Invocation ofThrowableFunction(@Nonnull ThrowableFunction<Object[], Object> function) {
		return (target, args) -> function.apply(args);
	}

}
