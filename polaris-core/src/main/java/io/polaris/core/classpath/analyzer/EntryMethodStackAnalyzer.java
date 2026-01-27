package io.polaris.core.classpath.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AnalyzerAdapter;

/**
 * @author Qt
 * @see AnalyzerAdapter
 * @since Aug 19, 2025
 */
public class EntryMethodStackAnalyzer extends MethodVisitor {

	private final List<StackEntry> stackEntries = new ArrayList<>();
	/**
	 * 当前执行帧的局部变量插槽。 包括：
	 * <ul>
	 * <li>基元类型：
	 * {@link Opcodes#TOP},
	 * {@link Opcodes#INTEGER},
	 * {@link Opcodes#FLOAT},
	 * {@link Opcodes#LONG},
	 * {@link Opcodes#DOUBLE},
	 * {@link Opcodes#NULL},
	 * {@link Opcodes#UNINITIALIZED_THIS}
	 * (long 和 double 由两个元素表示，第二个元素是 TOP).
	 * </li>
	 * <li>
	 * 引用类型: 由 String 对象表示（internal names, see {@link
	 * Type#getInternalName()})
	 * </li>
	 * <li>
	 * 未初始化的类型：由 Label 对象表示，表示 NEW 指令创建的未初始化值的类对象引用
	 * </li>
	 * </ul>
	 * 对于无法访问的指令，将置为 null。
	 */
	public List<Object> local;
	/**
	 * 当前执行帧的操作数栈槽。
	 *  <ul>
	 *  <li>基元类型：
	 * {@link Opcodes#TOP},
	 * {@link Opcodes#INTEGER},
	 * {@link Opcodes#FLOAT},
	 * {@link Opcodes#LONG},
	 * {@link Opcodes#DOUBLE},
	 * {@link Opcodes#NULL},
	 * {@link Opcodes#UNINITIALIZED_THIS}
	 * (long 和 double 由两个元素表示，第二个元素是 TOP).
	 *  </li>
	 * <li>
	 * 引用类型: 由 String 对象表示（internal names, see {@link
	 * Type#getInternalName()})
	 * </li>
	 * <li>
	 * 未初始化的类型：由 Label 对象表示，表示 NEW 指令创建的未初始化值的类对象引用
	 * </li>
	 *  </ul>
	 * 对于无法访问的指令，将置为 null。
	 */
	public List<Object> stack;
	/**
	 * 当前执行帧中未初始化的类型。
	 * 此映射将内部名称关联到标签对象（ {@link Type#getInternalName()}）。
	 * 每个标签表示一个 NEW 指令创建的未初始化值的类对象引用，
	 * 关联的内部名称表示 NEW 指令最终的初始化类型值。
	 */
	public Map<Object, Object> uninitializedTypes;
	/** 指定要访问的下一个指令的标签。 */
	private List<Label> labels;
	/** 此方法的最大栈大小。 */
	private int maxStack;
	/** 该方法的最大局部变量数。 */
	private int maxLocal;
	/** 所有者的内部类名。 */
	private final String owner;


	public EntryMethodStackAnalyzer(final String owner, final int access, final String name, final String descriptor) {
		this(Opcodes.ASM9, owner, access, name, descriptor, null);
	}

	public EntryMethodStackAnalyzer(final String owner, final int access, final String name, final String descriptor, MethodVisitor mv) {
		this(Opcodes.ASM9, owner, access, name, descriptor, mv);
	}

	public EntryMethodStackAnalyzer(final int api, final String owner, final int access, final String name, final String descriptor) {
		this(api, owner, access, name, descriptor, null);
	}

