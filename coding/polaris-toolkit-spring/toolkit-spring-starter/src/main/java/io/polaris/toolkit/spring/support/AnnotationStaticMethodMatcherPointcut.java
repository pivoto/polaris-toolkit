package io.polaris.toolkit.spring.support;

import io.polaris.toolkit.spring.jdbc.TargetDataSource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author Qt
 * @version Jan 05, 2022
 * @since 1.8
 */
@Getter
@Setter
public class AnnotationStaticMethodMatcherPointcut extends StaticMethodMatcherPointcut {
	private final Class<? extends Annotation> annotationType;

	public AnnotationStaticMethodMatcherPointcut(Class<? extends Annotation> annotationType) {
		this.annotationType = annotationType;
		this.setClassFilter(InheritedAnnotationClassFilter.withClassOrMethodAnnotation(annotationType));
	}

	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		if (AnnotatedElementUtils.hasAnnotation(targetClass, annotationType)) {
			return true;
		}
		if (AnnotationUtils.findAnnotation(method, annotationType) != null) {
			return true;
		}
		return false;
	}
}
