package io.polaris.core.spi.condition.support;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.consts.JavaVersion;
import io.polaris.core.lang.annotation.AnnotationAttributes;
import io.polaris.core.lang.primitive.Booleans;
import io.polaris.core.spi.condition.SpiCondition;
import io.polaris.core.spi.condition.SpiConditional;

/**
 * @author Qt
 * @since Mar 24, 2026
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SpiConditional(SpiConditionalOnJavaVersion.SpiConditionOnJavaVersion.class)
public @interface SpiConditionalOnJavaVersion {

	JavaVersion value();

	boolean equalsOrNewer() default true;

	static class SpiConditionOnJavaVersion implements SpiCondition {

		@Override
		public boolean matches(Class<? extends Annotation> annotationType, AnnotationAttributes annotationAttributes, Class<?> targetType, ClassLoader targetClassLoader) {
			if (!SpiConditionalOnJavaVersion.class.isAssignableFrom(annotationType)) {
				return false;
			}
			JavaVersion version = annotationAttributes.getEnum("value", JavaVersion.class);
			if (version == null || version == JavaVersion.UNKNOWN) {
				return true;
			}
			Boolean equalsOrNewer = annotationAttributes.getBoolean("equalsOrNewer");
			if (Booleans.isFalse(equalsOrNewer)) {
				return JavaVersion.CURRENT.isOlderThan(version);
			}
			return JavaVersion.CURRENT.isEqualOrNewerThan(version);
		}
	}
}
