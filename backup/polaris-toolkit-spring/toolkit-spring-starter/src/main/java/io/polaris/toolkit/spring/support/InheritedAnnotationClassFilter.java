package io.polaris.toolkit.spring.support;

import lombok.Getter;
import lombok.Setter;
import org.springframework.aop.ClassFilter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Qt
 * @version Jan 05, 2022
 * @since 1.8
 */
@Getter
@Setter
public class InheritedAnnotationClassFilter implements ClassFilter {

	private final Class<? extends Annotation> annotationType;
	private boolean checkInherited = true;
	private boolean checkMethods;

	public InheritedAnnotationClassFilter(Class<? extends Annotation> annotationType) {
		this.annotationType = annotationType;
	}

	public InheritedAnnotationClassFilter(Class<? extends Annotation> annotationType,
			boolean checkInherited) {
		this.annotationType = annotationType;
		this.checkInherited = checkInherited;
	}

	public InheritedAnnotationClassFilter(Class<? extends Annotation> annotationType,
			boolean checkInherited, boolean checkMethods) {
		this.annotationType = annotationType;
		this.checkInherited = checkInherited;
		this.checkMethods = checkMethods;
	}

	public static InheritedAnnotationClassFilter withClassAnnotation(Class<? extends Annotation> annotationType, boolean checkInherited) {
		return new InheritedAnnotationClassFilter(annotationType, checkInherited);
	}

	public static InheritedAnnotationClassFilter withClassAnnotation(Class<? extends Annotation> annotationType) {
		return new InheritedAnnotationClassFilter(annotationType, true);
	}

	public static InheritedAnnotationClassFilter withClassOrMethodAnnotation(Class<? extends Annotation> annotationType, boolean checkInherited) {
		return new InheritedAnnotationClassFilter(annotationType, checkInherited, true);
	}

	public static InheritedAnnotationClassFilter withClassOrMethodAnnotation(Class<? extends Annotation> annotationType) {
		return new InheritedAnnotationClassFilter(annotationType, true, true);
	}

	@Override
	public boolean matches(Class<?> targetClass) {
		if (checkInherited) {
			if (AnnotatedElementUtils.hasAnnotation(targetClass, this.annotationType)) {
				return true;
			}
		} else {
			if (targetClass.isAnnotationPresent(this.annotationType)) {
				return true;
			}
		}
		if (checkMethods) {
			Set<Class<?>> classes = new LinkedHashSet<>();
			if (!Proxy.isProxyClass(targetClass)) {
				classes.add(ClassUtils.getUserClass(targetClass));
			}
			classes.addAll(ClassUtils.getAllInterfacesForClassAsSet(targetClass));

			for (Class<?> clazz : classes) {
				Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
				for (Method method : methods) {
					if (AnnotatedElementUtils.hasAnnotation(method, this.annotationType)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
