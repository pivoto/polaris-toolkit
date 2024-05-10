package io.polaris.core.lang.annotation;

import io.polaris.core.TestConsole;
import io.polaris.core.reflect.Reflects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.*;

/**
 * @author Qt
 * @since  Jan 07, 2024
 */
public class AnnotationsTest03 {

	@Test
	@DisplayName("类注解测试")
	void testClassAlias01() {
		TestConsole.println(MergedAnnotations.of(C1.class).getMergedRepeatableAnnotation(R1.class));
		TestConsole.println(MergedAnnotations.of(C1_1.class).getMergedRepeatableAnnotation(R1.class));
		TestConsole.println(MergedAnnotations.of(C1_1x.class).getMergedRepeatableAnnotation(R1.class));
		TestConsole.println(MergedAnnotations.of(C1_2.class).getMergedRepeatableAnnotation(R1.class));
		TestConsole.println(MergedAnnotations.of(C1_2x.class).getMergedRepeatableAnnotation(R1.class));
		TestConsole.println(MergedAnnotations.of(C1_2_1.class).getMergedRepeatableAnnotation(R1.class));
	}

	@Test
	@DisplayName("方法注解测试")
	void testClassAlias02() {

		TestConsole.println(MergedAnnotations.of(Reflects.getMethod(C1.class, "m1")).getMergedRepeatableAnnotation(R1.class));
		TestConsole.println(MergedAnnotations.of(Reflects.getMethod(C1_1.class, "m1")).getMergedRepeatableAnnotation(R1.class));
		TestConsole.println(MergedAnnotations.of(Reflects.getMethod(C1_1x.class, "m1")).getMergedRepeatableAnnotation(R1.class));
		TestConsole.println(MergedAnnotations.of(Reflects.getMethod(C1_2.class, "m1")).getMergedRepeatableAnnotation(R1.class));
		TestConsole.println(MergedAnnotations.of(Reflects.getMethod(C1_2x.class, "m1")).getMergedRepeatableAnnotation(R1.class));
		TestConsole.println(MergedAnnotations.of(Reflects.getMethod(C1_2_1.class, "m1")).getMergedRepeatableAnnotation(R1.class));
	}

	@A1(name = "C1")
	@R1(name = "C1.0")
	@R1(name = "C1.1")
	static class C1 {
		@A1(name = "C1.m1")
		@R1(name = "C1.m1.0")
		@R1(name = "C1.m1.1")
		void m1() {
		}
	}

	@A1(name = "C1_1")
	@R1(name = "C1_1.0")
	@R1(name = "C1_1.1")
	static class C1_1 extends C1 {
		@A1(name = "C1_1.m1")
		@R1(name = "C1_1.m1.0")
		@R1(name = "C1_1.m1.1")
		void m1() {
		}
	}

	static class C1_1x extends C1_1 {
		void m1() {
		}
	}

	@A1(name = "C1_2")
	@R1(name = "C1_2.0")
	@R1(name = "C1_2.1")
	static class C1_2 extends C1 {
		@A1(name = "C1_2.m1")
		@R1(name = "C1_2.m1.0")
		@R1(name = "C1_2.m1.1")
		void m1() {
		}
	}

	static class C1_2x extends C1_2 {
		void m1() {
		}
	}

	@A1(name = "C1_2_1")
	@R1(name = "C1_2_1.0")
	@R1(name = "C1_2_1.1")
	static class C1_2_1 extends C1_2 {
		@A1(name = "C1_2_1.m1")
		@R1(name = "C1_2_1.m1.0")
		@R1(name = "C1_2_1.m1.1")
		void m1() {
		}
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


	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER})
	@Repeatable(R1S.class)
	public static @interface R1 {
		@Alias(value = "name")
		String value() default "R1";

		@Alias(value = "value")
		String name() default "R1";

		String desc() default "R1.desc";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER})
	public static @interface R1S {
		R1[] value();
	}
}
