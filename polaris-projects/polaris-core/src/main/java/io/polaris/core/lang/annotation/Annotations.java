package io.polaris.core.lang.annotation;

import io.polaris.core.reflect.Reflects;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * @author Qt
 * @since 1.8,  Nov 12, 2023
 */
public class Annotations {


	public static <A extends Annotation> A getAnnotation(AnnotatedElement element, Class<A> annotationType) {
		return RawAnnotations.getAnnotation(element, annotationType);
	}

	public static <A extends Annotation> A[] getRepeatableAnnotation(AnnotatedElement element, Class<A> annotationType) {
		return RawAnnotations.getRepeatableAnnotation(element, annotationType);
	}

	public static <A extends Annotation> A getMergedAnnotation(AnnotatedElement element, Class<A> annotationType) {
		return MergedAnnotations.of(element).getMergedAnnotation(annotationType);
	}

	public static <A extends Annotation> Set<A> getMergedRepeatableAnnotation(AnnotatedElement element, Class<A> annotationType) {
		return MergedAnnotations.of(element).getMergedRepeatableAnnotation(annotationType);
	}

	public static <A extends Annotation> A newInstance(Class<A> annotationType, Map<String, Object> values) {
		return AnnotationInvocationHandler.createProxy(annotationType, values);
	}

	public static Annotation[] getRepeatedAnnotations(Annotation annotation) {
		Class<? extends Annotation> annotationType = annotation.annotationType();
		if (!isRepeatable(annotationType)) {
			return null;
		}
		return (Annotation[]) Reflects.invokeQuietly(annotation, annotationType.getDeclaredMethods()[0]);
	}

	@SuppressWarnings("unchecked")
	public static Class<? extends Annotation> getRepeatedAnnotationType(Class<? extends Annotation> annotationType) {
		Method[] methods = annotationType.getDeclaredMethods();
		if (methods.length != 1) {
			return null;
		}
		Method method = methods[0];
		if (!method.getName().equals("value")) {
			return null;
		}
		Class<?> returnType = method.getReturnType();
		if (!returnType.isArray()) {
			return null;
		}
		Class<?> componentType = returnType.getComponentType();
		if (!componentType.isAnnotation()) {
			return null;
		}
		Repeatable annotation = componentType.getAnnotation(Repeatable.class);
		if (annotation == null) {
			return null;
		}
		if (annotation.value() == annotationType) {
			return (Class<? extends Annotation>) componentType;
		}
		return null;
	}

	public static <A extends Annotation> boolean isRepeatable(Class<A> annotationType) {
		return getRepeatedAnnotationType(annotationType) != null;
	}

	public static <A extends Annotation> boolean hasAliasDefinition(Class<A> annotationType) {
		Method[] methods = AnnotationAttributes.getAnnotationMembers(annotationType);
		for (Method method : methods) {
			Alias alias = method.getAnnotation(Alias.class);
			if (alias != null && (alias.annotation() == annotationType || alias.annotation() == Alias.DEFAULT_ANNOTATION)) {
				return true;
			}
		}
		return false;
	}
}
