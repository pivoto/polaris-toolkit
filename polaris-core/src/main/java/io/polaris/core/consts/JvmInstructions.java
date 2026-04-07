package io.polaris.core.consts;

/**
 * JVM指令集
 * <p>
 * JVM指令主要分为：
 * <ul>
 * <li> 本地变量表到操作数栈类指令</li>
 * <li> 操作数栈到本地变量表类指令</li>
 * <li> 常数到操作数栈类指令</li>
 * <li> 将数组指定索引的数组推送至操作数栈类指令</li>
 * <li> 将操作数栈数存储到数组指定索引类指令</li>
 * <li> 操作数栈其他相关类指令</li>
 * <li> 运算相关类指令</li>
 * <li> 条件转移类指令</li>
 * <li> 类和数组类指令</li>
 * <li> 其他指令。</li>
 * </ul>
 * <ul>
 * <li> i 开头的指令操作数类型是integer类型</li>
 * <li> l 开头的指令操作数类型是long类型</li>
 * <li> f 开头的指令操作数类型是float类型</li>
 * <li> d 开头的指令操作数类型是double</li>
 * <li> a 开头的指令操作数类型是引用类型（reference）</li>
 * <li> load 类指令将数据从本地变量表加载到操作数栈</li>
 * <li> store类指令将数据从操作数栈存储到本地变量表中</li>
 * <li> 其他的指令主要用于操作数栈。</li>
 * </ul>
 * <p>
 * https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html
 *
 * @author Qt
 * @since Apr 07, 2026
 */
public interface JvmInstructions {

	/** 无操作 */
	int NOP = 0x00; // 0 visitInsn

	// region 操作数栈（operand stack）相关

	// region 常数（constant）到操作数栈（operand stack）

	/** null进栈。注意：JVM并没有为null指派一个具体的值 */
	int ACONST_NULL = 0x01; // 1 -
	/** int型常量值-1进栈 */
	int ICONST_M1 = 0x02; // 2 -
	/** int型常量值0进栈 */
	int ICONST_0 = 0x03; // 3 -
	/** int型常量值1进栈 */
	int ICONST_1 = 0x04; // 4 -
	/** int型常量值2进栈 */
	int ICONST_2 = 0x05; // 5 -
	/** int型常量值3进栈 */
	int ICONST_3 = 0x06; // 6 -
	/** int型常量值4进栈 */
	int ICONST_4 = 0x07; // 7 -
	/** int型常量值5进栈 */
	int ICONST_5 = 0x08; // 8 -
	/** long型常量值0进栈 */
	int LCONST_0 = 0x09; // 9 -
	/** long型常量值1进栈 */
	int LCONST_1 = 0x0A; // 10 -
	/** float型常量值0进栈 */
	int FCONST_0 = 0x0B; // 11 -
	/** float型常量值1进栈 */
	int FCONST_1 = 0x0C; // 12 -
	/** float型常量值2进栈 */
	int FCONST_2 = 0x0D; // 13 -
	/** double型常量值0进栈 */
	int DCONST_0 = 0x0E; // 14 -
	/** double型常量值1进栈 */
	int DCONST_1 = 0x0F; // 15 -
	/** 将一个byte型常量值(-128~127)推送至栈顶 */
	int BIPUSH = 0x10; // 16 visitIntInsn
	/** 将一个short型常量值(-32768~32767)推送至栈顶 */
	int SIPUSH = 0x11; // 17 -
	/** 将int、float或String型常量值从常量池中推送至栈顶 */
	int LDC = 0x12; // 18 visitLdcInsn
	/** 将int、float或String型常量值从常量池中推送至栈顶（宽索引） */
	int LDC_W = 0x13; // 19
	/** 将long或double型常量值从常量池中推送至栈顶（宽索引） */
	int LDC2_W = 0x14; // 20

	// endregion

	// region 本地变量表（local variable table）到操作数栈（operand stack）

