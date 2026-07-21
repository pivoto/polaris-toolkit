package io.polaris.core.asm.generator;

import java.io.File;

import io.polaris.core.asm.internal.AsmConsts;
import io.polaris.core.env.Config;
import io.polaris.core.io.IO;
import io.polaris.core.lang.JavaAccessControllers;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.string.Strings;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * 调试类写入器
 * <p>扩展 ClassVisitor，支持将生成的类字节码缓存到文件系统，便于调试分析</p>
 *
 * @author Qt
 * @since May 10, 2024
 */
public class DebuggingClassWriter extends ClassVisitor {
	/** 日志记录器 */
	private static final Logger log = Loggers.of(DebuggingClassWriter.class);
	/** 类字节码缓存目录路径 */
	private static final String classBytesCacheDir;
	/** 类字节码缓存是否启用 */
	private static final boolean classBytesCacheEnabled;

	/** 当前生成的类名 */
	private String className;
	/** 当前生成的父类名 */
	private String superName;

	static {
		// 从配置中获取字节码生成器的临时目录路径
		String tmpdir = Config.INSTANCE.bytecode().generator().tmpdir();
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

	/**
	 * 构造函数
	 *
	 * @param flags ASM ClassWriter 的标志位
	 */
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
		return (byte[]) JavaAccessControllers.get(() -> {
			byte[] b = ((ClassWriter) DebuggingClassWriter.super.cv).toByteArray();
			if (classBytesCacheEnabled) {
				try {
					File file = new File(classBytesCacheDir + File.separatorChar + className.replace('.', File.separatorChar) + ".class");
					if (log.isDebugEnabled()) {
						log.debug("缓存动态生成类的字节码：{}", file.getAbsolutePath());
					}
					IO.writeBytes(file, b);
				} catch (Throwable ignored) {
				}
			}
			return b;
		});

	}
}
