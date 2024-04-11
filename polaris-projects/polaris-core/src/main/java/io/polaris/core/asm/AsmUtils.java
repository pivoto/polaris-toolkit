package io.polaris.core.asm;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

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


	public static Label[] newLabels(int size) {
		Label[] label = new Label[size];
		for (int i = 0; i < size; i++) {
			label[i] = new Label();
		}
		return label;
	}
}
