package io.polaris.core.asm.internal;

import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Consumer;

import io.polaris.core.TestConsole;
import io.polaris.core.asm.BaseAsmTest;
import io.polaris.core.asm.generator.DebuggingClassWriter;
import io.polaris.core.random.Randoms;
import io.polaris.core.reflect.Reflects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

class ClassEmitterTest extends BaseAsmTest {

	private static Object generateSimpleAndNewInstance(TestInfo testInfo, Consumer<ClassEmitter> consumer) throws Exception {
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
		Class clazz = AsmReflects.defineClass(ClassNameReader.getClassName(new ClassReader(bytes)), bytes, classLoader);
		TestConsole.printx("class: {}", clazz);
		Object o = clazz.newInstance();
		return o;
	}

	@Test
	void test01(TestInfo testInfo) throws Exception {
		Object o = generateSimpleAndNewInstance(testInfo, ce -> {
			ce.declare_field(AsmConsts.ACC_PUBLIC, "name", Type.getType(String.class), Randoms.randomString(10));
			ce.declare_field(AsmConsts.ACC_STATIC | AsmConsts.ACC_PUBLIC, "static_name", Type.getType(String.class), Randoms.randomString(10));
			ce.declare_field(AsmConsts.ACC_STATIC | AsmConsts.ACC_PUBLIC, "static_int", Type.getType(int.class), 123);
		});
		{
			Field[] fields = Reflects.getFields(o.getClass());
			for (Field field : fields) {
				field.setAccessible(true);
				TestConsole.printx("field: {}, value: {}", field, field.get(o));
			}
		}
	}

	@Test
	void test02(TestInfo testInfo) throws Exception {
		Object o = generateSimpleAndNewInstance(testInfo, ce -> {
			CodeEmitter e = ce.begin_method(AsmConsts.ACC_PUBLIC, new Signature("test", Type.VOID_TYPE, new Type[]{Type.getType(String[].class)}), null);
			e.load_arg(0);
			Emitters.process_array(e, Type.getType(String[].class), new ProcessArrayCallback() {
				@Override
				public void processElement(Type type) {
					e.checkcast(Type.getType(String.class));
					Local param = e.make_local(Type.getType(String.class));
					e.store_local(param);
					Local out = e.make_local(Type.getType(PrintStream.class));
					e.getstatic(Type.getType(System.class), "out", Type.getType(PrintStream.class));
					e.store_local(out);
					e.load_local(out);
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
				}
			});
			e.return_value();
			e.end_method();
		});

		Method m = Reflects.getMethodByName(o.getClass(), "test");
		TestConsole.printx("method: {}", m);
		Reflects.invoke(o, m, new Object[]{new String[]{"a", "b", "c"}});
	}
}