	public EntryMethodStackAnalyzer(final int api, final String owner, final int access, final String name, final String descriptor, MethodVisitor mv) {
		super(api, mv);
		this.owner = owner;
		local = new ArrayList<>();
		stack = new ArrayList<>();
		uninitializedTypes = new HashMap<>();

		// 非静态方法，本地变量槽添加this
		if ((access & Opcodes.ACC_STATIC) == 0) {
			if ("<init>".equals(name)) {
				local.add(Opcodes.UNINITIALIZED_THIS);
			} else {
				local.add(owner);
			}
		}

		// 本地变量槽添加所有方法参数
		for (Type argumentType : Type.getArgumentTypes(descriptor)) {
			switch (argumentType.getSort()) {
				case Type.BOOLEAN:
				case Type.CHAR:
				case Type.BYTE:
				case Type.SHORT:
				case Type.INT:
					local.add(Opcodes.INTEGER);
					break;
				case Type.FLOAT:
					local.add(Opcodes.FLOAT);
					break;
				case Type.LONG:
					local.add(Opcodes.LONG);
					local.add(Opcodes.TOP);
					break;
				case Type.DOUBLE:
					local.add(Opcodes.DOUBLE);
					local.add(Opcodes.TOP);
					break;
				case Type.ARRAY:
					local.add(argumentType.getDescriptor());
					break;
				case Type.OBJECT:
					local.add(argumentType.getInternalName());
					break;
				default:
					throw new AssertionError();
			}
		}

		// 最大局部变量数
		maxLocal = local.size();
	}

	private static void visitFrameTypes(int numTypes, Object[] frameTypes, List<Object> result) {
		for (int i = 0; i < numTypes; ++i) {
			Object frameType = frameTypes[i];
			result.add(frameType);
			if (frameType == Opcodes.LONG || frameType == Opcodes.DOUBLE) {
				result.add(Opcodes.TOP);
			}
		}
	}

	@Override
	public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
		if (type != Opcodes.F_NEW) { // Uncompressed frame.
			throw new IllegalArgumentException(
				"only accepts expanded frames (see ClassReader.EXPAND_FRAMES)");
		}

		super.visitFrame(type, numLocal, local, numStack, stack);

