package io.polaris.core.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.reflect.Reflects;

/**
 * @author Qt
 * @since Nov 12, 2023
 */
public class Annotations {


	@Nullable
	public static <A extends Annotation> A getRawAnnotation(AnnotatedElement element, Class<A> annotationType) {
		return RawAnnotations.getAnnotation(element, annotationType);
	}

	@Nonnull
	public static <A extends Annotation> A[] getRawRepeatableAnnotation(AnnotatedElement element, Class<A> annotationType) {
		return RawAnnotations.getRepeatableAnnotation(element, annotationType);
	}

	@Nullable
	public static <A extends Annotation> A getMergedAnnotation(AnnotatedElement element, Class<A> annotationType) {
		return MergedAnnotations.getMergedAnnotation(element, annotationType);
	}

	@Nonnull
	public static <A extends Annotation> Set<A> getMergedRepeatableAnnotation(AnnotatedElement element, Class<A> annotationType) {
		return MergedAnnotations.getMergedRepeatableAnnotation(element, annotationType);
	}

	@Nonnull
	public static <A extends Annotation> Set<A> getTopMergedRepeatableAnnotation(AnnotatedElement element, Class<A> annotationType) {
		return MergedAnnotations.getTopMergedRepeatableAnnotation(element, annotationType);
	}

	public static <A extends Annotation> A newInstance(Class<A> annotationType, Map<String, Object> values) {
		return AnnotationInvocationHandler.createProxy(annotationType, values);
	}

	public static <A extends Annotation> A newInstance(Class<A> annotationType, Map<String, Object> values, boolean allowedIncomplete) {
		return AnnotationInvocationHandler.createProxy(annotationType, values, allowedIncomplete);
	}

	public static <A extends Annotation> A newInstanceWithDefaults(Class<A> annotationType, Map<String, Object> values) {
		return AnnotationAttributes.of(annotationType).asAnnotation();
	}

	public static Annotation[] getRepeatedAnnotations(Annotation annotation) {
		Class<? extends Annotation> annotationType = annotation.annotationType();
		if (!isRepeatable(annotationType)) {
			return null;
		}
		return (Annotation[]) Reflects.invokeQuietly(annotation, annotationType.getDeclaredMethods()[0]);
	}

	/**
	 * 获取可重复注解的目标元素类型
	 * <p>
	 * 通过检查注解类型是否是某个可重复注解的容器来确定其对应的可重复注解类型
	 *
	 * @param annotationType 注解类型
	 * @return 如果是可重复注解的容器类型，则返回对应的可重复注解类型；否则返回null
	 */
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
			Set<AliasAttribute> aliasAttributes = AliasFinders.findAliasAttributes(method);
			if (aliasAttributes != null) {
				for (AliasAttribute alias : aliasAttributes) {
					if (alias != null && (alias.annotationType() == annotationType || alias.annotationType() == Annotation.class)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static <A extends Annotation> boolean hasAliasDefinition(AliasFinder aliasFinder, Class<A> annotationType) {
		Method[] methods = AnnotationAttributes.getAnnotationMembers(annotationType);
		for (Method method : methods) {
			Set<AliasAttribute> aliasAttributes = AliasFinders.findAliasAttributes(aliasFinder, method);
			if (aliasAttributes != null) {
				for (AliasAttribute alias : aliasAttributes) {
					if (alias != null && (alias.annotationType() == annotationType || alias.annotationType() == Annotation.class)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static AnnotationAttributes asAnnotationAttributes(Annotation annotation) {
		return AnnotationAttributes.of(annotation);
	}

	public static AnnotationAttributes asAnnotationAttributes(Class<? extends Annotation> annotationType) {
		return AnnotationAttributes.of(annotationType);
	}

	public static MergedAnnotation asMergedAnnotation(Annotation annotation) {
		return MergedAnnotation.of(annotation);
	}

	public static HierarchyMergedAnnotation asHierarchyMergedAnnotation(List<Set<MergedAnnotation>> hierarchyAnnotations) {
		return HierarchyMergedAnnotation.of(hierarchyAnnotations);
	}

	public static HierarchyMergedAnnotation asHierarchyMergedAnnotation(AnnotatedElement element) {
		return HierarchyMergedAnnotation.of(element);
	}

}
