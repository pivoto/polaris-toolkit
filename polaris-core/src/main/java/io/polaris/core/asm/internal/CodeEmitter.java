package io.polaris.core.asm.internal;

import java.util.Arrays;

import io.polaris.core.asm.consts.AsmCmpConsts;
import io.polaris.core.asm.consts.AsmMathConsts;
import io.polaris.core.err.BytecodeOperationException;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * 代码发射器，继承自 LocalVariablesSorter
 * <p>
 * 提供高级 API 用于生成 JVM 字节码指令，封装了 ASM 的底层操作
 *
 * @author Qt
 * @since May 10, 2024
 */
public class CodeEmitter extends LocalVariablesSorter {
	/** 布尔值转换方法签名：booleanValue() */
	private static final Signature BOOLEAN_VALUE =
		AsmTypes.parseSignature("boolean booleanValue()");
	/** 字符值转换方法签名：charValue() */
	private static final Signature CHAR_VALUE =
		AsmTypes.parseSignature("char charValue()");
	/** 长整型转换方法签名：longValue() */
	private static final Signature LONG_VALUE =
		AsmTypes.parseSignature("long longValue()");
	/** 双精度浮点转换方法签名：doubleValue() */
	private static final Signature DOUBLE_VALUE =
		AsmTypes.parseSignature("double doubleValue()");
	/** 浮点转换方法签名：floatValue() */
	private static final Signature FLOAT_VALUE =
		AsmTypes.parseSignature("float floatValue()");
	/** 整型转换方法签名：intValue() */
	private static final Signature INT_VALUE =
		AsmTypes.parseSignature("int intValue()");
	/** 无参构造方法签名 */
	private static final Signature CSTRUCT_NULL =
		AsmTypes.parseConstructor("");
	/** String 参数构造方法签名 */
	private static final Signature CSTRUCT_STRING =
		AsmTypes.parseConstructor("String");

	/** 代码发射器引用 */
	private final ClassEmitter ce;
	/** 方法描述信息 */
	private final MethodDescriptor methodDescriptor;
	/** 方法访问标志 */
	private final int access;
	/** 方法签名 */
	private final Signature signature;
	/** 参数类型数组 */
	private final Type[] argumentTypes;
	/** 局部变量偏移量 */
	private final int localOffset;

	CodeEmitter(ClassEmitter ce, MethodVisitor mv, int access, Signature sig, Type[] exceptionTypes) {
		super(AsmConsts.ASM_API, access, sig.getDescriptor(), mv);
		this.ce = ce;
		this.methodDescriptor = new AsmMethodDescriptor(ce.getClassDescriptor(), access, sig, exceptionTypes);
		this.access = access;
		this.signature = sig;
		this.argumentTypes = sig.getArgumentTypes();
		this.localOffset = AsmTypes.isStatic(access) ? 0 : 1;
	}

	public boolean isStaticHook() {
		return false;
	}

	public Signature getSignature() {
		return this.signature;
	}

	public Type getReturnType() {
		return this.signature.getReturnType();
	}

	public MethodDescriptor getMethodDescriptor() {
		return this.methodDescriptor;
	}

	public ClassEmitter getClassEmitter() {
		return ce;
	}

	/**
	 * 结束当前方法的生成
	 */
	public void end_method() {
		visitMaxs(0, 0);
	}

	/**
	 * 开始一个新的代码块
	 *
	 * @return Block 对象，表示新创建的代码块
	 */
	public Block begin_block() {
		return new Block(this);
	}

	/**
	 * 捕获代码块中的异常
	 *
	 * @param block 要捕获异常的代码块
	 * @param exception 异常类型
	 */
	public void catch_exception(Block block, Type exception) {
		if (block.getEnd() == null) {
			throw new IllegalStateException("end of block is unset");
		}
		// 访问 try-catch 块，设置异常处理器
		mv.visitTryCatchBlock(block.getStart(),
			block.getEnd(),
			mark(),
			exception.getInternalName());
	}

	/**
	 * 无条件跳转到指定标签
	 *
	 * @param label 目标标签
	 */
	public void goTo(Label label) {
		mv.visitJumpInsn(AsmConsts.GOTO, label);
	}

