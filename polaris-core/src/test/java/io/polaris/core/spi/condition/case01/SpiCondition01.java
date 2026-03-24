package io.polaris.core.spi.condition.case01;

import java.lang.annotation.Annotation;

import io.polaris.core.env.GlobalStdEnv;
import io.polaris.core.lang.annotation.AnnotationAttributes;
import io.polaris.core.spi.condition.SpiCondition;

/**
 * @author Qt
 * @since Mar 24, 2026
 */
public class SpiCondition01 implements SpiCondition {
	@Override
	public boolean matches(Class<? extends Annotation> annotationType, AnnotationAttributes annotationAttributes, Class<?> targetType, ClassLoader targetClassLoader) {
		return GlobalStdEnv.getBoolean(SpiCondition01.class.getName(),false);
	}
}
