package io.polaris.core.classpath.analyzer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import io.polaris.core.crypto.digest.Digests;
import io.polaris.core.lang.primitive.Bytes;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.regex.Patterns;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.Printer;

/**
 * @author Qt
 * @since Aug 11, 2025
 */
public class EntryAnalyzer {
	private static final Logger log = Loggers.of(EntryAnalyzer.class);

	/** The names of the Java Virtual Machine opcodes. */
	public static final String[] OPCODES = Printer.OPCODES;
	/**
	 * The names of the {@code operand} values of the {@link
	 * org.objectweb.asm.MethodVisitor#visitIntInsn} method when {@code opcode} is {@code NEWARRAY}.
	 */
	public static final String[] TYPES = Printer.TYPES;
	/** The names of the {@code tag} field values for {@link org.objectweb.asm.Handle}. */
	public static final String[] HANDLE_TAG = Printer.HANDLE_TAG;

	/**
	 * 将 int 型操作码转换为对应的字段名
	 *
	 * @param opcode 操作码数值
	 * @return 对应的字段名字符串，若不存在则返回 "UNKNOWN(xxx)"
	 */
	public static String asOpcodeString(int opcode) {
		return asOpcodeString(opcode, "UNKNOWN");
	}

	/**
	 * 将 int 型操作码转换为对应的字段名
	 *
	 * @param opcode     操作码数值
	 * @param unknownStr 未知操作码的字段名
	 * @return 对应的字段名字符串，若不存在则返回 "unknownStr(xxx)"
	 */
	public static String asOpcodeString(int opcode, String unknownStr) {
		return (opcode >= 0 && opcode < OPCODES.length ? OPCODES[opcode] : unknownStr) + "(" + opcode + ")";
	}

	public static boolean match(String str, String regex) {
		Pattern pattern = Patterns.getPattern(regex);
		return Patterns.matches(pattern, str);
	}

	private static InputStream getInputStream(String path, Class<?> entryClass) throws FileNotFoundException {
		InputStream in = null;
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		if (entryClass != null) {
			in = entryClass.getResourceAsStream(path);
		}
		if (in == null) {
			if (path.startsWith("/")) {
				in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path.substring(1));
			} else {
				in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
			}
		}
		if (in == null) {
			in = ClassLoader.getSystemResourceAsStream(path);
		}
		if (in == null) {
			throw new FileNotFoundException(path);
		}
		return in;
	}

	public static InputStream getClassBytesInputStream(String classInternalName, Class<?> entryClass) throws FileNotFoundException {
		if (entryClass == null) {
			entryClass = EntryAnalyzer.class;
		}
		return getInputStream('/' + classInternalName.replace('.', '/') + ".class", entryClass);
	}

	public static InputStream getClassBytesInputStream(String classInternalName) throws FileNotFoundException {
		return getClassBytesInputStream(classInternalName, EntryAnalyzer.class);
	}


	public static ClassReader getReader(String classInternalName, Class<?> entryClass) throws IOException {
		return new ClassReader(getClassBytesInputStream(classInternalName, entryClass));
	}

	public static ClassReader getReader(String classInternalName) throws IOException {
		return new ClassReader(getClassBytesInputStream(classInternalName));
	}

	public static ClassReader getReader(Class<?> clazz) throws IOException {
		String classInternalName = Type.getInternalName(clazz);
		return new ClassReader(getClassBytesInputStream(classInternalName, clazz));
	}

	public static String calcBase32Sha1(byte[] bytes) {
		return Bytes.toBase32String(Digests.sha1(bytes));
	}

	public static String calcBase32Sha1Char8(byte[] bytes) {
		return calcBase32Sha1(bytes).substring(0, 8);
	}

	public static String calcBase32Sha1(String bytes) {
		return Bytes.toBase32String(Digests.sha1(bytes));
	}

	public static String calcBase32Sha1Char8(String bytes) {
		return calcBase32Sha1(bytes).substring(0, 8);
	}

}