	/** 指定的int型局部变量进栈 */
	int ILOAD = 0x15; // 21 visitVarInsn
	/** 指定的long型局部变量进栈 */
	int LLOAD = 0x16; // 22 -
	/** 指定的float型局部变量进栈 */
	int FLOAD = 0x17; // 23 -
	/** 指定的double型局部变量进栈 */
	int DLOAD = 0x18; // 24 -
	/** 将指定的引用类型本地变量推送至栈顶 */
	int ALOAD = 0x19; // 25 -

	/** 第一个int型局部变量进栈 */
	int ILOAD_0 = 0x1A; // 26
	/** 第二个int型局部变量进栈 */
	int ILOAD_1 = 0x1B; // 27
	/** 第三个int型局部变量进栈 */
	int ILOAD_2 = 0x1C; // 28
	/** 第四个int型局部变量进栈 */
	int ILOAD_3 = 0x1D; // 29
	/** 第一个long型局部变量进栈 */
	int LLOAD_0 = 0x1E; // 30
	/** 第二个long型局部变量进栈 */
	int LLOAD_1 = 0x1F; // 31
	/** 第三个long型局部变量进栈 */
	int LLOAD_2 = 0x20; // 32
	/** 第四个long型局部变量进栈 */
	int LLOAD_3 = 0x21; // 33
	/** 第一个float型局部变量进栈 */
	int FLOAD_0 = 0x22; // 34
	/** 第二个float型局部变量进栈 */
	int FLOAD_1 = 0x23; // 35
	/** 第三个float型局部变量进栈 */
	int FLOAD_2 = 0x24; // 36
	/** 第四个float型局部变量进栈 */
	int FLOAD_3 = 0x25; // 37
	/** 第一个double型局部变量进栈 */
	int DLOAD_0 = 0x26; // 38
	/** 第二个double型局部变量进栈 */
	int DLOAD_1 = 0x27; // 39
	/** 第三个double型局部变量进栈 */
	int DLOAD_2 = 0x28; // 40
	/** 第四个double型局部变量进栈 */
	int DLOAD_3 = 0x29; // 41
	/** 将第0个引用类型本地变量推送至栈顶 */
	int ALOAD_0 = 0x2A; // 42
	/** 将第1个引用类型本地变量推送至栈顶 */
	int ALOAD_1 = 0x2B; // 43
	/** 将第2个引用类型本地变量推送至栈顶 */
	int ALOAD_2 = 0x2C; // 44
	/** 将第3个引用类型本地变量推送至栈顶 */
	int ALOAD_3 = 0x2D; // 45

	// endregion

	// region 将数组指定索引的数组推送至操作数栈（operand stack）
	// 栈顶的数组下标（index）、数组引用（arrayref）出栈，并根据这两个数值取出对应的数组元素值（value）进栈。

	/** 指定的int型数组的指定下标处的值进栈 */
	int IALOAD = 0x2E; // 46 visitInsn
	/** 指定的long型数组的指定下标处的值进栈 */
	int LALOAD = 0x2F; // 47 -
	/** 指定的float型数组的指定下标处的值进栈 */
	int FALOAD = 0x30; // 48 -
	/** 指定的double型数组的指定下标处的值进栈 */
	int DALOAD = 0x31; // 49 -
	/** 将引用型数组指定索引的值推送至栈顶 */
	int AALOAD = 0x32; // 50 -
	/** 指定的boolean或byte型数组的指定下标处的值进栈 */
	int BALOAD = 0x33; // 51 -
	/** 指定的char型数组的指定下标处的值进栈 */
	int CALOAD = 0x34; // 52 -
	/** 指定的short型数组的指定下标处的值进栈 */
	int SALOAD = 0x35; // 53 -

	// endregion

	// region 操作数栈（operand stack）到本地变量表（local variable table）

