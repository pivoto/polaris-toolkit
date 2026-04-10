package io.polaris.core.asm.internal;

import java.io.PrintStream;

import io.polaris.core.asm.consts.AsmAccessConsts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

class ClassEmittersTest {


	@Test
	void test01(TestInfo testInfo) throws Exception {
		String className = testInfo.getTestClass().get().getName() + "$" + testInfo.getTestMethod().get().getName();
		Class<?> clazz = ClassEmitters.createClass(className, (ClassEmitter ce) -> {
			Emitters.null_constructor(ce);

			CodeEmitter e = ce.begin_method(AsmAccessConsts.ACC_PUBLIC, Signature.of("test", Type.VOID_TYPE), new Type[]{Type.getType(Throwable.class)});


			Label label = e.make_label();
			e.push(11);
			e.push(22);
			e.visitJumpInsn(AsmConsts.IF_ICMPGE, label);

			e.aconst_null();
			e.pop();

			e.push(11L);
			e.push(22L);
			e.if_cmp(Type.LONG_TYPE, CodeEmitter.GE, label);
			e.aconst_null();
			e.pop();
//			e.if_cmp(Type.INT_TYPE, CodeEmitter.GE, label);
//			e.if_jump( CodeEmitter.GE, label);

			e.push(1);
			e.push(2);
			e.if_cmp(Type.INT_TYPE, CodeEmitter.GE, label);
			e.aconst_null();
			e.pop();

			e.getstatic(Type.getType(System.class), "out", Type.getType(PrintStream.class));
			e.push("test");
			e.invoke_virtual(Type.getType(PrintStream.class), Signature.of("println", Type.VOID_TYPE, new Type[]{Type.getType(Object.class)}));


			e.mark(label);

			e.return_value();
			e.end_method();
		});

		Object obj = clazz.newInstance();
		clazz.getMethod("test").invoke(obj);
	}

}