	/**
	 * 如果栈顶对象为 null 则跳转到指定标签
	 *
	 * @param label 目标标签
	 */
	public void ifnull(Label label) {
		mv.visitJumpInsn(AsmConsts.IFNULL, label);
	}

	/**
	 * 如果栈顶对象不为 null 则跳转到指定标签
	 *
	 * @param label 目标标签
	 */
	public void ifnonnull(Label label) {
		mv.visitJumpInsn(AsmConsts.IFNONNULL, label);
	}

	public void if_jump(int opcode, Label label) {
		mv.visitJumpInsn(opcode, label);
	}

	public void if_icmp(int mode, Label label) {
		if_cmp(Type.INT_TYPE, mode, label);
	}

	public void if_icmp_eq(Label label) {
		if_cmp(Type.INT_TYPE, AsmCmpConsts.EQ, label);
	}

	public void if_icmp_ne(Label label) {
		if_cmp(Type.INT_TYPE, AsmCmpConsts.NE, label);
	}

	public void if_icmp_gt(Label label) {
		if_cmp(Type.INT_TYPE, AsmCmpConsts.GT, label);
	}

	public void if_icmp_ge(Label label) {
		if_cmp(Type.INT_TYPE, AsmCmpConsts.GE, label);
	}

	public void if_icmp_lt(Label label) {
		if_cmp(Type.INT_TYPE, AsmCmpConsts.LT, label);
	}

	public void if_icmp_le(Type type, Label label) {
		if_cmp(type, AsmCmpConsts.LE, label);
	}

	public void if_cmp_eq(Type type, Label label) {
		if_cmp(type, AsmCmpConsts.EQ, label);
	}

	public void if_cmp_ne(Type type, Label label) {
		if_cmp(type, AsmCmpConsts.NE, label);
	}

	public void if_cmp_gt(Type type, Label label) {
		if_cmp(type, AsmCmpConsts.GT, label);
	}

	public void if_cmp_ge(Type type, Label label) {
		if_cmp(type, AsmCmpConsts.GE, label);
	}

	public void if_cmp_lt(Type type, Label label) {
		if_cmp(type, AsmCmpConsts.LT, label);
	}

	public void if_cmp_le(Type type, Label label) {
		if_cmp(type, AsmCmpConsts.LE, label);
	}

	public void if_cmp(Type type, int mode, Label label) {
		switch (type.getSort()) {
			case Type.LONG:
				mv.visitInsn(AsmConsts.LCMP);
				break;
			case Type.DOUBLE:
				mv.visitInsn(AsmConsts.DCMPG);
				break;
			case Type.FLOAT:
				mv.visitInsn(AsmConsts.FCMPG);
				break;
			case Type.ARRAY:
			case Type.OBJECT:
				switch (mode) {
					case AsmCmpConsts.EQ:
						mv.visitJumpInsn(AsmConsts.IF_ACMPEQ, label);
						return;
					case AsmCmpConsts.NE:
						mv.visitJumpInsn(AsmConsts.IF_ACMPNE, label);
						return;
				}
				throw new IllegalArgumentException("Bad comparison for type " + type);
			default:
				int intOp = -1;
				switch (mode) {
					case AsmCmpConsts.EQ:
						intOp = AsmConsts.IF_ICMPEQ;
						break;
					case AsmCmpConsts.NE:
						intOp = AsmConsts.IF_ICMPNE;
						break;
					case AsmCmpConsts.GE:
						intOp = AsmConsts.IF_ICMPGE;
						break;
					case AsmCmpConsts.LT:
						intOp = AsmConsts.IF_ICMPLT;
						break;
					case AsmCmpConsts.LE:
						intOp = AsmConsts.IF_ICMPLE;
						break;
					case AsmCmpConsts.GT:
						intOp = AsmConsts.IF_ICMPGT;
						break;
				}
				mv.visitJumpInsn(intOp, label);
				return;
		}
		if_jump(mode, label);
	}
	/*
	public void if_cmp(Type type, int mode, Label label) {
		int intOp = -1;
		int jumpmode = mode;
		switch (mode) {
			case GE:
				jumpmode = LT;
				break;
			case LE:
				jumpmode = GT;
				break;
		}
		switch (type.getSort()) {
			case Type.LONG:
				mv.visitInsn(AsmConsts.LCMP);
				break;
			case Type.DOUBLE:
				mv.visitInsn(AsmConsts.DCMPG);
				break;
			case Type.FLOAT:
				mv.visitInsn(AsmConsts.FCMPG);
				break;
			case Type.ARRAY:
			case Type.OBJECT:
				switch (mode) {
					case EQ:
						mv.visitJumpInsn(AsmConsts.IF_ACMPEQ, label);
						return;
					case NE:
						mv.visitJumpInsn(AsmConsts.IF_ACMPNE, label);
						return;
				}
				throw new IllegalArgumentException("Bad comparison for type " + type);
			default:
				switch (mode) {
					case EQ:
						intOp = AsmConsts.IF_ICMPEQ;
						break;
					case NE:
						intOp = AsmConsts.IF_ICMPNE;
						break;
					case GE:
						swap(); // fall through
					case LT:
						intOp = AsmConsts.IF_ICMPLT;
						break;
					case LE:
						swap(); // fall through
					case GT:
						intOp = AsmConsts.IF_ICMPGT;
						break;
				}
				mv.visitJumpInsn(intOp, label);
				return;
		}
		if_jump(jumpmode, label);
	}
	*/

