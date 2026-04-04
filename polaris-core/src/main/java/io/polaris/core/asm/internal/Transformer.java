package io.polaris.core.asm.internal;

/**
 * 转换器接口，用于对对象进行转换操作
 * 
 * @author Qt
 * @since May 10, 2024
 */
public interface Transformer {

	/**
	 * 对输入对象进行转换
	 *
	 * @param value 待转换的对象
	 * @return 转换后的对象
	 */
	Object transform(Object value);

}
