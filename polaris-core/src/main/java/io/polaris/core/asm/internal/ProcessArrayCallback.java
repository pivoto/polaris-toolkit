package io.polaris.core.asm.internal;

import org.objectweb.asm.Type;

/**
 * 数组处理回调接口，用于遍历处理数组元素
 *
 * @author Qt
 * @since May 10, 2024
 */
public interface ProcessArrayCallback {

	/**
	 * 处理数组元素
	 *
	 * @param type 数组元素的类型
	 */
	void processElement(Type type);

}