	public void pop() {
		mv.visitInsn(AsmConsts.POP);
	}

	public void pop2() {
		mv.visitInsn(AsmConsts.POP2);
	}

	public void dup() {
		mv.visitInsn(AsmConsts.DUP);
	}

	public void dup2() {
		mv.visitInsn(AsmConsts.DUP2);
	}

	public void dup_x1() {
		mv.visitInsn(AsmConsts.DUP_X1);
	}

	public void dup_x2() {
		mv.visitInsn(AsmConsts.DUP_X2);
	}

	public void dup2_x1() {
		mv.visitInsn(AsmConsts.DUP2_X1);
	}

	public void dup2_x2() {
		mv.visitInsn(AsmConsts.DUP2_X2);
	}

	public void swap() {
		mv.visitInsn(AsmConsts.SWAP);
	}

	public void aconst_null() {
		mv.visitInsn(AsmConsts.ACONST_NULL);
	}

	public void swap(Type prev, Type type) {
		if (type.getSize() == 1) {
			if (prev.getSize() == 1) {
				swap(); // same as dup_x1(), pop();
			} else {
				dup_x2();
				pop();
			}
		} else {
			if (prev.getSize() == 1) {
				dup2_x1();
				pop2();
			} else {
				dup2_x2();
				pop2();
			}
		}
	}

	public void monitorenter() {
		mv.visitInsn(AsmConsts.MONITORENTER);
	}

	public void monitorexit() {
		mv.visitInsn(AsmConsts.MONITOREXIT);
	}

	/**
	 * 执行数学或位运算指令
	 *
	 * @param op   操作码，如 ADD, SUB, MUL, DIV, REM, NEG, AND, OR, XOR, USHR 等
	 * @param type 操作数的类型
	 */
	public void math(int op, Type type) {
		mv.visitInsn(type.getOpcode(op));
	}

	/**
	 * 加法运算
	 *
	 * @param type 操作数类型
	 */
	public void math_add(Type type) {
		math(AsmMathConsts.ADD, type);
	}

	/**
	 * 乘法运算
	 *
	 * @param type 操作数类型
	 */
	public void math_mul(Type type) {
		math(AsmMathConsts.MUL, type);
	}

	/**
	 * 异或运算
	 *
	 * @param type 操作数类型
	 */
	public void math_xor(Type type) {
		math(AsmMathConsts.XOR, type);
	}

	/**
	 * 无符号右移运算
	 *
	 * @param type 操作数类型
	 */
	public void math_ushr(Type type) {
		math(AsmMathConsts.USHR, type);
	}

	/**
	 * 减法运算
	 *
	 * @param type 操作数类型
	 */
	public void math_sub(Type type) {
		math(AsmMathConsts.SUB, type);
	}

	/**
	 * 除法运算
	 *
	 * @param type 操作数类型
	 */
	public void math_div(Type type) {
		math(AsmMathConsts.DIV, type);
	}

