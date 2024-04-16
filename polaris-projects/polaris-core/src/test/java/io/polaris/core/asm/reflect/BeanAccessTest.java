package io.polaris.core.asm.reflect;

import java.io.IOException;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since 1.8,  Apr 11, 2024
 */
public class BeanAccessTest {
	static {
		System.setProperty("java.memory.bytecode.tmpdir", "/data/classes");
	}

	@Test
	void testGenerate() throws IOException {
//		System.out.println(ClassAccess.get(AccessBean01.class));
//		System.out.println(ClassLambdaAccess.get(AccessBean01.class));
//		System.out.println(BeanAccess.get(AccessBean01.class));
		System.out.println(BeanLambdaAccess.get(AccessBean01.class));
	}

	@Test
	void testBeanAccess() throws IOException {
		BeanAccess<AccessBean01> access = BeanAccess.get(AccessBean01.class);
		AccessBean01 o = new AccessBean01();
		TestConsole.printx("bean: {}", o);

		TestConsole.printx(access.allPropertyNames());
		TestConsole.printx(access.getterPropertyNames());

		access.setProperty(o, "publicStrVal0", "newStrVal");
		Assertions.assertEquals("newStrVal",access.getProperty(o, "publicStrVal0"));
		TestConsole.printx("get: {}", access.getProperty(o, "publicStrVal0"));

		access.setField(o, "publicStrVal1", "newStrVal");
		Assertions.assertEquals("newStrVal",access.getField(o, "publicStrVal1"));
		TestConsole.printx("get: {}", access.getField(o, "publicStrVal1"));

	}
	@Test
	void testBeanLambdaAccess() throws IOException {
		BeanLambdaAccess<AccessBean01> access = BeanLambdaAccess.get(AccessBean01.class);
		AccessBean01 o = new AccessBean01();
		TestConsole.printx("bean: {}", o);

		TestConsole.printx(access.allPropertyNames());
		TestConsole.printx(access.getterPropertyNames());

		access.setProperty(o, "publicStrVal0", "newStrVal");
		Assertions.assertEquals("newStrVal",access.getProperty(o, "publicStrVal0"));
		TestConsole.printx("get: {}", access.getProperty(o, "publicStrVal0"));

		access.setField(o, "publicStrVal1", "newStrVal");
		Assertions.assertEquals("newStrVal",access.getField(o, "publicStrVal1"));
		TestConsole.printx("get: {}", access.getField(o, "publicStrVal1"));

	}
}
