package io.polaris.core.reflect;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author Qt
 * @since 1.8
 */
public class ReflectsTest2 {

	@Test
	void test01() {
		TestConsole.println(Arrays.toString(
			Reflects.getFields(C.class, c -> c.getType().isPrimitive())
		));
	}

	@Test
	void test02() {
		TestConsole.println(Reflects.findParameterizedTypes(I.class, C.class));
		TestConsole.println(Reflects.findActualTypeArgument(I.class, C.class, 0));
		TestConsole.println(Reflects.findAllParameterizedTypes(C.class));
		TestConsole.println(Arrays.toString(Reflects.findActualTypeArguments(C.class)));

		TestConsole.println(Arrays.toString(C.class.getTypeParameters()));
	}

	static interface I<A> {
	}

	static class C<A> implements I<Integer[]> {
		String s;
		int i;
		byte b;
	}
}
