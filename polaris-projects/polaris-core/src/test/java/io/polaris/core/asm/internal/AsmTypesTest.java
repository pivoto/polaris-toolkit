package io.polaris.core.asm.internal;

import java.util.Map;

import io.polaris.core.TestConsole;
import io.polaris.core.lang.JavaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.objectweb.asm.Type;

import static org.mockito.Mockito.*;

class AsmTypesTest {


	@Test
	void test_toTypeSignature() throws ClassNotFoundException {
		TestConsole.printx(AsmTypes.toTypeSignature(Map.class));
		TestConsole.printx(AsmTypes.toTypeSignature(JavaType.of("java.util.Map<String,String>")));
	}

	@Test
	void test_getType() {
		TestConsole.printx(Type.getType(Object[][].class));
		TestConsole.printx(Type.getType(String[][].class));
		Type result = AsmTypes.getType(String.class.getName());
		TestConsole.printx(result);
		Assertions.assertEquals(Type.getType(String.class), result);
	}
	@Test
	void test_getClassName() throws ClassNotFoundException {
		TestConsole.printx(AsmTypes.getClassName(Type.getType(String.class)));
		TestConsole.printx(AsmTypes.getClassName(Type.getType(String[].class)));
		TestConsole.printx(AsmTypes.getClassName(Type.getType(String[][].class)));
	}

}

