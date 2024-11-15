package io.polaris.core.lang.annotation;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class AnnotationsTest00 {

	@Test
	void test01() {
		String msg7 = "T1: " + Annotations.getAnnotation(T1.class, A1.class);
		Consoles.println(msg7);
		String msg6 = "T2: " + Annotations.getAnnotation(T2.class, A1.class);
		Consoles.println(msg6);
		String msg5 = "T3: " + Annotations.getAnnotation(T3.class, A1.class);
		Consoles.println(msg5);
		String msg4 = "T4: " + Annotations.getAnnotation(T4.class, A1.class);
		Consoles.println(msg4);
		String msg3 = "T5: " + Annotations.getAnnotation(T5.class, A1.class);
		Consoles.println(msg3);
		String msg2 = "T6: " + Annotations.getAnnotation(T6.class, A1.class);
		Consoles.println(msg2);
		String msg1 = "T7: " + Annotations.getAnnotation(T7.class, A1.class);
		Consoles.println(msg1);
		String msg = "T8: " + Annotations.getAnnotation(T8.class, A1.class);
		Consoles.println(msg);
	}

	@Test
	void test02() {
		String msg7 = "T1: " + Arrays.toString(Annotations.getRepeatableAnnotation(T1.class, R1.class));
		Consoles.println(msg7);
		String msg6 = "T2: " + Arrays.toString(Annotations.getRepeatableAnnotation(T2.class, R1.class));
		Consoles.println(msg6);
		String msg5 = "T3: " + Arrays.toString(Annotations.getRepeatableAnnotation(T3.class, R1.class));
		Consoles.println(msg5);
		String msg4 = "T4: " + Arrays.toString(Annotations.getRepeatableAnnotation(T4.class, R1.class));
		Consoles.println(msg4);
		String msg3 = "T5: " + Arrays.toString(Annotations.getRepeatableAnnotation(T5.class, R1.class));
		Consoles.println(msg3);
		String msg2 = "T6: " + Arrays.toString(Annotations.getRepeatableAnnotation(T6.class, R1.class));
		Consoles.println(msg2);
		String msg1 = "T7: " + Arrays.toString(Annotations.getRepeatableAnnotation(T7.class, R1.class));
		Consoles.println(msg1);
		String msg = "T8: " + Arrays.toString(Annotations.getRepeatableAnnotation(T8.class, R1.class));
		Consoles.println(msg);
	}

	@Test
	void test03() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException {
		A1 a1 = Annotations.getAnnotation(T1.class, A1.class);
		InvocationHandler handler = Proxy.getInvocationHandler(a1);
		Field field = handler.getClass().getDeclaredField("memberValues");
		field.setAccessible(true);
		Consoles.println(field.get(handler));

		{
			Method value = A2.class.getMethod("value");
			Consoles.println(value.getDefaultValue());
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