	/** 将栈顶int型数值存入指定的局部变量 */
	int ISTORE = 0x36; // 54 visitVarInsn
	/** 将栈顶long型数值存入指定的局部变量 */
	int LSTORE = 0x37; // 55 -
	/** 将栈顶float型数值存入指定的局部变量 */
	int FSTORE = 0x38; // 56 -
	/** 将栈顶double型数值存入指定的局部变量 */
	int DSTORE = 0x39; // 57 -
	/** 将栈顶数值（objectref）存入当前frame的局部变量数组中指定下标（index）处的变量中，栈顶数值出栈。 */
	int ASTORE = 0x3A; // 58 -

	/** 将栈顶int型数值存入第一个局部变量 */
	int ISTORE_0 = 0x3B;
	/** 将栈顶int型数值存入第二个局部变量 */
	int ISTORE_1 = 0x3C;
	/** 将栈顶int型数值存入第三个局部变量 */
	int ISTORE_2 = 0x3D;
	/** 将栈顶int型数值存入第四个局部变量 */
	int ISTORE_3 = 0x3E;

	/** 将栈顶long型数值存入第一个局部变量 */
	int LSTORE_0 = 0x3F;
	/** 将栈顶long型数值存入第二个局部变量 */
	int LSTORE_1 = 0x40;
	/** 将栈顶long型数值存入第三个局部变量 */
	int LSTORE_2 = 0x41;
	/** 将栈顶long型数值存入第四个局部变量 */
	int LSTORE_3 = 0x42;

	/** 将栈顶float型数值存入第一个局部变量 */
	int FSTORE_0 = 0x43;
	/** 将栈顶float型数值存入第二个局部变量 */
	int FSTORE_1 = 0x44;
	/** 将栈顶float型数值存入第三个局部变量 */
	int FSTORE_2 = 0x45;
	/** 将栈顶float型数值存入第四个局部变量 */
	int FSTORE_3 = 0x46;

	/** 将栈顶double型数值存入第一个局部变量 */
	int DSTORE_0 = 0x47;
	/** 将栈顶double型数值存入第二个局部变量 */
	int DSTORE_1 = 0x48;
	/** 将栈顶double型数值存入第三个局部变量 */
	int DSTORE_2 = 0x49;
	/** 将栈顶double型数值存入第四个局部变量 */
	int DSTORE_3 = 0x4A;
	/** 将栈顶引用型数值存入第一个局部变量 */
	int ASTORE_0 = 0x4B;
	/** 将栈顶引用型数值存入第二个局部变量 */
	int ASTORE_1 = 0x4C;
	/** 将栈顶引用型数值存入第三个局部变量 */
	int ASTORE_2 = 0x4D;
	/** 将栈顶引用型数值存入第四个局部变量 */
	int ASTORE_3 = 0x4E;

	// endregion

	// region 将操作数栈数存储到数组指定索引
	// 根据栈顶的引用型数值（value）、数组下标（index）、数组引用（arrayref）出栈，将数值存入对应的数组元素中。

	/** 将栈顶int型数值存入指定数组的指定下标处 */
	int IASTORE = 0x4F; // 79 visitInsn
	/** 将栈顶long型数值存入指定数组的指定下标处 */
	int LASTORE = 0x50; // 80 -
	/** 将栈顶float型数值存入指定数组的指定下标处 */
	int FASTORE = 0x51; // 81 -
	/** 将栈顶double型数值存入指定数组的指定下标处 */
	int DASTORE = 0x52; // 82 -
	/** 将栈顶引用型数值存入指定数组的指定下标处 */
	int AASTORE = 0x53; // 83 -
	/** 将栈顶boolean或byte型数值存入指定数组的指定下标处 */
	int BASTORE = 0x54; // 84 -
	/** 将栈顶char型数值存入指定数组的指定下标处 */
	int CASTORE = 0x55; // 85 -
	/** 将栈顶short型数值存入指定数组的指定下标处 */
	int SASTORE = 0x56; // 86 -

