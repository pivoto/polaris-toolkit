package io.polaris.core.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Deque;

import io.polaris.core.TestConsole;
import io.polaris.core.collection.ObjectArrays;
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
			TestConsole.println(q);
			TestConsole.println(Arrays.toString(Reflects.findActualTypeArguments(c)));
		}
	}


	@Test
	void test02() throws NoSuchMethodException {
		Method m = C.class.getMethod("get", new Class[]{Object.class});
		TestConsole.println(Reflects.findMethodGenericReturnType(m, C.class));
		TestConsole.println(Reflects.findActualTypeArgument(I.class, C.class, 0));
		TestConsole.println(Reflects.findActualTypeArgument(I.class, C.class, 1));
		TestConsole.println(Reflects.findActualTypeArgument(I.class, C.class, 2));
	}


	@Test
	void test03() throws ClassNotFoundException {
		TestConsole.println(Reflects.loadClass("int"));
		TestConsole.println(Reflects.loadClass("[int"));
		TestConsole.println(Reflects.loadClass("[int[]"));
		TestConsole.println(Reflects.loadClass(I.class.getCanonicalName()));
		TestConsole.println(Reflects.loadClass(I.II.class.getCanonicalName()));
		TestConsole.println(Reflects.loadClass(I.II.IIC.class.getCanonicalName()));
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
