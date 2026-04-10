package io.polaris.core.asm.internal;

import java.util.function.Consumer;

import io.polaris.core.asm.generator.DebuggingClassWriter;
import io.polaris.core.string.Strings;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since Apr 10, 2026
 */
public class ClassEmitters {


	public static Class<?> createClass(ClassLoader classLoader, Integer access, String className, Type superType, Type[] interfaces, String source, Consumer<ClassEmitter> consumer) throws Exception {
		if (classLoader == null) {
			classLoader = Thread.currentThread().getContextClassLoader();
		}
		if (classLoader == null) {
			throw new IllegalStateException("Cannot determine classloader");
		}
		DebuggingClassWriter cw = new DebuggingClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassEmitter ce = new ClassEmitter(cw);
		{
			superType = superType != null ? superType : AsmConsts.TYPE_OBJECT;
			interfaces = interfaces != null ? interfaces : new Type[]{};
			ce.begin_class(AsmConsts.V1_8,
				access != null ? access : AsmConsts.ACC_PUBLIC,
				className, superType, interfaces,
				Strings.coalesce(source, AsmConsts.SOURCE_FILE)
			);
			if (consumer == null) {
				Emitters.null_constructor(ce);
			} else {
				consumer.accept(ce);
			}
			ce.end_class();
		}
		byte[] bytes = cw.toByteArray();
		return AsmReflects.defineClass(ClassNameReader.getClassName(new ClassReader(bytes)), bytes, classLoader);
	}

	public static Class<?> createClass(ClassLoader classLoader, Integer access, String className, Type superType, Type[] interfaces, Consumer<ClassEmitter> consumer) throws Exception {
		return createClass(classLoader, access, className, superType, interfaces, null, consumer);
	}

	public static Class<?> createClass(ClassLoader classLoader, Integer access, String className, Type superType, Consumer<ClassEmitter> consumer) throws Exception {
		return createClass(classLoader, access, className, superType, null, null, consumer);
	}

	public static Class<?> createClass(ClassLoader classLoader, Integer access, String className, Consumer<ClassEmitter> consumer) throws Exception {
		return createClass(classLoader, access, className, null, null, null, consumer);
	}

	public static Class<?> createClass(ClassLoader classLoader, String className, Consumer<ClassEmitter> consumer) throws Exception {
		return createClass(classLoader, null, className, null, null, null, consumer);
	}

	public static Class<?> createClass(String className, Consumer<ClassEmitter> consumer) throws Exception {
		return createClass(null, null, className, null, null, null, consumer);
	}

	public static Class<?> createClass(Integer access, String className, Type superType, Type[] interfaces, Consumer<ClassEmitter> consumer) throws Exception {
		return createClass(null, access, className, superType, interfaces, null, consumer);
	}

	public static Class<?> createClass(String className, Type superType, Type[] interfaces, Consumer<ClassEmitter> consumer) throws Exception {
		return createClass(null, null, className, superType, interfaces, null, consumer);
	}


	public static Class<?> createClass(String className, Type superType, Consumer<ClassEmitter> consumer) throws Exception {
		return createClass(null, null, className, superType, null, null, consumer);
	}

	public static Class<?> createClass(String className, Type[] interfaces, Consumer<ClassEmitter> consumer) throws Exception {
		return createClass(null, null, className, null, interfaces, null, consumer);
	}


}
