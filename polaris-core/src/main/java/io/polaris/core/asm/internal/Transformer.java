package io.polaris.core.asm.internal;

/**
 * 转换器接口，用于对对象进行转换操作
 *
 * @author Qt
 * @since May 10, 2024
 */
@FunctionalInterface
public interface Transformer<S, T> {

	/**
	 * 对输入对象进行转换
	 *
	 * @param value 待转换的对象
	 * @return 转换后的对象
	 */
	T transform(S value);

}
