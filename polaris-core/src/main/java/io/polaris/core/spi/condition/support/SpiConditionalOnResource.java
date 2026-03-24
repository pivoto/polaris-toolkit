package io.polaris.core.spi.condition.support;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import io.polaris.core.lang.annotation.AnnotationAttributes;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.spi.condition.SpiCondition;
import io.polaris.core.spi.condition.SpiConditional;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since Mar 24, 2026
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SpiConditional(SpiConditionalOnResource.SpiConditionOnResource.class)
public @interface SpiConditionalOnResource {

	String[] value() default {};

	static class SpiConditionOnResource implements SpiCondition {

		@Override
		public boolean matches(Class<? extends Annotation> annotationType, AnnotationAttributes annotationAttributes, Class<?> targetType, ClassLoader targetClassLoader) {
			if (!SpiConditionalOnResource.class.isAssignableFrom(annotationType)) {
				return false;
			}
			String[] nameArray = annotationAttributes.getStringArray("value");
			Set<String> classNames = new LinkedHashSet<>();
			if (nameArray != null) {
				for (String s : nameArray) {
					if (Strings.isBlank(s)) {
						continue;
					}
					URL url = targetClassLoader.getResource(s);
					if (url == null) {
						return false;
					}
				}
			}
			return true;
		}

	}
}
