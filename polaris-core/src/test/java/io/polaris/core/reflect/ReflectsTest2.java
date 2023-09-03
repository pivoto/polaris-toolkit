package io.polaris.core.reflect;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author Qt
 * @since 1.8
 */
public class ReflectsTest2 {

	@Test
	void test01() {
		System.out.println(Arrays.toString(
			Reflects.getFields(C.class, c -> c.getType().isPrimitive())
		));
	}

	@Test
	void test02() {
		System.out.println(Reflects.findParameterizedTypes(I.class, C.class));
		System.out.println(Reflects.findParameterizedType(I.class, C.class, 0));
		System.out.println(Reflects.findAllParameterizedTypes(C.class));
		System.out.println(Arrays.toString(Reflects.findParameterizedTypes(C.class)));

		System.out.println(Arrays.toString(C.class.getTypeParameters()));
	}

	static interface I<A> {
	}

	static class C<A> implements I<Integer[]> {
		String s;
		int i;
		byte b;
	}
}
