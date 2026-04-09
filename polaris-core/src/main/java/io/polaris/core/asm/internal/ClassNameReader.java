package io.polaris.core.asm.internal;

import java.util.ArrayList;
import java.util.List;

import io.polaris.core.err.UncheckedException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

/**
 * 类名读取器，用于从字节码中提取类信息
 * <p>
 * 通过 ASM ClassVisitor 快速读取类名、父类名和接口名
 *
 * @author Qt
 * @since May 10, 2024
 */
public class ClassNameReader {

	/** 用于提前退出 ClassVisitor 的异常 */
	private static final EarlyExitException EARLY_EXIT = new EarlyExitException();

	/**
	 * 获取类名
	 *
	 * @param r ASM ClassReader
	 * @return 类的全限定名（使用点分隔符）
	 */
	public static String getClassName(ClassReader r) {
		return getClassNames(r)[0];
	}

	/**
	 * 获取类信息数组
	 * <p>
	 * 返回包含类名、父类名和接口名的数组
	 *
	 * @param r ASM ClassReader
	 * @return 类信息数组，[0]=类名，[1]=父类名，后续=接口名
	 */
	public static String[] getClassNames(ClassReader r) {
		final List<String> array = new ArrayList<>();
		try {
			// 使用 ClassVisitor 读取类信息，读取后立即抛出异常退出
			r.accept(new ClassVisitor(AsmConsts.ASM_API, null) {
				@Override
				public void visit(int version,
					int access,
					String name,
					String signature,
					String superName,
					String[] interfaces) {
					// 添加类名（转换为点分隔符）
					array.add(name.replace('/', '.'));
					// 添加父类名
					if (superName != null) {
						array.add(superName.replace('/', '.'));
					}
					// 添加所有接口名
					for (int i = 0; i < interfaces.length; i++) {
						array.add(interfaces[i].replace('/', '.'));
					}
					// 读取完成，抛出异常退出
					throw EARLY_EXIT;
				}
			}, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
		} catch (EarlyExitException ignored) {}

		return array.toArray(new String[0]);
	}

	/**
	 * 提前退出异常，用于在读取类信息后立即终止 ClassVisitor 的遍历
	 */
	private static class EarlyExitException extends UncheckedException {
		private static final long serialVersionUID = 1L;
	}
}
