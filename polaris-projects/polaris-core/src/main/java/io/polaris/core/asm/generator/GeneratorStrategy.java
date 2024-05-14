package io.polaris.core.asm.generator;

/**
 * @author Qt
 * @since May 10, 2024
 */
public interface GeneratorStrategy {

	byte[] generate(ClassGenerator cg) throws Exception;

}
