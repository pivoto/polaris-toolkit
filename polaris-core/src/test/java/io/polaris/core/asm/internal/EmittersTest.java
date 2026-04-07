package io.polaris.core.asm.internal;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.function.Consumer;

import io.polaris.core.asm.BaseAsmTest;
import io.polaris.core.asm.generator.DebuggingClassWriter;
import io.polaris.core.io.Consoles;
import io.polaris.core.reflect.Reflects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

class EmittersTest extends BaseAsmTest {

	private static Object generate(TestInfo testInfo, Consumer<ClassEmitter> consumer) throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		DebuggingClassWriter cw = new DebuggingClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassEmitter ce = new ClassEmitter(cw);
		{
			String className = testInfo.getTestClass().get().getName() + "$" + testInfo.getTestMethod().get().getName();
			ce.begin_class(AsmConsts.V1_8,
				AsmConsts.ACC_PUBLIC,
				className,
				Type.getType(Object.class),
				new Type[]{Type.getType(Serializable.class)},
				null);

			Emitters.null_constructor(ce);

			consumer.accept(ce);

			ce.end_class();
		}

		byte[] bytes = cw.toByteArray();
		Class<?> clazz = AsmReflects.defineClass(ClassNameReader.getClassName(new ClassReader(bytes)), bytes, classLoader);
		Consoles.log("class: {}", clazz);
		Object o = clazz.newInstance();
		return o;
	}
	@Test
	void test01(TestInfo testInfo) throws Exception {
		generate(testInfo, ce -> {
			Emitters.factory_method(ce, new Signature("factoryMethod", ce.getClassType(), new Type[]{Type.getType(int.class)}));
			Emitters.factory_method(ce, new Signature("factoryMethod", ce.getClassType(), new Type[]{}));
		});
	}

	@Test
	void test02(TestInfo testInfo) throws Exception {
		Object o = generate(testInfo, ce -> {
			CodeEmitter e = ce.begin_method(AsmConsts.ACC_PUBLIC, new Signature("test", Type.VOID_TYPE, new Type[]{Type.getType(String[].class)}), null);
			e.load_arg(0);

			Emitters.process_array(e, Type.getType(String[].class), (eleType) -> {
				e.checkcast(Type.getType(String.class));
				Local param = e.make_local(Type.getType(String.class));
				e.store_local(param);

				e.getstatic(Type.getType(System.class), "out", Type.getType(PrintStream.class));
				e.push("print: [%s] \n");

				e.push(1);
				e.newarray(Type.getType(Object.class));
				e.dup();
				e.push(0);
				e.load_local(param);
				e.checkcast(Type.getType(Object.class));
				e.array_store(Type.getType(Object.class));

				e.invoke_virtual(Type.getType(PrintStream.class), new Signature("printf", Type.getType(PrintStream.class), new Type[]{Type.getType(String.class), Type.getType(Object[].class)}));
				e.pop();

				e.getstatic(Type.getType(System.class), "out", Type.getType(PrintStream.class));
				e.load_local(param);
				e.invoke_virtual(Type.getType(PrintStream.class), new Signature("println", Type.VOID_TYPE, new Type[]{Type.getType(String.class)}));
			});

			e.return_value();
			e.end_method();
		});
		Reflects.invoke(o, Reflects.getMethod(o.getClass(), "test", String[].class), new Object[]{new String[]{"a", "b", "c"}});
	}
	@Test
	void test03(TestInfo testInfo) throws Exception {
		Object o = generate(testInfo, ce -> {
			CodeEmitter e = ce.begin_method(AsmConsts.ACC_PUBLIC,
				new Signature("test", Type.VOID_TYPE, new Type[]{Type.getType(String[].class),Type.getType(String[].class)}), null);
			e.load_arg(0);
			e.load_arg(1);

			Emitters.process_arrays(e, Type.getType(String[].class), (eleType) -> {
				e.checkcast(Type.getType(String.class));
				Local param = e.make_local(Type.getType(String.class));
				Local param1 = e.make_local(Type.getType(String.class));
				Local param2 = e.make_local(Type.getType(String.class));
				e.store_local(param1);
				e.store_local(param2);
				e.new_instance(Type.getType(StringBuilder.class));
				e.dup();
				e.invoke_constructor(Type.getType(StringBuilder.class), new Signature("<init>", Type.VOID_TYPE, new Type[]{}));
				e.load_local(param1);
				e.invoke_virtual(Type.getType(StringBuilder.class), new Signature("append", Type.getType(StringBuilder.class), new Type[]{Type.getType(String.class)}));
				e.push(" + ");
				e.invoke_virtual(Type.getType(StringBuilder.class), new Signature("append", Type.getType(StringBuilder.class), new Type[]{Type.getType(String.class)}));
				e.load_local(param2);
				e.invoke_virtual(Type.getType(StringBuilder.class), new Signature("append", Type.getType(StringBuilder.class), new Type[]{Type.getType(String.class)}));
				e.invoke_virtual(Type.getType(StringBuilder.class), new Signature("toString", Type.getType(String.class), new Type[]{}));
				e.store_local(param);

				e.getstatic(Type.getType(System.class), "out", Type.getType(PrintStream.class));
				e.push("print: [%s] \n");

				e.push(1);
				e.newarray(Type.getType(Object.class));
				e.dup();
				e.push(0);
				e.load_local(param);
				e.checkcast(Type.getType(Object.class));
				e.array_store(Type.getType(Object.class));

				e.invoke_virtual(Type.getType(PrintStream.class), new Signature("printf", Type.getType(PrintStream.class), new Type[]{Type.getType(String.class), Type.getType(Object[].class)}));
				e.pop();

				e.getstatic(Type.getType(System.class), "out", Type.getType(PrintStream.class));
				e.load_local(param);
				e.invoke_virtual(Type.getType(PrintStream.class), new Signature("println", Type.VOID_TYPE, new Type[]{Type.getType(String.class)}));
			});

			e.return_value();
			e.end_method();
		});
		Reflects.invoke(o, Reflects.getMethod(o.getClass(), "test", String[].class,String[].class), new Object[]{new String[]{"a", "b", "c"}, new String[]{"x", "y", "z"}});
	}

}
