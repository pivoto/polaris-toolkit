package io.polaris.core.asm.generator;

import org.objectweb.asm.ClassWriter;

/**
 * @author Qt
 * @since May 10, 2024
 */
public class DefaultGeneratorStrategy implements GeneratorStrategy {
	public static final DefaultGeneratorStrategy INSTANCE = new DefaultGeneratorStrategy();


	@Override
	public byte[] generate(ClassGenerator cg) throws Exception {
		DebuggingClassWriter cw = getClassVisitor();
		transform(cg).generateClass(cw);
		return transform(cw.toByteArray());
	}

	protected DebuggingClassWriter getClassVisitor() throws Exception {
		return new DebuggingClassWriter(ClassWriter.COMPUTE_FRAMES);
	}

	protected byte[] transform(byte[] b) throws Exception {
		return b;
	}

	protected ClassGenerator transform(ClassGenerator cg) throws Exception {
		return cg;
	}
}
