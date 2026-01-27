package io.polaris.core.classpath.analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.annotation.concurrent.NotThreadSafe;

import lombok.Getter;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since Aug 19, 2025
 */
@NotThreadSafe
public class MethodCodeAnalyzer extends MethodVisitor {
	public static final String TRY_CATCH_BLOCK = "X-TryCatchBlock";
	public static final String LOCAL = "X-Local";
	public static final String LINE = "X-Line";
	public static final String LABEL = "X-Label";

	/** 所有者的内部类名。 */
	@Getter
	private final String methodOwner;
	@Getter
	private final String methodName;
	@Getter
	private final String methodDescriptor;
	@Getter
	private final String[] methodExceptions;
	/** 字节码指令行 */
	@Getter
	private final List<CodeLine> lines = new ArrayList<>();
	/** 当前执行帧的局部变量插槽 */
	@Getter
	private final List<CodeValue> local = new ArrayList<>();
	/** 当前执行帧的操作数栈槽 */
	@Getter
	private final List<CodeValue> stack = new ArrayList<>();

	private Label lastLabel;
	/**
	 * 当前执行帧中未初始化的类型。
	 * 此映射将内部名称关联到标签对象（ {@link Type#getInternalName()}）。
	 * 每个标签表示一个 NEW 指令创建的未初始化值的类对象引用，
	 * 关联的内部名称表示 NEW 指令最终的初始化类型值。
	 */
	public Map<Object, String> uninitializedTypes = new HashMap<>();
	/** 该方法的最大局部变量数。 */
	@Getter
	private int maxLocal;
	/** 此方法的最大栈大小。 */
	@Getter
	private int maxStack;
	private BiConsumer<MethodCodeAnalyzer, MethodCall> onMethodCall;
	private List<String> referenceResources;


	public MethodCodeAnalyzer(final String owner, final int access, final String name, final String descriptor, String[] methodExceptions) {
		this(Opcodes.ASM9, owner, access, name, descriptor, methodExceptions, null);
	}

	public MethodCodeAnalyzer(final String owner, final int access, final String name, final String descriptor, String[] methodExceptions, MethodVisitor mv) {
		this(Opcodes.ASM9, owner, access, name, descriptor, methodExceptions, mv);
	}

	public MethodCodeAnalyzer(final int api, final String owner, final int access, final String name, final String descriptor, String[] methodExceptions) {
		this(api, owner, access, name, descriptor, methodExceptions, null);
	}

	public MethodCodeAnalyzer(final int api, final String owner, final int access, final String name, final String descriptor, String[] methodExceptions, MethodVisitor mv) {
		super(api, mv);
		this.methodOwner = owner;
		this.methodName = name;
		this.methodDescriptor = descriptor;
		this.methodExceptions = methodExceptions;

		// 非静态方法，本地变量槽添加this
		if ((access & Opcodes.ACC_STATIC) == 0) {
			if ("<init>".equals(name)) {
				local.add(new CodeValue(Opcodes.UNINITIALIZED_THIS));
			} else {
				local.add(new CodeValue(owner));
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
					local.add(new CodeValue(Opcodes.INTEGER));
					break;
				case Type.FLOAT:
					local.add(new CodeValue(Opcodes.FLOAT));
					break;
				case Type.LONG:
					local.add(new CodeValue(Opcodes.LONG));
					local.add(new CodeValue(Opcodes.TOP));
					break;
				case Type.DOUBLE:
					local.add(new CodeValue(Opcodes.DOUBLE));
					local.add(new CodeValue(Opcodes.TOP));
					break;
				case Type.ARRAY:
					local.add(new CodeValue(argumentType.getDescriptor()));
					break;
				case Type.OBJECT:
					local.add(new CodeValue(argumentType.getInternalName()));
					break;
				default:
					throw new AssertionError();
			}
		}

		// 最大局部变量数
		maxLocal = local.size();
	}


	public void onMethodCall(BiConsumer<MethodCodeAnalyzer, MethodCall> onMethodCall) {
		this.onMethodCall = onMethodCall;
	}

	/**
	 * 添加引用资源， 用于在onMethodCall监听方法逻辑中注入关联资源，用于计算sha1值，体现方法变更情况
	 */
	public void addReferenceResource(String resource) {
		if (referenceResources == null) {
			referenceResources = new ArrayList<>();
		}
		referenceResources.add(resource);
	}


	public String calcSha1() {
		return EntryAnalyzer.calcBase32Sha1(dump());
	}

	public String calcSha1Char8() {
		return EntryAnalyzer.calcBase32Sha1Char8(dump());
	}

	private void appendCodeValue(StringBuilder sb, List<CodeValue> list) {
		for (CodeValue val : list) {
			sb.append(val.dump());
			sb.append(", ");
		}
		if (!list.isEmpty()) {
			sb.delete(sb.length() - 2, sb.length());
		}
	}

	public String dump() {
		return dump(0);
	}

