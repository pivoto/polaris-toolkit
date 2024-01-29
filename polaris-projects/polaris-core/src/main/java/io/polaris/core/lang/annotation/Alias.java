package io.polaris.core.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 * @since 1.8,  Jan 06, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Alias {

	Class<? extends Annotation> DEFAULT_ANNOTATION = Annotation.class;

	String value() default "";

	Class<? extends Annotation> annotation() default Annotation.class;
}
