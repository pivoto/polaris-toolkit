package io.polaris.core.lang.annotation.case02;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.Arrays;

import io.polaris.core.io.Consoles;

/**
 * @author Qt
 * @since Mar 18, 2026
 */
@RepeatedAnno("from RepeatableClass 1")
@RepeatedAnno("from RepeatableClass 2")
//@RepeatableAnno
public class RepeatableClass {

	public static void main(String[] args) {
		AnnotatedType[] annotatedInterfaces = RepeatableClass.class.getAnnotatedInterfaces();
		Consoles.println(Arrays.toString(annotatedInterfaces));
		for (AnnotatedType type : annotatedInterfaces) {
			Consoles.println(type, type.getType());
		}

		Annotation[] annotations = RepeatableClass.class.getAnnotations();
		Consoles.println(Arrays.toString(annotations));
		for (Annotation annotation : annotations) {
			Consoles.println(annotation);
		}
	}
}
