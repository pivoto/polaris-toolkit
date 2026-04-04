package io.polaris.core.asm.generator;

/**
 * 生成策略接口
 * <p>定义类字节码生成的策略，控制如何将 ClassGenerator 转换为实际的字节码数组</p>
 *
 * @author Qt
 * @since May 10, 2024
 */
public interface GeneratorStrategy {

	/**
	 * 执行类生成
	 * <p>使用指定的 ClassGenerator 生成字节码数组</p>
	 *
	 * @param cg 类生成器，提供类结构信息
	 * @return 生成的字节码数组
	 * @throws Exception 生成过程中可能出现的异常
	 */
	byte[] generate(ClassGenerator cg) throws Exception;

}
