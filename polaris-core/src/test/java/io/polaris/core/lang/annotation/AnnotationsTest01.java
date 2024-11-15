package io.polaris.core.lang.annotation;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.lang.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @since  Jan 06, 2024
 */
public class AnnotationsTest01 {


	@Test
	void testNewInstance() {
		Map<String, Object> map = new HashMap<>();
		map.put("value", "test...");
		map.put("name", "test...");
		Anno1 anno1 = Annotations.newInstance(Anno1.class, map);
		Anno1 anno2 = Annotations.newInstance(Anno1.class, map);
		Object[] args4 = new Object[]{anno1.annotationType()};
		Consoles.println(args4);
		Consoles.println(anno1);
		Consoles.println(anno2);
		Consoles.println(anno1.equals(anno2));
		Object[] args3 = new Object[]{anno1.hashCode()};
		Consoles.println(args3);
		Object[] args2 = new Object[]{anno2.hashCode()};
		Consoles.println(args2);
		Object[] args1 = new Object[]{anno1.annotationType()};
		Consoles.println(args1);
		Object[] args = new Object[]{anno2.annotationType()};
		Consoles.println(args);
		Consoles.println(anno1.value());
		Consoles.println(anno2.value());
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
		Consoles.println(mergedAnnotations);
		mergedAnnotations.getSortedAnnotations().forEach((k, v) -> {
			Consoles.println(">>" + k + ">>");
			for (MergedAnnotation mergedAnnotation : v) {
				Consoles.println(mergedAnnotation);
			}
		});
		Object[] args = new Object[]{mergedAnnotations.getMergedAnnotation(RepeatableAnno1.class)};
		Consoles.println(args);
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
