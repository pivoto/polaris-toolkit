package io.polaris.core.lang;

import org.junit.jupiter.api.Test;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class AnnotationsTest {

	@Test
	void test01() {
		System.out.println("T1: " + Annotations.get(T1.class, A1.class));
		System.out.println("T2: " + Annotations.get(T2.class, A1.class));
		System.out.println("T3: " + Annotations.get(T3.class, A1.class));
		System.out.println("T4: " + Annotations.get(T4.class, A1.class));
		System.out.println("T5: " + Annotations.get(T5.class, A1.class));
		System.out.println("T6: " + Annotations.get(T6.class, A1.class));
		System.out.println("T7: " + Annotations.get(T7.class, A1.class));
		System.out.println("T8: " + Annotations.get(T8.class, A1.class));
	}

	@Test
	void test02() {
		System.out.println("T1: " + Arrays.toString(Annotations.getRepeatable(T1.class, R1.class)));
		System.out.println("T2: " + Arrays.toString(Annotations.getRepeatable(T2.class, R1.class)));
		System.out.println("T3: " + Arrays.toString(Annotations.getRepeatable(T3.class, R1.class)));
		System.out.println("T4: " + Arrays.toString(Annotations.getRepeatable(T4.class, R1.class)));
		System.out.println("T5: " + Arrays.toString(Annotations.getRepeatable(T5.class, R1.class)));
		System.out.println("T6: " + Arrays.toString(Annotations.getRepeatable(T6.class, R1.class)));
		System.out.println("T7: " + Arrays.toString(Annotations.getRepeatable(T7.class, R1.class)));
		System.out.println("T8: " + Arrays.toString(Annotations.getRepeatable(T8.class, R1.class)));
	}

	@Test
	void test04() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException {
		A1 a1 = Annotations.get(T1.class, A1.class);
		InvocationHandler handler = Proxy.getInvocationHandler(a1);
		Field field = handler.getClass().getDeclaredField("memberValues");
		field.setAccessible(true);
		System.out.println(field.get(handler));

		{
			Method value = A2.class.getMethod("value");
			System.out.println(value.getDefaultValue());
		}
		Proxy.newProxyInstance(A1.class.getClassLoader(), new Class[]{A1.class}, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				return null;
			}
		});
	}

	@A1("I1")
	@R1("I1.1")
	@R1("I1.2")
	public static interface I1 {
	}

	@A1("I2")
	@R1("I2.1")
	@R1("I2.2")
	public static interface I2 {
	}

	@A1("T1")
	@R1("T1.1")
	@R1("T1.2")
	public static class T1 implements I1, I2 {
	}

	public static class T2 extends T1 implements I1, I2 {
	}

	@A2
	@R2
	public static class T3 {
	}

	public static class T4 implements I1, I2 {
	}

	@A2
	@R2
	public static class T5 implements I1, I2 {
	}

	public static class T6 extends T3 {
	}

	public static class T7 extends T4 {
	}

	public static class T8 extends T5 {
	}


	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER})
	public static @interface A1 {
		String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER})
	@A1("A2")
	public static @interface A2 {
		String value() default "A2";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER})
	@R1("R2.1")
	@R1("R2.2")
	public static @interface R2 {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER})
	@Repeatable(R1s.class)
	public static @interface R1 {
		String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER})
	public static @interface R1s {
		R1[] value();
	}
}