	/**
	 * 取反运算（负号）
	 *
	 * @param type 操作数类型
	 */
	public void math_neg(Type type) {
		math(AsmMathConsts.NEG, type);
	}

	/**
	 * 取余运算
	 *
	 * @param type 操作数类型
	 */
	public void math_rem(Type type) {
		math(AsmMathConsts.REM, type);
	}

	/**
	 * 按位与运算
	 *
	 * @param type 操作数类型
	 */
	public void math_and(Type type) {
		math(AsmMathConsts.AND, type);
	}

	/**
	 * 按位或运算
	 *
	 * @param type 操作数类型
	 */
	public void math_or(Type type) {
		math(AsmMathConsts.OR, type);
	}


	public void array_load(Type type) {
		mv.visitInsn(type.getOpcode(AsmConsts.IALOAD));
	}

	public void array_store(Type type) {
		mv.visitInsn(type.getOpcode(AsmConsts.IASTORE));
	}

	/**
	 * Casts from one primitive numeric type to another
	 */
	public void cast_numeric(Type from, Type to) {
		if (from != to) {
			if (from == Type.DOUBLE_TYPE) {
				if (to == Type.FLOAT_TYPE) {
					mv.visitInsn(AsmConsts.D2F);
				} else if (to == Type.LONG_TYPE) {
					mv.visitInsn(AsmConsts.D2L);
				} else {
					mv.visitInsn(AsmConsts.D2I);
					cast_numeric(Type.INT_TYPE, to);
				}
			} else if (from == Type.FLOAT_TYPE) {
				if (to == Type.DOUBLE_TYPE) {
					mv.visitInsn(AsmConsts.F2D);
				} else if (to == Type.LONG_TYPE) {
					mv.visitInsn(AsmConsts.F2L);
				} else {
					mv.visitInsn(AsmConsts.F2I);
					cast_numeric(Type.INT_TYPE, to);
				}
			} else if (from == Type.LONG_TYPE) {
				if (to == Type.DOUBLE_TYPE) {
					mv.visitInsn(AsmConsts.L2D);
				} else if (to == Type.FLOAT_TYPE) {
					mv.visitInsn(AsmConsts.L2F);
				} else {
					mv.visitInsn(AsmConsts.L2I);
					cast_numeric(Type.INT_TYPE, to);
				}
			} else {
				if (to == Type.BYTE_TYPE) {
					mv.visitInsn(AsmConsts.I2B);
				} else if (to == Type.CHAR_TYPE) {
					mv.visitInsn(AsmConsts.I2C);
				} else if (to == Type.DOUBLE_TYPE) {
					mv.visitInsn(AsmConsts.I2D);
				} else if (to == Type.FLOAT_TYPE) {
					mv.visitInsn(AsmConsts.I2F);
				} else if (to == Type.LONG_TYPE) {
					mv.visitInsn(AsmConsts.I2L);
				} else if (to == Type.SHORT_TYPE) {
					mv.visitInsn(AsmConsts.I2S);
				}
			}
		}
	}

	@SuppressWarnings("UnnecessaryBoxing")
	public void push(int i) {
		if (i < -1) {
			mv.visitLdcInsn(Integer.valueOf(i));
		} else if (i <= 5) {
			mv.visitInsn(AsmTypes.ICONST(i));
		} else if (i <= Byte.MAX_VALUE) {
			mv.visitIntInsn(AsmConsts.BIPUSH, i);
		} else if (i <= Short.MAX_VALUE) {
			mv.visitIntInsn(AsmConsts.SIPUSH, i);
		} else {
			mv.visitLdcInsn(Integer.valueOf(i));
		}
	}

	@SuppressWarnings("UnnecessaryBoxing")
	public void push(long value) {
		if (value == 0L || value == 1L) {
			mv.visitInsn(AsmTypes.LCONST(value));
		} else {
			mv.visitLdcInsn(Long.valueOf(value));
		}
	}

	@SuppressWarnings("UnnecessaryBoxing")
	public void push(float value) {
		if (value == 0f || value == 1f || value == 2f) {
			mv.visitInsn(AsmTypes.FCONST(value));
		} else {
			mv.visitLdcInsn(Float.valueOf(value));
		}
	}

