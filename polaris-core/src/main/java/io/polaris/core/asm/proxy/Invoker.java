package io.polaris.core.asm.proxy;

import java.lang.reflect.InvocationTargetException;

/**
 * 调用器接口，用于通过索引调用目标方法
 *
 * @author Qt
 * @since May 11, 2024
 */
public interface Invoker {

	/**
	 * 通过方法索引调用指定对象的方法
	 *
	 * @param index 方法在目标方法数组中的索引位置
	 * @param proxy 代理对象实例
	 * @param target 目标对象实例
	 * @param args 方法调用参数数组
	 * @return 方法执行结果
	 * @throws InvocationTargetException 方法调用过程中发生的异常
	 */
	Object invoke(int index, Object proxy, Object target, Object[] args) throws InvocationTargetException;

}
