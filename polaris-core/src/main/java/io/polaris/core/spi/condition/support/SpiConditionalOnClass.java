package io.polaris.core.spi.condition.support;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import io.polaris.core.env.GlobalStdEnv;
import io.polaris.core.lang.annotation.AnnotationAttributes;
import io.polaris.core.lang.primitive.Booleans;
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
@SpiConditional(SpiConditionalOnClass.SpiConditionOnClass.class)
public @interface SpiConditionalOnClass {

	Class<?>[] value() default {};

	String[] name() default {};

	static class SpiConditionOnClass implements SpiCondition {

		@Override
		public boolean matches(Class<? extends Annotation> annotationType, AnnotationAttributes annotationAttributes, Class<?> targetType, ClassLoader targetClassLoader) {
			if (!SpiConditionalOnClass.class.isAssignableFrom(annotationType)) {
				return false;
			}
			Class<?>[] valueArray = annotationAttributes.getClassArray("value");
			String[] nameArray = annotationAttributes.getStringArray("name");
			Set<String> classNames = new LinkedHashSet<>();
			if (valueArray != null) {
				for (Class<?> aClass : valueArray) {
					classNames.add(aClass.getName());
				}
			}
			if (nameArray != null && nameArray.length > 0) {
				classNames.addAll(Arrays.asList(nameArray));
			}

			for (String className : classNames) {
				if (Strings.isBlank(className)) {
					continue;
				}
				if (!Reflects.hasCLass(className, targetClassLoader)) {
					return false;
				}
			}
			return true;
		}


	}
}