	@SuppressWarnings("UnnecessaryBoxing")
	public void push(double value) {
		if (value == 0d || value == 1d) {
			mv.visitInsn(AsmTypes.DCONST(value));
		} else {
			mv.visitLdcInsn(Double.valueOf(value));
		}
	}

	public void push(String value) {
		mv.visitLdcInsn(value);
	}

	public void push(Type value) {
		mv.visitLdcInsn(value);
	}

	public void newarray() {
		newarray(AsmConsts.TYPE_OBJECT);
	}

	public void newarray(Type type) {
		if (AsmTypes.isPrimitive(type)) {
			mv.visitIntInsn(AsmConsts.NEWARRAY, AsmTypes.NEWARRAY(type));
		} else {
			emit_type(AsmConsts.ANEWARRAY, type);
		}
	}

	public void arraylength() {
		mv.visitInsn(AsmConsts.ARRAYLENGTH);
	}

	public void load_this() {
		if (AsmTypes.isStatic(this.access)) {
			throw new IllegalStateException("no 'this' pointer within static method");
		}
		mv.visitVarInsn(AsmConsts.ALOAD, 0);
	}

	/**
	 * Pushes all of the arguments of the current method onto the stack.
	 */
	public void load_args() {
		load_args(0, this.argumentTypes.length);
	}

	/**
	 * Pushes the specified argument of the current method onto the stack.
	 *
	 * @param index the zero-based index into the argument list
	 */
	public void load_arg(int index) {
		load_local(this.argumentTypes[index],
			this.localOffset + skipArgs(index));
	}

	// zero-based (see load_this)
	public void load_args(int fromArg, int count) {
		int pos = this.localOffset + skipArgs(fromArg);
		for (int i = 0; i < count; i++) {
			Type t = this.argumentTypes[fromArg + i];
			load_local(t, pos);
			pos += t.getSize();
		}
	}

	private int skipArgs(int numArgs) {
		int amount = 0;
		for (int i = 0; i < numArgs; i++) {
			amount += this.argumentTypes[i].getSize();
		}
		return amount;
	}

	private void load_local(Type t, int pos) {
		// TODO: make t == null ok?
		mv.visitVarInsn(t.getOpcode(AsmConsts.ILOAD), pos);
	}

	private void store_local(Type t, int pos) {
		// TODO: make t == null ok?
		mv.visitVarInsn(t.getOpcode(AsmConsts.ISTORE), pos);
	}

	public void iinc(Local local, int amount) {
		mv.visitIincInsn(local.getIndex(), amount);
	}

	public void store_local(Local local) {
		store_local(local.getType(), local.getIndex());
	}

	public void load_local(Local local) {
		load_local(local.getType(), local.getIndex());
	}

	public void return_value() {
		mv.visitInsn(this.signature.getReturnType().getOpcode(AsmConsts.IRETURN));
	}

	public void getfield(String name) {
		ClassEmitter.FieldInfo info = ce.getFieldInfo(name);
		int opcode = AsmTypes.isStatic(info.access) ? AsmConsts.GETSTATIC : AsmConsts.GETFIELD;
		emit_field(opcode, ce.getClassType(), name, info.type);
	}

	public void putfield(String name) {
		ClassEmitter.FieldInfo info = ce.getFieldInfo(name);
		int opcode = AsmTypes.isStatic(info.access) ? AsmConsts.PUTSTATIC : AsmConsts.PUTFIELD;
		emit_field(opcode, ce.getClassType(), name, info.type);
	}

	public void super_getfield(String name, Type type) {
		emit_field(AsmConsts.GETFIELD, ce.getSuperType(), name, type);
	}

	public void super_putfield(String name, Type type) {
		emit_field(AsmConsts.PUTFIELD, ce.getSuperType(), name, type);
	}

	public void super_getstatic(String name, Type type) {
		emit_field(AsmConsts.GETSTATIC, ce.getSuperType(), name, type);
	}

	public void super_putstatic(String name, Type type) {
		emit_field(AsmConsts.PUTSTATIC, ce.getSuperType(), name, type);
	}

	public void getfield(Type owner, String name, Type type) {
		emit_field(AsmConsts.GETFIELD, owner, name, type);
	}

