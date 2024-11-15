package io.polaris.core.lang.annotation;

import io.polaris.core.io.Consoles;
import io.polaris.core.reflect.Reflects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 * @since  Jan 07, 2024
 */
public class AnnotationsTest02 {

	@Test
	@DisplayName("类注解测试")
	void testClassAlias01() {
		Object[] args5 = new Object[]{MergedAnnotations.of(C1.class).getMergedAnnotation(A1.class)};
		Consoles.println(args5);
		Object[] args4 = new Object[]{MergedAnnotations.of(C1_1.class).getMergedAnnotation(A1.class)};
		Consoles.println(args4);
		Object[] args3 = new Object[]{MergedAnnotations.of(C1_1x.class).getMergedAnnotation(A1.class)};
		Consoles.println(args3);
		Object[] args2 = new Object[]{MergedAnnotations.of(C1_2.class).getMergedAnnotation(A1.class)};
		Consoles.println(args2);
		Object[] args1 = new Object[]{MergedAnnotations.of(C1_2x.class).getMergedAnnotation(A1.class)};
		Consoles.println(args1);
		Object[] args = new Object[]{MergedAnnotations.of(C1_2_1.class).getMergedAnnotation(A1.class)};
		Consoles.println(args);
		//		Consoles.println(MergedAnnotations.of(C1_2_1.class).getMergedRepeatableAnnotation(A1.class));
	}
	@Test
	@DisplayName("方法注解测试")
	void testClassAlias02() {

		Object[] args5 = new Object[]{MergedAnnotations.of(Reflects.getMethod(C1.class,"m1")).getMergedAnnotation(A1.class)};
		Consoles.println(args5);
		Object[] args4 = new Object[]{MergedAnnotations.of(Reflects.getMethod(C1_1.class,"m1")).getMergedAnnotation(A1.class)};
		Consoles.println(args4);
		Object[] args3 = new Object[]{MergedAnnotations.of(Reflects.getMethod(C1_1x.class,"m1")).getMergedAnnotation(A1.class)};
		Consoles.println(args3);
		Object[] args2 = new Object[]{MergedAnnotations.of(Reflects.getMethod(C1_2.class,"m1")).getMergedAnnotation(A1.class)};
		Consoles.println(args2);
		Object[] args1 = new Object[]{MergedAnnotations.of(Reflects.getMethod(C1_2x.class,"m1")).getMergedAnnotation(A1.class)};
		Consoles.println(args1);
		Object[] args = new Object[]{MergedAnnotations.of(Reflects.getMethod(C1_2_1.class,"m1")).getMergedAnnotation(A1.class)};
		Consoles.println(args);
	}

	@A1(name = "C1")
	static class C1 {
		@A1(name = "C1.m1")
		void m1(){}
	}

	@A1(name = "C1_1")
	static class C1_1 extends C1 {
		@A1(name = "C1_1.m1")
		void m1(){}
	}

	static class C1_1x extends C1_1 {
		void m1(){}
	}

	@A1(name = "C1_2")
	static class C1_2 extends C1 {
		@A1(name = "C1_2.m1")
		void m1(){}
	}
	static class C1_2x extends C1_2 {
		void m1(){}
	}

	@A1(name = "C1_2_1")
	static class C1_2_1 extends C1_2 {
		@A1(name = "C1_2_1.m1")
		void m1(){}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER})
	public static @interface A1 {
		@Alias(value = "name")
		String value() default "A1";

		@Alias(value = "value")
		String name() default "A1";

		String desc() default "A1.desc";
	}


}