	// endregion

	// region 操作数栈其他相关：弹出、复制、交换

	/** 栈顶数值出栈 (该栈顶数值不能是long或double型) */
	int POP = 0x57; // 87 -
	/** 栈顶的一个（如果是long、double型的)或两个（其它类型的）数值出栈 */
	int POP2 = 0x58; // 88 -
	/** 复制栈顶数值，并且复制值进栈 */
	int DUP = 0x59; // 89 -
	/** 复制栈顶数值，并且复制值进栈2次 */
	int DUP_X1 = 0x5A; // 90 -
	/** 复制栈顶数值，并且复制值进栈2次或3次 */
	int DUP_X2 = 0x5B; // 91 -
	/** 复制栈顶一个（long、double型的)或两个（其它类型的）数值，并且复制值进栈 */
	int DUP2 = 0x5C; // 92 -
	/** 复制栈顶2个数值，并压入栈顶2次 */
	int DUP2_X1 = 0x5D; // 93 -
	/** 复制栈顶2个数值，并压入栈顶3次 */
	int DUP2_X2 = 0x5E; // 94 -
	/** 栈顶的两个数值互换(要求栈顶的两个数值不能是long或double型的) */
	int SWAP = 0x5F; // 95 -

	// endregion

	// endregion 操作数栈（operand stack）相关

	// region 运算相关

	// region 数学运算

	/** 栈顶两int型数值相加，并且结果进栈 */
	int IADD = 0x60; // 96 -
	/** 栈顶两个long型数值相加，并且结果进栈 */
	int LADD = 0x61; // 97 -
	/** 栈顶两个float型数值相加，并且结果进栈 */
	int FADD = 0x62; // 98 -
	/** 栈顶两个double型数值相加，并且结果进栈 */
	int DADD = 0x63; // 99 -
	/** 栈顶两个int型数值相减，并且结果进栈 */
	int ISUB = 0x64; // 100 -
	/** 栈顶两个long型数值相减，并且结果进栈 */
	int LSUB = 0x65; // 101 -
	/** 栈顶两个float型数值相减，并且结果进栈 */
	int FSUB = 0x66; // 102 -
	/** 栈顶两个double型数值相减，并且结果进栈 */
	int DSUB = 0x67; // 103 -
	/** 栈顶两个long型数值相乘，并且结果进栈 */
	int IMUL = 0x68; // 104 -
	/** 栈顶两个long型数值相乘，并且结果进栈 */
	int LMUL = 0x69; // 105 -
	/** 栈顶两个float型数值相乘，并且结果进栈 */
	int FMUL = 0x6A; // 106 -
	/** 栈顶两个double型数值相乘，并且结果进栈 */
	int DMUL = 0x6B; // 107 -
	/** 栈顶两个int型数值相除，并且结果进栈 */
	int IDIV = 0x6C; // 108 -
	/** 栈顶两个long型数值相除，并且结果进栈 */
	int LDIV = 0x6D; // 109 -
	/** 栈顶两个float型数值相除，并且结果进栈 */
	int FDIV = 0x6E; // 110 -
	/** 栈顶两个double型数值相除，并且结果进栈 */
	int DDIV = 0x6F; // 111 -
	/** 栈顶两个int型数值取余，并且结果进栈 */
	int IREM = 0x70; // 112 -
	/** 栈顶两个long型数值取余，并且结果进栈 */
	int LREM = 0x71; // 113 -
	/** 栈顶两个float型数值取余，并且结果进栈 */
	int FREM = 0x72; // 114 -
	/** 栈顶两个double型数值取余，并且结果进栈 */
	int DREM = 0x73; // 115 -
	/** 栈顶的int型数值取负数，并且结果进栈 */
	int INEG = 0x74; // 116 -
	/** 栈顶的long型数值取负数，并且结果进栈 */
	int LNEG = 0x75; // 117 -
	/** 栈顶float型数值取负数，并且结果进栈 */
	int FNEG = 0x76; // 118 -
	/** 栈顶的double型数值取负数，并且结果进栈 */
	int DNEG = 0x77; // 119 -