	public void putfield(Type owner, String name, Type type) {
		emit_field(AsmConsts.PUTFIELD, owner, name, type);
	}

	public void getstatic(Type owner, String name, Type type) {
		emit_field(AsmConsts.GETSTATIC, owner, name, type);
	}

	public void putstatic(Type owner, String name, Type type) {
		emit_field(AsmConsts.PUTSTATIC, owner, name, type);
	}

	// package-protected for Emitters, try to fix
	void emit_field(int opcode, Type ctype, String name, Type ftype) {
		mv.visitFieldInsn(opcode,
			ctype.getInternalName(),
			name,
			ftype.getDescriptor());
	}

	public void super_invoke() {
		super_invoke(this.signature);
	}

	public void super_invoke(Signature sig) {
		emit_invoke(AsmConsts.INVOKESPECIAL, ce.getSuperType(), sig, false);
	}

	public void invoke_constructor(Type type) {
		invoke_constructor(type, CSTRUCT_NULL);
	}

	public void super_invoke_constructor() {
		invoke_constructor(ce.getSuperType());
	}

	public void invoke_constructor_this() {
		invoke_constructor(ce.getClassType());
	}


	public void invoke_lambda(Type funcType, Signature funcSig, Type owner, Signature implSig) {
		mv.visitInvokeDynamicInsn(funcSig.getName(), "()" + funcType.getDescriptor(),
			new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory",
				"metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false),
			new Object[]{
				Type.getType(funcSig.getDescriptor()),
				new Handle(Opcodes.H_INVOKESTATIC, owner.getInternalName(), implSig.getName(), implSig.getDescriptor(), false),
				Type.getType(implSig.getDescriptor())
			});
	}


	private void emit_invoke(int opcode, Type type, Signature sig, boolean isInterface) {
		if (sig.getName().equals(AsmConsts.CONSTRUCTOR_NAME) &&
			((opcode == AsmConsts.INVOKEVIRTUAL) ||
				(opcode == AsmConsts.INVOKESTATIC))) {
			// TODO: error
		}
		mv.visitMethodInsn(opcode,
			type.getInternalName(),
			sig.getName(),
			sig.getDescriptor(),
			isInterface);
	}

	public void invoke_interface(Type owner, Signature sig) {
		emit_invoke(AsmConsts.INVOKEINTERFACE, owner, sig, true);
	}

	public void super_invoke_interface(Type owner, Signature sig) {
		emit_invoke(AsmConsts.INVOKESPECIAL, owner, sig, true);
	}

	public void invoke_virtual(Type owner, Signature sig) {
		emit_invoke(AsmConsts.INVOKEVIRTUAL, owner, sig, false);
	}

	@Deprecated
	public void invoke_static(Type owner, Signature sig) {
		invoke_static(owner, sig, false);
	}

	public void invoke_static(Type owner, Signature sig, boolean isInterface) {
		emit_invoke(AsmConsts.INVOKESTATIC, owner, sig, isInterface);
	}

	public void invoke_virtual_this(Signature sig) {
		invoke_virtual(ce.getClassType(), sig);
	}

	public void invoke_static_this(Signature sig) {
		Type owner = ce.getClassType();
		invoke_static(owner, sig, false);
	}

	public void invoke_constructor(Type type, Signature sig) {
		emit_invoke(AsmConsts.INVOKESPECIAL, type, sig, false);
	}

	public void invoke_constructor_this(Signature sig) {
		invoke_constructor(ce.getClassType(), sig);
	}

	public void super_invoke_constructor(Signature sig) {
		invoke_constructor(ce.getSuperType(), sig);
	}

	public void new_instance_this() {
		new_instance(ce.getClassType());
	}

	public void new_instance(Type type) {
		emit_type(AsmConsts.NEW, type);
	}

	public void new_instance_constructor_this() {
		new_instance_constructor(ce.getClassType(), this.signature);
	}

	public void new_instance_constructor(Type type, Signature sig) {
		emit_type(AsmConsts.NEW, type);
		dup();
		invoke_constructor(type, sig);
	}

	private void emit_type(int opcode, Type type) {
		String desc;
		if (AsmTypes.isArray(type)) {
			desc = type.getDescriptor();
		} else {
			desc = type.getInternalName();
		}
		mv.visitTypeInsn(opcode, desc);
	}

