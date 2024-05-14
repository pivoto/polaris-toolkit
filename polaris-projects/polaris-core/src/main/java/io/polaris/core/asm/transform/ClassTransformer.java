package io.polaris.core.asm.transform;

import io.polaris.core.asm.internal.AsmConsts;
import org.objectweb.asm.ClassVisitor;

/**
 * @author Qt
 * @since May 10, 2024
 */
public abstract class ClassTransformer extends ClassVisitor {
	public ClassTransformer() {
		super(AsmConsts.ASM_API);
	}

	public ClassTransformer(int opcode) {
		super(opcode);
	}

	public abstract void setTarget(ClassVisitor target);
}
