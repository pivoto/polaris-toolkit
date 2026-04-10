package io.polaris.core.asm.consts;

import io.polaris.core.asm.internal.AsmConsts;
import org.objectweb.asm.Opcodes;

/**
 * @author Qt
 * @since Apr 10, 2026
 */
public interface AsmMathConsts {

	int ADD = Opcodes.IADD;
	int MUL = Opcodes.IMUL;
	int XOR = Opcodes.IXOR;
	int USHR = Opcodes.IUSHR;
	int SUB = Opcodes.ISUB;
	int DIV = Opcodes.IDIV;
	int NEG = Opcodes.INEG;
	int REM = Opcodes.IREM;
	int AND = Opcodes.IAND;
	int OR = Opcodes.IOR;


}
