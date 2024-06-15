package io.polaris.core.asm.generator;

import org.objectweb.asm.ClassVisitor;

/**
 * @author Qt
 * @since May 10, 2024
 */
public interface ClassGenerator {

	void generateClass(ClassVisitor v) throws Exception;

}