	// endregion

	// region 位运算
	/** 栈顶的int型数值左移指定位数，并且结果进栈 */
	int ISHL = 0x78; // 120 -
	/** 栈顶的long型数值左移指定位数，并且结果进栈 */
	int LSHL = 0x79; // 121 -
	/** 栈顶的int型数值右移指定位数，并且结果进栈 */
	int ISHR = 0x7A; // 122 -
	/** 栈顶的long型数值右移指定位数，并且结果进栈 */
	int LSHR = 0x7B; // 123 -
	/** 栈顶的int型数值无符号右移指定位数，并且结果进栈 */
	int IUSHR = 0x7C; // 124 -
	/** 栈顶的long型数值无符号右移指定位数，并且结果进栈 */
	int LUSHR = 0x7D; // 125 -
	/** 栈顶两int型数值按位与，并且结果进栈 */
	int IAND = 0x7E; // 126 -
	/** 栈顶两long型数值按位与，并且结果进栈 */
	int LAND = 0x7F; // 127 -
	/** 栈顶两int型数值按位或，并且结果进栈 */
	int IOR = 0x80; // 128 -
	/** 栈顶两long型数值按位或，并且结果进栈 */
	int LOR = 0x81; // 129 -
	/** 栈顶两int型数值按位异或，并且结果进栈 */
	int IXOR = 0x82; // 130 -
	/** 栈顶两long型数值按位异或，并且结果进栈 */
	int LXOR = 0x83; // 131 -

	// endregion

	// region 自增
	/** 栈顶的int型变量增加指定值，并且结果进栈 */
	int IINC = 0x84; // 132 visitIincInsn
	// endregion

	// region 类型转换

	/** 栈顶int值强转long值，并且结果进栈 */
	int I2L = 0x85; // 133 visitInsn
	/** 栈顶int值强转float值，并且结果进栈 */
	int I2F = 0x86; // 134 -
	/** 栈顶int值强转double值，并且结果进栈 */
	int I2D = 0x87; // 135 -
	/** 栈顶long值强转int值，并且结果进栈 */
	int L2I = 0x88; // 136 -
	/** 栈顶long值强转float值，并且结果进栈 */
	int L2F = 0x89; // 137 -
	/** 栈顶long值强转double值，并且结果进栈 */
	int L2D = 0x8A; // 138 -
	/** 栈顶float值强转int值，并且结果进栈 */
	int F2I = 0x8B; // 139 -
	/** 栈顶float值强转long值，并且结果进栈 */
	int F2L = 0x8C; // 140 -
	/** 栈顶float值强转double值，并且结果进栈 */
	int F2D = 0x8D; // 141 -
	/** 栈顶double值强转int值，并且结果进栈 */
	int D2I = 0x8E; // 142 -
	/** 栈顶double值强转long值，并且结果进栈 */
	int D2L = 0x8F; // 143 -
	/** 栈顶double值强转float值，并且结果进栈 */
	int D2F = 0x90; // 144 -
	/** 栈顶int值强转byte值，并且结果进栈 */
	int I2B = 0x91; // 145 -
	/** 栈顶int值强转char值，并且结果进栈 */
	int I2C = 0x92; // 146 -
	/** 栈顶int值强转short值，并且结果进栈 */
	int I2S = 0x93; // 147 -

	// endregion

	// endregion 运算相关

