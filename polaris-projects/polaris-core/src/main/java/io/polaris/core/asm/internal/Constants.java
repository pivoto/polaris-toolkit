package io.polaris.core.asm.internal;

import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since  May 09, 2024
 */
public interface Constants extends org.objectweb.asm.Opcodes {

	Class[] EMPTY_CLASS_ARRAY = {};
	Type[] TYPES_EMPTY = {};

	Signature SIG_STATIC =
		AsmTypes.parseSignature("void <clinit>()");


	Type TYPE_OBJECT_ARRAY = AsmTypes.parseType("Object[]");
	Type TYPE_CLASS_ARRAY = AsmTypes.parseType("Class[]");
	Type TYPE_STRING_ARRAY = AsmTypes.parseType("String[]");

	Type TYPE_OBJECT = AsmTypes.parseType("Object");
	Type TYPE_CLASS = AsmTypes.parseType("Class");
	Type TYPE_CLASS_LOADER = AsmTypes.parseType("ClassLoader");
	Type TYPE_CHARACTER = AsmTypes.parseType("Character");
	Type TYPE_BOOLEAN = AsmTypes.parseType("Boolean");
	Type TYPE_DOUBLE = AsmTypes.parseType("Double");
	Type TYPE_FLOAT = AsmTypes.parseType("Float");
	Type TYPE_LONG = AsmTypes.parseType("Long");
	Type TYPE_INTEGER = AsmTypes.parseType("Integer");
	Type TYPE_SHORT = AsmTypes.parseType("Short");
	Type TYPE_BYTE = AsmTypes.parseType("Byte");
	Type TYPE_NUMBER = AsmTypes.parseType("Number");
	Type TYPE_STRING = AsmTypes.parseType("String");
	Type TYPE_THROWABLE = AsmTypes.parseType("Throwable");
	Type TYPE_BIG_INTEGER = AsmTypes.parseType("java.math.BigInteger");
	Type TYPE_BIG_DECIMAL = AsmTypes.parseType("java.math.BigDecimal");
	Type TYPE_STRING_BUILDER = AsmTypes.parseType("StringBuilder");
	Type TYPE_STRING_BUFFER = AsmTypes.parseType("StringBuffer");
	Type TYPE_RUNTIME_EXCEPTION = AsmTypes.parseType("RuntimeException");
	Type TYPE_ILLEGAL_ARGUMENT_EXCEPTION = AsmTypes.parseType("IllegalArgumentException");
	Type TYPE_ERROR = AsmTypes.parseType("Error");
	Type TYPE_SYSTEM = AsmTypes.parseType("System");
	Type TYPE_SIGNATURE = Type.getType(Signature.class);
	Type TYPE_TYPE = Type.getType(Type.class);

	String CONSTRUCTOR_NAME = "<init>";
	String STATIC_NAME = "<clinit>";
	String SOURCE_FILE = "<generated>";
	String SUID_FIELD_NAME = "serialVersionUID";

	int PRIVATE_FINAL_STATIC = ACC_PRIVATE | ACC_FINAL | ACC_STATIC;

	int SWITCH_STYLE_TRIE = 0;
	int SWITCH_STYLE_HASH = 1;
	int SWITCH_STYLE_HASHONLY = 2;

}