	public void aaload(int index) {
		push(index);
		aaload();
	}

	public void aaload() {
		mv.visitInsn(AsmConsts.AALOAD);
	}

	public void aastore() {
		mv.visitInsn(AsmConsts.AASTORE);
	}

	public void athrow() {
		mv.visitInsn(AsmConsts.ATHROW);
	}

	public Label make_label() {
		return new Label();
	}

	public Local make_local() {
		return make_local(AsmConsts.TYPE_OBJECT);
	}

	public Local make_local(Type type) {
		int index = newLocal(type);
		return new Local(index, type);
	}

	public void checkcast_this() {
		checkcast(ce.getClassType());
	}

	public void checkcast(Type type) {
		if (!type.equals(AsmConsts.TYPE_OBJECT)) {
			emit_type(AsmConsts.CHECKCAST, type);
		}
	}

	public void instance_of(Type type) {
		emit_type(AsmConsts.INSTANCEOF, type);
	}

	public void instance_of_this() {
		instance_of(ce.getClassType());
	}

	public void process_switch(int[] keys, ProcessSwitchCallback callback) {
		float density;
		if (keys.length == 0) {
			density = 0;
		} else {
			density = (float) keys.length / (keys[keys.length - 1] - keys[0] + 1);
		}
		process_switch(keys, callback, density >= 0.5f);
	}

	public void process_switch(int[] keys, ProcessSwitchCallback callback, boolean useTable) {
		if (!isSorted(keys))
			throw new IllegalArgumentException("keys to switch must be sorted ascending");
		Label def = make_label();
		Label end = make_label();

		try {
			if (keys.length > 0) {
				int len = keys.length;
				int min = keys[0];
				int max = keys[len - 1];
				int range = max - min + 1;

				if (useTable) {
					Label[] labels = new Label[range];
					Arrays.fill(labels, def);
					for (int i = 0; i < len; i++) {
						labels[keys[i] - min] = make_label();
					}
					mv.visitTableSwitchInsn(min, max, def, labels);
					for (int i = 0; i < range; i++) {
						Label label = labels[i];
						if (label != def) {
							mark(label);
							callback.processCase(i + min, end);
						}
					}
				} else {
					Label[] labels = new Label[len];
					for (int i = 0; i < len; i++) {
						labels[i] = make_label();
					}
					mv.visitLookupSwitchInsn(def, keys, labels);
					for (int i = 0; i < len; i++) {
						mark(labels[i]);
						callback.processCase(keys[i], end);
					}
				}
			}

			mark(def);
			callback.processDefault();
			mark(end);

		} catch (RuntimeException | Error e) {
			throw e;
		} catch (Exception e) {
			throw new BytecodeOperationException(e);
		}
	}

	private static boolean isSorted(int[] keys) {
		for (int i = 1; i < keys.length; i++) {
			if (keys[i] < keys[i - 1])
				return false;
		}
		return true;
	}

	public void mark(Label label) {
		mv.visitLabel(label);
	}

	Label mark() {
		Label label = make_label();
		mv.visitLabel(label);
		return label;
	}

	public void push(boolean value) {
		push(value ? 1 : 0);
	}

	/**
	 * Toggles the integer on the top of the stack from 1 to 0 or vice versa
	 */
	public void not() {
		push(1);
		math(AsmMathConsts.XOR, Type.INT_TYPE);
	}

	public void throw_exception(Type type, String msg) {
		new_instance(type);
		dup();
		push(msg);
		invoke_constructor(type, CSTRUCT_STRING);
		athrow();
	}

	/**
	 * If the argument is a primitive class, replaces the primitive value
	 * on the top of the stack with the wrapped (Object) equivalent. For
	 * example, char -> Character.
	 * If the class is Void, a null is pushed onto the stack instead.
	 *
	 * @param type the class indicating the current type of the top stack value
	 */
	public void box(Type type) {
		if (AsmTypes.isPrimitive(type)) {
			if (type == Type.VOID_TYPE) {
				aconst_null();
			} else {
				Type boxed = AsmTypes.getBoxedType(type);
				new_instance(boxed);
				if (type.getSize() == 2) {
					// Pp -> Ppo -> oPpo -> ooPpo -> ooPp -> o
					dup_x2();
					dup_x2();
					pop();
				} else {
					// p -> po -> opo -> oop -> o
					dup_x1();
					swap();
				}
				invoke_constructor(boxed, new Signature(AsmConsts.CONSTRUCTOR_NAME, Type.VOID_TYPE, new Type[]{type}));
			}
		}
	}

