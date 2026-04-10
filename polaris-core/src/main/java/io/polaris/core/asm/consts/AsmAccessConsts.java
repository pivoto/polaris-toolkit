package io.polaris.core.asm.consts;

import org.objectweb.asm.Opcodes;

/**
 * @author Qt
 * @see Opcodes
 * @since Apr 10, 2026
 */
public interface AsmAccessConsts {

	// Access flags values, defined in
	// - https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.1-200-E.1
	// - https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.5-200-A.1
	// - https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.6-200-A.1
	// - https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.25

	int ACC_PUBLIC = 0x0001; // class, field, method
	int ACC_PRIVATE = 0x0002; // class, field, method
	int ACC_PROTECTED = 0x0004; // class, field, method
	int ACC_STATIC = 0x0008; // field, method
	int ACC_FINAL = 0x0010; // class, field, method, parameter
	int ACC_SUPER = 0x0020; // class
	int ACC_SYNCHRONIZED = 0x0020; // method
	int ACC_OPEN = 0x0020; // module
	int ACC_TRANSITIVE = 0x0020; // module requires
	int ACC_VOLATILE = 0x0040; // field
	int ACC_BRIDGE = 0x0040; // method
	int ACC_STATIC_PHASE = 0x0040; // module requires
	int ACC_VARARGS = 0x0080; // method
	int ACC_TRANSIENT = 0x0080; // field
	int ACC_NATIVE = 0x0100; // method
	int ACC_INTERFACE = 0x0200; // class
	int ACC_ABSTRACT = 0x0400; // class, method
	int ACC_STRICT = 0x0800; // method
	int ACC_SYNTHETIC = 0x1000; // class, field, method, parameter, module *
	int ACC_ANNOTATION = 0x2000; // class
	int ACC_ENUM = 0x4000; // class(?) field inner
	int ACC_MANDATED = 0x8000; // field, method, parameter, module, module *
	int ACC_MODULE = 0x8000; // class

	// ASM specific access flags.
	// WARNING: the 16 least significant bits must NOT be used, to avoid conflicts with standard
	// access flags, and also to make sure that these flags are automatically filtered out when
	// written in class files (because access flags are stored using 16 bits only).

	int ACC_RECORD = 0x10000; // class
	int ACC_DEPRECATED = 0x20000; // class, field, method

}