	public String dump(int level) {
		char[] tab = new char[level];
		Arrays.fill(tab, '\t');
		StringBuilder sb = new StringBuilder();
		sb.append(methodOwner).append(".").append(methodName).append(methodDescriptor);
		sb.append(" throws ").append(Arrays.toString(methodExceptions));
		sb.append("\n").append(tab);
		sb.append("maxLocal: ").append(maxLocal)
			.append(", maxStack: ").append(maxStack)
			.append("\n").append(tab);
		sb.append("local: [");
		appendCodeValue(sb, local);
		sb.append("]\n").append(tab);
		sb.append("stack: [");
		appendCodeValue(sb, stack);
		sb.append("]\n").append(tab);
		sb.append("lines:");
		for (CodeLine line : this.lines) {
			sb.append("\n").append(tab).append("\t").append(line.dump());
		}
		if (this.referenceResources != null && !this.referenceResources.isEmpty()) {
			sb.append("\n").append(tab).append("referenceResources: ");
			for (String resource : this.referenceResources) {
				sb.append("\n").append(tab).append("\t").append(resource);
			}
		}
		return sb.toString();
	}

	private void addCode(CodeLine line) {
		if (lastLabel != null) {
			line.setLabel(lastLabel);
			lastLabel = null;// 移除标签记录
		}
		lines.add(line);
	}


	/**
	 * Visits the current state of the local variables and operand stack elements. This method must(*)
	 * be called <i>just before</i> any instruction <b>i</b> that follows an unconditional branch
	 * instruction such as GOTO or THROW, that is the target of a jump instruction, or that starts an
	 * exception handler block. The visited types must describe the values of the local variables and
	 * of the operand stack elements <i>just before</i> <b>i</b> is executed.<br>
	 * <br>
	 * (*) this is mandatory only for classes whose version is greater than or equal to {@link
	 * Opcodes#V1_6}. <br>
	 * <br>
	 * The frames of a method must be given either in expanded form, or in compressed form (all frames
	 * must use the same format, i.e. you must not mix expanded and compressed frames within a single
	 * method):
	 *
	 * <ul>
	 *   <li>In expanded form, all frames must have the F_NEW type.
	 *   <li>In compressed form, frames are basically "deltas" from the state of the previous frame:
	 *       <ul>
	 *         <li>{@link Opcodes#F_SAME} representing frame with exactly the same locals as the
	 *             previous frame and with the empty stack.
	 *         <li>{@link Opcodes#F_SAME1} representing frame with exactly the same locals as the
	 *             previous frame and with single value on the stack ( <code>numStack</code> is 1 and
	 *             <code>stack[0]</code> contains value for the type of the stack item).
	 *         <li>{@link Opcodes#F_APPEND} representing frame with current locals are the same as the
	 *             locals in the previous frame, except that additional locals are defined (<code>
	 *             numLocal</code> is 1, 2 or 3 and <code>local</code> elements contains values
	 *             representing added types).
	 *         <li>{@link Opcodes#F_CHOP} representing frame with current locals are the same as the
	 *             locals in the previous frame, except that the last 1-3 locals are absent and with
	 *             the empty stack (<code>numLocal</code> is 1, 2 or 3).
	 *         <li>{@link Opcodes#F_FULL} representing complete frame data.
	 *       </ul>
	 * </ul>
	 *
	 * <br>
	 * In both cases the first frame, corresponding to the method's parameters and access flags, is
	 * implicit and must not be visited. Also, it is illegal to visit two or more frames for the same
	 * code location (i.e., at least one instruction must be visited between two calls to visitFrame).
	 *
	 * @param type     the type of this stack map frame. Must be {@link Opcodes#F_NEW} for expanded
	 *                 frames, or {@link Opcodes#F_FULL}, {@link Opcodes#F_APPEND}, {@link Opcodes#F_CHOP}, {@link
	 *                 Opcodes#F_SAME} or {@link Opcodes#F_APPEND}, {@link Opcodes#F_SAME1} for compressed frames.
	 * @param numLocal the number of local variables in the visited frame. Long and double values
	 *                 count for one variable.
	 * @param local    the local variable types in this frame. This array must not be modified. Primitive
	 *                 types are represented by {@link Opcodes#TOP}, {@link Opcodes#INTEGER}, {@link
	 *                 Opcodes#FLOAT}, {@link Opcodes#LONG}, {@link Opcodes#DOUBLE}, {@link Opcodes#NULL} or
	 *                 {@link Opcodes#UNINITIALIZED_THIS} (long and double are represented by a single element).
	 *                 Reference types are represented by String objects (representing internal names, see {@link
	 *                 Type#getInternalName()}), and uninitialized types by Label objects (this label designates
	 *                 the NEW instruction that created this uninitialized value).
	 * @param numStack the number of operand stack elements in the visited frame. Long and double
	 *                 values count for one stack element.
	 * @param stack    the operand stack types in this frame. This array must not be modified. Its
	 *                 content has the same format as the "local" array.
	 * @throws IllegalStateException if a frame is visited just after another one, without any
	 *                               instruction between the two (unless this frame is a Opcodes#F_SAME frame, in which case it
	 *                               is silently ignored).
	 */
	@Override
	public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
		// 必须启用 `ClassReader.EXPAND_FRAMES`
		if (type != Opcodes.F_NEW) {
			// Uncompressed frame.
			throw new IllegalArgumentException(
				"only accepts expanded frames (see ClassReader.EXPAND_FRAMES)");
		}

