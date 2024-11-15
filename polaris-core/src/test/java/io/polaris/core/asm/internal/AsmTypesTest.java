package io.polaris.core.asm.internal;

import java.util.Map;

import io.polaris.core.io.Consoles;
import io.polaris.core.lang.JavaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;

class AsmTypesTest {


	@Test
	void test_toTypeSignature() throws ClassNotFoundException {
		String msg1 = AsmTypes.toTypeSignature(Map.class);
		Consoles.log(msg1);
		String msg = AsmTypes.toTypeSignature(JavaType.of("java.util.Map<String,String>"));
		Consoles.log(msg);
	}

	@Test
	void test_getType() {
		Object[] args1 = new Object[]{Type.getType(Object[][].class)};
		Consoles.log("", args1);
		Object[] args = new Object[]{Type.getType(String[][].class)};
		Consoles.log("", args);
		Type result = AsmTypes.getType(String.class.getName());
		Consoles.log("", result);
		Assertions.assertEquals(Type.getType(String.class), result);
	}
	@Test
	void test_getClassName() throws ClassNotFoundException {
		String msg2 = AsmTypes.getClassName(Type.getType(String.class));
		Consoles.log(msg2);
		String msg1 = AsmTypes.getClassName(Type.getType(String[].class));
		Consoles.log(msg1);
		String msg = AsmTypes.getClassName(Type.getType(String[][].class));
		Consoles.log(msg);
	}

}

