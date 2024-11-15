package io.polaris.core.reflect;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author Qt
 * @since 1.8
 */
public class ReflectsTest2 {

	@Test
	void test01() {
		String msg = Arrays.toString(
			Reflects.getFields(C.class, c -> c.getType().isPrimitive())
		);
		Consoles.println(msg);
	}

	@Test
	void test02() {
		Object[] args2 = new Object[]{Reflects.findParameterizedTypes(I.class, C.class)};
		Consoles.println(args2);
		Object[] args1 = new Object[]{Reflects.findActualTypeArgument(I.class, C.class, 0)};
		Consoles.println(args1);
		Object[] args = new Object[]{Reflects.findAllParameterizedTypes(C.class)};
		Consoles.println(args);
		String msg = Arrays.toString(Reflects.findActualTypeArguments(C.class));
		Consoles.println(msg);

		Consoles.println(Arrays.toString(C.class.getTypeParameters()));
	}

	static interface I<A> {
	}

	static class C<A> implements I<Integer[]> {
		String s;
		int i;
		byte b;
	}
}