	// region 条件转移
	// region 有条件转移
	/** 比较栈顶两long型数值大小，并且结果（1，0，-1）进栈 */
	int LCMP = 0x94; // 148 -
	/** 比较栈顶两float型数值大小，并且结果（1，0，-1）进栈；当其中一个数值为NaN时， -1进栈 */
	int FCMPL = 0x95; // 149 -
	/** 比较栈顶两float型数值大小，并且结果（1，0，-1）进栈；当其中一个数值为NaN时，1进栈 */
	int FCMPG = 0x96; // 150 -
	/** 比较栈顶两double型数值大小，并且结果（1，0，-1）进栈；当其中一个数值为NaN时，-1进栈 */
	int DCMPL = 0x97; // 151 -
	/** 比较栈顶两double型数值大小，并且结果（1，0，-1）进栈；当其中一个数值为NaN时，1进栈 */
	int DCMPG = 0x98; // 152 -
	/** 当栈顶int型数值等于0时跳转 */
	int IFEQ = 0x99; // 153 visitJumpInsn
	/** 当栈顶int型数值不等于0时跳转 */
	int IFNE = 0x9A; // 154 -
	/** 当栈顶int型数值小于0时跳转 */
	int IFLT = 0x9B; // 155 -
	/** 当栈顶int型数值大于等于0时跳转 */
	int IFGE = 0x9C; // 156 -
	/** 当栈顶int型数值大于0时跳转 */
	int IFGT = 0x9D; // 157 -
	/** 当栈顶int型数值小于等于0时跳转 */
	int IFLE = 0x9E; // 158 -
	/** 比较栈顶两int型数值大小，当结果等于0时跳转 */
	int IF_ICMPEQ = 0x9F; // 159 -
	/** 比较栈顶两int型数值大小，当结果不等于0时跳转 */
	int IF_ICMPNE = 0xA0; // 160 -
	/** 比较栈顶两int型数值大小，当结果小于0时跳转 */
	int IF_ICMPLT = 0xA1; // 161 -
	/** 比较栈顶两int型数值大小，当结果大于等于0时跳转 */
	int IF_ICMPGE = 0xA2; // 162 -
	/** 比较栈顶两int型数值大小，当结果大于0时跳转 */
	int IF_ICMPGT = 0xA3; // 163 -
	/** 比较栈顶两int型数值大小，当结果小于等于0时跳转 */
	int IF_ICMPLE = 0xA4; // 164 -
	/** 比较栈顶两引用型数值，当结果相等时跳转 */
	int IF_ACMPEQ = 0xA5; // 165 -
	/** 比较栈顶两引用型数值，当结果不相等时跳转 */
	int IF_ACMPNE = 0xA6; // 166 -
	// endregion

	// region 无条件转移
	/** 无条件跳转 */
	int GOTO = 0xA7; // 167 -
	/** 跳转至指定16位offset位置，并将jsr下一条指令地址压入栈顶 */
	int JSR = 0xA8; // 168 -
	/** 返回至局部变量指定的index的指令位置（通常与jsr、jsr_w联合使用） */
	int RET = 0xA9; // 169 visitVarInsn
	// endregion

	// region 复合条件转移
	/** 用于switch条件跳转，case值连续（可变长度指令） */
	int TABLESWITCH = 0xAA; // 170 visiTableSwitchInsn
	/** 用于switch条件跳转，case值不连续（可变长度指令） */
	int LOOKUPSWITCH = 0xAB; // 171 visitLookupSwitch
	// endregion

	// endregion 条件转移

	// region 返回
	/** 当前方法返回int */
	int IRETURN = 0xAC; // 172 visitInsn
	/** 当前方法返回long */
	int LRETURN = 0xAD; // 173 -
	/** 当前方法返回float */
	int FRETURN = 0xAE; // 174 -
	/** 当前方法返回double */
	int DRETURN = 0xAF; // 175 -
	/** 当前方法返回引用 */
	int ARETURN = 0xB0; // 176 -
	/** 当前方法返回void */
	int RETURN = 0xB1; // 177 -
	// endregion