		doFilterFrame(numLocal, local, this.local);
		doFilterFrame(numStack, stack, this.stack);

		maxLocal = Math.max(maxLocal, this.local.size());
		maxStack = Math.max(maxStack, this.stack.size());
		super.visitFrame(type, numLocal, local, numStack, stack);
	}

	private void doFilterFrame(int numTypes, Object[] frameTypes, List<CodeValue> result) {
		int localSize = 0;
		for (int i = 0; i < numTypes; i++) {
			Object frameType = frameTypes[i];
			if (frameType == Opcodes.LONG || frameType == Opcodes.DOUBLE) {
				localSize += 2;
				if (localSize <= result.size()) {
					CodeValue curr = result.get(localSize - 2);
					// todo 保持变量值
					result.set(localSize - 2, new CodeValue(frameType).copyFrom(curr));
					result.set(localSize - 1, new CodeValue(Opcodes.TOP));
				} else if (localSize == 1 + result.size()) {
					CodeValue curr = result.get(localSize - 1);
					result.set(localSize - 1, new CodeValue(frameType).copyFrom(curr));
					result.add(new CodeValue(Opcodes.TOP));
				} else {
					result.add(new CodeValue(frameType));
					result.add(new CodeValue(Opcodes.TOP));
				}
			} else {
				localSize++;
				if (localSize <= result.size()) {
					CodeValue curr = result.get(localSize - 1);
					// todo 保持变量值
					result.set(localSize - 1, new CodeValue(frameType).copyFrom(curr));
				} else {
					result.add(new CodeValue(frameType));
				}
			}
		}
		// 删除多余的变量或栈帧
		if (localSize < result.size()) {
			result.subList(localSize, result.size()).clear();
		}
	}


	/**
	 * Visits a zero operand instruction.
	 *
	 * @param opcode the opcode of the instruction to be visited. This opcode is either NOP,
	 *               ACONST_NULL, ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5,
	 *               LCONST_0, LCONST_1, FCONST_0, FCONST_1, FCONST_2, DCONST_0, DCONST_1, IALOAD, LALOAD,
	 *               FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD, IASTORE, LASTORE, FASTORE, DASTORE,
	 *               AASTORE, BASTORE, CASTORE, SASTORE, POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2,
	 *               SWAP, IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB, IMUL, LMUL, FMUL, DMUL, IDIV, LDIV,
	 *               FDIV, DDIV, IREM, LREM, FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL, LSHL, ISHR, LSHR, IUSHR,
	 *               LUSHR, IAND, LAND, IOR, LOR, IXOR, LXOR, I2L, I2F, I2D, L2I, L2F, L2D, F2I, F2L, F2D, D2I,
	 *               D2L, D2F, I2B, I2C, I2S, LCMP, FCMPL, FCMPG, DCMPL, DCMPG, IRETURN, LRETURN, FRETURN,
	 *               DRETURN, ARETURN, RETURN, ARRAYLENGTH, ATHROW, MONITORENTER, or MONITOREXIT.
	 */
	@Override
	public void visitInsn(final int opcode) {
		addCode(new CodeLine(opcode, new Object[]{}));

		execute(opcode, 0, null);
		if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW) {
			// return或throw 指令
			this.local.clear();
			this.stack.clear();
		}
		super.visitInsn(opcode);
	}

	/**
	 * Visits an instruction with a single int operand.
	 *
	 * @param opcode  the opcode of the instruction to be visited. This opcode is either BIPUSH, SIPUSH
	 *                or NEWARRAY.
	 * @param operand the operand of the instruction to be visited.<br>
	 *                When opcode is BIPUSH, operand value should be between Byte.MIN_VALUE and Byte.MAX_VALUE.
	 *                <br>
	 *                When opcode is SIPUSH, operand value should be between Short.MIN_VALUE and Short.MAX_VALUE.
	 *                <br>
	 *                When opcode is NEWARRAY, operand value should be one of {@link Opcodes#T_BOOLEAN}, {@link
	 *                Opcodes#T_CHAR}, {@link Opcodes#T_FLOAT}, {@link Opcodes#T_DOUBLE}, {@link Opcodes#T_BYTE},
	 *                {@link Opcodes#T_SHORT}, {@link Opcodes#T_INT} or {@link Opcodes#T_LONG}.
	 */
	@Override
	public void visitIntInsn(final int opcode, final int operand) {
		addCode(new CodeLine(opcode, new Object[]{operand}));
		execute(opcode, operand, null);
		super.visitIntInsn(opcode, operand);
	}


	/**
	 * Visits a local variable instruction. A local variable instruction is an instruction that loads
	 * or stores the value of a local variable.
	 *
	 * @param opcode   the opcode of the local variable instruction to be visited. This opcode is either
	 *                 ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET.
	 * @param varIndex the operand of the instruction to be visited. This operand is the index of a
	 *                 local variable.
	 */
	@Override
	public void visitVarInsn(final int opcode, final int varIndex) {
		addCode(new CodeLine(opcode, new Object[]{varIndex}));
		boolean isLongOrDouble =
			opcode == Opcodes.LLOAD
				|| opcode == Opcodes.DLOAD
				|| opcode == Opcodes.LSTORE
				|| opcode == Opcodes.DSTORE;
		maxLocal = Math.max(maxLocal, varIndex + (isLongOrDouble ? 2 : 1));
		execute(opcode, varIndex, null);
		super.visitVarInsn(opcode, varIndex);
	}

	/**
	 * Visits a type instruction. A type instruction is an instruction that takes the internal name of
	 * a class as parameter (see {@link Type#getInternalName()}).
	 *
	 * @param opcode the opcode of the type instruction to be visited. This opcode is either NEW,
	 *               ANEWARRAY, CHECKCAST or INSTANCEOF.
	 * @param type   the operand of the instruction to be visited. This operand must be the internal
	 *               name of an object or array class (see {@link Type#getInternalName()}).
	 */
	@Override
	public void visitTypeInsn(final int opcode, final String type) {
		addCode(new CodeLine(opcode, new Object[]{type}));
		if (opcode == Opcodes.NEW) {
			if (lastLabel == null) {
				Label label = new Label();
				lastLabel = label;
				if (mv != null) {
					mv.visitLabel(label);
				}
			}
			uninitializedTypes.put(lastLabel, type);
		}
		execute(opcode, 0, type);
		super.visitTypeInsn(opcode, type);
	}

	/**
	 * Visits a field instruction. A field instruction is an instruction that loads or stores the
	 * value of a field of an object.
	 *
	 * @param opcode     the opcode of the type instruction to be visited. This opcode is either
	 *                   GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
	 * @param owner      the internal name of the field's owner class (see {@link Type#getInternalName()}).
	 * @param name       the field's name.
	 * @param descriptor the field's descriptor (see {@link Type}).
	 */
	@Override
	public void visitFieldInsn(
		final int opcode, final String owner, final String name, final String descriptor) {
		addCode(new CodeLine(opcode, new Object[]{owner, name, descriptor}));
		execute(opcode, 0, descriptor);
		if (opcode == Opcodes.GETSTATIC) {
			// 获取静态字段，设置静态字段的值
			CodeValue val = peek2();
			assert val != null;
			val.withGetStatic(owner, name);
		}
		super.visitFieldInsn(opcode, owner, name, descriptor);
	}


	/**
	 * Visits a method instruction. A method instruction is an instruction that invokes a method.
	 *
	 * @param opcode      the opcode of the type instruction to be visited. This opcode is either
	 *                    INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
	 * @param owner       the internal name of the method's owner class (see {@link
	 *                    Type#getInternalName()}).
	 * @param name        the method's name.
	 * @param descriptor  the method's descriptor (see {@link Type}).
	 * @param isInterface if the method's owner class is an interface.
	 */
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
		if (api < Opcodes.ASM5 && (opcode & Opcodes.SOURCE_DEPRECATED) == 0) {
			// Redirect the call to the deprecated version of this method.
			super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
			return;
		}
		addCode(new CodeLine(opcode, new Object[]{owner, name, descriptor, isInterface}));
		opcode = opcode & ~Opcodes.SOURCE_MASK;

		// 注入方法监听事件，抓取特定类与方法的信息: rpc:xxx, mq:xxx, job:xxx, sql:xxx
		if (onMethodCall != null) {
			MethodCall methodCall = new MethodCall();

			methodCall.setStaticMethod(opcode == Opcodes.INVOKESTATIC);
			methodCall.setSpecialMethod(opcode == Opcodes.INVOKESPECIAL);
			methodCall.setVirtualMethod(opcode == Opcodes.INVOKEVIRTUAL);
			methodCall.setInterfaceMethod(opcode == Opcodes.INVOKEINTERFACE);

			methodCall.setClassName(owner);
			methodCall.setMethodName(name);
			methodCall.setMethodDesc(descriptor);
			methodCall.setMethodReturnType(Type.getReturnType(descriptor));
			methodCall.setMethodArgumentTypes(Type.getArgumentTypes(descriptor));

			CodeValue[] args = peek2(descriptor);
			methodCall.setMethodArgs(args);

			onMethodCall.accept(this, methodCall);
		}


		pop(descriptor);
		if (opcode != Opcodes.INVOKESTATIC) {
			CodeValue value = pop();
			if (opcode == Opcodes.INVOKESPECIAL && name.equals("<init>")) {
				String initializedValue;
				if (value.getType() == Opcodes.UNINITIALIZED_THIS) {
					initializedValue = this.methodOwner;
				} else {
					initializedValue = uninitializedTypes.get(value.getType());
				}
				for (int i = 0; i < local.size(); ++i) {
					if (local.get(i).getType() == value.getType()) {
						local.set(i, new CodeValue(initializedValue));
					}
				}
				for (int i = 0; i < stack.size(); ++i) {
					if (stack.get(i).getType() == value.getType()) {
						stack.set(i, new CodeValue(initializedValue));
					}
				}
			}
		}
		pushDescriptor(descriptor);
		lastLabel = null;// 移除标签记录

		super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
	}


	/**
	 * Visits an invokedynamic instruction.
	 *
	 * @param name                     the method's name.
	 * @param descriptor               the method's descriptor (see {@link Type}).
	 * @param bootstrapMethodHandle    the bootstrap method.
	 * @param bootstrapMethodArguments the bootstrap method constant arguments. Each argument must be
	 *                                 an {@link Integer}, {@link Float}, {@link Long}, {@link Double}, {@link String}, {@link
	 *                                 Type}, {@link Handle} or {@link ConstantDynamic} value. This method is allowed to modify
	 *                                 the content of the array so a caller should expect that this array may change.
	 */
	@Override
	public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
		addCode(new CodeLine(Opcodes.INVOKEDYNAMIC, new Object[]{name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments}));

		pop(descriptor);
		pushDescriptor(descriptor);
		lastLabel = null;// 移除标签记录
		super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
	}

	/**
	 * Visits a jump instruction. A jump instruction is an instruction that may jump to another
	 * instruction.
	 *
	 * @param opcode the opcode of the type instruction to be visited. This opcode is either IFEQ,
	 *               IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT,
	 *               IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE, GOTO, JSR, IFNULL or IFNONNULL.
	 * @param label  the operand of the instruction to be visited. This operand is a label that
	 *               designates the instruction to which the jump instruction may jump.
	 */
	@Override
	public void visitJumpInsn(final int opcode, final Label label) {
		addCode(new CodeLine(opcode, new Object[]{label}));
		execute(opcode, 0, null);
		super.visitJumpInsn(opcode, label);
	}

	/**
	 * Visits a label. A label designates the instruction that will be visited just after it.
	 *
	 * @param label a {@link Label} object.
	 */
	@Override
	public void visitLabel(final Label label) {
		addCode(new CodeLine(-1, LABEL, new Object[]{label}));
		this.lastLabel = label;
		super.visitLabel(label);
	}

	/**
	 * Visits a LDC instruction. Note that new constant types may be added in future versions of the
	 * Java Virtual Machine. To easily detect new constant types, implementations of this method
	 * should check for unexpected constant types, like this:
	 *
	 * <pre>
	 * if (cst instanceof Integer) {
	 *     // ...
	 * } else if (cst instanceof Float) {
	 *     // ...
	 * } else if (cst instanceof Long) {
	 *     // ...
	 * } else if (cst instanceof Double) {
	 *     // ...
	 * } else if (cst instanceof String) {
	 *     // ...
	 * } else if (cst instanceof Type) {
	 *     int sort = ((Type) cst).getSort();
	 *     if (sort == Type.OBJECT) {
	 *         // ...
	 *     } else if (sort == Type.ARRAY) {
	 *         // ...
	 *     } else if (sort == Type.METHOD) {
	 *         // ...
	 *     } else {
	 *         // throw an exception
	 *     }
	 * } else if (cst instanceof Handle) {
	 *     // ...
	 * } else if (cst instanceof ConstantDynamic) {
	 *     // ...
	 * } else {
	 *     // throw an exception
	 * }
	 * </pre>
	 *
	 * @param value the constant to be loaded on the stack. This parameter must be a non null {@link
	 *              Integer}, a {@link Float}, a {@link Long}, a {@link Double}, a {@link String}, a {@link
	 *              Type} of OBJECT or ARRAY sort for {@code .class} constants, for classes whose version is
	 *              49, a {@link Type} of METHOD sort for MethodType, a {@link Handle} for MethodHandle
	 *              constants, for classes whose version is 51 or a {@link ConstantDynamic} for a constant
	 *              dynamic for classes whose version is 55.
	 */
	@Override
	public void visitLdcInsn(final Object value) {
		addCode(new CodeLine(Opcodes.LDC, new Object[]{value}));
		if (value instanceof Integer) {
			push(Opcodes.INTEGER).withLdc(value);
		} else if (value instanceof Long) {
			push(Opcodes.LONG).withLdc(value);
			push(Opcodes.TOP);
		} else if (value instanceof Float) {
			push(Opcodes.FLOAT).withLdc(value);
		} else if (value instanceof Double) {
			push(Opcodes.DOUBLE).withLdc(value);
			push(Opcodes.TOP);
		} else if (value instanceof String) {
			push("java/lang/String").withLdc(value);
		} else if (value instanceof Type) {
			int sort = ((Type) value).getSort();
			if (sort == Type.OBJECT || sort == Type.ARRAY) {
				push("java/lang/Class").withLdc(value);
			} else if (sort == Type.METHOD) {
				push("java/lang/invoke/MethodType").withLdc(value);
			} else {
				throw new IllegalArgumentException();
			}
		} else if (value instanceof Handle) {
			push("java/lang/invoke/MethodHandle").withLdc(value);
		} else if (value instanceof ConstantDynamic) {
			pushDescriptor(((ConstantDynamic) value).getDescriptor()).withLdc(value);
		} else {
			throw new IllegalArgumentException();
		}
		lastLabel = null;// 移除标签记录
		super.visitLdcInsn(value);
	}

	/**
	 * Visits an IINC instruction.
	 *
	 * @param varIndex  index of the local variable to be incremented.
	 * @param increment amount to increment the local variable by.
	 */
	@Override
	public void visitIincInsn(final int varIndex, final int increment) {
		addCode(new CodeLine(Opcodes.IINC, new Object[]{varIndex, increment}));
		maxLocal = Math.max(maxLocal, varIndex + 1);
		execute(Opcodes.IINC, varIndex, null);
		CodeValue val = get(varIndex);

		Object ov = null;
		Object nv = null;
		if (val.isConstant()) {
			ov = val.getConstantValue();
		} else if (val.isLdc()) {
			ov = val.getLdcValue();
		}
		if (ov instanceof Integer) {
			int v = (Integer) ov + increment;
			nv = v;
		} else if (ov instanceof Long) {
			long v = (Long) ov + increment;
			nv = v;
		} else if (ov instanceof Float) {
			float v = (Float) ov + increment;
			nv = v;
		} else if (ov instanceof Double) {
			double v = (Double) ov + increment;
			nv = v;
		} else if (ov instanceof Short) {
			short v = (short) ((Short) ov + increment);
			nv = v;
		} else if (ov instanceof Byte) {
			byte v = (byte) ((Byte) ov + increment);
			nv = v;
		}
		if (nv != null) {
			if (val.isConstant()) {
				val.setConstantValue(nv);
			} else if (val.isLdc()) {
				val.setLdcValue(nv);
			}
		}
		super.visitIincInsn(varIndex, increment);
	}

	/**
	 * Visits a TABLESWITCH instruction.
	 *
	 * @param min    the minimum key value.
	 * @param max    the maximum key value.
	 * @param dflt   beginning of the default handler block.
	 * @param labels beginnings of the handler blocks. {@code labels[i]} is the beginning of the
	 *               handler block for the {@code min + i} key.
	 */
	@Override
	public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label... labels) {
		addCode(new CodeLine(Opcodes.TABLESWITCH, new Object[]{min, max, dflt, labels}));
		execute(Opcodes.TABLESWITCH, 0, null);
		super.visitTableSwitchInsn(min, max, dflt, labels);
	}

	/**
	 * Visits a LOOKUPSWITCH instruction.
	 *
	 * @param dflt   beginning of the default handler block.
	 * @param keys   the values of the keys.
	 * @param labels beginnings of the handler blocks. {@code labels[i]} is the beginning of the
	 *               handler block for the {@code keys[i]} key.
	 */
	@Override
	public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
		addCode(new CodeLine(Opcodes.LOOKUPSWITCH, new Object[]{dflt, keys, labels}));
		execute(Opcodes.LOOKUPSWITCH, 0, null);
		super.visitLookupSwitchInsn(dflt, keys, labels);
	}

	/**
	 * Visits a MULTIANEWARRAY instruction.
	 *
	 * @param descriptor    an array type descriptor (see {@link Type}).
	 * @param numDimensions the number of dimensions of the array to allocate.
	 */
	@Override
	public void visitMultiANewArrayInsn(final String descriptor, final int numDimensions) {
		addCode(new CodeLine(Opcodes.MULTIANEWARRAY, new Object[]{descriptor, numDimensions}));
		execute(Opcodes.MULTIANEWARRAY, numDimensions, descriptor);
		super.visitMultiANewArrayInsn(descriptor, numDimensions);
	}

	/**
	 * Visits a try catch block.
	 *
	 * @param start   the beginning of the exception handler's scope (inclusive).
	 * @param end     the end of the exception handler's scope (exclusive).
	 * @param handler the beginning of the exception handler's code.
	 * @param type    the internal name of the type of exceptions handled by the handler (see {@link
	 *                Type#getInternalName()}), or {@literal null} to catch any exceptions (for "finally"
	 *                blocks).
	 * @throws IllegalArgumentException if one of the labels has already been visited by this visitor
	 *                                  (by the {@link #visitLabel} method).
	 */
	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
		addCode(new CodeLine(-1, TRY_CATCH_BLOCK, new Object[]{start, end, handler, type}));
		super.visitTryCatchBlock(start, end, handler, type);
	}

	/**
	 * Visits a local variable declaration.
	 *
	 * @param name       the name of a local variable.
	 * @param descriptor the type descriptor of this local variable.
	 * @param signature  the type signature of this local variable. May be {@literal null} if the local
	 *                   variable type does not use generic types.
	 * @param start      the first instruction corresponding to the scope of this local variable
	 *                   (inclusive).
	 * @param end        the last instruction corresponding to the scope of this local variable (exclusive).
	 * @param index      the local variable's index.
	 * @throws IllegalArgumentException if one of the labels has not already been visited by this
	 *                                  visitor (by the {@link #visitLabel} method).
	 */
	@Override
	public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
		addCode(new CodeLine(-1, LOCAL, new Object[]{name, descriptor, signature, start, end, index}));
		char firstDescriptorChar = descriptor.charAt(0);
		maxLocal =
			Math.max(
				maxLocal, index + (firstDescriptorChar == 'J' || firstDescriptorChar == 'D' ? 2 : 1));
		super.visitLocalVariable(name, descriptor, signature, start, end, index);
	}

	/**
	 * Visits a line number declaration.
	 *
	 * @param line  a line number. This number refers to the source file from which the class was
	 *              compiled.
	 * @param start the first instruction corresponding to this line number.
	 * @throws IllegalArgumentException if {@code start} has not already been visited by this visitor
	 *                                  (by the {@link #visitLabel} method).
	 */
	@Override
	public void visitLineNumber(int line, Label start) {
		addCode(new CodeLine(-1, LINE, new Object[]{line, start}));
		super.visitLineNumber(line, start);
	}

	/**
	 * Visits the maximum stack size and the maximum number of local variables of the method.
	 *
	 * @param maxStack  maximum stack size of the method.
	 * @param maxLocals maximum number of local variables for the method.
	 */
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
	public CodeValue get(final int local) {
		maxLocal = Math.max(maxLocal, local + 1);
		return local < this.local.size() ? this.local.get(local) : new CodeValue(Opcodes.TOP);
	}

	private CodeValue set(final int local, final Object type) {
		maxLocal = Math.max(maxLocal, local + 1);
		while (local >= this.local.size()) {
			this.local.add(new CodeValue(Opcodes.TOP));
		}
		CodeValue val;
		if (type instanceof CodeValue) {
			val = new CodeValue(((CodeValue) type).getType()).copyFrom((CodeValue) type);
		} else {
			val = new CodeValue(type);
		}
		this.local.set(local, val);
		return val;
	}

	private CodeValue push(final Object type) {
		CodeValue val;
		if (type instanceof CodeValue) {
			val = new CodeValue(((CodeValue) type).getType()).copyFrom((CodeValue) type);
		} else {
			val = new CodeValue(type);
		}
		stack.add(val);
		maxStack = Math.max(maxStack, stack.size());
		return val;
	}

	private CodeValue pushDescriptor(final String fieldOrMethodDescriptor) {
		String descriptor =
			fieldOrMethodDescriptor.charAt(0) == '('
				? Type.getReturnType(fieldOrMethodDescriptor).getDescriptor()
				: fieldOrMethodDescriptor;
		switch (descriptor.charAt(0)) {
			case 'V':
				return null;
			case 'Z':
			case 'C':
			case 'B':
			case 'S':
			case 'I':
				return push(Opcodes.INTEGER);
			case 'F':
				return push(Opcodes.FLOAT);
			case 'J': {
				CodeValue val = push(Opcodes.LONG);
				push(Opcodes.TOP);
				return val;
			}
			case 'D': {
				CodeValue val = push(Opcodes.DOUBLE);
				push(Opcodes.TOP);
				return val;
			}
			case '[':
				return push(descriptor);
			case 'L':
				return push(descriptor.substring(1, descriptor.length() - 1));
			default:
				throw new AssertionError();
		}
	}

	public CodeValue peek() {
		int size = stack.size();
		if (size == 0) {
			return new CodeValue(Opcodes.TOP);
		}
		return stack.get(size - 1);
	}

	public CodeValue[] peek(final int numSlots) {
		int size = stack.size();
		int end = size - numSlots;
		CodeValue[] values = new CodeValue[numSlots];
		for (int i = size - 1; i >= end; --i) {
			CodeValue val = i < stack.size() ? stack.get(i) : new CodeValue(Opcodes.TOP);
			values[i - end] = val;
		}
		return values;
	}

	public CodeValue[] peek(final String descriptor) {
		char firstDescriptorChar = descriptor.charAt(0);
		if (firstDescriptorChar == '(') {
			// method descriptor
			int numSlots = 0;
			Type[] types = Type.getArgumentTypes(descriptor);
			for (Type type : types) {
				numSlots += type.getSize();
			}
			return peek(numSlots);
		} else if (firstDescriptorChar == 'J' || firstDescriptorChar == 'D') {
			// double or long
			return peek(2);
		} else {
			return peek(1);
		}
	}

	public CodeValue peek2() {
		int size = stack.size();
		if (size == 0) {
			return new CodeValue(Opcodes.TOP);
		}
		CodeValue val = stack.get(size - 1);
		if (val.isTop()) {
			if (size > 1) {
				return stack.get(size - 2);
			} else {
				return new CodeValue(Opcodes.TOP);
			}
		} else {
			return val;
		}
	}

	public CodeValue[] peek2(final int numSlots) {
		int size = stack.size();
		CodeValue[] values = new CodeValue[numSlots];
		int idx = size - 1;
		int count = 0;
		while (idx >= 0 && count < numSlots) {
			CodeValue val = idx < stack.size() ? stack.get(idx) : new CodeValue(Opcodes.TOP);
			if (val.isTop()) {
				idx -= 1;
				if (idx < 0) {
					val = new CodeValue(Opcodes.TOP);
				} else {
					val = idx < stack.size() ? stack.get(idx) : new CodeValue(Opcodes.TOP);
				}
			}
			values[numSlots - 1 - count] = val;
			count++;
			idx -= 1;
		}
		return values;
	}

	public CodeValue[] peek2(final String descriptor) {
		char firstDescriptorChar = descriptor.charAt(0);
		if (firstDescriptorChar == '(') {
			// method descriptor
			int numSlots = 0;
			Type[] types = Type.getArgumentTypes(descriptor);
			for (Type type : types) {
				numSlots += type.getSize();
			}
			return peek2(numSlots);
		} else if (firstDescriptorChar == 'J' || firstDescriptorChar == 'D') {
			// double or long
			return peek2(2);
		} else {
			return peek2(1);
		}
	}

	private CodeValue pop() {
		return stack.remove(stack.size() - 1);
	}


	private CodeValue[] pop(final int numSlots) {
		int size = stack.size();
		int end = size - numSlots;
		CodeValue[] values = new CodeValue[numSlots];
		for (int i = size - 1; i >= end; --i) {
			CodeValue val = stack.remove(i);
			values[i - end] = val;
		}
		return values;
	}

	private CodeValue[] pop(final String descriptor) {
		char firstDescriptorChar = descriptor.charAt(0);
		if (firstDescriptorChar == '(') {
			// method descriptor
			int numSlots = 0;
			Type[] types = Type.getArgumentTypes(descriptor);
			for (Type type : types) {
				numSlots += type.getSize();
			}
			return pop(numSlots);
		} else if (firstDescriptorChar == 'J' || firstDescriptorChar == 'D') {
			// double or long
			return pop(2);
		} else {
			return pop(1);
		}
	}

	private void execute(final int opcode, final int intArg, final String stringArg) {
		if (opcode == Opcodes.JSR || opcode == Opcodes.RET) {
			throw new IllegalArgumentException("JSR/RET are not supported");
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
				push(Opcodes.INTEGER).withConstant(-1);
				break;
			case Opcodes.ICONST_0: // int型常量值0进栈
				push(Opcodes.INTEGER).withConstant(0);
				break;
			case Opcodes.ICONST_1: // int型常量值1进栈
				push(Opcodes.INTEGER).withConstant(1);
				break;
			case Opcodes.ICONST_2: // int型常量值2进栈
				push(Opcodes.INTEGER).withConstant(2);
				break;
			case Opcodes.ICONST_3: // int型常量值3进栈
				push(Opcodes.INTEGER).withConstant(3);
				break;
			case Opcodes.ICONST_4: // int型常量值4进栈
				push(Opcodes.INTEGER).withConstant(4);
				break;
			case Opcodes.ICONST_5: // int型常量值5进栈
				push(Opcodes.INTEGER).withConstant(5);
				break;
			case Opcodes.BIPUSH: // 将一个byte型常量值(-128~127)推送至栈顶
				push(Opcodes.INTEGER).withConstant((byte) intArg);
				break;
			case Opcodes.SIPUSH: // 将一个short型常量值(-32768~32767)推送至栈顶
				push(Opcodes.INTEGER).withConstant((short) intArg);
				break;
			case Opcodes.LCONST_0: // long型常量值0进栈
				push(Opcodes.LONG).withConstant(0L);
				push(Opcodes.TOP);
				break;
			case Opcodes.LCONST_1: // long型常量值1进栈
				push(Opcodes.LONG).withConstant(1L);
				push(Opcodes.TOP);
				break;
			case Opcodes.FCONST_0: // float型常量值0进栈
				push(Opcodes.FLOAT).withConstant(0F);
				break;
			case Opcodes.FCONST_1: // float型常量值1进栈
				push(Opcodes.FLOAT).withConstant(1F);
				break;
			case Opcodes.FCONST_2: // float型常量值2进栈
				push(Opcodes.FLOAT).withConstant(2F);
				break;
			case Opcodes.DCONST_0: // double型常量值0进栈
				push(Opcodes.DOUBLE).withConstant(0D);
				push(Opcodes.TOP);
				break;
			case Opcodes.DCONST_1: // double型常量值1进栈
				push(Opcodes.DOUBLE).withConstant(1D);
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
				push(lastLabel);
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


		lastLabel = null;// 移除标签记录
	}

	// endregion 更新变量槽与栈槽

}
