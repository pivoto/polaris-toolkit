package io.polaris.core.consts;

import javax.annotation.Nullable;

/**
 * @author Qt
 * @since Jan 25, 2026
 */
public enum SystemProperty {

	/** Java Runtime Environment version. */
	JAVA_VERSION("java.version"),

	/** Java Runtime Environment vendor. */
	JAVA_VENDOR("java.vendor"),

	/** Java vendor URL. */
	JAVA_VENDOR_URL("java.vendor.url"),

	/** Java installation directory. */
	JAVA_HOME("java.home"),

	/** Java Virtual Machine specification version. */
	JAVA_VM_SPECIFICATION_VERSION("java.vm.specification.version"),

	/** Java Virtual Machine specification vendor. */
	JAVA_VM_SPECIFICATION_VENDOR("java.vm.specification.vendor"),

	/** Java Virtual Machine specification name. */
	JAVA_VM_SPECIFICATION_NAME("java.vm.specification.name"),

	/** Java Virtual Machine implementation version. */
	JAVA_VM_VERSION("java.vm.version"),

	/** Java Virtual Machine implementation vendor. */
	JAVA_VM_VENDOR("java.vm.vendor"),

	/** Java Virtual Machine implementation name. */
	JAVA_VM_NAME("java.vm.name"),

	/** Java Runtime Environment specification version. */
	JAVA_SPECIFICATION_VERSION("java.specification.version"),

	/** Java Runtime Environment specification vendor. */
	JAVA_SPECIFICATION_VENDOR("java.specification.vendor"),

	/** Java Runtime Environment specification name. */
	JAVA_SPECIFICATION_NAME("java.specification.name"),

	/** Java class format version number. */
	JAVA_CLASS_VERSION("java.class.version"),

	/** Java class path. */
	JAVA_CLASS_PATH("java.class.path"),

	/** List of paths to search when loading libraries. */
	JAVA_LIBRARY_PATH("java.library.path"),

	/** Default temp file path. */
	JAVA_IO_TMPDIR("java.io.tmpdir"),

	/** Name of JIT compiler to use. */
	JAVA_COMPILER("java.compiler"),

	/** Path of extension directory or directories. */
	JAVA_EXT_DIRS("java.ext.dirs"),

	/** Operating system name. */
	OS_NAME("os.name"),

	/** Operating system architecture. */
	OS_ARCH("os.arch"),

	/** Operating system version. */
	OS_VERSION("os.version"),

	/** File separator ("/" on UNIX). */
	FILE_SEPARATOR("file.separator"),

	/** Path separator (":" on UNIX). */
	PATH_SEPARATOR("path.separator"),

	/** Line separator ("\n" on UNIX). */
	LINE_SEPARATOR("line.separator"),

	/** User's account name. */
	USER_NAME("user.name"),

	/** User's home directory. */
	USER_HOME("user.home"),

	/** User's current working directory. */
	USER_DIR("user.dir"),

	/** CPU endianness. */
	SUN_CPU_ENDIAN("sun.cpu.endian"),

	;

	private final String key;

	SystemProperty(String key) {
		this.key = key;
	}

	public String key() {
		return key;
	}

	@Nullable
	public String value() {
		return System.getProperty(key);
	}

	@Override
	public String toString() {
		return key() + "=" + value();
	}
}
