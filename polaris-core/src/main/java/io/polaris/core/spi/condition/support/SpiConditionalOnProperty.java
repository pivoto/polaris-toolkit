package io.polaris.core.spi.condition.support;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.env.GlobalStdEnv;
import io.polaris.core.lang.annotation.AnnotationAttributes;
import io.polaris.core.lang.primitive.Booleans;
import io.polaris.core.spi.condition.SpiCondition;
import io.polaris.core.spi.condition.SpiConditional;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since Mar 24, 2026
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SpiConditional(SpiConditionalOnProperty.SpiConditionOnProperty.class)
public @interface SpiConditionalOnProperty {

	String prefix() default "";

	String[] name() default {};

	String[] havingValue() default {};

	boolean matchIfMissing() default false;

	static class SpiConditionOnProperty implements SpiCondition {

		@Override
		public boolean matches(Class<? extends Annotation> annotationType, AnnotationAttributes annotationAttributes, Class<?> targetType, ClassLoader targetClassLoader) {
			if (!SpiConditionalOnProperty.class.isAssignableFrom(annotationType)) {
				return false;
			}
			String[] nameArray = annotationAttributes.getStringArray("name");
			String[] valueArray = annotationAttributes.getStringArray("havingValue");
			Boolean matchIfMissing = annotationAttributes.getBoolean("matchIfMissing");
			String prefix = annotationAttributes.getString("prefix");
			if (Strings.isNotBlank(prefix)) {
				prefix = prefix.replaceAll("\\.+$", "");
				if (nameArray != null && nameArray.length > 0) {
					for (int i = 0; i < nameArray.length; i++) {
						String property = prefix + "." + nameArray[i];
						String havingValue = valueArray.length <= i ? null : valueArray[i];
						if (!matches(property, havingValue, matchIfMissing)) {
							return false;
						}
					}
				}
			} else {
				if (nameArray != null && nameArray.length > 0) {
					for (int i = 0; i < nameArray.length; i++) {
						String property = nameArray[i];
						String havingValue = valueArray.length <= i ? null : valueArray[i];
						if (!matches(property, havingValue, matchIfMissing)) {
							return false;
						}
					}
				}
			}
			return true;
		}

		private boolean matches(String property, String havingValue, Boolean matchIfMissing) {
			String val = GlobalStdEnv.get(property);
			if (val == null) {
				return Booleans.isTrue(matchIfMissing);
			}
			if (Strings.isNotBlank(havingValue)) {
				return val.equals(havingValue);
			}
			return !"false".equalsIgnoreCase(val);
		}
	}
}
