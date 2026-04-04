package io.polaris.core.asm.proxy;

import java.lang.reflect.Method;

/**
 * 拦截器接口，用于在方法调用前后执行额外的逻辑
 * 
 * @author Qt
 * @since May 11, 2024
 */
public interface Interceptor {

	/**
	 * 拦截方法调用并执行额外逻辑
	 *
	 * @param obj 被拦截方法的对象实例
	 * @param method 被拦截的方法对象
	 * @param args 方法调用参数数组
	 * @param invocation 调用上下文，用于继续执行后续拦截器或目标方法
	 * @return 方法执行结果
	 * @throws Throwable 拦截器执行过程中可能抛出的任何异常
	 */
	Object intercept(Object obj, Method method, Object[] args, Invocation invocation) throws Throwable;

}
