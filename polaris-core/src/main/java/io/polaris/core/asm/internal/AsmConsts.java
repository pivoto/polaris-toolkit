package io.polaris.core.asm.internal;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since May 09, 2024
 */
public interface AsmConsts extends org.objectweb.asm.Opcodes {
	int ASM_API = Opcodes.ASM9;

	Class[] EMPTY_CLASS_ARRAY = {};
	Type[] TYPES_EMPTY = {};

	Signature SIG_STATIC = AsmTypes.parseSignature("void <clinit>()");


	Type TYPE_OBJECT_ARRAY = Type.getType(Object[].class);
	Type TYPE_CLASS_ARRAY = Type.getType(Class[].class);
	Type TYPE_STRING_ARRAY = Type.getType(String[].class);

	Type TYPE_OBJECT = Type.getType(Object.class);
	Type TYPE_CLASS = Type.getType(Class.class);
	Type TYPE_CLASS_LOADER = Type.getType(ClassLoader.class);
	Type TYPE_CHARACTER = Type.getType(Character.class);
	Type TYPE_BOOLEAN = Type.getType(Boolean.class);
	Type TYPE_DOUBLE = Type.getType(Double.class);
	Type TYPE_FLOAT = Type.getType(Float.class);
	Type TYPE_LONG = Type.getType(Long.class);
	Type TYPE_INTEGER = Type.getType(Integer.class);
	Type TYPE_SHORT = Type.getType(Short.class);
	Type TYPE_BYTE = Type.getType(Byte.class);
	Type TYPE_NUMBER = Type.getType(Number.class);
	Type TYPE_STRING = Type.getType(String.class);
	Type TYPE_THROWABLE = Type.getType(Throwable.class);
	Type TYPE_BIG_INTEGER = Type.getType(java.math.BigInteger.class);
	Type TYPE_BIG_DECIMAL = Type.getType(java.math.BigDecimal.class);
	Type TYPE_STRING_BUILDER = Type.getType(StringBuilder.class);
	Type TYPE_STRING_BUFFER = Type.getType(StringBuffer.class);
	Type TYPE_RUNTIME_EXCEPTION = Type.getType(RuntimeException.class);
	Type TYPE_ILLEGAL_ARGUMENT_EXCEPTION = Type.getType(IllegalArgumentException.class);
	Type TYPE_ERROR = Type.getType(Error.class);
	Type TYPE_SYSTEM = Type.getType(System.class);
	Type TYPE_SIGNATURE = Type.getType(Signature.class);
	Type TYPE_TYPE = Type.getType(Type.class);

	String CONSTRUCTOR_NAME = "<init>";
	String STATIC_NAME = "<clinit>";
	String SOURCE_FILE = "<generated>";
	String SUID_FIELD_NAME = "serialVersionUID";
	String CLASS_TAG_SEPARATOR = "$$";
	String CLASS_TAG_DEFAULT = "Generated";
	String CLASS_ENHANCED_FIELD = "GENERATED$ENHANCED";

	int PRIVATE_FINAL_STATIC = ACC_PRIVATE | ACC_FINAL | ACC_STATIC;
	int PRIVATE_STATIC = ACC_PRIVATE | ACC_STATIC;

	int SWITCH_STYLE_TRIE = 0;
	int SWITCH_STYLE_HASH = 1;
	int SWITCH_STYLE_HASHONLY = 2;

}
