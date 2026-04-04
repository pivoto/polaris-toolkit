package io.polaris.core.asm.generator;

import org.objectweb.asm.ClassVisitor;

/**
 * 类生成器接口
 * <p>定义类生成的基本契约，用于通过 ASM 字节码生成库动态创建类</p>
 *
 * @author Qt
 * @since May 10, 2024
 */
public interface ClassGenerator {

	/**
	 * 生成类字节码
	 * <p>通过 ClassVisitor 将类结构写入到 ASM 框架中</p>
	 *
	 * @param v 类访问者，用于接收类结构信息
	 * @throws Exception 生成过程中可能出现的异常
	 */
	void generateClass(ClassVisitor v) throws Exception;

}
