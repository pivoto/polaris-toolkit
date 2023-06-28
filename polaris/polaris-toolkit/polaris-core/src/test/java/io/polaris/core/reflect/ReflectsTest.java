package io.polaris.core.reflect;

import io.polaris.core.tuple.Ref;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Deque;

class ReflectsTest {

	@Test
	void test01() {
		class A implements ParamImpl2<String> {
		}
		Class c = A.class;
//		c = new ParamImpl2<String>(){}.getClass();
		Deque<ParameterizedType> q = Reflects.findAllParameterizedTypes(c);
		System.out.println(q);
		System.out.println(Arrays.toString(Reflects.findParameterizedTypes(c)));
	}


	@Test
	void test02() throws NoSuchMethodException {
		Method m = C.class.getMethod("get", new Class[]{Object.class});
		System.out.println(Reflects.findMethodGenericReturnType(m, C.class));
		System.out.println(Reflects.findParameterizedType(I.class, C.class, 0));
		System.out.println(Reflects.findParameterizedType(I.class, C.class, 1));
		System.out.println(Reflects.findParameterizedType(I.class, C.class, 2));
	}


	@Test
	void test03() throws ClassNotFoundException {
		System.out.println(Reflects.loadClass("int"));
		System.out.println(Reflects.loadClass("[int"));
		System.out.println(Reflects.loadClass("[int[]"));
		System.out.println(Reflects.loadClass(I.class.getCanonicalName()));
		System.out.println(Reflects.loadClass(I.II.class.getCanonicalName()));
		System.out.println(Reflects.loadClass(I.II.IIC.class.getCanonicalName()));
	}

	interface I<K, V> {

		default V get(K k) {
			return null;
		}

		interface II{
			class IIC{

			}
		}
	}

	interface I2<V> extends I<String, V> {

	}

	class C implements I2<Integer> {

	}
}
