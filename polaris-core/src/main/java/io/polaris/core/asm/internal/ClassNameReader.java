package io.polaris.core.asm.internal;

import java.util.ArrayList;
import java.util.List;

import io.polaris.core.err.UncheckedException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

/**
 * @author Qt
 * @since May 10, 2024
 */
public class ClassNameReader {

	private static final EarlyExitException EARLY_EXIT = new EarlyExitException();

	public static String getClassName(ClassReader r) {

		return getClassInfo(r)[0];

	}

	public static String[] getClassInfo(ClassReader r) {
		final List<String> array = new ArrayList<>();
		try {
			r.accept(new ClassVisitor(AsmConsts.ASM_API, null) {
				@Override
				public void visit(int version,
					int access,
					String name,
					String signature,
					String superName,
					String[] interfaces) {
					array.add(name.replace('/', '.'));
					if (superName != null) {
						array.add(superName.replace('/', '.'));
					}
					for (int i = 0; i < interfaces.length; i++) {
						array.add(interfaces[i].replace('/', '.'));
					}
					throw EARLY_EXIT;
				}
			}, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
		} catch (EarlyExitException ignored) {}

		return array.toArray(new String[0]);
	}

	private static class EarlyExitException extends UncheckedException {
		private static final long serialVersionUID = 1L;
	}
}
