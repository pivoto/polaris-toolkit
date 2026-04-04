package io.polaris.core.asm.internal;

import org.objectweb.asm.Label;

/**
 * 整数 Switch 回调接口，用于处理 switch 语句的不同分支
 *
 * @author Qt
 * @since May 10, 2024
 */
public interface ProcessSwitchCallback {

	/**
	 * 处理匹配的 case 分支
	 *
	 * @param key 匹配的整数值
	 * @param end 结束标签，用于跳转
	 * @throws Exception 处理过程中可能抛出的异常
	 */
	void processCase(int key, Label end) throws Exception;

	/**
	 * 处理 default 分支
	 *
	 * @throws Exception 处理过程中可能抛出的异常
	 */
	void processDefault() throws Exception;

}
