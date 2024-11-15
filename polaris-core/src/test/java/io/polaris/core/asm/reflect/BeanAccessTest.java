package io.polaris.core.asm.reflect;

import java.io.IOException;

import io.polaris.core.asm.BaseAsmTest;
import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since Apr 11, 2024
 */
public class BeanAccessTest extends BaseAsmTest {

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
		Consoles.log("bean: {}", o);

		Object[] args1 = new Object[]{access.allPropertyNames()};
		Consoles.log("", args1);
		Object[] args = new Object[]{access.getterPropertyNames()};
		Consoles.log("", args);

		access.setProperty(o, "publicStrVal0", "newStrVal");
		Assertions.assertEquals("newStrVal", access.getProperty(o, "publicStrVal0"));
		Object[] args3 = new Object[]{access.getProperty(o, "publicStrVal0")};
		Consoles.log("get: {}", args3);

		access.setField(o, "publicStrVal1", "newStrVal");
		Assertions.assertEquals("newStrVal", access.getField(o, "publicStrVal1"));
		Object[] args2 = new Object[]{access.getField(o, "publicStrVal1")};
		Consoles.log("get: {}", args2);

	}

	@Test
	void testBeanLambdaAccess() throws IOException {
		BeanLambdaAccess<AccessBean01> access = BeanLambdaAccess.get(AccessBean01.class);
		AccessBean01 o = new AccessBean01();
		Consoles.log("bean: {}", o);

		Object[] args1 = new Object[]{access.allPropertyNames()};
		Consoles.log("", args1);
		Object[] args = new Object[]{access.getterPropertyNames()};
		Consoles.log("", args);

		access.setProperty(o, "publicStrVal0", "newStrVal");
		Assertions.assertEquals("newStrVal", access.getProperty(o, "publicStrVal0"));
		Object[] args3 = new Object[]{access.getProperty(o, "publicStrVal0")};
		Consoles.log("get: {}", args3);

		access.setField(o, "publicStrVal1", "newStrVal");
		Assertions.assertEquals("newStrVal", access.getField(o, "publicStrVal1"));
		Object[] args2 = new Object[]{access.getField(o, "publicStrVal1")};
		Consoles.log("get: {}", args2);

	}
}
