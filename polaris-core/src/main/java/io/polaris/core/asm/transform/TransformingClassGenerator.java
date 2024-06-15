package io.polaris.core.asm.transform;

import io.polaris.core.asm.generator.ClassGenerator;
import org.objectweb.asm.ClassVisitor;

/**
 * @author Qt
 * @since May 10, 2024
 */
public class TransformingClassGenerator implements ClassGenerator {
	private ClassGenerator gen;
	private ClassTransformer t;

	public TransformingClassGenerator(ClassGenerator gen, ClassTransformer t) {
		this.gen = gen;
		this.t = t;
	}

	@Override
	public void generateClass(ClassVisitor v) throws Exception {
		t.setTarget(v);
		gen.generateClass(t);
	}
}
