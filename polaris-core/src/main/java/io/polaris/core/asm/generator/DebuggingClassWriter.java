package io.polaris.core.asm.generator;

import java.io.File;

import io.polaris.core.asm.internal.AsmConsts;
import io.polaris.core.consts.StdKeys;
import io.polaris.core.env.GlobalStdEnv;
import io.polaris.core.io.IO;
import io.polaris.core.string.Strings;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * @author Qt
 * @since May 10, 2024
 */
public class DebuggingClassWriter extends ClassVisitor {
	private static final String classBytesCacheDir;
	private static final boolean classBytesCacheEnabled;

	private String className;
	private String superName;

	static {
		String tmpdir = GlobalStdEnv.get(StdKeys.JAVA_CLASS_BYTES_TMPDIR);
		if (Strings.isNotBlank(tmpdir)) {
			File dir = new File(tmpdir.trim());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			classBytesCacheDir = dir.getAbsolutePath();
			classBytesCacheEnabled = dir.exists();
		} else {
			classBytesCacheDir = null;
			classBytesCacheEnabled = false;
		}
	}

	public DebuggingClassWriter(int flags) {
		super(AsmConsts.ASM_API, new ClassWriter(flags));
	}

	public void visit(int version,
		int access,
		String name,
		String signature,
		String superName,
		String[] interfaces) {
		className = name.replace('/', '.');
		this.superName = superName.replace('/', '.');
		super.visit(version, access, name, signature, superName, interfaces);
	}

	public String getClassName() {
		return className;
	}

	public String getSuperName() {
		return superName;
	}

	public byte[] toByteArray() {
		return (byte[]) java.security.AccessController.doPrivileged(
			new java.security.PrivilegedAction() {
				public Object run() {
					byte[] b = ((ClassWriter) DebuggingClassWriter.super.cv).toByteArray();
					if (classBytesCacheEnabled) {
						try {
							IO.writeBytes(new File(classBytesCacheDir + File.separatorChar + className.replace('.', File.separatorChar) + ".class"), b);
						} catch (Throwable ignored) {
						}
					}
					return b;
				}
			});

	}
}
