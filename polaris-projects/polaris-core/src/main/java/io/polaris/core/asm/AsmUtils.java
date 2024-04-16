package io.polaris.core.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.RETURN;

/**
 * @author Qt
 * @since 1.8,  Apr 11, 2024
 */
public class AsmUtils {

	/**
	 * 添加自动拆箱指令
	 */
	public static void autoUnBoxing(MethodVisitor mv, Class<?> clz) {
		autoUnBoxing(mv, Type.getType(clz));
	}

	/**
	 * 添加自动拆箱指令
	 */
	public static void autoUnBoxing(MethodVisitor mv, Type fieldType) {
		switch (fieldType.getSort()) {
			case Type.BOOLEAN:
				mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
				break;
			case Type.BYTE:
				mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
				break;
			case Type.CHAR:
				mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
				break;
			case Type.SHORT:
				mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
				break;
			case Type.INT:
				mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
				break;
			case Type.FLOAT:
				mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
				break;
			case Type.LONG:
				mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
				break;
			case Type.DOUBLE:
				mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
				break;
			case Type.ARRAY:
				mv.visitTypeInsn(CHECKCAST, fieldType.getDescriptor());
				break;
			case Type.OBJECT:
				mv.visitTypeInsn(CHECKCAST, fieldType.getInternalName());
				break;
			default:
				mv.visitTypeInsn(CHECKCAST, fieldType.getInternalName());
		}
	}

	/**
	 * 添加自动装箱指令
	 */
	public static void autoBoxing(MethodVisitor mv, Class<?> clz) {
		autoBoxing(mv, Type.getType(clz));
	}

	/**
	 * 添加自动装箱指令
	 */
	public static void autoBoxing(MethodVisitor mv, Type fieldType) {
		switch (fieldType.getSort()) {
			case Type.BOOLEAN:
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
				break;
			case Type.BYTE:
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
				break;
			case Type.CHAR:
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
				break;
			case Type.SHORT:
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
				break;
			case Type.INT:
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				break;
			case Type.FLOAT:
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
				break;
			case Type.LONG:
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
				break;
			case Type.DOUBLE:
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
				break;
			default:
		}
	}

	/**
	 * 添加自动装箱指令
	 */
	public static void autoBoxingForReturn(MethodVisitor mv, Class<?> clz) {
		autoBoxingForReturn(mv, Type.getType(clz));
	}

	/**
	 * 添加自动装箱指令
	 */
	public static void autoBoxingForReturn(MethodVisitor mv, Type fieldType) {
		if (fieldType.getSort() == Type.VOID) {
			mv.visitInsn(ACONST_NULL);
		} else {
			autoBoxing(mv, fieldType);
		}
	}

	public static void insertDefaultConstructor(ClassWriter cw, String superclassNameInternal) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, superclassNameInternal, "<init>", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	public static Label[] newLabels(int size) {
		Label[] label = new Label[size];
		for (int i = 0; i < size; i++) {
			label[i] = new Label();
		}
		return label;
	}

	public static void storeVar(MethodVisitor mv, Class<?> type, int varIndex) {
		if (type == byte.class || type == boolean.class || type == short.class || type == char.class || type == int.class) {
			mv.visitVarInsn(Opcodes.ISTORE, varIndex);
		} else if (type == long.class) {
			mv.visitVarInsn(Opcodes.LSTORE, varIndex);
		} else if (type == float.class) {
			mv.visitVarInsn(Opcodes.FSTORE, varIndex);
		} else if (type == double.class) {
			mv.visitVarInsn(Opcodes.DSTORE, varIndex);
//		} else if (type == byte[].class || type == boolean[].class) {
//			mv.visitVarInsn(Opcodes.BASTORE, varIndex);
//		} else if (type == char[].class) {
//			mv.visitVarInsn(Opcodes.CASTORE, varIndex);
//		} else if (type == short[].class) {
//			mv.visitVarInsn(Opcodes.SASTORE, varIndex);
//		} else if (type == long[].class) {
//			mv.visitVarInsn(Opcodes.LASTORE, varIndex);
//		} else if (type == float[].class) {
//			mv.visitVarInsn(Opcodes.FASTORE, varIndex);
//		} else if (type == double[].class) {
//			mv.visitVarInsn(Opcodes.DASTORE, varIndex);
//		} else if (type.isArray()) {
//			mv.visitVarInsn(Opcodes.AASTORE, varIndex);
		} else {
			mv.visitVarInsn(Opcodes.ASTORE, varIndex);
		}
	}

	public static void loadVar(MethodVisitor mv, Class<?> type, int varIndex) {
		if (type == byte.class || type == boolean.class || type == short.class || type == char.class || type == int.class) {
			mv.visitVarInsn(Opcodes.ILOAD, varIndex);
		} else if (type == long.class) {
			mv.visitVarInsn(Opcodes.LLOAD, varIndex);
		} else if (type == float.class) {
			mv.visitVarInsn(Opcodes.FLOAD, varIndex);
		} else if (type == double.class) {
			mv.visitVarInsn(Opcodes.DLOAD, varIndex);
//		} else if (type == byte[].class || type == boolean[].class) {
//			mv.visitVarInsn(Opcodes.BALOAD, varIndex);
//		} else if (type == char[].class) {
//			mv.visitVarInsn(Opcodes.CALOAD, varIndex);
//		} else if (type == short[].class) {
//			mv.visitVarInsn(Opcodes.SALOAD, varIndex);
//		} else if (type == long[].class) {
//			mv.visitVarInsn(Opcodes.LALOAD, varIndex);
//		} else if (type == float[].class) {
//			mv.visitVarInsn(Opcodes.FALOAD, varIndex);
//		} else if (type == double[].class) {
//			mv.visitVarInsn(Opcodes.DALOAD, varIndex);
//		} else if (type.isArray()) {
//			mv.visitVarInsn(Opcodes.AALOAD, varIndex);
		} else {
			mv.visitVarInsn(Opcodes.ALOAD, varIndex);
		}
	}

}
