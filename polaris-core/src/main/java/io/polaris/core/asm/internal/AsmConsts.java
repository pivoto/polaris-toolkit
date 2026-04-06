package io.polaris.core.asm.internal;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * ASM 常量接口，定义所有 ASM 相关的常量
 * <p>
 * 包含常用的类型、方法签名、访问标志等常量定义
 *
 * @author Qt
 * @since May 09, 2024
 */
public interface AsmConsts extends org.objectweb.asm.Opcodes {
	/** ASM API 版本，使用 ASM9 */
	int ASM_API = Opcodes.ASM9;

	/** 空 Class 数组 */
	Class[] EMPTY_CLASS_ARRAY = {};
	/** 空 Type 数组 */
	Type[] TYPES_EMPTY = {};

	/** 静态初始化方法签名 */
	Signature SIG_STATIC = AsmTypes.parseSignature("void <clinit>()");


	/** Object[] 的 Type，表示对象数组类型 */
	Type TYPE_OBJECT_ARRAY = Type.getType(Object[].class);
	/** Class[] 的 Type，表示类数组类型 */
	Type TYPE_CLASS_ARRAY = Type.getType(Class[].class);
	/** String[] 的 Type，表示字符串数组类型 */
	Type TYPE_STRING_ARRAY = Type.getType(String[].class);

	/** Object 的 Type，表示 Object 类型 */
	Type TYPE_OBJECT = Type.getType(Object.class);
	/** Class 的 Type，表示 Class 类型 */
	Type TYPE_CLASS = Type.getType(Class.class);
	/** ClassLoader 的 Type，表示类加载器类型 */
	Type TYPE_CLASS_LOADER = Type.getType(ClassLoader.class);
	/** Character 的 Type，表示 Character 包装类型 */
	Type TYPE_CHARACTER = Type.getType(Character.class);
	/** Boolean 的 Type，表示 Boolean 包装类型 */
	Type TYPE_BOOLEAN = Type.getType(Boolean.class);
	/** Double 的 Type，表示 Double 包装类型 */
	Type TYPE_DOUBLE = Type.getType(Double.class);
	/** Float 的 Type，表示 Float 包装类型 */
	Type TYPE_FLOAT = Type.getType(Float.class);
	/** Long 的 Type，表示 Long 包装类型 */
	Type TYPE_LONG = Type.getType(Long.class);
	/** Integer 的 Type，表示 Integer 包装类型 */
	Type TYPE_INTEGER = Type.getType(Integer.class);
	/** Short 的 Type，表示 Short 包装类型 */
	Type TYPE_SHORT = Type.getType(Short.class);
	/** Byte 的 Type，表示 Byte 包装类型 */
	Type TYPE_BYTE = Type.getType(Byte.class);
	/** Number 的 Type，表示 Number 抽象父类型 */
	Type TYPE_NUMBER = Type.getType(Number.class);
	/** String 的 Type，表示字符串类型 */
	Type TYPE_STRING = Type.getType(String.class);
	/** Throwable 的 Type，表示异常基类 */
	Type TYPE_THROWABLE = Type.getType(Throwable.class);
	/** BigInteger 的 Type，表示大整数类型 */
	Type TYPE_BIG_INTEGER = Type.getType(java.math.BigInteger.class);
	/** BigDecimal 的 Type，表示大浮点数类型 */
	Type TYPE_BIG_DECIMAL = Type.getType(java.math.BigDecimal.class);
	/** StringBuilder 的 Type，表示可变字符串构建器 */
	Type TYPE_STRING_BUILDER = Type.getType(StringBuilder.class);
	/** StringBuffer 的 Type，表示线程安全的可变字符串缓冲区 */
	Type TYPE_STRING_BUFFER = Type.getType(StringBuffer.class);
	/** RuntimeException 的 Type，表示运行时异常 */
	Type TYPE_RUNTIME_EXCEPTION = Type.getType(RuntimeException.class);
	/** IllegalArgumentException 的 Type，表示非法参数异常 */
	Type TYPE_ILLEGAL_ARGUMENT_EXCEPTION = Type.getType(IllegalArgumentException.class);
	/** Error 的 Type，表示错误类型 */
	Type TYPE_ERROR = Type.getType(Error.class);
	/** System 的 Type，表示系统工具类 */
	Type TYPE_SYSTEM = Type.getType(System.class);
	/** Signature 的 Type，表示方法签名类 */
	Type TYPE_SIGNATURE = Type.getType(Signature.class);
	/** Type 的 Type，表示 ASM 的 Type 类 */
	Type TYPE_TYPE = Type.getType(Type.class);

	/** 构造方法名称，JVM 内部标识符 */
	String CONSTRUCTOR_NAME = "<init>";
	/** 静态初始化方法名称，JVM 内部标识符 */
	String STATIC_NAME = "<clinit>";
	/** 生成的源文件标记 */
	String SOURCE_FILE = "<generated>";
	/** serialVersionUID 字段名称，用于 Java 序列化 */
	String SUID_FIELD_NAME = "serialVersionUID";
	/** 基础类名与类标签分隔符，用于生成类名 */
	String CLASS_TAG_SEPARATOR = "$$";
	/** 默认类标签，用于标识生成的类 */
	String CLASS_TAG_DEFAULT = "Generated";
	/** 生成类的命名分隔符，用于生成类名 */
	String CLASS_NAME_SEPARATOR = "$";
	/** 增强类的标记字段名称 */
	String CLASS_ENHANCED_FIELD = "GENERATED$ENHANCED";

	/** 私有静态常量访问标志组合 */
	int PRIVATE_FINAL_STATIC = ACC_PRIVATE | ACC_FINAL | ACC_STATIC;
	/** 私有静态访问标志组合 */
	int PRIVATE_STATIC = ACC_PRIVATE | ACC_STATIC;

	/** Switch 语句实现方式：使用查找表（Trie） */
	int SWITCH_STYLE_TRIE = 0;
	/** Switch 语句实现方式：使用哈希表 */
	int SWITCH_STYLE_HASH = 1;
	/** Switch 语句实现方式：仅使用哈希，不进行 equals 比较 */
	int SWITCH_STYLE_HASHONLY = 2;

}
