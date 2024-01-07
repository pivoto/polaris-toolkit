package io.polaris.core.lang.annotation;

import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.lang.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Jan 06, 2024
 */
public class AnnotationsTest01 {


	@Test
	void testNewInstance() {
		Map<String, Object> map = new HashMap<>();
		map.put("value", "test...");
		Anno1 anno1 = Annotations.newInstance(Anno1.class, map);
		Anno1 anno2 = Annotations.newInstance(Anno1.class, map);
		System.out.println(anno1.annotationType());
		System.out.println(anno1);
		System.out.println(anno2);
		System.out.println(anno1.equals(anno2));
		System.out.println(anno1.hashCode());
		System.out.println(anno2.hashCode());
		System.out.println(anno1.annotationType());
		System.out.println(anno2.annotationType());
		System.out.println(anno1.value());
		System.out.println(anno2.value());
	}

	@Test
	void testMergeAnnotation() {
		@Anno1x1
		class A {
		}

		MergedAnnotations mergedAnnotations = new MergedAnnotations(A.class);
		MergedAnnotationsTest.display(mergedAnnotations,new PrintWriter(System.out));
	}

	@Test
	void testMergeClass() {
		@Anno1x1("A")
		class A implements I {
		}
		@Anno1x1("B")
		class B extends A implements I {
		}

		MergedAnnotations mergedAnnotations = new MergedAnnotations(B.class);
		System.out.println(mergedAnnotations);
		mergedAnnotations.getSortedAnnotations().forEach((k, v) -> {
			System.out.println(">>" + k + ">>");
			for (MergedAnnotation mergedAnnotation : v) {
				System.out.println(mergedAnnotation);
			}
		});
		System.out.println(mergedAnnotations.getMergedAnnotation(RepeatableAnno1.class));
	}

	@Anno1x1("I")
	public static interface I {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER})
	public static @interface Anno1 {
		String value() default "Anno1";

		@Alias(annotation = Anno1.class, value = "name")
		String name() default "Anno1";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER})
	@Anno1
	public static @interface Anno1x1 {
		@Alias(annotation = Anno1.class, value = "value")
		String value() default "Anno1x1";
	}


	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER})
	@Repeatable(RepeatableAnnoArray.class)
	public static @interface RepeatableAnno1 {
		String value() default "RepeatableAnno1";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER})
	public static @interface RepeatableAnnoArray {
		RepeatableAnno1[] value();
	}
}