	/**
	 * If the argument is a primitive class, replaces the object
	 * on the top of the stack with the unwrapped (primitive)
	 * equivalent. For example, Character -> char.
	 *
	 * @param type the class indicating the desired type of the top stack value
	 */
	public void unbox(Type type) {
		Type t = AsmConsts.TYPE_NUMBER;
		Signature sig = null;
		switch (type.getSort()) {
			case Type.VOID:
				return;
			case Type.CHAR:
				t = AsmConsts.TYPE_CHARACTER;
				sig = CHAR_VALUE;
				break;
			case Type.BOOLEAN:
				t = AsmConsts.TYPE_BOOLEAN;
				sig = BOOLEAN_VALUE;
				break;
			case Type.DOUBLE:
				sig = DOUBLE_VALUE;
				break;
			case Type.FLOAT:
				sig = FLOAT_VALUE;
				break;
			case Type.LONG:
				sig = LONG_VALUE;
				break;
			case Type.INT:
			case Type.SHORT:
			case Type.BYTE:
				sig = INT_VALUE;
		}

		if (sig == null) {
			checkcast(type);
		} else {
			checkcast(t);
			invoke_virtual(t, sig);
		}
	}

	/**
	 * Allocates and fills an Object[] array with the arguments to the
	 * current method. Primitive values are inserted as their boxed
	 * (Object) equivalents.
	 */
	public void create_arg_array() {
		/*
		generates:
		Object[] args = new Object[]{ arg1, new Integer(arg2) };
		*/
		push(this.argumentTypes.length);
		newarray();
		for (int i = 0; i < this.argumentTypes.length; i++) {
			dup();
			push(i);
			load_arg(i);
			box(this.argumentTypes[i]);
			aastore();
		}
	}


	/**
	 * Pushes a zero onto the stack if the argument is a primitive class, or a null otherwise.
	 */
	public void zero_or_null(Type type) {
		if (AsmTypes.isPrimitive(type)) {
			switch (type.getSort()) {
				case Type.DOUBLE:
					push(0d);
					break;
				case Type.LONG:
					push(0L);
					break;
				case Type.FLOAT:
					push(0f);
					break;
				case Type.VOID:
					aconst_null();
					break;
				default:
					push(0);
			}
		} else {
			aconst_null();
		}
	}

	/**
	 * Unboxes the object on the top of the stack. If the object is null, the
	 * unboxed primitive value becomes zero.
	 */
	public void unbox_or_zero(Type type) {
		if (AsmTypes.isPrimitive(type)) {
			if (type != Type.VOID_TYPE) {
				Label nonNull = make_label();
				Label end = make_label();
				dup();
				ifnonnull(nonNull);
				pop();
				zero_or_null(type);
				goTo(end);
				mark(nonNull);
				unbox(type);
				mark(end);
			}
		} else {
			checkcast(type);
		}
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		if (!AsmTypes.isAbstract(this.access)) {
			mv.visitMaxs(0, 0);
		}
	}

	public void invoke(MethodDescriptor method, Type virtualType) {
		ClassDescriptor classDescriptor = method.getClassDescriptor();
		Type type = classDescriptor.getType();
		Signature sig = method.getSignature();
		if (sig.getName().equals(AsmConsts.CONSTRUCTOR_NAME)) {
			invoke_constructor(type, sig);
		} else if (AsmTypes.isStatic(method.getModifiers())) {
			invoke_static(type, sig, AsmTypes.isInterface(classDescriptor.getModifiers()));
		} else if (AsmTypes.isInterface(classDescriptor.getModifiers())) {
			invoke_interface(type, sig);
		} else {
			invoke_virtual(virtualType, sig);
		}
	}

	public void invoke(MethodDescriptor method) {
		invoke(method, method.getClassDescriptor().getType());
	}
}
