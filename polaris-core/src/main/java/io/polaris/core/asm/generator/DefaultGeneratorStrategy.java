package io.polaris.core.asm.generator;

import org.objectweb.asm.ClassWriter;

/**
 * 默认生成策略实现类
 * <p>使用 DebuggingClassWriter 生成类字节码，支持调试和缓存功能</p>
 *
 * @author Qt
 * @since May 10, 2024
 */
public class DefaultGeneratorStrategy implements GeneratorStrategy {
	/** 单例实例 */
	public static final DefaultGeneratorStrategy INSTANCE = new DefaultGeneratorStrategy();


	/**
	 * 执行类字节码生成
	 *
	 * @param cg 类生成器，提供类结构信息
	 * @return 生成的字节码数组
	 * @throws Exception 生成过程中可能出现的异常
	 */
	@Override
	public byte[] generate(ClassGenerator cg) throws Exception {
		// 创建调试类写入器
		DebuggingClassWriter cw = getClassVisitor();
		// 使用 ClassGenerator 生成类结构
		transform(cg).generate(cw);
		// 获取并返回字节码数组
		return transform(cw.toByteArray());
	}

	/**
	 * 获取 ClassVisitor 实例
	 * <p>默认创建 DebuggingClassWriter，启用计算栈帧</p>
	 *
	 * @return DebuggingClassWriter 实例
	 * @throws Exception 创建过程中可能出现的异常
	 */
	protected DebuggingClassWriter getClassVisitor() throws Exception {
		return new DebuggingClassWriter(ClassWriter.COMPUTE_FRAMES);
	}

	/**
	 * 转换字节码数组
	 * <p>子类可以重写此方法对生成的字节码进行后处理</p>
	 *
	 * @param b 原始字节码数组
	 * @return 转换后的字节码数组
	 * @throws Exception 转换过程中可能出现的异常
	 */
	protected byte[] transform(byte[] b) throws Exception {
		return b;
	}

	/**
	 * 转换 ClassGenerator
	 * <p>子类可以重写此方法对生成器进行装饰或修改</p>
	 *
	 * @param cg 原始 ClassGenerator
	 * @return 转换后的 ClassGenerator
	 * @throws Exception 转换过程中可能出现的异常
	 */
	protected ClassGenerator transform(ClassGenerator cg) throws Exception {
		return cg;
	}
}
