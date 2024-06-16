package io.polaris.core.asm.internal;

import java.util.Arrays;

import io.polaris.core.TestConsole;
import io.polaris.core.err.CheckedException;
import io.polaris.core.reflect.Reflects;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since May 10, 2024
 */
public class AsmReflectsTest {

	@Test
	void test01() throws RuntimeException, CheckedException {
		Type[] types = AsmReflects.getExceptionTypes(Reflects.getMethodByName(getClass(), "test01"));
		TestConsole.printx("types: {}", Arrays.asList(types));

	}
}
