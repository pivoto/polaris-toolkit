package io.polaris.core.spi.condition;

import java.lang.annotation.Annotation;

import io.polaris.core.lang.annotation.AnnotationAttributes;

/**
 * @author Qt
 * @since Mar 19, 2026
 */
public interface SpiCondition {

	boolean matches(Class<? extends Annotation> annotationType, AnnotationAttributes annotationAttributes, Class<?> targetType, ClassLoader targetClassLoader);

}