		if (this.local != null) {
			this.local.clear();
			this.stack.clear();
		} else {
			this.local = new ArrayList<>();
			this.stack = new ArrayList<>();
		}
		visitFrameTypes(numLocal, local, this.local);
		visitFrameTypes(numStack, stack, this.stack);
		maxLocal = Math.max(maxLocal, this.local.size());
		maxStack = Math.max(maxStack, this.stack.size());
	}

	@Override
	public void visitInsn(final int opcode) {
		super.visitInsn(opcode);
		execute(opcode, 0, null);
		if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW) {
			this.local = null;
			this.stack = null;
		}
	}

	@Override
	public void visitIntInsn(final int opcode, final int operand) {
		super.visitIntInsn(opcode, operand);
		execute(opcode, operand, null);
	}

	@Override
	public void visitVarInsn(final int opcode, final int varIndex) {
		super.visitVarInsn(opcode, varIndex);
		boolean isLongOrDouble =
			opcode == Opcodes.LLOAD
				|| opcode == Opcodes.DLOAD
				|| opcode == Opcodes.LSTORE
				|| opcode == Opcodes.DSTORE;
		maxLocal = Math.max(maxLocal, varIndex + (isLongOrDouble ? 2 : 1));
		execute(opcode, varIndex, null);
	}

	@Override
	public void visitTypeInsn(final int opcode, final String type) {
		if (opcode == Opcodes.NEW) {
			if (labels == null) {
				Label label = new Label();
				labels = new ArrayList<>(3);
				labels.add(label);
				if (mv != null) {
					mv.visitLabel(label);
				}
			}
			for (Label label : labels) {
				uninitializedTypes.put(label, type);
			}
		}
		super.visitTypeInsn(opcode, type);
		execute(opcode, 0, type);
	}

	@Override
	public void visitFieldInsn(
		final int opcode, final String owner, final String name, final String descriptor) {
		super.visitFieldInsn(opcode, owner, name, descriptor);
		execute(opcode, 0, descriptor);
	}

	@Override
	public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
		if (api < Opcodes.ASM5 && (opcodeAndSource & Opcodes.SOURCE_DEPRECATED) == 0) {
			// Redirect the call to the deprecated version of this method.
			super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
			return;
		}
		super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
		int opcode = opcodeAndSource & ~Opcodes.SOURCE_MASK;

		if (this.local == null) {
			labels = null;
			return;
		}
		pop(descriptor);
		if (opcode != Opcodes.INVOKESTATIC) {
			Object value = pop();
			if (opcode == Opcodes.INVOKESPECIAL && name.equals("<init>")) {
				Object initializedValue;
				if (value == Opcodes.UNINITIALIZED_THIS) {
					initializedValue = this.owner;
				} else {
					initializedValue = uninitializedTypes.get(value);
				}
				for (int i = 0; i < local.size(); ++i) {
					if (local.get(i) == value) {
						local.set(i, initializedValue);
					}
				}
				for (int i = 0; i < stack.size(); ++i) {
					if (stack.get(i) == value) {
						stack.set(i, initializedValue);
					}
				}
			}
		}
		pushDescriptor(descriptor);
		labels = null;
	}

	@Override
	public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
		super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
		if (this.local == null) {
			labels = null;
			return;
		}
		pop(descriptor);
		pushDescriptor(descriptor);
		labels = null;
	}

	@Override
	public void visitJumpInsn(final int opcode, final Label label) {
		super.visitJumpInsn(opcode, label);
		execute(opcode, 0, null);
		if (opcode == Opcodes.GOTO) {
			this.local = null;
			this.stack = null;
		}
	}

	@Override
	public void visitLabel(final Label label) {
		super.visitLabel(label);
		if (labels == null) {
			labels = new ArrayList<>(3);
		}
		labels.add(label);
	}

	@Override
	public void visitLdcInsn(final Object value) {
		super.visitLdcInsn(value);
		if (this.local == null) {
			labels = null;
			return;
		}
		if (value instanceof Integer) {
			push(Opcodes.INTEGER);
		} else if (value instanceof Long) {
			push(Opcodes.LONG);
			push(Opcodes.TOP);
		} else if (value instanceof Float) {
			push(Opcodes.FLOAT);
		} else if (value instanceof Double) {
			push(Opcodes.DOUBLE);
			push(Opcodes.TOP);
		} else if (value instanceof String) {
			push("java/lang/String");
		} else if (value instanceof Type) {
			int sort = ((Type) value).getSort();
			if (sort == Type.OBJECT || sort == Type.ARRAY) {
				push("java/lang/Class");
			} else if (sort == Type.METHOD) {
				push("java/lang/invoke/MethodType");
			} else {
				throw new IllegalArgumentException();
			}
		} else if (value instanceof Handle) {
			push("java/lang/invoke/MethodHandle");
		} else if (value instanceof ConstantDynamic) {
			pushDescriptor(((ConstantDynamic) value).getDescriptor());
		} else {
			throw new IllegalArgumentException();
		}
		labels = null;
	}

	@Override
	public void visitIincInsn(final int varIndex, final int increment) {
		super.visitIincInsn(varIndex, increment);
		maxLocal = Math.max(maxLocal, varIndex + 1);
		execute(Opcodes.IINC, varIndex, null);
	}

	@Override
	public void visitTableSwitchInsn(
		final int min, final int max, final Label dflt, final Label... labels) {
		super.visitTableSwitchInsn(min, max, dflt, labels);
		execute(Opcodes.TABLESWITCH, 0, null);
		this.local = null;
		this.stack = null;
	}

	@Override
	public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
		super.visitLookupSwitchInsn(dflt, keys, labels);
		execute(Opcodes.LOOKUPSWITCH, 0, null);
		this.local = null;
		this.stack = null;
	}

	@Override
	public void visitMultiANewArrayInsn(final String descriptor, final int numDimensions) {
		super.visitMultiANewArrayInsn(descriptor, numDimensions);
		execute(Opcodes.MULTIANEWARRAY, numDimensions, descriptor);
	}

	@Override
	public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
		char firstDescriptorChar = descriptor.charAt(0);
		maxLocal =
			Math.max(
				maxLocal, index + (firstDescriptorChar == 'J' || firstDescriptorChar == 'D' ? 2 : 1));
		super.visitLocalVariable(name, descriptor, signature, start, end, index);
	}


	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		if (mv != null) {
			this.maxStack = Math.max(this.maxStack, maxStack);
			this.maxLocal = Math.max(this.maxLocal, maxLocals);
			mv.visitMaxs(this.maxStack, this.maxLocal);
		}
	}


	// region 更新变量槽与栈槽


	/**
	 * 获取指定局部变量槽中的值
	 *
	 * @param local 局部变量槽索引
	 * @return 局部变量槽中的值，如果索引超出范围则返回 Opcodes.TOP
	 */
	private Object get(final int local) {
		maxLocal = Math.max(maxLocal, local + 1);
		return local < this.local.size() ? this.local.get(local) : Opcodes.TOP;
	}

	private void set(final int local, final Object type) {
		maxLocal = Math.max(maxLocal, local + 1);
		while (local >= this.local.size()) {
			this.local.add(Opcodes.TOP);
		}
		this.local.set(local, type);
	}


	private void push(final Object type) {
		stack.add(type);
	}

	private void pushDescriptor(final String fieldOrMethodDescriptor) {
		String descriptor =
			fieldOrMethodDescriptor.charAt(0) == '('
				? Type.getReturnType(fieldOrMethodDescriptor).getDescriptor()
				: fieldOrMethodDescriptor;
		switch (descriptor.charAt(0)) {
			case 'V':
				return;
			case 'Z':
			case 'C':
			case 'B':
			case 'S':
			case 'I':
				push(Opcodes.INTEGER);
				return;
			case 'F':
				push(Opcodes.FLOAT);
				return;
			case 'J':
				push(Opcodes.LONG);
				push(Opcodes.TOP);
				return;
			case 'D':
				push(Opcodes.DOUBLE);
				push(Opcodes.TOP);
				return;
			case '[':
				push(descriptor);
				break;
			case 'L':
				push(descriptor.substring(1, descriptor.length() - 1));
				break;
			default:
				throw new AssertionError();
		}
	}

	private Object pop() {
		return stack.remove(stack.size() - 1);
	}

	private void pop(final int numSlots) {
		int size = stack.size();
		int end = size - numSlots;
		for (int i = size - 1; i >= end; --i) {
			stack.remove(i);
		}
	}

	private void pop(final String descriptor) {
		char firstDescriptorChar = descriptor.charAt(0);
		if (firstDescriptorChar == '(') {
			int numSlots = 0;
			Type[] types = Type.getArgumentTypes(descriptor);
			for (Type type : types) {
				numSlots += type.getSize();
			}
			pop(numSlots);
		} else if (firstDescriptorChar == 'J' || firstDescriptorChar == 'D') {
			pop(2);
		} else {
			pop(1);
		}
	}


	private void execute(final int opcode, final int intArg, final String stringArg) {
		if (opcode == Opcodes.JSR || opcode == Opcodes.RET) {
			throw new IllegalArgumentException("JSR/RET are not supported");
		}
		if (this.local == null) {
			labels = null;
			return;
		}
		Object value1;
		Object value2;
		Object value3;
		Object t4;
		switch (opcode) {
			case Opcodes.NOP: // 无操作
			case Opcodes.INEG: // 栈顶int型数值取负，并且结果进栈
			case Opcodes.LNEG: // 栈顶long型数值取负，并且结果进栈
			case Opcodes.FNEG: // 栈顶float型数值取负，并且结果进栈
			case Opcodes.DNEG: // 栈顶double型数值取负，并且结果进栈
			case Opcodes.I2B: // 栈顶int值强转byte值，并且结果进栈
			case Opcodes.I2C: // 栈顶int值强转char值，并且结果进栈
			case Opcodes.I2S: // 栈顶int值强转short值，并且结果进栈
			case Opcodes.GOTO: // 无条件跳转
			case Opcodes.RETURN: // 当前方法返回void
				break;
			case Opcodes.ACONST_NULL: // 将null推送至栈顶
				push(Opcodes.NULL);
				break;
			case Opcodes.ICONST_M1: // int型常量值-1进栈
			case Opcodes.ICONST_0: // int型常量值0进栈
			case Opcodes.ICONST_1: // int型常量值1进栈
			case Opcodes.ICONST_2: // int型常量值2进栈
			case Opcodes.ICONST_3: // int型常量值3进栈
			case Opcodes.ICONST_4: // int型常量值4进栈
			case Opcodes.ICONST_5: // int型常量值5进栈
			case Opcodes.BIPUSH: // 将一个byte型常量值(-128~127)推送至栈顶
			case Opcodes.SIPUSH: // 将一个short型常量值(-32768~32767)推送至栈顶
				push(Opcodes.INTEGER);
				break;
			case Opcodes.LCONST_0: // long型常量值0进栈
			case Opcodes.LCONST_1: // long型常量值1进栈
				push(Opcodes.LONG);
				push(Opcodes.TOP);
				break;
			case Opcodes.FCONST_0: // float型常量值0进栈
			case Opcodes.FCONST_1: // float型常量值1进栈
			case Opcodes.FCONST_2: // float型常量值2进栈
				push(Opcodes.FLOAT);
				break;
			case Opcodes.DCONST_0: // double型常量值0进栈
			case Opcodes.DCONST_1: // double型常量值1进栈
				push(Opcodes.DOUBLE);
				push(Opcodes.TOP);
				break;
			case Opcodes.ILOAD: // 指定的int型局部变量进栈
			case Opcodes.FLOAD: // 指定的float型局部变量进栈
			case Opcodes.ALOAD: // 将指定的引用类型本地变量推送至栈顶
				push(get(intArg));
				break;
			case Opcodes.LLOAD: // 指定的long型局部变量进栈
			case Opcodes.DLOAD: // 指定的double型局部变量进栈
				push(get(intArg));
				push(Opcodes.TOP);
				break;
			case Opcodes.LALOAD: // 指定的long型数组的指定下标处的值进栈
			case Opcodes.D2L: // 栈顶double值强转long值，并且结果进栈
				pop(2);
				push(Opcodes.LONG);
				push(Opcodes.TOP);
				break;
			case Opcodes.DALOAD: // 指定的double型数组的指定下标处的值进栈
			case Opcodes.L2D: // 栈顶long值强转double值，并且结果进栈
				pop(2);
				push(Opcodes.DOUBLE);
				push(Opcodes.TOP);
				break;
			case Opcodes.AALOAD: // 将引用型数组指定索引的值推送至栈顶
				pop(1);
				value1 = pop();
				if (value1 instanceof String) {
					pushDescriptor(((String) value1).substring(1));
				} else if (value1 == Opcodes.NULL) {
					push(value1);
				} else {
					push("java/lang/Object");
				}
				break;
			case Opcodes.ISTORE: // 将栈顶int型数值存入指定的局部变量
			case Opcodes.FSTORE: // 将栈顶float型数值存入指定的局部变量
			case Opcodes.ASTORE: // 将栈顶引用类型值存入指定的局部变量
				value1 = pop();
				set(intArg, value1);
				if (intArg > 0) {
					value2 = get(intArg - 1);
					if (value2 == Opcodes.LONG || value2 == Opcodes.DOUBLE) {
						set(intArg - 1, Opcodes.TOP);
					}
				}
				break;
			case Opcodes.LSTORE: // 将栈顶long型数值存入指定的局部变量
			case Opcodes.DSTORE: // 将栈顶double型数值存入指定的局部变量
				pop(1);
				value1 = pop();
				set(intArg, value1);
				set(intArg + 1, Opcodes.TOP);
				if (intArg > 0) {
					value2 = get(intArg - 1);
					if (value2 == Opcodes.LONG || value2 == Opcodes.DOUBLE) {
						set(intArg - 1, Opcodes.TOP);
					}
				}
				break;
			case Opcodes.IASTORE: // 将栈顶int型数值存入指定数组的指定下标处
			case Opcodes.BASTORE: // 将栈顶boolean或byte型数值存入指定数组的指定下标处
			case Opcodes.CASTORE: // 将栈顶char型数值存入指定数组的指定下标处
			case Opcodes.SASTORE: // 将栈顶short型数值存入指定数组的指定下标处
			case Opcodes.FASTORE: // 将栈顶float型数值存入指定数组的指定下标处
			case Opcodes.AASTORE: // 将栈顶引用型数值存入指定数组的指定下标处
				pop(3);
				break;
			case Opcodes.LASTORE: // 将栈顶long型数值存入指定数组的指定下标处
			case Opcodes.DASTORE: // 将栈顶double型数值存入指定数组的指定下标处
				pop(4);
				break;
			case Opcodes.POP: // 将栈顶数值弹出 (数值不能是long或double类型的)
			case Opcodes.IFEQ: // 当栈顶int型数值等于0时跳转
			case Opcodes.IFNE: // 当栈顶int型数值不等于0时跳转
			case Opcodes.IFLT: // 当栈顶int型数值小于0时跳转
			case Opcodes.IFGE: // 当栈顶int型数值大于等于0时跳转
			case Opcodes.IFGT: // 当栈顶int型数值大于0时跳转
			case Opcodes.IFLE: // 当栈顶int型数值小于等于0时跳转
			case Opcodes.IRETURN: // 当前方法返回int
			case Opcodes.FRETURN: // 当前方法返回float
			case Opcodes.ARETURN: // 当前方法返回对象引用
			case Opcodes.TABLESWITCH: // 用于switch条件跳转，case值连续（可变长度指令）
			case Opcodes.LOOKUPSWITCH: // 用于switch条件跳转，case值不连续（可变长度指令）
			case Opcodes.ATHROW: // 将栈顶的异常抛出
			case Opcodes.MONITORENTER: // 获得对象锁，用于同步方法或同步块
			case Opcodes.MONITOREXIT: // 释放对象锁，用于同步方法或同步块
			case Opcodes.IFNULL: // 栈顶对象为null时跳转
			case Opcodes.IFNONNULL: // 栈顶对象不为null时跳转
				pop(1);
				break;
			case Opcodes.POP2: // 将栈顶的一个（long或double类型的)或两个数值弹出（其它）
			case Opcodes.IF_ICMPEQ: // 比较栈顶两int型数值大小，当结果等于0时跳转
			case Opcodes.IF_ICMPNE: // 比较栈顶两int型数值大小，当结果不等于0时跳转
			case Opcodes.IF_ICMPLT: // 比较栈顶两int型数值大小，当结果小于0时跳转
			case Opcodes.IF_ICMPGE: // 比较栈顶两int型数值大小，当结果大于等于0时跳转
			case Opcodes.IF_ICMPGT: // 比较栈顶两int型数值大小，当结果大于0时跳转
			case Opcodes.IF_ICMPLE: // 比较栈顶两int型数值大小，当结果小于等于0时跳转
			case Opcodes.IF_ACMPEQ: // 比较栈顶两引用型数值，当结果相等时跳转
			case Opcodes.IF_ACMPNE: // 比较栈顶两引用型数值，当结果不相等时跳转
			case Opcodes.LRETURN: // 当前方法返回long
			case Opcodes.DRETURN: // 当前方法返回double
				pop(2);
				break;
			case Opcodes.DUP: // 复制栈顶数值并将复制值压入栈顶
				value1 = pop();
				push(value1);
				push(value1);
				break;
			case Opcodes.DUP_X1: // 复制操作数堆栈上的顶值并插入栈顶往下数两个值之下
				value1 = pop();
				value2 = pop();
				push(value1);
				push(value2);
				push(value1);
				break;
			case Opcodes.DUP_X2: // 复制操作数栈顶部操的值并向下插入2个或3个值之下
				value1 = pop();
				value2 = pop();
				value3 = pop();
				push(value1);
				push(value3);
				push(value2);
				push(value1);
				break;
			case Opcodes.DUP2: // 复制栈顶一个（long或double类型的)或两个（其它）数值并将复制值压入栈顶
				value1 = pop();
				value2 = pop();
				push(value2);
				push(value1);
				push(value2);
				push(value1);
				break;
			case Opcodes.DUP2_X1: // 复制栈顶一个（long或double类型的)或两个（其它）数值，并插入栈顶往下数3个值之下， 这个执行其实就是栈顶元素占用2个slot版本的dup_x1
				value1 = pop();
				value2 = pop();
				value3 = pop();
				push(value2);
				push(value1);
				push(value3);
				push(value2);
				push(value1);
				break;
			case Opcodes.DUP2_X2: // 复制栈顶一个（long或double类型的)或两个（其它）数值，并插入栈顶往下数4个值之下， 这个指令就是栈顶元素占用2个slot版本的dup_x2。
				value1 = pop();
				value2 = pop();
				value3 = pop();
				t4 = pop();
				push(value2);
				push(value1);
				push(t4);
				push(value3);
				push(value2);
				push(value1);
				break;
			case Opcodes.SWAP: // 栈顶的两个数值互换(要求栈顶的两个数值不能是long或double型的)
				value1 = pop();
				value2 = pop();
				push(value1);
				push(value2);
				break;
			case Opcodes.IALOAD: // 将int型数组指定索引的值推送至栈顶
			case Opcodes.BALOAD: // 将boolean或byte型数组指定索引的值推送至栈顶
			case Opcodes.CALOAD: // 将char型数组指定索引的值推送至栈顶
			case Opcodes.SALOAD: // 将short型数组指定索引的值推送至栈顶
			case Opcodes.IADD: // 栈顶两int型数值相加，并且结果进栈
			case Opcodes.ISUB: // 栈顶两int型数值相减，并且结果进栈
			case Opcodes.IMUL: // 栈顶两int型数值相乘，并且结果进栈
			case Opcodes.IDIV: // 栈顶两int型数值相除，并且结果进栈
			case Opcodes.IREM: // 栈顶两int型数值作取模运算，并且结果进栈
			case Opcodes.IAND: // 栈顶两int型数值按位与，并且结果进栈
			case Opcodes.IOR: // 栈顶两int型数值按位或，并且结果进栈
			case Opcodes.IXOR: // 栈顶两int型数值按位异或，并且结果进栈
			case Opcodes.ISHL: // int型数值左移指定位数，并且结果进栈
			case Opcodes.ISHR: // int型数值带符号右移指定位数，并且结果进栈
			case Opcodes.IUSHR: // int型数值无符号右移指定位数，并且结果进栈
			case Opcodes.L2I: // 栈顶long值强转int值，并且结果进栈
			case Opcodes.D2I: // 栈顶double值强转int值，并且结果进栈
			case Opcodes.FCMPL: // 比较栈顶两float型数值大小，并且结果（1，0，-1）进栈；当其中一个数值为NaN时， -1进栈
			case Opcodes.FCMPG: // 比较栈顶两float型数值大小，并且结果（1，0，-1）进栈；当其中一个数值为NaN时，1进栈
				pop(2);
				push(Opcodes.INTEGER);
				break;
			case Opcodes.LADD: // 栈顶两long型数值相加，并且结果进栈
			case Opcodes.LSUB: // 栈顶两long型数值相减，并且结果进栈
			case Opcodes.LMUL: // 栈顶两long型数值相乘，并且结果进栈
			case Opcodes.LDIV: // 栈顶两long型数值相除，并且结果进栈
			case Opcodes.LREM: // 栈顶两long型数值作取模运算，并且结果进栈
			case Opcodes.LAND: // 栈顶两long型数值按位与，并且结果进栈
			case Opcodes.LOR: // 栈顶两long型数值按位或，并且结果进栈
			case Opcodes.LXOR: // 栈顶两long型数值按位异或，并且结果进栈
				pop(4);
				push(Opcodes.LONG);
				push(Opcodes.TOP);
				break;
			case Opcodes.FALOAD: // 将float型数组指定索引的值推送至栈顶
			case Opcodes.FADD: // 栈顶两float型数值相加，并且结果进栈
			case Opcodes.FSUB: // 栈顶两float型数值相减，并且结果进栈
			case Opcodes.FMUL: // 栈顶两float型数值相乘，并且结果进栈
			case Opcodes.FDIV: // 栈顶两float型数值相除，并且结果进栈
			case Opcodes.FREM: // 栈顶两float型数值作取模运算，并且结果进栈
			case Opcodes.L2F: // 栈顶long值强转float值，并且结果进栈
			case Opcodes.D2F: // 栈顶double值强转float值，并且结果进栈
				pop(2);
				push(Opcodes.FLOAT);
				break;
			case Opcodes.DADD: // 栈顶两double型数值相加，并且结果进栈
			case Opcodes.DSUB: // 栈顶两double型数值相减，并且结果进栈
			case Opcodes.DMUL: // 栈顶两double型数值相乘，并且结果进栈
			case Opcodes.DDIV: // 栈顶两double型数值相除，并且结果进栈
			case Opcodes.DREM: // 栈顶两double型数值作取模运算，并且结果进栈
				pop(4);
				push(Opcodes.DOUBLE);
				push(Opcodes.TOP);
				break;
			case Opcodes.LSHL: // long型数值左移指定位数，并且结果进栈
			case Opcodes.LSHR: // long型数值带符号右移指定位数，并且结果进栈
			case Opcodes.LUSHR: // long型数值无符号右移指定位数，并且结果进栈
				pop(3);
				push(Opcodes.LONG);
				push(Opcodes.TOP);
				break;
			case Opcodes.IINC: // 指定int型变量增加指定值
				set(intArg, Opcodes.INTEGER);
				break;
			case Opcodes.I2L: // 栈顶int值强转long值，并且结果进栈
			case Opcodes.F2L: // 栈顶float值强转long值，并且结果进栈
				pop(1);
				push(Opcodes.LONG);
				push(Opcodes.TOP);
				break;
			case Opcodes.I2F: // 栈顶int值强转float值，并且结果进栈
				pop(1);
				push(Opcodes.FLOAT);
				break;
			case Opcodes.I2D: // 栈顶int值强转double值，并且结果进栈
			case Opcodes.F2D: // 栈顶float值强转double值，并且结果进栈
				pop(1);
				push(Opcodes.DOUBLE);
				push(Opcodes.TOP);
				break;
			case Opcodes.F2I: // 栈顶float值强转int值，并且结果进栈
			case Opcodes.ARRAYLENGTH: // 获得数组的长度值并压入栈顶
			case Opcodes.INSTANCEOF: // 检查对象是否是指定的类的实例。如果是，1进栈；否则，0进栈
				pop(1);
				push(Opcodes.INTEGER);
				break;
			case Opcodes.LCMP: // 比较栈顶两long型数值大小，并且结果（1，0，-1）进栈
			case Opcodes.DCMPL: // 比较栈顶两double型数值大小，并且结果（1，0，-1）进栈；当其中一个数值为NaN时，-1进栈
			case Opcodes.DCMPG: // 比较栈顶两double型数值大小，并且结果（1，0，-1）进栈；当其中一个数值为NaN时，1进栈
				pop(4);
				push(Opcodes.INTEGER);
				break;
			case Opcodes.GETSTATIC: // 获取指定类的静态域，并将其值压入栈顶
				pushDescriptor(stringArg);
				break;
			case Opcodes.PUTSTATIC: // 为指定的类的静态域赋值
				pop(stringArg);
				break;
			case Opcodes.GETFIELD: // 获取指定类的实例域，并将其值压入栈顶
				pop(1);
				pushDescriptor(stringArg);
				break;
			case Opcodes.PUTFIELD: // 为指定的类的实例域赋值
				pop(stringArg);
				pop();
				break;
			case Opcodes.NEW: // 创建一个对象，并且其引用进栈
				push(labels.get(0));
				break;
			case Opcodes.NEWARRAY: // 栈顶数值（count）作为数组长度，创建一个基本类型数组，并且其引用进栈
				pop();
				switch (intArg) {
					case Opcodes.T_BOOLEAN: //
						pushDescriptor("[Z");
						break;
					case Opcodes.T_CHAR: //
						pushDescriptor("[C");
						break;
					case Opcodes.T_BYTE: //
						pushDescriptor("[B");
						break;
					case Opcodes.T_SHORT: //
						pushDescriptor("[S");
						break;
					case Opcodes.T_INT: //
						pushDescriptor("[I");
						break;
					case Opcodes.T_FLOAT: //
						pushDescriptor("[F");
						break;
					case Opcodes.T_DOUBLE: //
						pushDescriptor("[D");
						break;
					case Opcodes.T_LONG: //
						pushDescriptor("[J");
						break;
					default:
						throw new IllegalArgumentException("Invalid array type " + intArg);
				}
				break;
			case Opcodes.ANEWARRAY: // 栈顶数值（count）作为数组长度，创建一个引用型（如类，接口，数组）的数组，并且其引用进栈
				pop();
				pushDescriptor("[" + Type.getObjectType(stringArg));
				break;
			case Opcodes.CHECKCAST: // 类型转换检查，如果该检查未通过将会抛出ClassCastException异常
				pop();
				pushDescriptor(Type.getObjectType(stringArg).getDescriptor());
				break;
			case Opcodes.MULTIANEWARRAY: // 创建指定类型和维度的多维数组（执行该指令时，栈中必须包含各维度的长度值），并且其引用值进栈
				pop(intArg);
				pushDescriptor(stringArg);
				break;
			default:
				throw new IllegalArgumentException("Invalid opcode " + opcode);
		}
		labels = null;
	}

	// endregion


	@Getter
	@RequiredArgsConstructor
	public static class  StackEntry {
		private final Object type;
		private final int codeLine;
	}


}