	// region 字段与方法访问
	/** 获取指定类的静态域，并将其值压入栈顶 */
	int GETSTATIC = 0xB2; // 178 visitFieldInsn
	/** 为指定的类的静态域赋值 */
	int PUTSTATIC = 0xB3; // 179 -
	/** 获取指定类的实例域，并将其值压入栈顶 */
	int GETFIELD = 0xB4; // 180 -
	/** 为指定的类的实例域赋值 */
	int PUTFIELD = 0xB5; // 181 -
	/** 调用实例方法 */
	int INVOKEVIRTUAL = 0xB6; // 182 visitMethodInsn
	/** 调用超类构造方法、实例初始化方法、私有方法 */
	int INVOKESPECIAL = 0xB7; // 183 -
	/** 调用静态方法 */
	int INVOKESTATIC = 0xB8; // 184 -
	/** 调用接口方法 */
	int INVOKEINTERFACE = 0xB9; // 185 -
	/** 调用动态方法。 用于在运行时动态解析出调用点限定符所引用的方法。并执行该方法。前面四条调用指令的分派逻辑都固化在Java虚拟机内部，用户无法改变，而invokedynamic指令的分派逻辑是由用户所设定的引导方法决定的。 */
	int INVOKEDYNAMIC = 0xBA; // 186 visitInvokeDynamicInsn
	// endregion

	// region 类和数组
	/** 创建一个对象，并且其引用进栈 */
	int NEW = 0xBB; // 187 visitTypeInsn
	/** 创建一个基本类型数组，并且其引用进栈 */
	int NEWARRAY = 0xBC; // 188 visitIntInsn
	/** 创建一个引用型（如类，接口，数组）的数组，并且其引用进栈 */
	int ANEWARRAY = 0xBD; // 189 visitTypeInsn
	/** 获得数组的长度值并压入栈顶 */
	int ARRAYLENGTH = 0xBE; // 190 visitInsn
	// endregion

	// region 异常处理
	/** 将栈顶的异常抛出 */
	int ATHROW = 0xBF; // 191 -
	// endregion

	// region 实例转换与检查
	/** 类型转换检查，如果该检查未通过将会抛出ClassCastException异常 */
	int CHECKCAST = 0xC0; // 192 visitTypeInsn
	/** 检查对象是否是指定的类的实例。如果是，1进栈；否则，0进栈 */
	int INSTANCEOF = 0xC1; // 193 -
	// endregion

	// region 锁控制
	/** 获得对象锁，用于同步方法或同步块 */
	int MONITORENTER = 0xC2; // 194 visitInsn
	/** 释放对象锁，用于同步方法或同步块 */
	int MONITOREXIT = 0xC3; // 195 -
	// endregion

	/** 用于修改其他指令的行为，当本地变量的索引超过255时使用该指令扩展索引宽度 */
	int WIDE = 0xC4; // 196 -
	/** 创建指定类型和维度的多维数组（执行该指令时，栈中必须包含各维度的长度值），并且其引用值进栈 */
	int MULTIANEWARRAY = 0xC5; // 197 visitMultiANewArrayInsn

	// region 空值检查跳转
	/** 为null时跳转 */
	int IFNULL = 0xC6; // 198 visitJumpInsn
	/** 不为null时跳转 */
	int IFNONNULL = 0xC7; // 199 -
	// endregion

	// region 宽索引指令
	/** 无条件跳转（宽索引） */
	int GOTO_W = 0xC8; // 200 -
	/** 跳转至指定32位offset位置，并且jsr_w下一条指令地址进栈 */
	int JSR_W = 0xC9; // 201 -
	// endregion

	// region 其他
	/** 为Java调试器中的断点保留；不应出现在任何类文件中 */
	int BREAKPOINT = 0xCA; // 202

	// `0xCB-0xFD`：尚未分配

	/** 为调试器内的实现相关操作保留；不应出现在任何类文件中 */
	int IMPDEP1 = 0xFE; // 254
	/** 为调试器内的实现相关操作保留；不应出现在任何类文件中 */
	int IMPDEP2 = 0xFF; // 255
	// endregion
}
