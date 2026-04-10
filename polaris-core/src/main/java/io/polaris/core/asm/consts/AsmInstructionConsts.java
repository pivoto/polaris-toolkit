package io.polaris.core.asm.consts;

import org.objectweb.asm.Opcodes;

/**
 * @author Qt
 * @see Opcodes
 * @since Apr 10, 2026
 */
public interface AsmInstructionConsts {

	// The JVM opcode values (with the MethodVisitor method name used to visit them in comment, and
	// where '-' means 'same method name as on the previous line').
	// See https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html.

	/** 无操作 */
	int NOP = 0; // visitInsn
	/** null进栈。注意：JVM并没有为null指派一个具体的值 */
	int ACONST_NULL = 1; // -
	/** int型常量值-1进栈 */
	int ICONST_M1 = 2; // -
	/** int型常量值0进栈 */
	int ICONST_0 = 3; // -
	/** int型常量值1进栈 */
	int ICONST_1 = 4; // -
	/** int型常量值2进栈 */
	int ICONST_2 = 5; // -
	/** int型常量值3进栈 */
	int ICONST_3 = 6; // -
	/** int型常量值4进栈 */
	int ICONST_4 = 7; // -
	/** int型常量值5进栈 */
	int ICONST_5 = 8; // -
	/** long型常量值0进栈 */
	int LCONST_0 = 9; // -
	/** long型常量值1进栈 */
	int LCONST_1 = 10; // -
	/** float型常量值0进栈 */
	int FCONST_0 = 11; // -
	/** float型常量值1进栈 */
	int FCONST_1 = 12; // -
	/** float型常量值2进栈 */
	int FCONST_2 = 13; // -
	/** double型常量值0进栈 */
	int DCONST_0 = 14; // -
	/** double型常量值1进栈 */
	int DCONST_1 = 15; // -
	/** 将一个byte型常量值(-128~127)推送至栈顶 */
	int BIPUSH = 16; // visitIntInsn
	/** 将一个short型常量值(-32768~32767)推送至栈顶 */
	int SIPUSH = 17; // -
	/** 将int、float或String型常量值从常量池中推送至栈顶 */
	int LDC = 18; // visitLdcInsn
	/** 指定的int型局部变量进栈 */
	int ILOAD = 21; // visitVarInsn
	/** 指定的long型局部变量进栈 */
	int LLOAD = 22; // -
	/** 指定的float型局部变量进栈 */
	int FLOAD = 23; // -
	/** 指定的double型局部变量进栈 */
	int DLOAD = 24; // -
	/** 将指定的引用类型本地变量推送至栈顶 */
	int ALOAD = 25; // -
	/** 指定的int型数组的指定下标处的值进栈 */
	int IALOAD = 46; // visitInsn
	/** 指定的long型数组的指定下标处的值进栈 */
	int LALOAD = 47; // -
	/** 指定的float型数组的指定下标处的值进栈 */
	int FALOAD = 48; // -
	/** 指定的double型数组的指定下标处的值进栈 */
	int DALOAD = 49; // -
	/** 将引用型数组指定索引的值推送至栈顶 */
	int AALOAD = 50; // -
	/** 指定的boolean或byte型数组的指定下标处的值进栈 */
	int BALOAD = 51; // -
	/** 指定的char型数组的指定下标处的值进栈 */
	int CALOAD = 52; // -
	/** 指定的short型数组的指定下标处的值进栈 */
	int SALOAD = 53; // -
	/** 将栈顶int型数值存入指定的局部变量 */
	int ISTORE = 54; // visitVarInsn
	/** 将栈顶long型数值存入指定的局部变量 */
	int LSTORE = 55; // -
	/** 将栈顶float型数值存入指定的局部变量 */
	int FSTORE = 56; // -
	/** 将栈顶double型数值存入指定的局部变量 */
	int DSTORE = 57; // -
	/** 将栈顶数值（objectref）存入当前frame的局部变量数组中指定下标（index）处的变量中，栈顶数值出栈。 */
	int ASTORE = 58; // -
	/** 将栈顶int型数值存入指定数组的指定下标处 */
	int IASTORE = 79; // visitInsn
	/** 将栈顶long型数值存入指定数组的指定下标处 */
	int LASTORE = 80; // -
	/** 将栈顶float型数值存入指定数组的指定下标处 */
	int FASTORE = 81; // -
	/** 将栈顶double型数值存入指定数组的指定下标处 */
	int DASTORE = 82; // -
	/** 将栈顶引用型数值存入指定数组的指定下标处 */
	int AASTORE = 83; // -
	/** 将栈顶boolean或byte型数值存入指定数组的指定下标处 */
	int BASTORE = 84; // -
	/** 将栈顶char型数值存入指定数组的指定下标处 */
	int CASTORE = 85; // -
	/** 将栈顶short型数值存入指定数组的指定下标处 */
	int SASTORE = 86; // -
	/** 栈顶数值出栈 (该栈顶数值不能是long或double型) */
	int POP = 87; // -
	/** 栈顶的一个（如果是long、double型的)或两个（其它类型的）数值出栈 */
	int POP2 = 88; // -
	/** 复制栈顶数值，并且复制值进栈 */
	int DUP = 89; // -
	/** 复制栈顶数值，并且复制值进栈2次 */
	int DUP_X1 = 90; // -
	/** 复制栈顶数值，并且复制值进栈2次或3次 */
	int DUP_X2 = 91; // -
	/** 复制栈顶一个（long、double型的)或两个（其它类型的）数值，并且复制值进栈 */
	int DUP2 = 92; // -
	/** 复制栈顶2个数值，并压入栈顶2次 */
	int DUP2_X1 = 93; // -
	/** 复制栈顶2个数值，并压入栈顶3次 */
	int DUP2_X2 = 94; // -
	/** 栈顶的两个数值互换(要求栈顶的两个数值不能是long或double型的) */
	int SWAP = 95; // -
	/** 栈顶两int型数值相加，并且结果进栈 */
	int IADD = 96; // -
	/** 栈顶两个long型数值相加，并且结果进栈 */
	int LADD = 97; // -
	/** 栈顶两个float型数值相加，并且结果进栈 */
	int FADD = 98; // -
	/** 栈顶两个double型数值相加，并且结果进栈 */
	int DADD = 99; // -
	/** 栈顶两个int型数值相减，并且结果进栈 */
	int ISUB = 100; // -
	/** 栈顶两个long型数值相减，并且结果进栈 */
	int LSUB = 101; // -
	/** 栈顶两个float型数值相减，并且结果进栈 */
	int FSUB = 102; // -
	/** 栈顶两个double型数值相减，并且结果进栈 */
	int DSUB = 103; // -
	/** 栈顶两个long型数值相乘，并且结果进栈 */
	int IMUL = 104; // -
	/** 栈顶两个long型数值相乘，并且结果进栈 */
	int LMUL = 105; // -
	/** 栈顶两个float型数值相乘，并且结果进栈 */
	int FMUL = 106; // -
	/** 栈顶两个double型数值相乘，并且结果进栈 */
	int DMUL = 107; // -
	/** 栈顶两个int型数值相除，并且结果进栈 */
	int IDIV = 108; // -
	/** 栈顶两个long型数值相除，并且结果进栈 */
	int LDIV = 109; // -
	/** 栈顶两个float型数值相除，并且结果进栈 */
	int FDIV = 110; // -
	/** 栈顶两个double型数值相除，并且结果进栈 */
	int DDIV = 111; // -
	/** 栈顶两个int型数值取余，并且结果进栈 */
	int IREM = 112; // -
	/** 栈顶两个long型数值取余，并且结果进栈 */
	int LREM = 113; // -
	/** 栈顶两个float型数值取余，并且结果进栈 */
	int FREM = 114; // -
	/** 栈顶两个double型数值取余，并且结果进栈 */
	int DREM = 115; // -
	/** 栈顶的int型数值取负数，并且结果进栈 */
	int INEG = 116; // -
	/** 栈顶的long型数值取负数，并且结果进栈 */
	int LNEG = 117; // -
	/** 栈顶float型数值取负数，并且结果进栈 */
	int FNEG = 118; // -
	/** 栈顶的double型数值取负数，并且结果进栈 */
	int DNEG = 119; // -
	/** 栈顶的int型数值左移指定位数，并且结果进栈 */
	int ISHL = 120; // -
	/** 栈顶的long型数值左移指定位数，并且结果进栈 */
	int LSHL = 121; // -
	/** 栈顶的int型数值右移指定位数，并且结果进栈 */
	int ISHR = 122; // -
	/** 栈顶的long型数值右移指定位数，并且结果进栈 */
	int LSHR = 123; // -
	/** 栈顶的int型数值无符号右移指定位数，并且结果进栈 */
	int IUSHR = 124; // -
	/** 栈顶的long型数值无符号右移指定位数，并且结果进栈 */
	int LUSHR = 125; // -
	/** 栈顶两int型数值按位与，并且结果进栈 */
	int IAND = 126; // -
	/** 栈顶两long型数值按位与，并且结果进栈 */
	int LAND = 127; // -
	/** 栈顶两int型数值按位或，并且结果进栈 */
	int IOR = 128; // -
	/** 栈顶两long型数值按位或，并且结果进栈 */
	int LOR = 129; // -
	/** 栈顶两int型数值按位异或，并且结果进栈 */
	int IXOR = 130; // -
	/** 栈顶两long型数值按位异或，并且结果进栈 */
	int LXOR = 131; // -
	/** 栈顶的int型变量增加指定值，并且结果进栈 */
	int IINC = 132; // visitIincInsn
	/** 栈顶int值强转long值，并且结果进栈 */
	int I2L = 133; // visitInsn
	/** 栈顶int值强转float值，并且结果进栈 */
	int I2F = 134; // -
	/** 栈顶int值强转double值，并且结果进栈 */
	int I2D = 135; // -
	/** 栈顶long值强转int值，并且结果进栈 */
	int L2I = 136; // -
	/** 栈顶long值强转float值，并且结果进栈 */
	int L2F = 137; // -
	/** 栈顶long值强转double值，并且结果进栈 */
	int L2D = 138; // -
	/** 栈顶float值强转int值，并且结果进栈 */
	int F2I = 139; // -
	/** 栈顶float值强转long值，并且结果进栈 */
	int F2L = 140; // -
	/** 栈顶float值强转double值，并且结果进栈 */
	int F2D = 141; // -
	/** 栈顶double值强转int值，并且结果进栈 */
	int D2I = 142; // -
	/** 栈顶double值强转long值，并且结果进栈 */
	int D2L = 143; // -
	/** 栈顶double值强转float值，并且结果进栈 */
	int D2F = 144; // -
	/** 栈顶int值强转byte值，并且结果进栈 */
	int I2B = 145; // -
	/** 栈顶int值强转char值，并且结果进栈 */
	int I2C = 146; // -
	/** 栈顶int值强转short值，并且结果进栈 */
	int I2S = 147; // -
	/** 比较栈顶两long型数值大小，并且结果（1，0，-1）进栈 */
	int LCMP = 148; // -
	/** 比较栈顶两float型数值大小，并且结果（1，0，-1）进栈；当其中一个数值为NaN时， -1进栈 */
	int FCMPL = 149; // -
	/** 比较栈顶两float型数值大小，并且结果（1，0，-1）进栈；当其中一个数值为NaN时，1进栈 */
	int FCMPG = 150; // -
	/** 比较栈顶两double型数值大小，并且结果（1，0，-1）进栈；当其中一个数值为NaN时，-1进栈 */
	int DCMPL = 151; // -
	/** 比较栈顶两double型数值大小，并且结果（1，0，-1）进栈；当其中一个数值为NaN时，1进栈 */
	int DCMPG = 152; // -
	/** 当栈顶int型数值等于0时跳转 */
	int IFEQ = 153; // visitJumpInsn
	/** 当栈顶int型数值不等于0时跳转 */
	int IFNE = 154; // -
	/** 当栈顶int型数值小于0时跳转 */
	int IFLT = 155; // -
	/** 当栈顶int型数值大于等于0时跳转 */
	int IFGE = 156; // -
	/** 当栈顶int型数值大于0时跳转 */
	int IFGT = 157; // -
	/** 当栈顶int型数值小于等于0时跳转 */
	int IFLE = 158; // -
	/** 比较栈顶两int型数值大小，当结果等于0时跳转 */
	int IF_ICMPEQ = 159; // -
	/** 比较栈顶两int型数值大小，当结果不等于0时跳转 */
	int IF_ICMPNE = 160; // -
	/** 比较栈顶两int型数值大小，当结果小于0时跳转 */
	int IF_ICMPLT = 161; // -
	/** 比较栈顶两int型数值大小，当结果大于等于0时跳转 */
	int IF_ICMPGE = 162; // -
	/** 比较栈顶两int型数值大小，当结果大于0时跳转 */
	int IF_ICMPGT = 163; // -
	/** 比较栈顶两int型数值大小，当结果小于等于0时跳转 */
	int IF_ICMPLE = 164; // -
	/** 比较栈顶两引用型数值，当结果相等时跳转 */
	int IF_ACMPEQ = 165; // -
	/** 比较栈顶两引用型数值，当结果不相等时跳转 */
	int IF_ACMPNE = 166; // -
	/** 无条件跳转 */
	int GOTO = 167; // -
	/** 跳转至指定16位offset位置，并将jsr下一条指令地址压入栈顶 */
	int JSR = 168; // -
	/** 返回至局部变量指定的index的指令位置（通常与jsr、jsr_w联合使用） */
	int RET = 169; // visitVarInsn
	/** 用于switch条件跳转，case值连续（可变长度指令） */
	int TABLESWITCH = 170; // visiTableSwitchInsn
	/** 用于switch条件跳转，case值不连续（可变长度指令） */
	int LOOKUPSWITCH = 171; // visitLookupSwitch
	/** 当前方法返回int */
	int IRETURN = 172; // visitInsn
	/** 当前方法返回long */
	int LRETURN = 173; // -
	/** 当前方法返回float */
	int FRETURN = 174; // -
	/** 当前方法返回double */
	int DRETURN = 175; // -
	/** 当前方法返回引用 */
	int ARETURN = 176; // -
	/** 当前方法返回void */
	int RETURN = 177; // -
	/** 获取指定类的静态域，并将其值压入栈顶 */
	int GETSTATIC = 178; // visitFieldInsn
	/** 为指定的类的静态域赋值 */
	int PUTSTATIC = 179; // -
	/** 获取指定类的实例域，并将其值压入栈顶 */
	int GETFIELD = 180; // -
	/** 为指定的类的实例域赋值 */
	int PUTFIELD = 181; // -
	/** 调用实例方法 */
	int INVOKEVIRTUAL = 182; // visitMethodInsn
	/** 调用超类构造方法、实例初始化方法、私有方法 */
	int INVOKESPECIAL = 183; // -
	/** 调用静态方法 */
	int INVOKESTATIC = 184; // -
	/** 调用接口方法 */
	int INVOKEINTERFACE = 185; // -
	/** 调用动态方法。 用于在运行时动态解析出调用点限定符所引用的方法。并执行该方法。前面四条调用指令的分派逻辑都固化在Java虚拟机内部，用户无法改变，而invokedynamic指令的分派逻辑是由用户所设定的引导方法决定的。 */
	int INVOKEDYNAMIC = 186; // visitInvokeDynamicInsn
	/** 创建一个对象，并且其引用进栈 */
	int NEW = 187; // visitTypeInsn
	/** 创建一个基本类型数组，并且其引用进栈 */
	int NEWARRAY = 188; // visitIntInsn
	/** 创建一个引用型（如类，接口，数组）的数组，并且其引用进栈 */
	int ANEWARRAY = 189; // visitTypeInsn
	/** 获得数组的长度值并压入栈顶 */
	int ARRAYLENGTH = 190; // visitInsn
	/** 将栈顶的异常抛出 */
	int ATHROW = 191; // -
	/** 类型转换检查，如果该检查未通过将会抛出ClassCastException异常 */
	int CHECKCAST = 192; // visitTypeInsn
	/** 检查对象是否是指定的类的实例。如果是，1进栈；否则，0进栈 */
	int INSTANCEOF = 193; // -
	/** 获得对象锁，用于同步方法或同步块 */
	int MONITORENTER = 194; // visitInsn
	/** 释放对象锁，用于同步方法或同步块 */
	int MONITOREXIT = 195; // -
	/** 创建指定类型和维度的多维数组（执行该指令时，栈中必须包含各维度的长度值），并且其引用值进栈 */
	int MULTIANEWARRAY = 197; // visitMultiANewArrayInsn
	/** 为null时跳转 */
	int IFNULL = 198; // visitJumpInsn
	/** 不为null时跳转 */
	int IFNONNULL = 199; // -

}
