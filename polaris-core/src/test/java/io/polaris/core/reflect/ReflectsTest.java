package io.polaris.core.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Deque;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

class ReflectsTest {

	@SuppressWarnings("serial")
	@Test
	void test01() {
		class A implements ParamImpl2<String> {
		}
		class B implements ParamImpl1 {
		}
		for (Class c : new Class[]{A.class, new ParamImpl2<String>() {}.getClass(), B.class, new ParamImpl1() {}.getClass() }) {
			Deque<ParameterizedType> q = Reflects.findAllParameterizedTypes(c);
			Consoles.println(q);
			String msg = Arrays.toString(Reflects.findActualTypeArguments(c));
			Consoles.println(msg);
		}
	}


	@Test
	void test02() throws NoSuchMethodException {
		Method m = C.class.getMethod("get", new Class[]{Object.class});
		Object[] args3 = new Object[]{Reflects.findMethodGenericReturnType(m, C.class)};
		Consoles.println(args3);
		Object[] args2 = new Object[]{Reflects.findActualTypeArgument(I.class, C.class, 0)};
		Consoles.println(args2);
		Object[] args1 = new Object[]{Reflects.findActualTypeArgument(I.class, C.class, 1)};
		Consoles.println(args1);
		Object[] args = new Object[]{Reflects.findActualTypeArgument(I.class, C.class, 2)};
		Consoles.println(args);
	}


	@Test
	void test03() throws ClassNotFoundException {
		Object[] args5 = new Object[]{Reflects.loadClass("int")};
		Consoles.println(args5);
		Object[] args4 = new Object[]{Reflects.loadClass("[int")};
		Consoles.println(args4);
		Object[] args3 = new Object[]{Reflects.loadClass("[int[]")};
		Consoles.println(args3);
		Object[] args2 = new Object[]{Reflects.loadClass(I.class.getCanonicalName())};
		Consoles.println(args2);
		Object[] args1 = new Object[]{Reflects.loadClass(I.II.class.getCanonicalName())};
		Consoles.println(args1);
		Object[] args = new Object[]{Reflects.loadClass(I.II.IIC.class.getCanonicalName())};
		Consoles.println(args);
	}

	interface I<K, V> {

		default V get(K k) {
			return null;
		}

		interface II {
			class IIC {

			}
		}
	}

	interface I2<V> extends I<String, V> {

	}

	class C implements I2<Integer> {

	}
}
