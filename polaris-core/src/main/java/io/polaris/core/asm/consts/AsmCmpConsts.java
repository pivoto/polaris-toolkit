package io.polaris.core.asm.consts;

import org.objectweb.asm.Opcodes;

/**
 * @author Qt
 * @since Apr 10, 2026
 */
public interface AsmCmpConsts {

	int EQ = Opcodes.IFEQ;
	int NE = Opcodes.IFNE;
	int LT = Opcodes.IFLT;
	int GE = Opcodes.IFGE;
	int GT = Opcodes.IFGT;
	int LE = Opcodes.IFLE;

}
